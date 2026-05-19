package com.zalo.modules.groupSetting.entities;

import lombok.Data;

@Data
public class GroupSetting {
    // --- QUYỀN THÀNH VIÊN (Trong ảnh mẫu) ---
    boolean allowChangeGroupInfo = true;
    boolean allowPinMessage = true;
    boolean allowCreateNote = true;
    boolean allowCreatePoll = true;
    boolean allowSendMessage = true;

    // --- CÁC CẤU HÌNH TOGGLE (Trong ảnh mẫu) ---
    boolean hideMemberList = false;
    boolean membershipApproval = false;
    boolean highlightAdminMessages = false;
    boolean newMembersReadRecentMessages = true;
}
