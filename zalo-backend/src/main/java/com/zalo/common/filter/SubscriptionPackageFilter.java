package com.zalo.common.filter;

import com.zalo.common.base.BaseFilter;
import com.zalo.modules.admin.subscriptionPackage.entity.SubscriptionCategory;
import com.zalo.modules.admin.subscriptionPackage.entity.SubscriptionPackage;
import com.zalo.modules.admin.subscriptionPackage.entity.TargetType;
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
public class SubscriptionPackageFilter extends BaseFilter {
    String name;
    SubscriptionCategory category; //'AI', 'BUSINESS', 'COMMUNITY'
    TargetType targetType;

    @Override
    public Specification<SubscriptionPackage> toSpecification() {
        List<Specification<SubscriptionPackage>> specs = new ArrayList<>();

        // filter riêng
        if (StringUtils.hasText(name)) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%"));
        }

        if (stt != null) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("stt"), stt));
        }

        // Kết hợp tất cả bằng AND
        return specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction()); // nếu không có filter → true
    }
}
