package mini_food.payment_notif_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PaymentRequest {
    Long orderId;
    String paymentMethod;
    String username;
}
