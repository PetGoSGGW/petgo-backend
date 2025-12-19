package pl.petgo.backend.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private String clientSecret; // Klucz dla frontendu do obsługi karty
    private Long paymentId;      // Wewnętrzne ID płatności
}