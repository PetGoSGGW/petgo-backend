package pl.petgo.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.petgo.backend.domain.Chat;
import pl.petgo.backend.domain.ChatMessage;
import pl.petgo.backend.domain.Reservation;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.exception.NotFoundException;
import pl.petgo.backend.repository.ChatMessageRepository;
import pl.petgo.backend.repository.ChatRepository;
import pl.petgo.backend.repository.ReservationRepository;
import pl.petgo.backend.repository.UserRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMessageRepository messageRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public Chat getOrCreateChat(Long reservationId) {
        return chatRepository.findByReservation_ReservationId(reservationId)
                .orElseGet(() -> createChat(reservationId));
    }

    private Chat createChat(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        Chat chat = Chat.builder()
                .reservation(reservation)
                .createdAt(Instant.now())
                .build();

        return chatRepository.save(chat);
    }

    public ChatMessage sendMessage(Long chatId, Long senderId, String content) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("Chat not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ChatMessage message = ChatMessage.builder()
                .chat(chat)
                .sender(sender)
                .content(content)
                .sentAt(Instant.now())
                .build();

        return messageRepository.save(message);
    }

    public List<ChatMessage> getMessages(Long chatId) {
        return messageRepository.findByChat_ChatIdOrderBySentAtAsc(chatId);
    }
}
