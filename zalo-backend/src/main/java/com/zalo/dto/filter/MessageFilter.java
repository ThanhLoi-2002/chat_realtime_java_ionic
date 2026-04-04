package com.zalo.dto.filter;

import com.zalo.dto.common.BaseFilter;
import com.zalo.model.Conversation;
import com.zalo.model.Message;
import com.zalo.model.enums.MessageType;
import lombok.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageFilter extends BaseFilter {
    private Long conversationId;
    private Long lastId;
    private MessageType contentType;


    @Override
    public Specification<Message> toSpecification() {
        List<Specification<Message>> specs = new ArrayList<>();

        // filter riêng
        if (conversationId != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("conversationId"), conversationId));
        }

        if (lastId != null) {
            specs.add((root, query, cb) ->
                    cb.lessThan(root.get("id"), lastId));
        }

        if (contentType != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("contentType"), contentType));
        }

        return specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction());
    }
}

