package me.lucasgusmao.financeai.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.lucasgusmao.financeai.exceptions.custom.AlreadyExistsException;
import me.lucasgusmao.financeai.model.entity.User;
import me.lucasgusmao.financeai.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    @Setter
    private User currentUser;

    public User register(String name, String email, String password) {
        isValidEmail(email);
        if (repository.existsByEmail(email)) {
            throw new AlreadyExistsException("Já existe um usuário cadastrado com este e-mail.");
        }
        String username = email.split("@")[0];
        String encodedPassword = encoder.encode(password);
        User user = new User(name, email, username, encodedPassword);
        this.currentUser = user;
        return repository.save(user);
    }

    public User login(String username, String password) {
        Optional<User> user = repository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Usuário e/ou senha inválidos.");
        }
        User userFound = user.get();
        if (!encoder.matches(password, userFound.getPassword())) {
            throw new IllegalArgumentException("Usuário e/ou senha inválidos.");
        }
        this.currentUser = userFound;
        return userFound;
    }

    public User getCurrentUser() {
        if (this.currentUser == null) {
            throw new IllegalStateException("Nenhum usuário está autenticado no momento.");
        }
        return this.currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

}
