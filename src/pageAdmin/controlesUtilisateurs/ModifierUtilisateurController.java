package pageAdmin.controlesUtilisateurs;

import dbUtil.dbConnection;
import javafx.collections.FXCollections;
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

public class ModifierUtilisateurController implements Initializable {

    private int idUser;
    private String nomUser;

    @FXML
    private TextField username;

    @FXML
    private TextField mdp;

    @FXML
    private TextField verifMDP;

    @FXML
    private ComboBox<modifierUtilisateurOptions> combobox;

    @FXML
    private TextField pointFidelite;

    @FXML
    private Label messageErreur;

    @FXML
    private Button boutonModifier;

    private final Connection conn = dbConnection.getConnection();
    public ModifierUtilisateurController() throws SQLException {
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.combobox.setItems(FXCollections.observableArrayList(modifierUtilisateurOptions.values()));
    }

    public void initialiseTextField(int id, String nom, String motdp, String role, int points){

        this.idUser = id;
        this.nomUser = nom;
        this.username.setText(nom);
        this.mdp.setText(motdp);
        this.verifMDP.setText(motdp);
        this.pointFidelite.setText(String.valueOf(points));

        // Sélectionne le premier élément du ComboBox.
        this.combobox.getSelectionModel().selectFirst();

        // Permet de séléctionner la catégorie du produit séléctionné dans le ComboBox.
        while(!this.combobox.getValue().toString().equals(role)){
            this.combobox.getSelectionModel().selectNext();
        }
    }

    @FXML
    private void modifierUtilisateur() throws Exception {

        try{

            this.messageErreur.setText("");

            // Si un des champs marqués d'une * n'est pas renseigné alors...
            if(this.username.getText().equals("") || this.mdp.getText().equals("") || this.verifMDP.getText().equals("") || this.combobox.getSelectionModel().isEmpty()){

                this.messageErreur.setText("Tout les champs marqués d'une * doivent être renseignés.");
            }
            // Sinon si le nom d'utilisateur à été modifié et que ce nom d'utilisateur existe déjà alors...
            else if(!this.username.getText().equals(this.nomUser) && existe(this.username.getText(), this.combobox.getValue().toString())){

                this.messageErreur.setText("Cet utilisateur existe déjà.");
            }
            // Sinon si le mot de passe renseigné est différent du champ de vérification alors...
            else if(!this.mdp.getText().equals(this.verifMDP.getText())){

                this.messageErreur.setText("Veuillez saisir 2 fois le même mot de passe.");
            }
            else{

                // Si le champ point de fidelité n'est pas renseigné alors le remplace par 0
                if(this.pointFidelite.getText().equals("")){
                    this.pointFidelite.setText("0");
                }

                // Doit contenir un Entier sinon Erreur ==> Traité dans le catch
                Integer.parseInt(this.pointFidelite.getText());

                // Doit être positif.
                if(Integer.parseInt(this.pointFidelite.getText()) < 0){
                    this.messageErreur.setText("Les points de fidélité ne peuvent pas être négatifs.");
                }
                else{
                    // Ajoute l'utilisateur à la BDD
                    modifierUtilisateurDansBDD(this.username.getText(), this.mdp.getText(), this.combobox.getValue().toString(), Integer.parseInt(this.pointFidelite.getText()));

                    // Ferme la fenetre
                    Stage stage = (Stage)this.boutonModifier.getScene().getWindow();
                    stage.close();
                }

            }
        }
        catch (NumberFormatException ex){
            this.messageErreur.setText("Le champ \"fidelité\" doit être vide ou contenir uniquement des chiffres.");
        }
    }

    public void modifierUtilisateurDansBDD(String nom, String mdp, String role, int fidelite){

        try{
            // Requête
            String sql = "UPDATE login SET username=?, password=?, role=?, pointFidelite=? WHERE userID=?";

            // Prépare la requête avec des valeurs vides à la place des ? en attendant qu'ils soit remplacés
            PreparedStatement stmt = conn.prepareStatement(sql);

            // username
            stmt.setString(1, nom);

            // password
            stmt.setString(2, mdp);

            // role
            stmt.setString(3, role);

            // pointFidelite
            stmt.setInt(4, fidelite);

            // Condition Where --> Précise sur quel utilisateur effectuer la modification (identifié par son ID).
            stmt.setInt(5, this.idUser);

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
