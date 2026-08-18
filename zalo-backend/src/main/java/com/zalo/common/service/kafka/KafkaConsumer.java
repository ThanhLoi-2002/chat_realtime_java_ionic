package com.zalo.common.service.kafka;

import com.zalo.modules.app.message.dto.request.CreateMessageRequest;
import com.zalo.modules.app.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    // Lắng nghe topic 'chat-messages-topic' với group-id xác định
    @KafkaListener(topics = "chat-messages-topic", groupId = "chat-service-group", batch = "false")
    public void consumeMessage(@Payload String payload, Acknowledgment acknowledgment) {
        try {
            // Parse chuỗi JSON thành Object thủ công
            CreateMessageRequest event = objectMapper.readValue(payload, CreateMessageRequest.class);

            log.info("Nhận tin nhắn mới từ Kafka - Người gửi ID: {}, Nội dung: {}",
                    event.getSenderId(), event.getContent());

            // ==========================================
            // BƯỚC XỬ LÝ NGHIỆP VỤ (BUSINESS LOGIC) TẠI ĐÂY
            messageService.sendMessage(event.conversationId, event.getSenderId(), event);

            // ==========================================

            // Xác nhận (Commit offset) là đã xử lý thành công tin nhắn này
            acknowledgment.acknowledge();
            log.info("Đã xử lý và commit offset thành công cho tin nhắn từ user ID: {}", event.getSenderId());

        } catch (Exception e) {
            log.error("Lỗi khi xử lý tin nhắn từ Kafka: {}. Lỗi: {}", payload, e.getMessage(), e);
            // Trong thực tế, nếu lỗi, bạn có thể quyết định không gọi ack()
            // hoặc đẩy message này sang Dead Letter Queue (DLQ) để tránh kẹt hàng đợi.
        }
    }
}
