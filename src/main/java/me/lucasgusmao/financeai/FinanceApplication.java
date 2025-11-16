package me.lucasgusmao.financeai;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.lucasgusmao.financeai.screens.SplashScreen;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FinanceApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private SplashScreen splashScreen;

    @Override
    public void init() {
        Platform.runLater(() -> {
            splashScreen = new SplashScreen();
            splashScreen.showAnimation();
        });
        springContext = SpringApplication.run(FinanceApplication.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Thread.sleep(1000);

        FXMLLoader fxmlLoader = new FXMLLoader(
                FinanceApplication.class.getResource("login-view.fxml")
        );
        fxmlLoader.setControllerFactory(springContext::getBean);

        Scene scene = new Scene(fxmlLoader.load(), 1400, 900);
        stage.setTitle("FinanCIA- Login");
        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.setScene(scene);
        Platform.runLater(() -> {
            splashScreen.closeAnimation();
            stage.show();
        });
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }
}