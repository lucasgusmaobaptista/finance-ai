package me.lucasgusmao.financeai.style.animation;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class TypingAnimation {

    private Timeline timeline;
    private String fullText;
    private Label label;
    private boolean showCursor;
    private int currentIndex = 0;

    public TypingAnimation(Label label, String text, boolean showCursor) {
        this.label = label;
        this.fullText = text;
        this.showCursor = showCursor;
    }

    public void play() {
        currentIndex = 0;
        label.setText("");

        timeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            if (currentIndex < fullText.length()) {
                label.setText(fullText.substring(0, currentIndex + 1) + (showCursor ? "|" : ""));
                currentIndex++;
            } else {
                if (showCursor) {
                    // Animação do cursor piscando
                    blinkCursor();
                }
                timeline.stop();
            }
        }));

        timeline.setCycleCount(fullText.length());
        timeline.play();
    }

    private void blinkCursor() {
        Timeline cursorBlink = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> label.setText(fullText + "|")),
                new KeyFrame(Duration.seconds(0.5), e -> label.setText(fullText))
        );
        cursorBlink.setCycleCount(Timeline.INDEFINITE);
        cursorBlink.play();
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
        label.setText(fullText);
    }
}