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
import me.lucasgusmao.financeai.style.animation.AnimationFX;
import me.lucasgusmao.financeai.style.animation.ParallaxFX;
import me.lucasgusmao.financeai.model.entity.User;
import me.lucasgusmao.financeai.service.AuthService;
import me.lucasgusmao.financeai.screens.TransitionScreen;
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
    private HBox benefit1;

    @FXML
    private HBox benefit2;

    @FXML
    private HBox benefit3;

    @FXML
    private StackPane benefitIcon1;

    @FXML
    private StackPane benefitIcon2;

    @FXML
    private StackPane benefitIcon3;

    @Autowired
    private AuthService authService;

    @Autowired
    private ApplicationContext springContext;

    @FXML
    public void initialize() {
        startAnimations();
        setupPasswordValidation();
        setupNameValidation();
        setupEmailValidation();
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

        if (benefit1 != null) {
            AnimationFX.fadeInUp(benefit1, 0.5);
        }

        if (benefit2 != null) {
            AnimationFX.fadeInUp(benefit2, 0.7);
        }

        if (benefit3 != null) {
            AnimationFX.fadeInUp(benefit3, 0.9);
        }

        if (benefitIcon1 != null) {
            AnimationFX.pulse(benefitIcon1);
        }

        if (benefitIcon2 != null) {
            AnimationFX.pulse(benefitIcon2);
        }

        if (benefitIcon3 != null) {
            AnimationFX.pulse(benefitIcon3);
        }

        if (rightContent != null) {
            AnimationFX.slideInRight(rightContent, 0.2);
        }
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty()) {
            showError("Por favor, preencha seu nome completo");
            nameField.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            showError("Por favor, preencha seu email");
            emailField.requestFocus();
            return;
        }

        if (!AuthService.isValidEmail(email)) {
            showError("Email inválido");
            emailField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Por favor, crie uma senha");
            passwordField.requestFocus();
            return;
        }

        if (password.length() < 6) {
            showError("A senha deve ter no mínimo 6 caracteres");
            passwordField.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            showError("Por favor, confirme sua senha");
            confirmPasswordField.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("As senhas não coincidem");
            confirmPasswordField.requestFocus();
            return;
        }

        try {
            User user = authService.register(name, email, password);
            showSuccess("Cadastro realizado! Redirecionando...");
            System.out.println("Usuário criado: " + user.getName());

            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(this::loadMainScreen);
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
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    FinanceApplication.class.getResource("login-view.fxml")
            );
            loader.setControllerFactory(springContext::getBean);

            Scene scene = new Scene(loader.load(), 1400, 900);
            Stage stage = (Stage) loginLink.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("FinanCIA - Login");
        } catch (Exception e) {
            showError("Erro ao voltar para login");
            e.printStackTrace();
        }
    }

    private void loadMainScreen() {
        try {
            Stage currentStage = (Stage) registerButton.getScene().getWindow();
            currentStage.hide();

            TransitionScreen transition = new TransitionScreen(() -> {
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

    private void setupPasswordValidation() {
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswords();
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswords();
        });
    }

    private void setupNameValidation() {
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateName();
        });
    }

    private void setupEmailValidation() {
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateEmail();
        });
    }


    private void validatePasswords() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!confirmPassword.isEmpty()) {
            if (!password.equals(confirmPassword)) {
                passwordField.getStyleClass().remove("input-success");
                passwordField.getStyleClass().add("input-error");

                confirmPasswordField.getStyleClass().remove("input-success");
                confirmPasswordField.getStyleClass().add("input-error");

                passwordField.setStyle("""
                    -fx-background-color: #18181B;
                    -fx-text-fill: #F4F4F5;
                    -fx-prompt-text-fill: #71717A;
                    -fx-background-radius: 10;
                    -fx-border-color: #EF4444;
                    -fx-border-radius: 10;
                    -fx-border-width: 2;
                    -fx-padding: 13 16;
                    -fx-font-size: 14px;
                    -fx-pref-width: 356px;
            """);

                confirmPasswordField.setStyle("""
                    -fx-background-color: #18181B;
                    -fx-text-fill: #F4F4F5;
                    -fx-prompt-text-fill: #71717A;
                    -fx-background-radius: 10;
                    -fx-border-color: #EF4444;
                    -fx-border-radius: 10;
                    -fx-border-width: 2;
                    -fx-padding: 13 16;
                    -fx-font-size: 14px;
                    -fx-pref-width: 356px;
            """);
            } else {
                passwordField.getStyleClass().remove("input-error");
                passwordField.getStyleClass().add("input-success");

                confirmPasswordField.getStyleClass().remove("input-error");
                confirmPasswordField.getStyleClass().add("input-success");

                passwordField.setStyle("""
                    -fx-background-color: #18181B;
                    -fx-text-fill: #F4F4F5;
                    -fx-prompt-text-fill: #71717A;
                    -fx-background-radius: 10;
                    -fx-border-color: #10B981;
                    -fx-border-radius: 10;
                    -fx-border-width: 2;
                    -fx-padding: 13 16;
                    -fx-font-size: 14px;
                    -fx-pref-width: 356px;
                    """);

                confirmPasswordField.setStyle("""
                    -fx-background-color: #18181B;
                    -fx-text-fill: #F4F4F5;
                    -fx-prompt-text-fill: #71717A;
                    -fx-background-radius: 10;
                    -fx-border-color: #10B981;
                    -fx-border-radius: 10;
                    -fx-border-width: 2;
                    -fx-padding: 13 16;
                    -fx-font-size: 14px;
                    -fx-pref-width: 356px;
            """);
            }
        } else {
            resetPasswordFieldsStyle();
        }
    }

    private void validateName() {
        String name = nameField.getText().trim();

        if (!name.isEmpty()) {
            if (name.length() >= 3) {
                nameField.setStyle("""
                    -fx-background-color: #18181B;
                    -fx-text-fill: #F4F4F5;
                    -fx-prompt-text-fill: #71717A;
                    -fx-background-radius: 10;
                    -fx-border-color: #10B981;
                    -fx-border-radius: 10;
                    -fx-border-width: 2;
                    -fx-padding: 13 16;
                    -fx-font-size: 14px;
                    -fx-pref-width: 356px;
                """);
            } else {
                nameField.setStyle("""
                    -fx-background-color: #18181B;
                    -fx-text-fill: #F4F4F5;
                    -fx-prompt-text-fill: #71717A;
                    -fx-background-radius: 10;
                    -fx-border-color: #EF4444;
                    -fx-border-radius: 10;
                    -fx-border-width: 2;
                    -fx-padding: 13 16;
                    -fx-font-size: 14px;
                    -fx-pref-width: 356px;
                """);
            }
        } else {
            resetNameFieldStyle();
        }
    }

    private void validateEmail() {
        String email = emailField.getText().trim();

        if (!email.isEmpty()) {
            if (AuthService.isValidEmail(email)) {
                emailField.setStyle("""
                    -fx-background-color: #18181B;
                    -fx-text-fill: #F4F4F5;
                    -fx-prompt-text-fill: #71717A;
                    -fx-background-radius: 10;
                    -fx-border-color: #10B981;
                    -fx-border-radius: 10;
                    -fx-border-width: 2;
                    -fx-padding: 13 16;
                    -fx-font-size: 14px;
                    -fx-pref-width: 356px;
                """);
            } else {
                emailField.setStyle("""
                    -fx-background-color: #18181B;
                    -fx-text-fill: #F4F4F5;
                    -fx-prompt-text-fill: #71717A;
                    -fx-background-radius: 10;
                    -fx-border-color: #EF4444;
                    -fx-border-radius: 10;
                    -fx-border-width: 2;
                    -fx-padding: 13 16;
                    -fx-font-size: 14px;
                    -fx-pref-width: 356px;
                """);
            }
        } else {
            resetEmailFieldStyle();
        }
    }

    private void resetPasswordFieldsStyle() {
        passwordField.getStyleClass().removeAll("input-error", "input-success");
        confirmPasswordField.getStyleClass().removeAll("input-error", "input-success");

        String defaultStyle = """
            -fx-background-color: #18181B;
            -fx-text-fill: #F4F4F5;
            -fx-prompt-text-fill: #71717A;
            -fx-background-radius: 10;
            -fx-border-color: #3F3F46;
            -fx-border-radius: 10;
            -fx-border-width: 1.5;
            -fx-padding: 13 16;
            -fx-font-size: 14px;
            -fx-pref-width: 356px;
            """;

        passwordField.setStyle(defaultStyle);
        confirmPasswordField.setStyle(defaultStyle);
    }

    private void resetNameFieldStyle() {
        String defaultStyle = """
            -fx-background-color: #18181B;
            -fx-text-fill: #F4F4F5;
            -fx-prompt-text-fill: #71717A;
            -fx-background-radius: 10;
            -fx-border-color: #3F3F46;
            -fx-border-radius: 10;
            -fx-border-width: 1.5;
            -fx-padding: 13 16;
            -fx-font-size: 14px;
            -fx-pref-width: 356px;
        """;

        nameField.setStyle(defaultStyle);
    }

    private void resetEmailFieldStyle() {
        String defaultStyle = """
            -fx-background-color: #18181B;
            -fx-text-fill: #F4F4F5;
            -fx-prompt-text-fill: #71717A;
            -fx-background-radius: 10;
            -fx-border-color: #3F3F46;
            -fx-border-radius: 10;
            -fx-border-width: 1.5;
            -fx-padding: 13 16;
            -fx-font-size: 14px;
            -fx-pref-width: 356px;
        """;

        emailField.setStyle(defaultStyle);
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
        AnimationFX.scaleIn(errorLabel, 0);
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
        AnimationFX.scaleIn(errorLabel, 0);
    }
}