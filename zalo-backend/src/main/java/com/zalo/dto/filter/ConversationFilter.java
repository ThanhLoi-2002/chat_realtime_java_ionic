package com.zalo.dto.filter;

import com.zalo.dto.common.BaseFilter;
import com.zalo.modules.conversation.entities.Conversation;
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
public class ConversationFilter extends BaseFilter {
    private Long id;
    private List<Long> ids;
    private String name;

    @Override
    public Specification<Conversation> toSpecification() {
        List<Specification<Conversation>> specs = new ArrayList<>();

        // search chung (tìm trên phone, username)
//        if (StringUtils.hasText(getSearch())) {
//            String searchPattern = "%" + getSearch().trim().toLowerCase() + "%";
//            specs.add((root, query, cb) -> cb.or(
//                    cb.like(cb.lower(root.get("phone")), searchPattern),
//                    cb.like(cb.lower(root.get("username")), searchPattern)
//            ));
//        }

        // filter riêng
        if (StringUtils.hasText(name)) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%"));
        }

        if (id != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("id"), id));
        }

        // ✅ filter theo list ids
        if (ids != null && !ids.isEmpty()) {
            specs.add((root, query, cb) ->
                    root.get("id").in(ids));
        }

        // ✅ mặc định: lastMessageId != null
        specs.add((root, query, cb) ->
                cb.isNotNull(root.get("lastMessageId")));

        Specification<Conversation> result = specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction());

        // ✅ thêm sort tại đây
        return (root, query, cb) -> {
            query.orderBy(cb.desc(root.get("et")));
            return result.toPredicate(root, query, cb);
        };
    }
}
