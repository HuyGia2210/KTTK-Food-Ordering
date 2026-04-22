package mini_food.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

	public static void main(String[] args) {
		if (System.getProperty("EUREKA_ENABLED") == null && System.getenv("EUREKA_ENABLED") == null) {
			System.setProperty("EUREKA_ENABLED", "true");
		}
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
