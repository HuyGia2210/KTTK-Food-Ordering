package mini_food.payment_notif_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentNotifServiceApplication {

	public static void main(String[] args) {
		if (System.getProperty("eureka.client.register-with-eureka") == null) {
			System.setProperty("eureka.client.register-with-eureka", "true");
		}
		if (System.getProperty("eureka.client.fetch-registry") == null) {
			System.setProperty("eureka.client.fetch-registry", "true");
		}
		SpringApplication.run(PaymentNotifServiceApplication.class, args);
	}

}
