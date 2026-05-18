import { useMessageStore } from "@/stores/message.storage";
import { useConversationStore } from "@/stores/conversation.storage";
import { nextTick } from "vue";

export function useMessage() {
    const messStorage = useMessageStore()
    const conversationStorage = useConversationStore()

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

    const scrollToMiddle = (el: HTMLElement) => {
        // 1. Tìm container chứa scroll: Thử tìm theo class, nếu không thấy thì tìm element cha có scroll gần nhất
        const container = el.closest<HTMLElement>('.overflow-y-auto') ||
            el.parentElement?.closest<HTMLElement>('[class*="scroll"]');

        if (container) {
            // Tọa độ của container và phần tử target đối với viewport
            const containerRect = container.getBoundingClientRect();
            const targetRect = el.getBoundingClientRect();

            // CÔNG THỨC CHUẨN: 
            // Vị trí hiện tại của scroll + Khoảng cách từ đỉnh container đến đỉnh target - (Một nửa chiều cao container) + (Một nửa chiều cao target)
            const offset = container.scrollTop + (targetRect.top - containerRect.top) - (containerRect.height / 2) + (el.offsetHeight / 2);

            container.scrollTo({
                top: offset,
                behavior: 'smooth'
            });
        } else {
            // Dự phòng nếu không tìm thấy container cụ thể nào
            el.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }

        highlightMessage(el);
    };

    const jumpToMessage = async (messageId: number) => {
        // 1. Tìm trong danh sách hiện tại (DOM)
        const element = document.getElementById(`msg-${messageId}`);

        if (element) {
            scrollToMiddle(element);
            return; // Thoát sớm để code gọn gàng
        }

        // 2. Kiểm tra trong storage xem message đã được lưu chưa
        const foundInStore = messStorage.messages.some(m => m.id === messageId);

        if (foundInStore) {
            await nextTick();
            // Thêm một chút timeout để đảm bảo Vue/React đã update hoàn toàn CSS/Layout
            setTimeout(() => {
                const newElement = document.getElementById(`msg-${messageId}`);
                if (newElement) scrollToMiddle(newElement);
            }, 50);
        } else {
            // 3. Chưa có trong store -> gọi API getMessagesAround
            const convId = conversationStorage.conversation?.id;
            if (!convId) return;

            await messStorage.getMessagesAround(messageId, convId);

            // 4. Đợi DOM render xong hoàn toàn
            await nextTick();

            // Sử dụng setTimeout (khoảng 60-100ms) cực kỳ quan trọng ở đây 
            // vì API mới về thường kéo theo ảnh, link preview... làm thay đổi chiều cao DOM liên tục
            setTimeout(() => {
                const newElement = document.getElementById(`msg-${messageId}`);
                if (newElement) {
                    scrollToMiddle(newElement);
                }
            }, 100);
        }
    };

    const highlightMessage = (el: HTMLElement) => {
        el.classList.add('animate-highlight');
        setTimeout(() => el.classList.remove('animate-highlight'), 2500);
    };

    return {
        formattedContentWithTag, stripMentionTag, onPreviewLink, formatHostname, jumpToMessage, highlightMessage
    }
}