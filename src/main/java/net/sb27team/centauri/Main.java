package net.sb27team.centauri;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sb27team.centauri.resource.ResourceManager;

public class Main extends Application {


    public void start(Stage stage) throws Exception {
        ResourceManager.loadResources();

        Parent root = FXMLLoader.load(Main.class.getResource("/gui/mainGui.fxml"));

        Scene scene = new Scene(root, 1000, 500);

        stage.setTitle("Centauri");
        stage.setScene(scene);
        stage.getIcons().add(ResourceManager.CENTAURI_ICON);
        stage.show();
    }

    public static void main(String args[]) {
        launch(args);
    }
}
