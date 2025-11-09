package pl.petgo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.petgo.backend.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndUserIdNot(String username, Long userId);

    boolean existsByEmailAndUserIdNot(String email, Long userId);
    List<User> findAll();

    Optional<User> findById(Long id);

}