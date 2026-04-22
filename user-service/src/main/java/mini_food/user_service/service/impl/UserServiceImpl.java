package mini_food.user_service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mini_food.user_service.entity.User;
import mini_food.user_service.repository.UserRepository;
import mini_food.user_service.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Le Tran Gia Huy
 * @created 08/04/2026 - 6:55 PM
 * @project eureka-server
 * @package mini_food.user_service.service.impl
 */

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    final JwtService jwtService;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username da ton tai");
        }
        if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email da ton tai");
        }
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public String verify(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            log.error("User not authenticated");
            throw new IllegalArgumentException("Sai tai khoan hoac mat khau");
        }
        log.info("User authenticated");
        return jwtService.generateToken(username);
    }

    public String verifyWithoutAuth(String username) {
        return jwtService.generateToken(username);
    }
}
