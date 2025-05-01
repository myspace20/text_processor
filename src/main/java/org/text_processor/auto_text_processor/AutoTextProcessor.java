package org.text_processor.auto_text_processor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class AutoTextProcessor extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AutoTextProcessor.class.getResource("text-editor.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root); // No hardcoded size — uses FXML's preferred size

        stage.setTitle("Text Processor!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        launch();
    }
}