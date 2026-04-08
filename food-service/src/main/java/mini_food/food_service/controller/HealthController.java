package mini_food.food_service.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/food-items/ping")
    public Map<String, String> ping() {
        return Map.of("message", "food-service is running");
    }
}
