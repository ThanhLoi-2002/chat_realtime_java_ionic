package com.zalo.dto.filter;

import com.zalo.dto.common.BaseFilter;
import com.zalo.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserFilter extends BaseFilter {
    private String phone;
    private String username;

    @Override
    public Specification<User> toSpecification() {
        List<Specification<User>> specs = new ArrayList<>();

        // search chung (tìm trên phone, username)
//        if (StringUtils.hasText(getSearch())) {
//            String searchPattern = "%" + getSearch().trim().toLowerCase() + "%";
//            specs.add((root, query, cb) -> cb.or(
//                    cb.like(cb.lower(root.get("phone")), searchPattern),
//                    cb.like(cb.lower(root.get("username")), searchPattern)
//            ));
//        }

        // filter riêng
        if (StringUtils.hasText(phone)) {
            specs.add((root, query, cb) -> cb.equal(root.get("phone"), phone.trim()));
        }
        if (StringUtils.hasText(username)) {
            specs.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("username")), "%" + username.trim().toLowerCase() + "%"));
        }

        // Kết hợp tất cả bằng AND
        return specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction()); // nếu không có filter → true
    }
}
