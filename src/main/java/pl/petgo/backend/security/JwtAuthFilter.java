package pl.petgo.backend.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.io.IOException;

@Component
public class JwtAuthFilter extends GenericFilter {
    private final JwtService jwt;
    private final AppUserDetailsService uds;

    public JwtAuthFilter(JwtService jwt, AppUserDetailsService uds) {
        this.jwt = jwt; this.uds = uds;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var http = (HttpServletRequest) request;
        String header = http.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String email = jwt.getSubject(token);
                var details = (AppUserDetails) uds.loadUserByUsername(email);
                var auth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(http));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ignored) {}
        }
        chain.doFilter(request, response);
    }
}