package com.zalo.common.filter;

import com.zalo.common.base.BaseFilter;
import com.zalo.modules.message.entity.Message;
import com.zalo.modules.message.entity.MessageType;
import lombok.*;
import org.springframework.data.jpa.domain.Specification;

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
    private Integer stt;
    private Boolean linkMetadata;


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

//        if (stt != null) {
//            specs.add((root, query, cb) ->
//                    cb.equal(root.get("stt"), stt));
//        }

        if (contentType != null) {
            if (contentType == MessageType.IMAGE) {
                // Nếu là IMAGE, lấy cả IMAGE và VIDEO
                specs.add((root, query, cb) ->
                        cb.or(
                                cb.equal(root.get("contentType"), MessageType.IMAGE),
                                cb.equal(root.get("contentType"), MessageType.VIDEO)
                        )
                );
            } else {
                // Các loại khác thì tìm chính xác
                specs.add((root, query, cb) ->
                        cb.equal(root.get("contentType"), contentType)
                );
            }

            // Luôn lọc theo trạng thái stt = 1 cho các loại media này
            specs.add((root, query, cb) ->
                    cb.equal(root.get("stt"), 1)
            );
        }

        if(linkMetadata != null && linkMetadata) {
            specs.add((root, query, cb) ->
                    cb.isNotNull(root.get("linkMetadata")));
        }

        return specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction());
    }
}

