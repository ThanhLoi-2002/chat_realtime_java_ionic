package com.zalo.modules.classificationCard;

import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.modules.classificationCard.dto.request.AssignClassificationCard;
import com.zalo.modules.classificationCard.dto.request.ClassificationCardRequest;
import com.zalo.modules.classificationCard.dto.request.PositionUpdateRequest;
import com.zalo.modules.classificationCard.dto.response.ClassificationCardResponse;
import com.zalo.modules.classificationCard.service.ClassificationCardService;
import com.zalo.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classification-card")
@RequiredArgsConstructor
public class ClassificationCardController {
    private final ClassificationCardService service;

    @GetMapping
    public List<ClassificationCardResponse> getAll(@CurrentUser User user) {
        return service.getAll(user.getId());
    }

    @PostMapping
    public ClassificationCardResponse create(@CurrentUser User user, @RequestBody ClassificationCardRequest card) {
        return new ClassificationCardResponse(service.create(card, user.getId()));
    }

    @PutMapping("/{id}")
    public ClassificationCardResponse update(@PathVariable Long id, @RequestBody ClassificationCardRequest card, @CurrentUser User user) {
        return new ClassificationCardResponse(service.update(id, card, user.getId()));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/update-positions")
    public void updatePositions(@RequestBody List<PositionUpdateRequest> updates) {
        service.updatePositions(updates);
    }

    @PatchMapping("/{id}/assign-card")
    public void assignCard(
            @PathVariable Long id,
            @RequestBody AssignClassificationCard dto,
            @CurrentUser User user) {

        service.assignCardToConversation(id, dto, user.getId());
    }
}
