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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final String userServiceUrl;
    private final String foodServiceUrl;

    public OrderService(
            OrderRepository orderRepository,
            RestTemplate restTemplate,
            @Value("${services.user.url}") String userServiceUrl,
            @Value("${services.food.url}") String foodServiceUrl
    ) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
        this.foodServiceUrl = foodServiceUrl;
    }

    public Order createOrder(CreateOrderRequest request) {
        UserResponse user = fetchUser(request.getUserId());
        Order order = new Order();
        order.setUserId(user.getId());
        order.setUsername(user.getUsername());
        order.setStatus(OrderStatus.CREATED);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.getItems()) {
            FoodResponse food = fetchFood(itemRequest.getFoodId());
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

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay don hang: " + orderId));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    private UserResponse fetchUser(Long userId) {
        try {
            return restTemplate.getForObject(
                    userServiceUrl + "/users/" + userId,
                    UserResponse.class
            );
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().equals(HttpStatusCode.valueOf(404))) {
                throw new IllegalArgumentException("Khong tim thay user: " + userId);
            }
            throw ex;
        }
    }

    private FoodResponse fetchFood(Long foodId) {
        try {
            return restTemplate.getForObject(
                    foodServiceUrl + "/foods/" + foodId,
                    FoodResponse.class
            );
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().equals(HttpStatusCode.valueOf(404))) {
                throw new IllegalArgumentException("Khong tim thay mon an: " + foodId);
            }
            throw ex;
        }
    }
}
