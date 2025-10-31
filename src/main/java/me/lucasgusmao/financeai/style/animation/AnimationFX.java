package me.lucasgusmao.financeai.style.animation;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.util.Duration;

public class AnimationFX {

    public static void fadeInUp(Node node, double delay) {
        node.setOpacity(0);
        node.setTranslateY(20);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.8), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.seconds(delay));
        fade.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.8), node);
        slide.setFromY(20);
        slide.setToY(0);
        slide.setDelay(Duration.seconds(delay));
        slide.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition parallel = new ParallelTransition(fade, slide);
        parallel.play();
    }

    public static void fadeIn(Node node, double delay) {
        node.setOpacity(0);

        FadeTransition fade = new FadeTransition(Duration.seconds(1), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.seconds(delay));
        fade.setInterpolator(Interpolator.EASE_OUT);
        fade.play();
    }

    public static void slideInRight(Node node, double delay) {
        node.setOpacity(0);
        node.setTranslateX(50);

        FadeTransition fade = new FadeTransition(Duration.seconds(1), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.seconds(delay));

        TranslateTransition slide = new TranslateTransition(Duration.seconds(1), node);
        slide.setFromX(50);
        slide.setToX(0);
        slide.setDelay(Duration.seconds(delay));
        slide.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition parallel = new ParallelTransition(fade, slide);
        parallel.play();
    }

    public static void scaleIn(Node node, double delay) {
        node.setOpacity(0);
        node.setScaleX(0.8);
        node.setScaleY(0.8);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.6), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.seconds(delay));

        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.6), node);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1);
        scale.setToY(1);
        scale.setDelay(Duration.seconds(delay));
        scale.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition parallel = new ParallelTransition(fade, scale);
        parallel.play();
    }

    public static void pulse(Node node) {
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), node);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.setInterpolator(Interpolator.EASE_BOTH);
        pulse.play();
    }

    public static void float3D(Node node, double delay) {
        TranslateTransition floatAnim = new TranslateTransition(Duration.seconds(3), node);
        floatAnim.setFromY(0);
        floatAnim.setToY(-10);
        floatAnim.setCycleCount(Animation.INDEFINITE);
        floatAnim.setAutoReverse(true);
        floatAnim.setDelay(Duration.seconds(delay));
        floatAnim.setInterpolator(Interpolator.EASE_BOTH);
        floatAnim.play();
    }

    public static void glow(Node node, double delay) {
        GaussianBlur blur = new GaussianBlur(0);
        node.setEffect(blur);

        Timeline glow = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(blur.radiusProperty(), 0)),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(blur.radiusProperty(), 3)),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(blur.radiusProperty(), 0))
        );
        glow.setDelay(Duration.seconds(delay));
        glow.setCycleCount(Animation.INDEFINITE);
        glow.play();
    }
}