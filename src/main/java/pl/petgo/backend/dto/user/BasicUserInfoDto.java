package pl.petgo.backend.dto.user;

import pl.petgo.backend.domain.User;

public record BasicUserInfoDto(Long userId,
                               String firstName,
                               String lastName) {

    public static BasicUserInfoDto from(User user) {
        return new BasicUserInfoDto(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}
