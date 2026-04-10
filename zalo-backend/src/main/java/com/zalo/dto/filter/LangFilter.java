package com.zalo.dto.filter;

import com.zalo.dto.common.BaseFilter;
import com.zalo.modules.lang.entity.Lang;
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
public class LangFilter extends BaseFilter {
    private String code;

    @Override
    public Specification<Lang> toSpecification() {
        List<Specification<Lang>> specs = new ArrayList<>();

        // filter riêng
        if (StringUtils.hasText(code)) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("code")), "%" + code.trim().toLowerCase() + "%"));
        }

        Specification<Lang> result = specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction());

        // ✅ thêm sort tại đây
        return (root, query, cb) -> {
            query.orderBy(cb.desc(root.get("ct")));
            return result.toPredicate(root, query, cb);
        };
    }
}
