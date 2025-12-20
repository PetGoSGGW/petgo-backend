package pl.petgo.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.chat.ChatDto;
import pl.petgo.backend.dto.chat.ChatMessageDto;
import pl.petgo.backend.dto.chat.SendMessageRequest;
import pl.petgo.backend.service.ChatService;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/reservation/{reservationId}")
    public ChatDto getOrCreateChat(@PathVariable Long reservationId) {
        return ChatDto.from(chatService.getOrCreateChat(reservationId));
    }

    @GetMapping("/{chatId}/messages")
    public List<ChatMessageDto> getMessages(@PathVariable Long chatId) {
        return chatService.getMessages(chatId).stream()
                .map(ChatMessageDto::from)
                .toList();
    }

    @PostMapping("/{chatId}/messages")
    public ChatMessageDto sendMessage(
            @PathVariable Long chatId,
            @RequestBody SendMessageRequest request
    ) {
        return ChatMessageDto.from(
                chatService.sendMessage(chatId, request.senderId(), request.content())
        );
    }
}