package com.zalo.dto.filter;

import com.zalo.dto.common.BaseFilter;
import com.zalo.model.Conversation;
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

    @Override
    public Specification<Conversation> toSpecification() {
        List<Specification<Conversation>> specs = new ArrayList<>();

        // filter riêng
        if (conversationId != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("conversation_id"), conversationId));
        }

        Specification<Conversation> result = specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction());

        // ✅ thêm sort tại đây
        return (root, query, cb) -> {
            query.orderBy(cb.desc(root.get("ct")));
            return result.toPredicate(root, query, cb);
        };
    }
}

