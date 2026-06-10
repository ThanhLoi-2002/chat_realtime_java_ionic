import { MessageType } from "@/types/entities";

export const translateMessageKey = (mess: MessageType, targetLang: string) => {
  return `translateMessage_${mess.conversationId}_${mess.id}_${targetLang}`
}