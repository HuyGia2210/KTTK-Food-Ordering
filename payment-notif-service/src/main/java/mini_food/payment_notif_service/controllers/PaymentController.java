package mini_food.payment_notif_service.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import mini_food.payment_notif_service.dto.request.PaymentRequest;
import mini_food.payment_notif_service.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
@RequestMapping({"/payments", "/api/v1/payments", "/api/payments"})
public class PaymentController {
    PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest request) {
        try {
            boolean isSuccess = paymentService.processAndNotify(request);
            if (isSuccess) {
                return ResponseEntity.ok("Payment processed successfully for order: " + request.getOrderId());
            } else {
                return ResponseEntity.badRequest().body("Payment failed to process.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("System Error: " + e.getMessage());
        }
    }
}
