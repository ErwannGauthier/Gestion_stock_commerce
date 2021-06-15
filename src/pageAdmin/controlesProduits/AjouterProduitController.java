package pageAdmin.controlesProduits;

import dbUtil.dbConnection;
import dbUtil.trouveID;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AjouterProduitController implements Initializable {

    @FXML
    private TextField nom;

    @FXML
    private TextField poids;

    @FXML
    private TextField marque;

    @FXML
    private TextField prix;

    @FXML
    private ComboBox<ajouterProduitOptions> combobox;

    @FXML
    private TextField prixKG;

    @FXML
    private TextField quantite;

    @FXML
    private TextField description;

    @FXML
    private Label messageErreur;

    @FXML
    private Button boutonAjouter;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.nom.setText("");
        this.poids.setText("");
        this.marque.setText("");
        this.prix.setText("");
        this.prixKG.setText("");
        this.quantite.setText("");
        this.description.setText("");
        this.combobox.setItems(FXCollections.observableArrayList(ajouterProduitOptions.values()));
    }

    @FXML
    private void ajouterProduit(){

        try{
            this.messageErreur.setText("");

            // Si un des champs marqués d'une * n'est pas renseigné alors...
            if(this.nom.getText().equals("") || this.poids.getText().equals("") || this.marque.getText().equals("") || this.prix.getText().equals("") || this.prixKG.getText().equals("") || this.quantite.getText().equals("") || this.combobox.getSelectionModel().isEmpty()){

                this.messageErreur.setText("Tout les champs marqués d'une * doivent être renseignés.");
            }
            else{

                // Doit contenir des Entiers ou des Doubles sinon Erreur ==> Traité dans le catch
                Integer.parseInt(this.poids.getText());
                Double.parseDouble(this.prix.getText());
                Double.parseDouble(this.prixKG.getText());
                Integer.parseInt(this.quantite.getText());

                // Les champs "Poids", "Quantité", "Prix" et "Prix au kg" doivent être positifs.
                if(Integer.parseInt(this.poids.getText()) < 0 || Double.parseDouble(this.prix.getText()) < 0 || Double.parseDouble(this.prixKG.getText()) < 0 || Integer.parseInt(this.quantite.getText()) < 0){
                    this.messageErreur.setText("Les champs \"Poids\", \"Quantité\", \"Prix\" et \"Prix au kg\" doivent être positifs.");
                }
                else{
                    // ajoute le produit à la BDD
                    ajouterProduitDansBDD(this.nom.getText(), this.combobox.getValue().toString(), Integer.parseInt(this.quantite.getText()), this.marque.getText(), Integer.parseInt(this.poids.getText()), Double.parseDouble(this.prix.getText()), Double.parseDouble(this.prixKG.getText()), this.description.getText());

                    // Ferme la fenetre
                    Stage stage = (Stage)this.boutonAjouter.getScene().getWindow();
                    stage.close();
                }
            }
        }
        catch (NumberFormatException exception){
            this.messageErreur.setText("Les champs \"Poids\" et  \"Quantité\" doivent uniquement contenir des chiffres. \"Prix\" et \"Prix au kg\" doivent uniquement contenir des chiffres ou des nombres à virgules.");
        }
    }

    public void ajouterProduitDansBDD(String nom, String categorie, int stock, String marque, int poids, double prix, double prixKg, String description){

        try{

            Connection conn = dbConnection.getConnection();

            if(categorie.equals("Viandes_Poissons")){
                categorie = "Viandes/Poissons";
            }
            else if(categorie.equals("Fruits_Legumes")){
                categorie = "Fruits/Legumes";
            }
            else if(categorie.equals("Surgelé")){
                categorie = "Surgele";
            }
            else if(categorie.equals("Féculents")){
                categorie = "Feculents";
            }
            else if(categorie.equals("Hygiène")){
                categorie = "Hygiene";
            }

            // Requête
            String sql = "INSERT INTO produit(produit_id, produit_nom, produit_categorie, produit_nombreStock, produit_marque, produit_poids, produit_prix, produit_prix_kg, produit_description) VALUES (?,?,?,?,?,?,?,?,?)";

            // Prépare la requête avec des valeurs vides à la place des ? en attendant qu'ils soit remplacés
            PreparedStatement stmt = conn.prepareStatement(sql);

            //produit_id
            // fait appel à la fonction trouveIdProduit avec en paramètre la catégorie séléctionnée dans le combobox
            stmt.setInt(1, trouveID.trouveIdProduit(categorie));

            //produit_nom
            stmt.setString(2, nom);

            //produit_categorie
            stmt.setString(3, categorie);

            //produit_nombreStock
            stmt.setInt(4, stock);

            //produit_marque
            stmt.setString(5, marque);

            //produit_poids
            stmt.setInt(6, poids);

            //produit_prix
            stmt.setDouble(7, prix);

            //produit_prix_kg
            stmt.setDouble(8, prixKg);

            //produit_description
            stmt.setString(9, description);

            // Execute la requete
            stmt.execute();

            // Ferme la connexion à la BDD
            conn.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }

    }
}
