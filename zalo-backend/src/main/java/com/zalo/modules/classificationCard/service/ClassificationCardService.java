package com.zalo.modules.classificationCard.service;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        // 2. Lấy tất cả các bản ghi trung gian kèm theo thông tin Conversation
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
            Long cardId = e.getId();

            List<ClassificationConversation> relations = card.conversationIds.stream()
                    .map(convId -> new ClassificationConversation(cardId, convId))
                    .collect(Collectors.toList());

            classificationConversationRepo.saveAll(relations);
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
            updateClassificationConvs(id, card.conversationIds);
        }

        return e;
    }

    public void updateClassificationConvs(Long cardId, List<Long> newConvIds) {
        // Lấy danh sách ID hiện tại từ DB
        List<Long> currentIds = classificationConversationRepo.findConversationIdsByCardId(cardId);

        // Những ID cần xóa: Có trong DB nhưng không có trong danh sách mới
        List<Long> idsToRemove = currentIds.stream()
                .filter(id -> !newConvIds.contains(id))
                .collect(Collectors.toList());

        // Những ID cần thêm: Có trong danh sách mới nhưng chưa có trong DB
        List<Long> idsToAdd = newConvIds.stream()
                .filter(id -> !currentIds.contains(id))
                .toList();

        if (!idsToRemove.isEmpty()) {
            classificationConversationRepo.deleteByClassificationIdAndConversationIdIn(cardId, idsToRemove);
        }

        if (!idsToAdd.isEmpty()) {
            List<ClassificationConversation> relations = idsToAdd.stream()
                    .map(convId -> new ClassificationConversation(cardId, convId))
                    .collect(Collectors.toList());
            classificationConversationRepo.saveAll(relations);
        }
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
}
