export function useMessage() {

    //format text with tag
    const formattedContentWithTag = (content: string, isOwner: boolean) => {
        // 1. Bảo mật: Escape HTML để tránh XSS
        const div = document.createElement('div');
        div.textContent = content;
        content = div.innerHTML;

        // 2. Regex tìm cấu trúc định dạng bạn đã lưu
        // $1: ID người dùng, $2: Tên hiển thị
        const mentionRegex = /\[mention:(\d+)\](.*?)\[\/mention\]/g;

        // 3. Quyết định màu sắc dựa trên chủ sở hữu tin nhắn
        // Nếu bạn gửi (nền xanh đậm), tag nên màu nhạt hơn (blue-200) để dễ nhìn
        const tagClass = isOwner ? 'text-blue-200' : 'text-blue-500';

        return content.replace(mentionRegex, (match: any, userId: number, username: string) => {
            return `<span class="${tagClass} mention-link cursor-pointer font-bold" data-user-id="${userId}">${username}</span>`;
        });
    };

    const stripMentionTag = (content: string | null) => {
        if (!content) return '';

        // Regex tìm cấu trúc [mention:id]username[/mention]
        // (\d+) là nhóm 1 (ID), (.*?) là nhóm 2 (Username)
        const mentionRegex = /\[mention:\d+\](.*?)\[\/mention\]/g;

        // Thay thế cả cụm bằng nhóm thứ 2 (chỉ lấy username)
        return content.replace(mentionRegex, '$1');
    };

    return {
        formattedContentWithTag, stripMentionTag
    }
}