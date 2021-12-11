package me.t3sl4.textfileencoder.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import me.t3sl4.textfileencoder.Main;

import java.io.IOException;

public class MainController {
    private Scene scene;
    private Stage stage;
    private Parent root;

    public void textEncodeClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("src/main/resources/me/t3sl4/textfileencoder/text-encode-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void fileEncodeClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("file-encode-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}