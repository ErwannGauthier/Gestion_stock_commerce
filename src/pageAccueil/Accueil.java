package pageAccueil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Accueil extends Application {

    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Accueil.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Commerce.io - Accueil");
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
