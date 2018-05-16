package net.sb27team.centauri;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {


    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/gui/mainGui.fxml"));

        Scene scene = new Scene(root, 1000, 500);

        stage.setTitle("Centauri");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/centauri.png")));
        stage.show();
    }

    public static void main(String args[]) {
        launch(args);
    }
}
