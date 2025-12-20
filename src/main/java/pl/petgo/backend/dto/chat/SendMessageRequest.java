package pl.petgo.backend.dto.chat;

public record SendMessageRequest(Long senderId, String content) {}