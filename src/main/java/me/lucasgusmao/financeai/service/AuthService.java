package me.lucasgusmao.financeai.service;

import lombok.RequiredArgsConstructor;
import me.lucasgusmao.financeai.exceptions.custom.AlreadyExistsException;
import me.lucasgusmao.financeai.model.User;
import me.lucasgusmao.financeai.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public User register(String name, String email, String password) {
        //TODO adicionar login social via google, verificação de força da senha e validação  de conta via email/sms
        isValidEmail(email);
        if (repository.existsByEmail(email)) {
            throw new AlreadyExistsException("Já existe um usuário cadastrado com este e-mail.");
        }
        String username = email.split("@")[0];
        String encodedPassword = encoder.encode(password);
        User user = new User(name, email, username, encodedPassword);
        return repository.save(user);
    }

    public User login(String username, String password) {
        //TODO adicionar verificação de duas etapas via email/sms
        Optional<User> user = repository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Usuário e/ou senha inválidos.");
        }
        User userFound = user.get();
        if (!encoder.matches(password, userFound.getPassword())) {
            throw new IllegalArgumentException("Usuário e/ou senha inválidos.");
        }
        return userFound;
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}
