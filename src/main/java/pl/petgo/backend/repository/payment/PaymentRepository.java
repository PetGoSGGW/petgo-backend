package pl.petgo.backend.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.petgo.backend.domain.Payment;
import pl.petgo.backend.domain.PaymentStatus;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByPayerId(Long payerId);
    List<Payment> findAllByPayeeId(Long payeeId);
    List<Payment> findAllByStatus(PaymentStatus status);
}