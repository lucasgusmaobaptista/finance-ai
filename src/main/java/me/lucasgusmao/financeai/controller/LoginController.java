package me.lucasgusmao.financeai.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import me.lucasgusmao.financeai.FinanceApplication;
import me.lucasgusmao.financeai.model.entity.User;
import me.lucasgusmao.financeai.service.AuthService;
import me.lucasgusmao.financeai.style.animation.AnimationFX;
import me.lucasgusmao.financeai.style.animation.ParallaxFX;
import me.lucasgusmao.financeai.screens.TransitionScreen;
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

    @FXML
    private StackPane rootPane;

    @FXML
    private Circle backgroundCircle1;

    @FXML
    private Circle backgroundCircle2;

    @FXML
    private VBox rightContent;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private HBox feature1;

    @FXML
    private HBox feature2;

    @FXML
    private HBox feature3;

    @Autowired
    private AuthService authService;

    @Autowired
    private ApplicationContext springContext;

    @FXML
    public void initialize() {
        startAnimations();
    }

    private void startAnimations() {
        if (backgroundCircle1 != null && backgroundCircle2 != null && rootPane != null) {
            ParallaxFX.applyToBackground(rootPane, backgroundCircle1, backgroundCircle2);
            AnimationFX.float3D(backgroundCircle1, 0);
            AnimationFX.float3D(backgroundCircle2, 0.5);
        }

        if (titleLabel != null) {
            AnimationFX.fadeInUp(titleLabel, 0.1);
        }

        if (subtitleLabel != null) {
            AnimationFX.fadeInUp(subtitleLabel, 0.3);
        }

        if (feature1 != null) {
            AnimationFX.fadeInUp(feature1, 0.5);
        }

        if (feature2 != null) {
            AnimationFX.fadeInUp(feature2, 0.7);
        }

        if (feature3 != null) {
            AnimationFX.fadeInUp(feature3, 0.9);
        }

        if (rightContent != null) {
            AnimationFX.slideInRight(rightContent, 0.2);
        }
    }

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
                    FinanceApplication.class.getResource("register-view.fxml")
            );
            loader.setControllerFactory(springContext::getBean);
            Scene scene = new Scene(loader.load(), 1400, 900);
            Stage stage = (Stage) registerLink.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("FinanCIA - Cadastro");
        } catch (Exception e) {
            showError("Erro ao abrir tela de cadastro");
            e.printStackTrace();
        }
    }

    private void loadMainScreen() {
        try {
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.hide();

            TransitionScreen transition =
                    new TransitionScreen(() -> {
                        javafx.application.Platform.runLater(() -> {
                            try {
                                FXMLLoader loader = new FXMLLoader(
                                        FinanceApplication.class.getResource("main-view.fxml")
                                );
                                loader.setControllerFactory(springContext::getBean);

                                Scene scene = new Scene(loader.load(), 1600, 900);
                                currentStage.setScene(scene);
                                currentStage.setTitle("FinanCIA - Dashboard");
                                currentStage.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    });

            transition.showAnimation();
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
        AnimationFX.fadeInUp(errorLabel, 0);
    }
}