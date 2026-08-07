package com.zalo.common.filter;

import com.zalo.common.base.BaseFilter;
import com.zalo.modules.oa.officialAccount.entity.OfficialAccount;
import lombok.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class OaFilter extends BaseFilter {

    @Override
    public Specification<OfficialAccount> toSpecification() {
        List<Specification<OfficialAccount>> specs = new ArrayList<>();

        // filter riêng
        if (this.getSearch() != null) {
            String keyword = "%" + this.getSearch().trim().toLowerCase() + "%";
            specs.add((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("name")), keyword),
                            cb.like(cb.lower(root.get("code")), keyword)
                    )
            );
        }

        return specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction());
    }
}
