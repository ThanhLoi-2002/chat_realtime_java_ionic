package com.zalo.modules.app.groupSetting.service;

import com.zalo.common.service.WebsocketService;
import com.zalo.modules.app.conversation.entities.Conversation;
import com.zalo.modules.app.conversation.service.ConversationRepository;
import com.zalo.modules.app.groupSetting.entities.GroupSetting;
import com.zalo.modules.app.message.dto.request.CreateSystemMessageRequest;
import com.zalo.modules.app.message.entity.SystemMessageType;
import com.zalo.modules.app.message.service.SystemMessageInterface;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupSettingService {
    ConversationRepository convRepo;
    SystemMessageInterface systemMessageInterface;
    WebsocketService websocketService;

    public void saveSetting(Long convId, Long userId, GroupSetting dto) {
        Conversation conv = convRepo.findById(convId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "notFound"));

        GroupSetting oldSettings = conv.getSettings();

        if (oldSettings == null) {
            oldSettings = new GroupSetting();
        }

        CreateSystemMessageRequest sysMes = new CreateSystemMessageRequest();
        sysMes.conversationId = convId;
        sysMes.senderId = userId;
        sysMes.systemMessageType = SystemMessageType.UPDATE_SETTING;

        // Giả định sysMes là object Tin nhắn hệ thống của bạn
        if (oldSettings.isAllowChangeGroupInfo() != dto.isAllowChangeGroupInfo()) {
            sysMes.setContent(dto.isAllowChangeGroupInfo() ? "allowChangeGroupInfo" : "notAllowChangeGroupInfo");
        }
        if (oldSettings.isAllowPinMessage() != dto.isAllowPinMessage()) {
            sysMes.setContent(dto.isAllowPinMessage() ? "allowPinMessages" : "notAllowPinMessages");
        }
        if (oldSettings.isAllowCreateNote() != dto.isAllowCreateNote()) {
            sysMes.setContent(dto.isAllowCreateNote() ? "allowCreateNote" : "notAllowCreateNote");
        }
        if (oldSettings.isAllowCreatePoll() != dto.isAllowCreatePoll()) {
            sysMes.setContent(dto.isAllowCreatePoll() ? "allowCreatePoll" : "notAllowCreatePoll");
        }
        if (oldSettings.isAllowSendMessage() != dto.isAllowSendMessage()) {
            sysMes.setContent(dto.isAllowSendMessage() ? "allowSendMessages" : "notAllowSendMessages");
        }

// --- CÁC CẤU HÌNH TOGGLE CỦA ADMIN ---
        if (oldSettings.isHideMemberList() != dto.isHideMemberList()) {
            sysMes.setContent(dto.isHideMemberList() ? "hideMemberList" : "notHideMemberList");
        }
        if (oldSettings.isMembershipApproval() != dto.isMembershipApproval()) {
            sysMes.setContent(dto.isMembershipApproval() ? "membershipApproval" : "notMembershipApproval");
        }
        if (oldSettings.isHighlightAdminMessages() != dto.isHighlightAdminMessages()) {
            sysMes.setContent(dto.isHighlightAdminMessages() ? "highlightAdminMessages" : "notHighlightAdminMessages");
        }
        if (oldSettings.isNewMembersReadRecentMessages() != dto.isNewMembersReadRecentMessages()) {
            sysMes.setContent(dto.isNewMembersReadRecentMessages() ? "newMembersReadRecentMessages" : "notNewMembersReadRecentMessages");
        }

        conv.setSettings(dto);
        convRepo.save(conv);
        systemMessageInterface.createSystemMessage(sysMes);
        websocketService.saveSetting(convId, dto);
    }
}
