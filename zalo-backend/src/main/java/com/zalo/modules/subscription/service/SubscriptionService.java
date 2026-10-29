package com.zalo.modules.subscription.service;

import com.zalo.modules.admin.subscriptionPackage.entity.SubscriptionCategory;
import com.zalo.modules.admin.subscriptionPackage.entity.SubscriptionPackage;
import com.zalo.modules.admin.subscriptionPackage.entity.TargetType;
import com.zalo.modules.admin.subscriptionPackage.repo.SubscriptionPackageRepo;
import com.zalo.modules.subscription.repo.SubscriptionRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionService {
    SubscriptionRepo subscriptionRepo;
    SubscriptionPackageRepo subscriptionPackageRepo;

    // --- 2. MANUAL ASSIGN: Gán/Kích hoạt gói cho User/Group (Không qua cổng thanh toán) ---
//    @Transactional
//    public SubscriptionDetailResponseDto assignSubscription(AssignSubscriptionRequestDto dto) {
//        SubscriptionPlanEntity plan = subscriptionPackageRepo.findById(dto.getPlanId())
//                .orElseThrow(() -> new RuntimeException("Plan không tồn tại"));
//
//        // Hủy hoặc đổi trạng thái gói cũ cùng loại nếu có
//        subscriptionRepository.findByTargetTypeAndTargetIdAndProductCategoryAndStatus(
//                        dto.getTargetType(), dto.getTargetId(), plan.getProductCategory(), SubscriptionStatus.ACTIVE)
//                .ifPresent(oldSub -> {
//                    oldSub.setStatus(SubscriptionStatus.CANCELLED);
//                    subscriptionRepository.save(oldSub);
//                });
//
//        // Tạo gói đăng ký mới
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime endDate = now.plusMonths(plan.getBillingCycle().getMonths());
//
//        SubscriptionEntity sub = SubscriptionEntity.builder()
//                .targetType(dto.getTargetType())
//                .targetId(dto.getTargetId())
//                .productCategory(plan.getProductCategory())
//                .planId(plan.getId())
//                .billingCycle(plan.getBillingCycle())
//                .status(SubscriptionStatus.ACTIVE)
//                .startDate(now)
//                .endDate(endDate)
//                .autoRenew(false)
//                .build();
//
//        subscriptionRepository.save(sub);
//
//        return buildDetailResponse(sub, plan);
//    }

    //     --- 3. QUERY / LOAD DATA: Lấy thông tin quyền sử dụng hiện tại ---
//    public SubscriptionDetailResponseDto getActiveSubscription(TargetType targetType, Long targetId, ProductCategory category) {
//        SubscriptionEntity sub = subscriptionRepository.findByTargetTypeAndTargetIdAndProductCategoryAndStatus(
//                        targetType, targetId, category, SubscriptionStatus.ACTIVE)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói active cho dịch vụ " + category));
//
//        SubscriptionPlanEntity plan = planRepository.findById(sub.getPlanId())
//                .orElseThrow(() -> new RuntimeException("Dữ liệu plan bị lỗi"));
//
//        return buildDetailResponse(sub, plan);
//    }

    //     Helper map dữ liệu không dùng JPA Associations
//    private SubscriptionDetailResponseDto buildDetailResponse(SubscriptionEntity sub, SubscriptionPlanEntity plan) {
//        return SubscriptionDetailResponseDto.builder()
//                .subscriptionId(sub.getId())
//                .targetType(sub.getTargetType())
//                .targetId(sub.getTargetId())
//                .productCategory(sub.getProductCategory())
//                .status(sub.getStatus())
//                .startDate(sub.getStartDate())
//                .endDate(sub.getEndDate())
//                .planId(plan.getId())
//                .planCode(plan.getCode())
//                .planName(plan.getName())
//                .price(plan.getPrice())
//                .featuresConfig(plan.getFeaturesConfig())
//                .build();
//    }
}
