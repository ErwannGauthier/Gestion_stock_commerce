package pageLogin;

import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import pageAdmin.AdminController;
import pagesClient.ClientController;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    LoginModel loginModel = new LoginModel();

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private ComboBox<option> combobox;
    @FXML
    private Label messageErreur;
    @FXML
    private Button boutonConnecter;


    public void initialize(URL url, ResourceBundle rb){
        this.combobox.setItems(FXCollections.observableArrayList(option.values()));
    }

    @FXML
    private void Login(){
        try{
            // Si l'utilisateur est dans la BDD alors...
            if(this.loginModel.isLogin(this.username.getText(), this.password.getText(), this.combobox.getValue().toString())){
                Stage stage = (Stage)this.boutonConnecter.getScene().getWindow();
                stage.close();

                // En fonction du role renseigné ouvre la fenetre en conséquence...
                switch(this.combobox.getValue().toString()){
                    case "Admin":
                        adminLogin();
                        break;
                    case "Client":
                        clientLogin();
                        break;
                }
            }
            else{
                this.messageErreur.setText("Mauvais nom d'utilisateur ou mot de passe !");
            }
        }
        catch(Exception localException){
            localException.printStackTrace();
        }
    }

    // Ouvre la fenetre Client
    public void clientLogin(){

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/client.fxml"));

            Stage stageClient = new Stage();
            stageClient.setScene(new Scene(loader.load()));

            ClientController clientController = loader.getController();
            // Envoie l'ID du client à la page Client
            clientController.recupID(getIdUtilisateur());

            stageClient.setTitle("Commerce.io - Accueil Client");
            stageClient.setResizable(false);
            stageClient.show();
        }
        catch(IOException | SQLException ex){
            ex.printStackTrace();
        }
    }

    // Ouvre la fenetre Admin
    public void adminLogin(){
        try{
            Stage adminStage = new Stage();
            FXMLLoader adminLoader = new FXMLLoader();
            Pane adminRoot = adminLoader.load(getClass().getResource("/pageAdmin/Admin.fxml").openStream());

            AdminController adminController = adminLoader.getController();

            Scene scene = new Scene(adminRoot);
            adminStage.setScene(scene);
            adminStage.setTitle("Commerce.io - Page Admin");
            adminStage.setResizable(false);
            adminStage.show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    // Retourne l'ID de l'utilisateur qui se connecte.
    public int getIdUtilisateur() throws SQLException {

        Connection conn = dbConnection.getConnection();
        PreparedStatement pr = null;
        ResultSet rs = null;

        try{
            pr = conn.prepareStatement("SELECT userID FROM login where username = ? and role = ?");
            pr.setString(1, this.username.getText());
            pr.setString(2, this.combobox.getValue().toString());

            rs = pr.executeQuery();

            return rs.getInt(1);
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }

        finally{
            conn.close();
            pr.close();
            rs.close();
        }

        // En cas d'erreur.
        return -1;
    }
}
