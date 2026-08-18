package com.zalo.common.service.kafka;

import com.zalo.modules.app.message.dto.request.CreateMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "chat-messages-topic";

    public void sendMessage(CreateMessageRequest request) {
        // Gửi vào topic, key là sender để đảm bảo thứ tự nếu cần
        kafkaTemplate.send(TOPIC, request.getSenderId().toString(), request);
    }
}
