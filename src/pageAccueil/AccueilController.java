package pageAccueil;

import dbUtil.tables.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pageInscrire.InscrireController;
import pageLogin.LoginController;
import pagesClient.ClientController;

import java.io.IOException;
import java.sql.SQLException;

public class AccueilController {

    @FXML
    private Button boutonConnecter;

    @FXML
    private void seConnecter(){
        try{
            // Ferme la fenetre Accueil.
            fermerFenetre();

            Stage loginStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Pane root = loader.load(getClass().getResource("/pageLogin/Login.fxml").openStream());

            LoginController loginController = loader.getController();

            Scene scene = new Scene(root);
            loginStage.setScene(scene);
            loginStage.setTitle("Commerce.io - Se connecter");
            loginStage.setResizable(false);
            loginStage.show();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void sInscrire(){
        try{
            // Ferme la fenetre Accueil.
            fermerFenetre();

            Stage inscrireStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Pane root = loader.load(getClass().getResource("/pageInscrire/Inscrire.fxml").openStream());

            InscrireController inscrireController = loader.getController();

            Scene scene = new Scene(root);
            inscrireStage.setScene(scene);
            inscrireStage.setTitle("Commerce.io - S'inscrire");
            inscrireStage.setResizable(false);
            inscrireStage.show();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void continuer(){

        try{
            // Ferme la fenetre Accueil.
            fermerFenetre();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/client.fxml"));

            Stage stageClient = new Stage();
            stageClient.setScene(new Scene(loader.load()));

            ClientController clientController = loader.getController();
            // Envoie l'ID du client à la page Client
            clientController.init(new Utilisateur(0, "Compte invité", "0", "Client", 0));

            stageClient.setTitle("Commerce.io - Accueil Client");
            stageClient.setResizable(false);
            stageClient.show();
        }
        catch(IOException | SQLException ex){
            ex.printStackTrace();
        }
    }

    public void fermerFenetre(){
        // Ferme la fenetre Accueil.
        Stage stage = (Stage)this.boutonConnecter.getScene().getWindow();
        stage.close();
    }
}
