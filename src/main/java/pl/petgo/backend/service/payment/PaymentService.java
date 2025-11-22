package pl.petgo.backend.service.payment;

import pl.petgo.backend.domain.Payment;
import pl.petgo.backend.dto.payment.*;

import java.util.List;

public interface PaymentService {
    PaymentResponseDto createPayment(PaymentRequestDto request);
    PaymentResponseDto getPayment(Long paymentId);
    void handleProviderCallback(String providerRef, String status); // np. Stripe webhook
    List<PaymentResponseDto> getPaymentsForUser(Long userId, boolean asPayer);
}