package mk.reklama8.controller;

import mk.reklama8.model.User;
import mk.reklama8.repository.UserRepository;
import mk.reklama8.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtil jwtUtils;
    @PostMapping("/signin")
    public Map<String, String> authenticateUser(@RequestBody User user) {

        if (!user.getUsername().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return Map.of("error", "Username must be a valid email!");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtils.generateToken(userDetails.getUsername());

            return Map.of("token", token);
        } catch (BadCredentialsException ex) {
            return Map.of("error", "Invalid username or password!");
        } catch (Exception ex) {
            return Map.of("error", "Authentication failed!");
        }
    }


    @PostMapping("/signup")
    public Map<String, String> registerUser(@RequestBody User user) {
        System.out.println(user);

        if (!user.getUsername().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return Map.of("error", "Username must be a valid email!");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            return Map.of("error", "Username is already taken!");
        }
        User newUser = new User(null, user.getUsername(), encoder.encode(user.getPassword()));
        userRepository.save(newUser);

        return Map.of("message", "User registered successfully!");
    }

}
