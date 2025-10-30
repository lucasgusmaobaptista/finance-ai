package me.lucasgusmao.financeai.view;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashScreen {

    private Stage splashStage;
    private Label statusLabel;

    public void show() {
        splashStage = new Stage();
        splashStage.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");
        Rectangle background = new Rectangle(700, 500);
        background.setArcWidth(20);
        background.setArcHeight(20);
        background.setFill(Color.web("#1C1C1E"));
        Circle circle1 = new Circle(150);
        circle1.setFill(Color.web("#6B4FBB", 0.08));
        circle1.setTranslateX(-250);
        circle1.setTranslateY(-180);
        BoxBlur blur = new BoxBlur(40, 40, 3);
        circle1.setEffect(blur);
        Circle circle2 = new Circle(180);
        circle2.setFill(Color.web("#8B5CF6", 0.06));
        circle2.setTranslateX(280);
        circle2.setTranslateY(200);
        circle2.setEffect(blur);
        VBox content = new VBox(50);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(600);
        StackPane loaderContainer = createMinimalLoader();
        VBox logoBox = new VBox(12);
        logoBox.setAlignment(Pos.CENTER);
        Label logoLabel = new Label("Financia Aí");
        logoLabel.setStyle("""
            -fx-font-size: 48px;
            -fx-font-weight: 600;
            -fx-text-fill: #E8E8E8;
            -fx-letter-spacing: -1px;
        """);
        Label taglineLabel = new Label("Sua assistente financeira inteligente");
        taglineLabel.setStyle("""
            -fx-font-size: 13px;
            -fx-text-fill: #8E8E93;
            -fx-letter-spacing: 0.5px;
        """);
        FadeTransition logoFade = new FadeTransition(Duration.seconds(1.5), logoLabel);
        logoFade.setFromValue(0);
        logoFade.setToValue(1);
        logoFade.setInterpolator(Interpolator.EASE_OUT);
        logoFade.play();
        logoBox.getChildren().addAll(logoLabel, taglineLabel);

        statusLabel = new Label("Inicializando");
        statusLabel.setStyle("""
            -fx-font-size: 12px;
            -fx-text-fill: #636366;
        """);

        animateStatus();

        Label versionLabel = new Label("v1.0 - Lucas Gusmão");
        versionLabel.setStyle("""
            -fx-font-size: 10px;
            -fx-text-fill: #48484A;
            -fx-padding: 30 0 0 0;
        """);

        content.getChildren().addAll(loaderContainer, logoBox, statusLabel, versionLabel);
        root.getChildren().addAll(background, circle1, circle2, content);

        Scene scene = new Scene(root, 700, 500);
        scene.setFill(Color.TRANSPARENT);
        splashStage.setScene(scene);
        splashStage.show();
    }

    private StackPane createMinimalLoader() {
        StackPane loader = new StackPane();
        loader.setPrefSize(120, 120);

        for (int i = 0; i < 3; i++) {
            Circle dot = new Circle(4);
            dot.setFill(Color.web("#8B5CF6", 0.7 - (i * 0.15)));

            double radius = 35;
            double angle = i * 120;
            double x = radius * Math.cos(Math.toRadians(angle));
            double y = radius * Math.sin(Math.toRadians(angle));

            dot.setTranslateX(x);
            dot.setTranslateY(y);
            RotateTransition rotate = new RotateTransition(Duration.seconds(3), loader);
            rotate.setByAngle(360);
            rotate.setCycleCount(Animation.INDEFINITE);
            rotate.setInterpolator(Interpolator.LINEAR);
            rotate.play();
            ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.5), dot);
            pulse.setFromX(1.0);
            pulse.setFromY(1.0);
            pulse.setToX(1.3);
            pulse.setToY(1.3);
            pulse.setCycleCount(Animation.INDEFINITE);
            pulse.setAutoReverse(true);
            pulse.setDelay(Duration.seconds(i * 0.5));
            pulse.play();
            loader.getChildren().add(dot);
        }
        return loader;
    }

    private void animateStatus() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> statusLabel.setText("Inicializando")),
                new KeyFrame(Duration.seconds(0.5), e -> statusLabel.setText("Inicializando.")),
                new KeyFrame(Duration.seconds(1), e -> statusLabel.setText("Inicializando..")),
                new KeyFrame(Duration.seconds(1.5), e -> statusLabel.setText("Inicializando..."))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void updateStatus(String status) {
        Platform.runLater(() -> statusLabel.setText(status));
    }

    public void close() {
        if (splashStage != null) {
            FadeTransition fadeOut = new FadeTransition(
                    Duration.seconds(0.8),
                    splashStage.getScene().getRoot()
            );
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setInterpolator(Interpolator.EASE_IN);
            fadeOut.setOnFinished(e -> splashStage.close());
            fadeOut.play();
        }
    }
}