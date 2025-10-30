package me.lucasgusmao.financeai.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import me.lucasgusmao.financeai.HelloApplication;
import me.lucasgusmao.financeai.model.User;
import me.lucasgusmao.financeai.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button registerButton;

    @FXML
    private Hyperlink loginLink;

    @Autowired
    private AuthService authService;  // ← Injeção do Service

    @Autowired
    private ApplicationContext springContext;

    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Preencha todos os campos");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("As senhas não coincidem");
            return;
        }

        if (password.length() < 6) {
            showError("A senha deve ter no mínimo 6 caracteres");
            return;
        }

        if (!AuthService.isValidEmail(email)) {
            showError("Email inválido");
            return;
        }

        try {
            //TODO tirar debug
            User user = authService.register(name, email, password);
            showSuccess("Cadastro realizado com sucesso!");
            System.out.println("Usuário criado: " + user.getName());
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(this::backToLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            showError(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void backToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource("login-view.fxml")
            );
            loader.setControllerFactory(springContext::getBean);

            Scene scene = new Scene(loader.load(), 400, 500);
            Stage stage = (Stage) loginLink.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("FinanceAI - Login");
        } catch (Exception e) {
            showError("Erro ao voltar para login");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("""
        -fx-text-fill: #FCA5A5;
        -fx-background-color: rgba(252, 165, 165, 0.08);
        -fx-background-radius: 8;
        -fx-padding: 11 14;
        -fx-border-color: rgba(252, 165, 165, 0.2);
        -fx-border-width: 1;
        -fx-border-radius: 8;
        -fx-font-size: 12px;
    """);
        errorLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("""
        -fx-text-fill: #86EFAC;
        -fx-background-color: rgba(134, 239, 172, 0.08);
        -fx-background-radius: 8;
        -fx-padding: 11 14;
        -fx-border-color: rgba(134, 239, 172, 0.2);
        -fx-border-width: 1;
        -fx-border-radius: 8;
        -fx-font-size: 12px;
    """);
        errorLabel.setVisible(true);
    }
}