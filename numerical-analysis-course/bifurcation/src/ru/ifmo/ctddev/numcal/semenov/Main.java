package ru.ifmo.ctddev.numcal.semenov;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("convergence.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Newton's method");
        primaryStage.setScene(new Scene(root/*, 600, 600*/));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
