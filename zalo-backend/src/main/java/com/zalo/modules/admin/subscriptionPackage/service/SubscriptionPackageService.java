package com.zalo.modules.admin.subscriptionPackage.service;

import com.zalo.common.filter.SubscriptionPackageFilter;
import com.zalo.modules.admin.subscriptionPackage.dto.request.SubscriptionPackageRequest;
import com.zalo.modules.admin.subscriptionPackage.dto.response.SubscriptionPackageResponse;
import com.zalo.modules.admin.subscriptionPackage.entity.BillingCycle;
import com.zalo.modules.admin.subscriptionPackage.entity.SubscriptionPackage;
import com.zalo.modules.admin.subscriptionPackage.repo.SubscriptionPackageRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionPackageService {
    SubscriptionPackageRepo subscriptionPackageRepo;

    public Page<SubscriptionPackage> findAll(SubscriptionPackageFilter filter) {
        Specification<SubscriptionPackage> spec = filter.toSpecification();
        Pageable pageable = filter.toPageable();
        return subscriptionPackageRepo.findAll(spec, pageable);
    }

    public SubscriptionPackage getById(Long id) {
        return subscriptionPackageRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));
    }

    public SubscriptionPackage create(SubscriptionPackageRequest req, Long userId) {

//        if (subscriptionPackageRepo.findByName(req.getName()).isPresent()) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "nameExisted");
//        }

        SubscriptionPackage e = new SubscriptionPackage();
        BeanUtils.copyProperties(req, e);
        e.setCu(userId);

        calculateMonthlyPrice(e, req);

        return subscriptionPackageRepo.save(e);
    }

    public SubscriptionPackage update(Long id, SubscriptionPackageRequest req, Long userId) {

        SubscriptionPackage e = subscriptionPackageRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));

        BeanUtils.copyProperties(req, e);
        e.setEu(userId);

        calculateMonthlyPrice(e, req);

        return subscriptionPackageRepo.save(e);
    }

    private void calculateMonthlyPrice(SubscriptionPackage e, SubscriptionPackageRequest req) {
        if (e.getPrice() != null && e.getBillingCycle() != null) {
            if ("YEARLY".equals(e.getBillingCycle().name())) {
                double rawMonthly = (double) e.getPrice() / 12;

                // 💡 Làm tròn đến hàng nghìn gần nhất (ví dụ: 165.833 -> 166.000)
                long roundedMonthly = Math.round(rawMonthly / 1000.0) * 1000;

                e.setMonthlyPrice(roundedMonthly);
            } else {
                e.setMonthlyPrice(e.getPrice());
            }
        } else {
            e.setMonthlyPrice(e.getPrice());
        }
    }

    public void delete(Long id) {
        SubscriptionPackage e = subscriptionPackageRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));

        e.setStt(-1);
        subscriptionPackageRepo.save(e);
    }

    public Map<BillingCycle, List<SubscriptionPackageResponse>> getAvailablePackages(SubscriptionPackageFilter filter) {
        // 1. Lấy danh sách ban đầu từ database
        List<SubscriptionPackage> packages = subscriptionPackageRepo.findAllByCategoryAndTargetTypeAndStt(
                filter.getCategory(),
                filter.getTargetType(),
                1
        );

        // 2. Xử lý stream, sắp xếp thứ tự ưu tiên và gom nhóm với LinkedHashMap
        return packages.stream()
                .map(SubscriptionPackageResponse::new)
                // Sắp xếp các gói cước (ví dụ: ưu tiên đưa chu kỳ YEARLY / Năm đầu tiên lên trước, sau đó đến MONTHLY)
                .sorted(Comparator.comparing(resp -> {
                    // Định nghĩa thứ tự ưu tiên cho BillingCycle
                    // 0 hoặc số nhỏ hơn sẽ đứng trước trong Map kết quả
                    if (resp.getBillingCycle() == BillingCycle.YEARLY) {
                        return 1; // Hoặc 1 tùy bạn muốn năm đứng trước hay sau
                    }
                    return 2;
                }))
                .collect(Collectors.groupingBy(
                        SubscriptionPackageResponse::getBillingCycle,
                        LinkedHashMap::new, // 👈 Giúp cố định và giữ nguyên thứ tự các key trong Map trả về
                        Collectors.toList()
                ));
    }
}
