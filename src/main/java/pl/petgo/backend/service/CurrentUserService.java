package pl.petgo.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        String subject = auth.getName();

        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException ignored) {
        }

        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + subject));

        return user.getUserId();
    }
}

