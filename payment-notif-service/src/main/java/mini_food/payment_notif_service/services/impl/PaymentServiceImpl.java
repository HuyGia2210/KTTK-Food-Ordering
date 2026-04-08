package mini_food.payment_notif_service.services.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import mini_food.payment_notif_service.dto.request.PaymentRequest;
import mini_food.payment_notif_service.entities.Payment;
import mini_food.payment_notif_service.repositories.PaymentRepository;
import mini_food.payment_notif_service.services.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {
    PaymentRepository paymentRepository;
    RestTemplate restTemplate;

    @NonFinal
    @Value("${app.order-service-url}")
    String orderServiceUrl;
    public boolean processAndNotify(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("SUCCESS");
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        try {
            String updateUrl = orderServiceUrl + "/orders/" + request.getOrderId() + "/status?status=PAID";
            restTemplate.put(updateUrl, null);

        } catch (Exception e) {
            System.err.println("Lỗi khi kết nối đến Order Service: " + e.getMessage());
        }

        System.out.println("================ NOTIFICATION ================");
        System.out.println(request.getUsername() + " đã đặt đơn #" + request.getOrderId() + " thành công");
        System.out.println("==============================================");

        return true;
    }
}
