package com.zalo.modules.classificationCard.service;

import com.zalo.common.configuration.json.G;
import com.zalo.modules.classificationCard.dto.request.AssignClassificationCard;
import com.zalo.modules.classificationCard.dto.request.ClassificationCardRequest;
import com.zalo.modules.classificationCard.dto.request.PositionUpdateRequest;
import com.zalo.modules.classificationCard.dto.response.ClassificationCardResponse;
import com.zalo.modules.classificationCard.entity.ClassificationCard;
import com.zalo.modules.classificationCard.entity.ClassificationConversation;
import com.zalo.modules.conversation.entities.Conversation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassificationCardService {
    ClassificationCardRepository classificationCardRepo;
    ClassificationConversationRepository classificationConversationRepo;

    public List<ClassificationCardResponse> getAll(Long userId) {
        // 1. Lấy tất cả các thẻ của user
        List<ClassificationCard> cards = classificationCardRepo.findAllByCuAndSttOrderByPositionAsc(userId, 1);

        if (cards.isEmpty()) return new ArrayList<>();

        List<Long> cardIds = cards.stream().map(ClassificationCard::getId).collect(Collectors.toList());

        // 2. Lấy tất cả các bản ghi trung gian
        List<ClassificationConversation> relations = classificationConversationRepo.findByClassificationIdIn(cardIds);

        // --- BẮT ĐẦU GHÉP MẢNG ---

        // 3. Gom nhóm ConversationId theo ClassificationId: Map<Long, List<Long>>
        Map<Long, List<Long>> convIdsByCardId = relations.stream()
                .collect(Collectors.groupingBy(
                        ClassificationConversation::getClassificationId,
                        Collectors.mapping(ClassificationConversation::getConversationId, Collectors.toList())
                ));

        return cards.stream().map(card -> {
            ClassificationCardResponse dto = new ClassificationCardResponse(card);

            // Lấy danh sách ID conv thuộc card này từ Map
            List<Long> convIds = convIdsByCardId.getOrDefault(card.getId(), new ArrayList<>());

            dto.setConversationIds(convIds);
            return dto;
        }).collect(Collectors.toList());
    }

    public ClassificationCard create(ClassificationCardRequest card, Long userId) {
        if (classificationCardRepo.existsByNameAndCuAndStt(card.name, userId, 1)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "nameExist");
        }

        ClassificationCard e = new ClassificationCard();
        e.setName(card.name);
        e.setColor(card.color);
        e.setPosition(card.position);
        e.setCu(userId);

        classificationCardRepo.save(e);

        // 2. Lưu trực tiếp vào bảng trung gian bằng ID
        if (card.conversationIds != null && !card.conversationIds.isEmpty()) {
            removeAndAddClassConvs(e.getId(), card.conversationIds, userId);
        }

        return e;
    }

    public ClassificationCard update(Long id, ClassificationCardRequest card, Long userId) {
        ClassificationCard e = findById(id);

        // Kiểm tra quyền sở hữu (Security check)
        if (!e.getCu().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "accessDenied");
        }

        // Nếu tên gửi lên khác với tên hiện tại trong DB
        if (!e.getName().equals(card.name)) {
            // Kiểm tra xem tên mới này đã được user này dùng cho card khác chưa
            if (classificationCardRepo.existsByNameAndCuAndStt(card.name, userId, 1)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "nameExist");
            }
        }

        e.setName(card.name);
        e.setColor(card.color);
        e.setPosition(card.position);

        classificationCardRepo.save(e);

        if (card.conversationIds != null && !card.conversationIds.isEmpty()) {
            removeAndAddClassConvs(id, card.conversationIds, userId);
        }

        return e;
    }

    public void removeAndAddClassConvs(Long cardId, List<Long> convIds, Long userId) {
        System.out.println(G.toJson(convIds));
        // xóa trước thêm sau
        classificationConversationRepo.deleteByConversationIdInAndUserId(convIds, userId);

        List<ClassificationConversation> relations = convIds.stream()
                .map(convId -> new ClassificationConversation(cardId, convId, userId))
                .collect(Collectors.toList());

        classificationConversationRepo.saveAll(relations);
    }

    public void delete(Long id) {
        ClassificationCard e = findById(id);

        e.setStt(-1);
        classificationCardRepo.save(e);
    }

    public ClassificationCard findById(Long id) {
        return classificationCardRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }

    public void updatePositions(List<PositionUpdateRequest> updates) {
        if (updates == null || updates.isEmpty()) return;

        // 1. Chuyển List updates thành Map để tra cứu cực nhanh
        Map<Long, Integer> idToPositionMap = updates.stream()
                .collect(Collectors.toMap(PositionUpdateRequest::getId, PositionUpdateRequest::getPosition));

        // 2. Lấy các thực thể từ DB (chỉ lấy những gì cần thiết)
        List<ClassificationCard> cards = classificationCardRepo.findAllById(idToPositionMap.keySet());

        // 3. Cập nhật position bằng cách tra cứu Map
        cards.forEach(card -> {
            Integer newPos = idToPositionMap.get(card.getId());
            if (newPos != null) {
                card.setPosition(newPos);
            }
        });

        // 4. Batch save (như đã cấu hình trong application.properties)
        classificationCardRepo.saveAll(cards);
    }

    public void assignCardToConversation(Long id, AssignClassificationCard dto, Long userId) {
        if (dto.type.equals("remove")) {
            Optional<ClassificationConversation> e = classificationConversationRepo.findByClassificationIdAndConversationId(id, dto.convId);

            e.ifPresent(classificationConversationRepo::delete);
        } else {
            removeAndAddClassConvs(id, Collections.singletonList(dto.convId), userId);
        }
    }
}
