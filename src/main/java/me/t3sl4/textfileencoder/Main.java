package me.t3sl4.textfileencoder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import me.t3sl4.textfileencoder.Controllers.TextEncodeController;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("text-encode-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1120, 480);
        stage.getIcons().add(new Image("https://i.imgur.com/puD6ReJ.png"));
        stage.setTitle("TFEncoder");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}