package me.lucasgusmao.financeai.style.animation;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.List;

public class SequenceTypingAnimation {

    private Label label;
    private List<String> texts;
    private int currentTextIndex = 0;
    private boolean loop;

    public SequenceTypingAnimation(Label label, List<String> texts, boolean loop) {
        this.label = label;
        this.texts = texts;
        this.loop = loop;
    }

    public void play() {
        if (texts.isEmpty()) return;
        animateNext();
    }

    private void animateNext() {
        if (currentTextIndex >= texts.size()) {
            if (loop) {
                currentTextIndex = 0;
            } else {
                return;
            }
        }

        String currentText = texts.get(currentTextIndex);
        TypingAnimation typing = new TypingAnimation(label, currentText, false);

        // Após completar a digitação, aguarda e vai para o próximo
        typing.play();

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> {
            currentTextIndex++;
            animateNext();
        });
        pause.play();
    }
}