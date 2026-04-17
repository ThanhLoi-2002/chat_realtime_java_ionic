import { useMessageStore } from "@/stores/message.storage";

export function useMessage() {
    const messStorage = useMessageStore()

    //format text with tag
    const formattedContentWithTag = (content: string | undefined, isOwner: boolean) => {
        if (!content) return ''
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

    const urlRegex = /(https?:\/\/[^\s]+)/g;

    const onPreviewLink = async (text: string) => {
        const match = text?.match(urlRegex);
        if (match && match.length > 0) {
            // Gọi API Backend để lấy preview cho match[0]
            return await messStorage.previewLink(match[0]);
        }
        return null
    };

    const formatHostname = (url: string) => {
        try {
            const host = new URL(url).hostname;
            return host.replace('www.', ''); // Bỏ www cho gọn giống Zalo
        } catch (e) {
            return url;
        }
    }

    const jumpToMessage = async (messageId: number) => {
        // 1. Tìm trong danh sách hiện tại
        const element = document.getElementById(`msg-${messageId}`);

        if (element) {
            // Nếu tìm thấy ngay
            element.scrollIntoView({ behavior: 'smooth', block: 'center' });
            highlightMessage(element);
        } else {
            // 2. Nếu chưa load, gọi API lấy data xung quanh ID đó
            // isLoading.value = true;
            // try {
            //     // Giả sử hàm này gọi API nạp lại messageStore
            //     await messageStorage.fetchMessagesAround(messageId);

            //     // Đợi Vue render lại DOM
            //     await nextTick();

            //     const newElement = document.getElementById(`msg-${messageId}`);
            //     if (newElement) {
            //         newElement.scrollIntoView({ behavior: 'auto', block: 'center' });
            //         highlightMessage(newElement);
            //     }
            // } finally {
            //     isLoading.value = false;
            // }
        }
    };

    const highlightMessage = (el: HTMLElement) => {
        el.classList.add('animate-highlight');
        setTimeout(() => el.classList.remove('animate-highlight'), 2000);
    };

    return {
        formattedContentWithTag, stripMentionTag, onPreviewLink, formatHostname, jumpToMessage, highlightMessage
    }
}