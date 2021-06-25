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

public class ModifierProduitController implements Initializable {

    private int idProd;

    private String categorie;

    @FXML
    private TextField nomProd;

    @FXML
    private TextField marqueProd;

    @FXML
    private ComboBox<modifierProduitOptions> combobox;

    @FXML
    private TextField quantiteProd;

    @FXML
    private TextField poidsProd;

    @FXML
    private TextField prixProd;

    @FXML
    private TextField prixKgProd;

    @FXML
    private TextField descriptionProd;

    @FXML
    private Label messageErreur;

    @FXML
    private Button boutonModifier;

    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.combobox.setItems(FXCollections.observableArrayList(modifierProduitOptions.values()));
    }

    public void initialiseTextField(int id, String nomProd, String marque, String cat, int qttDispo, int poids, double prix, double prixKg, String description){

        this.idProd = id;
        this.categorie = cat;

        // Initialise les champs avec les données de l'utilisateur reçues en paramètre
        this.nomProd.setText(nomProd);
        this.marqueProd.setText(marque);
        this.quantiteProd.setText(String.valueOf(qttDispo));
        this.poidsProd.setText(String.valueOf(poids));
        this.prixProd.setText(String.valueOf(prix));
        this.prixKgProd.setText(String.valueOf(prixKg));
        this.descriptionProd.setText(description);

        // Sélectionne le premier élément du ComboBox.
        this.combobox.getSelectionModel().selectFirst();

        // Modifie categorie dans le cas de figure où il y a un "/" dans le nom de la catégorie.
        //   Sinon la boucle while ne fonctionnera pas pour ces catégories.
        if(this.categorie.equals("Viandes/Poissons")){
            this.categorie = "Viandes_Poissons";
        }
        else if(this.categorie.equals("Fruits/Legumes")){
            this.categorie = "Fruits_Legumes";
        }
        else if(this.categorie.equals("Surgele")){
            this.categorie = "Surgelé";
        }
        else if(this.categorie.equals("Feculents")){
            this.categorie = "Féculents";
        }
        else if(this.categorie.equals("Hygiene")){
            this.categorie = "Hygiène";
        }

        // Permet de séléctionner la catégorie du produit séléctionné dans le ComboBox.
        while(!this.combobox.getValue().toString().equals(this.categorie)){
            this.combobox.getSelectionModel().selectNext();
        }
    }

    @FXML
    private void modifierProduit(){

        try{
            this.messageErreur.setText("");

            // Si un des champs marqués d'une * n'est pas renseigné alors...
            if(this.nomProd.getText().equals("") || this.poidsProd.getText().equals("") || this.marqueProd.getText().equals("") || this.prixProd.getText().equals("") || this.prixKgProd.getText().equals("") || this.quantiteProd.getText().equals("") || this.combobox.getSelectionModel().isEmpty()){

                this.messageErreur.setText("Tout les champs marqués d'une * doivent être renseignés.");
            }
            else{

                // Doit contenir des Entiers ou des Doubles sinon Erreur ==> Traité dans le catch
                Integer.parseInt(this.poidsProd.getText());
                Double.parseDouble(this.prixProd.getText());
                Double.parseDouble(this.prixKgProd.getText());
                Integer.parseInt(this.quantiteProd.getText());

                // Les champs "Poids", "Quantité", "Prix" et "Prix au kg" doivent être positifs.
                if(Integer.parseInt(this.poidsProd.getText()) < 0 || Double.parseDouble(this.prixProd.getText()) < 0 || Double.parseDouble(this.prixKgProd.getText()) < 0 || Integer.parseInt(this.quantiteProd.getText()) < 0){
                    this.messageErreur.setText("Les champs \"Poids\", \"Quantité\", \"Prix\" et \"Prix au kg\" doivent être positifs.");
                }
                else{
                    // ajoute le produit à la BDD
                    modifierProduitDansBDD(this.nomProd.getText(), this.combobox.getValue().toString(), Integer.parseInt(this.quantiteProd.getText()), this.marqueProd.getText(), Integer.parseInt(this.poidsProd.getText()), Double.parseDouble(this.prixProd.getText()), Double.parseDouble(this.prixKgProd.getText()), this.descriptionProd.getText());

                    // Ferme la fenetre
                    Stage stage = (Stage)this.boutonModifier.getScene().getWindow();
                    stage.close();
                }

            }
        }
        catch (NumberFormatException exception){
            this.messageErreur.setText("Les champs \"Poids\" et  \"Quantité\" doivent uniquement contenir des chiffres. \"Prix\" et \"Prix au kg\" doivent uniquement contenir des chiffres ou des nombres à virgules.");
        }
    }

    private void modifierProduitDansBDD(String nom, String cat, int stock, String marque, int poids, double prix, double prixKg, String description){

        try{
            Connection conn = dbConnection.getConnection();

            if(this.categorie.equals("Viandes_Poissons")){
                this.categorie = "Viandes/Poissons";
            }
            else if(this.categorie.equals("Fruits_Legumes")){
                this.categorie = "Fruits/Legumes";
            }
            else if(this.categorie.equals("Surgelé")){
                this.categorie = "Surgele";
            }
            else if(this.categorie.equals("Féculents")){
                this.categorie = "Feculents";
            }
            else if(this.categorie.equals("Hygiène")){
                this.categorie = "Hygiene";
            }

            if(cat.equals("Viandes_Poissons")){
                cat = "Viandes/Poissons";
            }
            else if(cat.equals("Fruits_Legumes")){
                cat = "Fruits/Legumes";
            }
            else if(cat.equals("Surgelé")){
                cat = "Surgele";
            }
            else if(cat.equals("Féculents")){
                cat = "Feculents";
            }
            else if(cat.equals("Hygiène")){
                cat = "Hygiene";
            }

            // Requête
            String sql = "UPDATE produit SET produit_id=?, produit_nom=?, produit_categorie=?, produit_nombreStock=?, produit_marque=?, produit_poids=?, produit_prix=?, produit_prix_kg=?, produit_description=? WHERE produit_id=?";

            // Prépare la requête avec des valeurs vides à la place des ? en attendant qu'ils soit remplacés
            PreparedStatement stmt = conn.prepareStatement(sql);

            //produit_id
            // Si la catégorie a été modifié alors on modifie l'ID du produit
            //      pour qu'il soit cohérant avec la convention des id des catégories de produits établie.
            if(!this.categorie.equals(cat)){

                // fait appel à la fonction trouveIdProduit avec en paramètre la catégorie séléctionnée dans le combobox
                stmt.setInt(1, trouveID.trouveIdProduit(cat));
            }
            // Sinon l'ID ne change pas.
            else{
                stmt.setInt(1, this.idProd);
            }


            //produit_nom
            stmt.setString(2, nom);

            //produit_categorie
            stmt.setString(3, cat);

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

            // Condition Where --> Précise sur quel produit effectuer la modification.
            stmt.setInt(10, this.idProd);

            // execute la requete
            stmt.execute();

            // Ferme la connexion à la BDD
            conn.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
