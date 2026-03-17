package com.zalo.service;

import com.zalo.model.Message;
import com.zalo.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    public Message save(Message msg) {
        return messageRepository.save(msg);
    }

    public List<Message> getHistory(int roomId, int size) {
        // return last 'size' messages ordered ascending
        return messageRepository.findByRoomId(roomId);
    }
}
