package mini_food.payment_notif_service.services;

import mini_food.payment_notif_service.dto.request.PaymentRequest;

public interface PaymentService {
    boolean processAndNotify(PaymentRequest request);
}
