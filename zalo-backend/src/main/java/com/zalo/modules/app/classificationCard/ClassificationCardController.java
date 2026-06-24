package com.zalo.modules.app.classificationCard;

import com.zalo.common.configuration.anotation.currentUser.CurrentUser;
import com.zalo.modules.app.classificationCard.dto.request.AssignClassificationCard;
import com.zalo.modules.app.classificationCard.dto.request.ClassificationCardRequest;
import com.zalo.modules.app.classificationCard.dto.request.PositionUpdateRequest;
import com.zalo.modules.app.classificationCard.dto.response.ClassificationCardResponse;
import com.zalo.modules.app.classificationCard.service.ClassificationCardService;
import com.zalo.modules.admin.system.user.dto.response.UserPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classification-card")
@RequiredArgsConstructor
public class ClassificationCardController {
    private final ClassificationCardService service;

    @GetMapping
    public List<ClassificationCardResponse> getAll(@CurrentUser UserPayload user) {
        return service.getAll(user.getId());
    }

    @PostMapping
    public ClassificationCardResponse create(@CurrentUser UserPayload user, @RequestBody ClassificationCardRequest card) {
        return new ClassificationCardResponse(service.create(card, user.getId()));
    }

    @PutMapping("/{id}")
    public ClassificationCardResponse update(@PathVariable Long id, @RequestBody ClassificationCardRequest card, @CurrentUser UserPayload user) {
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
            @CurrentUser UserPayload user) {

        service.assignCardToConversation(id, dto, user.getId());
    }
}
