package pl.petgo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.chat.ChatDto;
import pl.petgo.backend.dto.chat.ChatMessageDto;
import pl.petgo.backend.dto.chat.SendMessageRequest;
import pl.petgo.backend.security.AppUserDetails;
import pl.petgo.backend.service.ChatService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@Tag(name = "Chat Module", description = "Endpoints for handling chats and messages between users regarding reservations.")
public class ChatController {

    private final ChatService chatService;

    @Operation(
            summary = "Get or create a chat for a reservation",
            description = "Retrieves an existing chat associated with a specific reservation ID. If no chat exists, a new one is created."
    )
    @GetMapping("/reservation/{reservationId}")
    public ChatDto getOrCreateChat(@PathVariable Long reservationId, @AuthenticationPrincipal AppUserDetails principal) {
        return chatService.getOrCreateChat(reservationId, principal);
    }

    @Operation(
            summary = "Get all messages for a specific chat",
            description = "Fetches a historical list of all messages sent within a specific chat session."
    )
    @GetMapping("/{chatId}/messages")
    public List<ChatMessageDto> getMessages(@PathVariable Long chatId, @AuthenticationPrincipal AppUserDetails principal) {
        return chatService.getMessages(chatId, principal).stream()
                .map(ChatMessageDto::from)
                .toList();
    }

    @Operation(
            summary = "Send a new message",
            description = "Sends a message to a specific chat. Requires the sender's ID and the text content."
    )
    @PostMapping("/{chatId}/messages")
    public ChatMessageDto sendMessage(
            @PathVariable Long chatId,
            @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal AppUserDetails principal
    ) {
        return ChatMessageDto.from(
                chatService.sendMessage(chatId, request.content(), principal)
        );
    }
}