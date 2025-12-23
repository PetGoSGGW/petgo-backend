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
@Tag(name = "Payments", description = "API for managing payment sessions and Stripe Checkout integration")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Initialize Stripe Checkout Session",
            description = "Creates a Stripe Checkout Session based on Reservation ID. Returns a URL for redirection to the hosted payment page."
    )
    @PostMapping("/init")
    public ResponseEntity<PaymentResponse> initPayment(@RequestBody PaymentRequest request) {
        try {
            PaymentResponse response = paymentService.initPayment(request);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.internalServerError().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Mock Success Page",
            description = "Endpoint to redirect the user after a successful payment (for backend testing purposes only)."
    )
    @GetMapping("/success-mock")
    public ResponseEntity<String> paymentSuccess() {
        return ResponseEntity.ok("<h1>Payment Successful! You can close this window.</h1>");
    }

    @Operation(
            summary = "Mock Cancel Page",
            description = "Endpoint to redirect the user after a cancelled payment (for backend testing purposes only)."
    )
    @GetMapping("/cancel-mock")
    public ResponseEntity<String> paymentCancel() {
        return ResponseEntity.ok("<h1>Payment Cancelled.</h1>");
    }
}