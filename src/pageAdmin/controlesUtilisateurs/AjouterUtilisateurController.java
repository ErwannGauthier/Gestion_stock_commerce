package pageAdmin.controlesUtilisateurs;

import dbUtil.dbConnection;
import dbUtil.trouveID;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AjouterUtilisateurController implements Initializable {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField verification;

    @FXML
    private ComboBox<ajouterUtilisateurOptions> combobox;

    @FXML
    private TextField fidelite;

    @FXML
    private Label messageErreur;

    @FXML
    private Button boutonAjouter;

    private final Connection conn = dbConnection.getConnection();
    public AjouterUtilisateurController() throws SQLException {
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.username.setText("");
        this.password.setText("");
        this.verification.setText("");
        this.fidelite.setText("");
        this.combobox.setItems(FXCollections.observableArrayList(ajouterUtilisateurOptions.values()));
    }

    @FXML
    private void ajouterUtilisateur() throws Exception {

        try{

            this.messageErreur.setText("");

            // Si un des champs marqués d'une * n'est pas renseigné alors...
            if(this.username.getText().equals("") || this.password.getText().equals("") || this.verification.getText().equals("") || this.combobox.getSelectionModel().isEmpty()){

                this.messageErreur.setText("Tout les champs marqués d'une * doivent être renseignés.");
            }
            // Sinon si l'utilisateur existe déjà alors...
            else if(existe(this.username.getText(), this.combobox.getValue().toString())){

                this.messageErreur.setText("Cet utilisateur existe déjà.");
            }
            // Sinon si le mot de passe renseigné est différent du champ de vérification alors...
            else if(!this.password.getText().equals(this.verification.getText())){

                this.messageErreur.setText("Veuillez saisir 2 fois le même mot de passe.");
            }
            else{

                // Si le champ point de fidelité n'est pas renseigné alors le remplace par 0
                if(this.fidelite.getText().equals("")){
                    this.fidelite.setText("0");
                }

                // Doit contenir un Entier sinon Erreur ==> Traité dans le catch
                Integer.parseInt(this.fidelite.getText());

                // Doit être positif.
                if(Integer.parseInt(this.fidelite.getText()) < 0){
                    this.messageErreur.setText("Les points de fidélité ne peuvent pas être négatifs.");
                }
                else{

                    // Ajoute l'utilisateur à la BDD
                    ajouterUtilisateurDansBDD(this.username.getText(), this.password.getText(), this.combobox.getValue().toString(), Integer.parseInt(this.fidelite.getText()));

                    // Ferme la fenetre
                    Stage stage = (Stage)this.boutonAjouter.getScene().getWindow();
                    stage.close();
                }
            }
        }
        catch (NumberFormatException ex){
            this.messageErreur.setText("Le champ \"fidelité\" doit être vide ou contenir uniquement des chiffres.");
        }
    }

    public void ajouterUtilisateurDansBDD(String nom, String mdp, String role, int fidelite){

        try{
            // Requête
            String sql = "INSERT INTO login(userID, username, password, role, pointFidelite) VALUES (?,?,?,?,?)";

            // Prépare la requête avec des valeurs vides à la place des ? en attendant qu'ils soit remplacés
            PreparedStatement stmt = conn.prepareStatement(sql);

            // userID
            // Fait appel à la fonction trouveIdUtilisateur
            stmt.setInt(1, trouveID.trouveIdUtilisateur());

            // username
            stmt.setString(2, nom);

            // password
            stmt.setString(3, mdp);

            // role
            stmt.setString(4, role);

            // pointFidelite
            stmt.setInt(5, fidelite);

            // Execute la requete
            stmt.execute();

            // Ferme la connexion à la BDD
            conn.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Retourne vrai si l'utilisateur passé en paramètre possède le même nom et role qu'un utilisateur dans la BDD
    public boolean existe(String user, String role) throws Exception{
        PreparedStatement pr = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM login where username = ? and role = ?";

        try{
            pr = this.conn.prepareStatement(sql);
            pr.setString(1, user);
            pr.setString(2, role);

            rs = pr.executeQuery();

            return rs.next();
        }
        catch(SQLException ex){
            return false;
        }

        finally{
            pr.close();
            rs.close();
        }
    }
}
