package org.test.concurrenciacaso1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("reserva-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Reserva concurrente de entradas");
        stage.setScene(scene);
        stage.show();
    }
}
