package mini_food.user_service.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mini_food.user_service.entity.User;
import mini_food.user_service.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Le Tran Gia Huy
 * @created 08/04/2026 - 7:12 PM
 * @project eureka-server
 * @package mini_food.user_service.controller
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping("/register")
    public ResponseEntity<?> saveUser(
            @RequestBody RegisterRequest requestForm,
            HttpServletResponse response
    ) {
        User tempAcc = new User();
        tempAcc.setUsername(requestForm.username);
        tempAcc.setPassword(requestForm.password);
        User createdAcc = userService.save(tempAcc);

        String token  = userService.verifyWithoutAuth(createdAcc.getUsername());
        // Tạo HTTP-only cookie
        Cookie cookie = new Cookie("jwt", token);

        final ResponseCookie responseCookie = ResponseCookie
                .from("jwt", token)
                .secure(false)
                .httpOnly(true)
                .path("/")
                .maxAge(10 * 24 * 60 * 60)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        String token  = userService.verify(request.getUsername(), request.getPassword());
        final ResponseCookie responseCookie = ResponseCookie
                .from("jwt", token)
                .secure(false)
                .httpOnly(true)
                .path("/")
                .maxAge(10 * 24 * 60 * 60)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        final ResponseCookie responseCookie = ResponseCookie
                .from("jwt", "")
                .secure(false)
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok("Logged out successfully");
    }

    @Data
    static class RegisterRequest {
        private String username;
        private String email;
        private String password;
    }

    @Data
    static class LoginRequest {
        private String username;
        private String password;
    }
}


