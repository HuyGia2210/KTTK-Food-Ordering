package mini_food.user_service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mini_food.user_service.entity.User;
import mini_food.user_service.repository.UserRepository;
import mini_food.user_service.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    final AuthenticationManager authenticationManager;
    final JwtService jwtService;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void registerUser(String username, String email, String password) {
        // Kiểm tra nếu username hoặc email đã tồn tại
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username đã tồn tại!");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email đã tồn tại!");
        }

        // Tạo và lưu người dùng mới
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password); // Lưu mật khẩu chưa mã hóa (chỉ để demo, không nên làm vậy trong thực tế)
        userRepository.save(newUser);
    }

    public User save(User user) {
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
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        if (auth.isAuthenticated()) {
            log.info("User authenticated");
            return jwtService.generateToken(username);
        } else {
            log.error("User not authenticated");
            return "400";
        }
    }

    public String verifyWithoutAuth(String username) {
        return jwtService.generateToken(username);
    }
}
