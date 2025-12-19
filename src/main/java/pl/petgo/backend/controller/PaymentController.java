package pl.petgo.backend.controller;

import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.payment.PaymentRequest;
import pl.petgo.backend.dto.payment.PaymentResponse;
import pl.petgo.backend.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/init")
    public ResponseEntity<PaymentResponse> initPayment(@RequestBody PaymentRequest request) {
        try {
            PaymentResponse response = paymentService.initPayment(request);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}