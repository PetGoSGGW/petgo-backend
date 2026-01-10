package pl.petgo.backend.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.petgo.backend.domain.Chat;
import pl.petgo.backend.domain.ChatMessage;
import pl.petgo.backend.domain.Reservation;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.dto.chat.ChatDto;
import pl.petgo.backend.exception.NotFoundException;
import pl.petgo.backend.repository.ChatMessageRepository;
import pl.petgo.backend.repository.ChatRepository;
import pl.petgo.backend.repository.ReservationRepository;
import pl.petgo.backend.repository.UserRepository;
import pl.petgo.backend.security.AppUserDetails;

import org.springframework.security.access.AccessDeniedException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMessageRepository messageRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public ChatDto getOrCreateChat(Long reservationId, AppUserDetails principal) throws AccessDeniedException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        User owner = reservation.getOwner();
        User walker = reservation.getWalker();
        Long currentUserId = principal.getUser().getUserId();

        if (!owner.getUserId().equals(currentUserId) && !walker.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not a participant in this reservation.");
        }

        Chat chat = chatRepository.findByReservation_ReservationId(reservationId)
                .orElseGet(() -> createChat(reservation));

        return ChatDto.from(chat, owner, walker);
    }

    private Chat createChat(Reservation reservation) {
        Chat chat = Chat.builder()
                .reservation(reservation)
                .createdAt(Instant.now())
                .build();

        return chatRepository.save(chat);
    }

    public ChatMessage sendMessage(Long chatId, String content, AppUserDetails principal) throws AccessDeniedException {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("Chat not found"));

        Long currentUserId = principal.getUser().getUserId();

        boolean isOwner = chat.getReservation().getOwner().getUserId().equals(currentUserId);
        boolean isWalker = chat.getReservation().getWalker().getUserId().equals(currentUserId);

        if (!isOwner && !isWalker) {
            throw new AccessDeniedException("You are not a participant in this chat.");
        }

        User sender = isOwner ? chat.getReservation().getOwner() : chat.getReservation().getWalker();

        ChatMessage message = ChatMessage.builder()
                .chat(chat)
                .sender(sender)
                .content(content)
                .sentAt(Instant.now())
                .build();

        return messageRepository.save(message);
    }

    public List<ChatMessage> getMessages(Long chatId, AppUserDetails principal) throws AccessDeniedException {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        Long currentUserId = principal.getUser().getUserId();

        boolean isOwner = chat.getReservation().getOwner().getUserId().equals(currentUserId);
        boolean isWalker = chat.getReservation().getWalker().getUserId().equals(currentUserId);

        if (!isOwner && !isWalker) {
            throw new AccessDeniedException("You do not have permission to view messages in this chat.");
        }

        return messageRepository.findByChat_ChatIdOrderBySentAtAsc(chatId);
    }
}
