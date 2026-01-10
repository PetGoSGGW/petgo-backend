package pl.petgo.backend.dto.chat;

import pl.petgo.backend.domain.Chat;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.dto.user.BasicUserInfoDto;

public record ChatDto(Long chatId, Long reservationId, BasicUserInfoDto owner, BasicUserInfoDto walker) {
    public static ChatDto from(Chat chat, User owner, User walker) {
        return new ChatDto(
                chat.getChatId(),
                chat.getReservation().getReservationId(),
                BasicUserInfoDto.from(owner),
                BasicUserInfoDto.from(walker)
        );
    }
}
