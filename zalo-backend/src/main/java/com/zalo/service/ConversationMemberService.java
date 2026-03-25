package com.zalo.service;

import com.zalo.repository.ConversationMemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class ConversationMemberService {
    private final ConversationMemberRepository memberRepo;

}
