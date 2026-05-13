package mini_food.order_service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import mini_food.order_service.dto.CreateOrderRequest;
import mini_food.order_service.dto.FoodResponse;
import mini_food.order_service.dto.OrderItemRequest;
import mini_food.order_service.dto.UserResponse;
import mini_food.order_service.entity.Order;
import mini_food.order_service.entity.OrderItem;
import mini_food.order_service.entity.OrderStatus;
import mini_food.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ExternalServiceClient externalServiceClient;

    public OrderService(
            OrderRepository orderRepository,
            ExternalServiceClient externalServiceClient
    ) {
        this.orderRepository = orderRepository;
        this.externalServiceClient = externalServiceClient;
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "createOrderFallback")
    @RateLimiter(name = "orderService")
    @Retry(name = "orderService")
    public Order createOrder(CreateOrderRequest request) {
        // Gọi User Service (đã có CircuitBreaker + Retry ở ExternalServiceClient)
        UserResponse user = externalServiceClient.fetchUser(request.getUserId());
        Order order = new Order();
        order.setUserId(user.getId());
        order.setUsername(user.getUsername());
        order.setStatus(OrderStatus.CREATED);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.getItems()) {
            // Gọi Food Service (đã có CircuitBreaker + Retry ở ExternalServiceClient)
            FoodResponse food = externalServiceClient.fetchFood(itemRequest.getFoodId());
            if (Boolean.FALSE.equals(food.getAvailable())) {
                throw new IllegalArgumentException("Mon an khong kha dung: " + food.getId());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setFoodId(food.getId());
            orderItem.setFoodName(food.getName());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(food.getPrice());
            BigDecimal lineTotal = food.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            orderItem.setLineTotal(lineTotal);
            items.add(orderItem);
            totalAmount = totalAmount.add(lineTotal);
        }

        order.setItems(items);
        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    public Order createOrderFallback(CreateOrderRequest request, Throwable throwable) {
        log.error("CircuitBreaker [orderService] - createOrder failed. Error: {}", throwable.getMessage());
        throw new RuntimeException("Dich vu tam thoi khong kha dung. Vui long thu lai sau. Loi: " + throwable.getMessage());
    }

    @RateLimiter(name = "orderService")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "updateStatusFallback")
    @Retry(name = "orderService")
    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay don hang: " + orderId));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order updateStatusFallback(Long orderId, OrderStatus status, Throwable throwable) {
        log.error("CircuitBreaker [orderService] - updateStatus failed. OrderId: {}, Error: {}", orderId, throwable.getMessage());
        throw new RuntimeException("Khong the cap nhat trang thai don hang. Vui long thu lai sau.");
    }
}
