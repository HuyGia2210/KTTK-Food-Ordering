package mini_food.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Le Tran Gia Huy
 * @created 08/04/2026 - 6:50 PM
 * @project eureka-server
 * @package mini_food.user_service.entity
 */

@Entity
@Table(name = "food_users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
}
