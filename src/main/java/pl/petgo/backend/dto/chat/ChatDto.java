package pl.petgo.backend.dto.chat;

import pl.petgo.backend.domain.Chat;

public record ChatDto(Long chatId, Long reservationId) {
    public static ChatDto from(Chat chat) {
        return new ChatDto(chat.getChatId(), chat.getReservation().getReservationId());
    }
}
