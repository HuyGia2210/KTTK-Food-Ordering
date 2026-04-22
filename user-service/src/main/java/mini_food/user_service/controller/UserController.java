package mini_food.user_service.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mini_food.user_service.entity.User;
import mini_food.user_service.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Le Tran Gia Huy
 * @created 08/04/2026 - 7:12 PM
 * @project eureka-server
 * @package mini_food.user_service.controller
 */

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
@RequestMapping
public class UserController {

    final UserService userService;

    @GetMapping({"/users", "/api/users"})
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping({"/users/{id}", "/api/users/{id}"})
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping({"/users/by-username", "/api/users/by-username"})
    public ResponseEntity<?> getUserByUsername(@RequestParam String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping({"/register", "/api/users/register"})
    public ResponseEntity<?> saveUser(
            @RequestBody RegisterRequest requestForm,
            HttpServletResponse response
    ) {
        User tempAcc = new User();
        tempAcc.setUsername(requestForm.username);
        tempAcc.setPassword(requestForm.password);
        tempAcc.setEmail(requestForm.email);
        tempAcc.setRole(requestForm.role == null || requestForm.role.isBlank() ? "USER" : requestForm.role);
        User createdAcc = userService.save(tempAcc);

        String token  = userService.verifyWithoutAuth(createdAcc.getUsername());
        final ResponseCookie responseCookie = ResponseCookie
                .from("jwt", token)
                .secure(false)
                .httpOnly(true)
                .path("/")
                .maxAge(10 * 24 * 60 * 60)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok(new AuthResponse(token, createdAcc));
    }

    @PostMapping({"/login", "/api/users/login"})
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        String token  = userService.verify(request.getUsername(), request.getPassword());
        User user = userService.findByUsername(request.getUsername());
        final ResponseCookie responseCookie = ResponseCookie
                .from("jwt", token)
                .secure(false)
                .httpOnly(true)
                .path("/")
                .maxAge(10 * 24 * 60 * 60)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok(new AuthResponse(token, user));
    }

    @PostMapping({"/logout", "/api/users/logout"})
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
        private String role;
    }

    @Data
    static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    static class AuthResponse {
        private final String token;
        private final User user;
    }
}


