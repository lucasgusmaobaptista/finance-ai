package me.lucasgusmao.financeai.view;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class TransitionScreen {

    private Stage stage;
    private Runnable onComplete;

    public TransitionScreen(Runnable onComplete) {
        this.onComplete = onComplete;
    }

    public void show() {
        stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #18181B;");

        Circle circle1 = new Circle(200);
        circle1.setFill(Color.web("#7C3AED", 0.1));
        GaussianBlur blur = new GaussianBlur(50);
        circle1.setEffect(blur);

        Label logo = new Label("FinanceAI");
        logo.setStyle("""
            -fx-font-size: 72px;
            -fx-font-weight: 700;
            -fx-text-fill: #F4F4F5;
            -fx-letter-spacing: -2px;
        """);
        logo.setOpacity(0);

        Label tagline = new Label("Preparando sua experiência...");
        tagline.setStyle("""
            -fx-font-size: 16px;
            -fx-text-fill: #A1A1AA;
            -fx-translate-y: 60px;
        """);
        tagline.setOpacity(0);
        root.getChildren().addAll(circle1, logo, tagline);

        Scene scene = new Scene(root, 800, 500);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
        animateTransition(circle1, logo, tagline);
    }

    private void animateTransition(Circle circle, Label logo, Label tagline) {
        ScaleTransition circleScale = new ScaleTransition(Duration.seconds(1.5), circle);
        circleScale.setFromX(0);
        circleScale.setFromY(0);
        circleScale.setToX(1);
        circleScale.setToY(1);
        circleScale.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition logoFade = new FadeTransition(Duration.seconds(1), logo);
        logoFade.setFromValue(0);
        logoFade.setToValue(1);
        logoFade.setDelay(Duration.seconds(0.3));

        ScaleTransition logoScale = new ScaleTransition(Duration.seconds(1), logo);
        logoScale.setFromX(0.8);
        logoScale.setFromY(0.8);
        logoScale.setToX(1);
        logoScale.setToY(1);
        logoScale.setDelay(Duration.seconds(0.3));
        logoScale.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition taglineFade = new FadeTransition(Duration.seconds(0.8), tagline);
        taglineFade.setFromValue(0);
        taglineFade.setToValue(1);
        taglineFade.setDelay(Duration.seconds(0.8));

        ParallelTransition parallel = new ParallelTransition(
                circleScale, logoFade, logoScale, taglineFade
        );

        parallel.setOnFinished(e -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(ev -> close());
            pause.play();
        });

        parallel.play();
    }

    private void close() {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), stage.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            stage.close();
            if (onComplete != null) {
                onComplete.run();
            }
        });
        fadeOut.play();
    }
}