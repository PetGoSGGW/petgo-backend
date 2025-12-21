package pl.petgo.backend.utils;

import lombok.experimental.UtilityClass;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@UtilityClass
public class SecurityUtils {

    public String getUserEmail() {
        return getContext().getAuthentication().getName();
    }

    public boolean isAdmin() {
        return getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }
}
