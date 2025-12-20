package pl.petgo.backend.controller;

import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.payment.PaymentRequest;
import pl.petgo.backend.dto.payment.PaymentResponse;
import pl.petgo.backend.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments Module", description = "API for managing payment processes and Stripe integration")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Initialize a new payment",
            description = "Creates a PaymentIntent in Stripe based on the provided Reservation ID. " +
                    "Returns a 'clientSecret' which is required by the frontend to render the Stripe payment form."
    )
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