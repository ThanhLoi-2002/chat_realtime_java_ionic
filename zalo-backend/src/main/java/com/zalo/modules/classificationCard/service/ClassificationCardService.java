package com.zalo.modules.classificationCard.service;

import com.zalo.modules.classificationCard.dto.request.ClassificationCardRequest;
import com.zalo.modules.classificationCard.dto.request.PositionUpdateRequest;
import com.zalo.modules.classificationCard.entity.ClassificationCard;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassificationCardService {
    ClassificationCardRepository classificationCardRepo;

    public List<ClassificationCard> getAll(Long userId) {
        return classificationCardRepo.findAllByCuAndSttOrderByPositionAsc(userId, 1);
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

        return classificationCardRepo.save(e);
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

        return classificationCardRepo.save(e);
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
