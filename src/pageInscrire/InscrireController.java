package pageInscrire;

import dbUtil.dbConnection;
import dbUtil.trouveID;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pageLogin.LoginController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class InscrireController{

    InscrireModel inscrireModel = new InscrireModel();

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField passwordVerification;
    @FXML
    private Label messageErreur;
    @FXML
    private Button boutonInscrire;

    // Une seule connection à la bdd est autorisé, il faut donc la definir ici
    private final Connection conn = dbConnection.getConnection();
    // Obligatoire sinon erreur à cause de la variable conn
    public InscrireController() throws SQLException {
    }

    @FXML
    private void Inscrire(){
        try{

            // Si utilisateur existe déjà alors...
            if(this.inscrireModel.existe(this.username.getText())){

                this.messageErreur.setText("Ce nom d'utilisateur est déjà utilisé !");
            }
            // Sinon si le mot de passe renseigné est différent du champ de vérification alors...
            else if (!this.password.getText().equals(this.passwordVerification.getText())){

                this.messageErreur.setText("Veuillez saisir 2 fois le même mot de passe.");
            }
            else{

                // Ajoute le client à la base de donnée
                ajouterClient(this.username.getText(), this.password.getText());

                // Ferme la fenetre Inscrire
                Stage stage = (Stage)this.boutonInscrire.getScene().getWindow();
                stage.close();

                // Ouvre la fenetre Login
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
        }
        catch(Exception localException){
            localException.printStackTrace();
        }
    }

    private void ajouterClient(String nom, String mdp){
        String sql = "INSERT INTO login(userID, username, password, role, pointFidelite) VALUES (?,?,?,?,?)";

        try{
            PreparedStatement stmt = conn.prepareStatement(sql);

            // userID
            stmt.setInt(1, (trouveID.trouveIdUtilisateur()));
            // username
            stmt.setString(2, nom);
            //password
            stmt.setString(3, mdp);
            // role
            stmt.setString(4, "Client");
            // pointFidelite
            stmt.setInt(5, 0);

            stmt.execute();
            conn.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

}
