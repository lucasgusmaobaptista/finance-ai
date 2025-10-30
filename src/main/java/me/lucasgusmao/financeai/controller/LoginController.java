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
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink registerLink;

    @Autowired
    private AuthService authService;

    @Autowired
    private ApplicationContext springContext;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Preencha todos os campos");
            return;
        }

        try {
            User user = authService.login(username, password);
            //TODO tirar debug antes de entregar o trabalho para professora
            System.out.println("Login realizado: " + user.getName());
            loadMainScreen();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Erro ao realizar login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource("register-view.fxml")
            );
            loader.setControllerFactory(springContext::getBean);
            Scene scene = new Scene(loader.load(), 400, 600);
            Stage stage = (Stage) registerLink.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Financia Aí - Cadastro");
        } catch (Exception e) {
            showError("Erro ao abrir tela de cadastro");
            e.printStackTrace();
        }
    }

    private void loadMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource("main-view.fxml")
            );
            loader.setControllerFactory(springContext::getBean);
            Scene scene = new Scene(loader.load(), 1200, 800);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Financia Aí - Dashboard");
        } catch (Exception e) {
            showError("Erro ao carregar tela principal");
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
}