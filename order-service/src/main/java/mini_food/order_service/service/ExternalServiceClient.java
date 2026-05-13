package mini_food.order_service.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import mini_food.order_service.dto.FoodResponse;
import mini_food.order_service.dto.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Client component chịu trách nhiệm gọi các service bên ngoài (User Service, Food Service).
 * Được tách ra để các annotation Resilience4j (@CircuitBreaker, @Retry) hoạt động
 * đúng qua Spring AOP proxy (chỉ hoạt động trên public method của bean khác).
 */
@Component
public class ExternalServiceClient {
    private static final Logger log = LoggerFactory.getLogger(ExternalServiceClient.class);

    private final RestTemplate restTemplate;
    private final String userServiceUrl;
    private final String foodServiceUrl;

    public ExternalServiceClient(
            RestTemplate restTemplate,
            @Value("${services.user.url}") String userServiceUrl,
            @Value("${services.food.url}") String foodServiceUrl
    ) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
        this.foodServiceUrl = foodServiceUrl;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fetchUserFallback")
    @Retry(name = "userService")
    public UserResponse fetchUser(Long userId) {
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

    public UserResponse fetchUserFallback(Long userId, Throwable throwable) {
        log.error("CircuitBreaker [userService] - fetchUser failed. UserId: {}, Error: {}", userId, throwable.getMessage());
        throw new RuntimeException("User Service khong kha dung. Vui long thu lai sau.");
    }

    @CircuitBreaker(name = "foodService", fallbackMethod = "fetchFoodFallback")
    @Retry(name = "foodService")
    public FoodResponse fetchFood(Long foodId) {
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

    public FoodResponse fetchFoodFallback(Long foodId, Throwable throwable) {
        log.error("CircuitBreaker [foodService] - fetchFood failed. FoodId: {}, Error: {}", foodId, throwable.getMessage());
        throw new RuntimeException("Food Service khong kha dung. Vui long thu lai sau.");
    }
}
