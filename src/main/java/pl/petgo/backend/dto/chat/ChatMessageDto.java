package pl.petgo.backend.dto.chat;

import pl.petgo.backend.domain.ChatMessage;

import java.time.Instant;

public record ChatMessageDto(Long messageId, Long senderId, String content, Instant sentAt) {
    public static ChatMessageDto from(ChatMessage chatMessage) {
        return new ChatMessageDto(
                chatMessage.getMessageId(),
                chatMessage.getSender().getUserId(),
                chatMessage.getContent(),
                chatMessage.getSentAt()
        );
    }
}
