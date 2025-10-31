package me.lucasgusmao.financeai.style.animation;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class ParallaxFX {

    public static void applyToBackground(Pane container, Node... nodes) {
        container.setOnMouseMoved(event -> {
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            double centerX = container.getWidth() / 2;
            double centerY = container.getHeight() / 2;

            for (int i = 0; i < nodes.length; i++) {
                Node node = nodes[i];
                double speed = 0.02 * (i + 1);

                double offsetX = (mouseX - centerX) * speed;
                double offsetY = (mouseY - centerY) * speed;

                node.setTranslateX(offsetX);
                node.setTranslateY(offsetY);
            }
        });
    }
}