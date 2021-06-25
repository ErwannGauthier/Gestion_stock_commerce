package pagesClient.categories.produits;

import dbUtil.tables.Categorie;
import dbUtil.tables.Produit;
import dbUtil.tables.Utilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import pagesClient.MenuClient;

import java.util.Collection;
import java.util.HashMap;

public class ProduitController {

    @FXML
    private AnchorPane fenetre;

    @FXML
    private ImageView imageView;

    @FXML
    private Label nom;

    @FXML
    private Label marque;

    @FXML
    private Label poids;

    @FXML
    private Label prix;

    @FXML
    private Label prixKg;

    @FXML
    private Button boutonMoins;

    @FXML
    private TextField quantite;

    @FXML
    private Button boutonPlus;

    @FXML
    private Button boutonCommander;

    @FXML
    private Label description;

    @FXML
    private Label messageConfirmation;

    private Utilisateur client;
    private HashMap<Produit, Integer> panier;
    private Collection<Produit> listeProduits;
    private Produit produit;
    private Categorie categorie;

    public void initialiseDonnees(Utilisateur client, HashMap<Produit, Integer> pan, Collection<Produit> liste, Produit p, Categorie cat){

        this.client = client;
        this.panier = pan;
        this.listeProduits = liste;
        this.produit = p;
        this.categorie = cat;

        MenuClient menu = new MenuClient(this.client, this.panier, this.listeProduits);
        menu.getMenuBar().setPrefWidth(600);
        this.fenetre.getChildren().add(menu.getMenuBar());

        afficheImage();

        this.nom.setText(this.produit.getNom());
        this.marque.setText("Marque : " + this.produit.getMarque());
        if(this.produit.getPoids() == 0){
            this.poids.setText("Poids : Vendu à l'unité.");
            this.prixKg.setText("");
        }
        else{

            this.poids.setText("Poids : " + this.produit.getPoids() + "g");
            this.prixKg.setText("Prix au Kg : " + this.produit.getPrixKg() + "€");
        }

        this.prix.setText("Prix : " + this.produit.getPrix() + "€");
        this.description.setText("Description : " + this.produit.getDescription());

        // Si le texteField subit une modification alors on vérifie que cette modification est correcte.
        this.quantite.textProperty().addListener((observable, oldValue, newValue) -> verifiePasErreur());
    }

    public void afficheImage(){

        String nomCat = this.categorie.getNom();
        Image img;
        if(nomCat.equals("Viandes/Poissons")){
            nomCat = "viandes_poissons";
        }
        else if(nomCat.equals("Fruits/Legumes")){
            nomCat = "fruits_legumes";
        }

        try{
            img = new Image("/images/categories/" + nomCat + "/" + this.produit.getId() + ".jfif");
        }
        catch(Exception ex){
            img = new Image("/images/categories/notFound.jfif");
        }

        this.imageView.setImage(img);
    }

    @FXML
    private void boutonMoinsUtilise(){

        this.boutonPlus.setDisable(false);

        if(Integer.parseInt(this.quantite.getText()) > 0){
            this.quantite.setText(String.valueOf(Integer.parseInt(this.quantite.getText()) - 1));
        }
        else{
            this.boutonMoins.setDisable(true);
        }
    }

    @FXML
    private void boutonPlusUtilise(){

        this.boutonMoins.setDisable(false);

        if(this.produit.getNbStock() > Integer.parseInt(this.quantite.getText())){
            this.quantite.setText(String.valueOf(Integer.parseInt(this.quantite.getText()) + 1));
        }
        else{
            this.boutonPlus.setDisable(true);
        }
    }

    @FXML
    private void verifiePasErreur(){

        this.boutonMoins.setDisable(false);
        this.boutonPlus.setDisable(false);
        this.boutonCommander.setDisable(false);

        // Si l'utilisateur saisi autre chose qu'un nombre alors quantite passe à 0.
        try{
            Integer.parseInt(this.quantite.getText());
        }
        catch(NumberFormatException ex){
            this.quantite.setText("0");
            this.boutonMoins.setDisable(true);
        }

        // Si l'utilisateur saisi une quantité négative alors quantite passe à 0.
        if(Integer.parseInt(this.quantite.getText()) < 0){
            this.quantite.setText("0");
            this.boutonMoins.setDisable(true);
        }

        // Si l'utilisateur saisi une quantité négative ou 0 alors il ne peut pas commander.
        if(Integer.parseInt(this.quantite.getText()) <= 0){
            this.boutonCommander.setDisable(true);
        }

        // Si l'utilisateur saisi une quantité supérieur au stock dispo alors quantite = stock disponible.
        if(Integer.parseInt(this.quantite.getText()) > this.produit.getNbStock()){
            this.quantite.setText(String.valueOf(this.produit.getNbStock()));
            this.boutonPlus.setDisable(true);
        }

        // Si l'utilisateur a déjà tout le stock du produit dans le panier, il ne peut plus commander.
        if(this.panier.containsKey(this.produit) && this.panier.get(this.produit) >= this.produit.getNbStock()){
            this.boutonCommander.setDisable(true);
            this.boutonMoins.setDisable(true);
            this.boutonPlus.setDisable(true);
            this.quantite.setEditable(false);
        }
        // Sinon si l'utilisateur possède déjà ce produit dans son panier, il ne peut pas commander plus que ce qu'il y a en stock - ce qu'il y a dans son panier
        else if(this.panier.containsKey(this.produit) && this.panier.get(this.produit) + Integer.parseInt(this.quantite.getText()) > this.produit.getNbStock()){
                this.quantite.setText(String.valueOf(this.produit.getNbStock() - this.panier.get(this.produit)));
                this.boutonPlus.setDisable(true);
        }
    }

    @FXML
    private void ajouterPanier(){

        // S'il y a déjà ce produit dans le panier alors remplacer la valeur par elle même + nouvelle valeur
        if(this.panier.containsKey(this.produit)){
            this.panier.replace(this.produit, Integer.parseInt(this.quantite.getText()) + this.panier.get(this.produit));
        }
        // Sinon ajouter au panier
        else{
            this.panier.put(this.produit, Integer.parseInt(this.quantite.getText()));
        }


        if(Integer.parseInt(this.quantite.getText()) == 1){
            this.messageConfirmation.setText(this.quantite.getText() + " produit ajouté au panier.");
        }
        else{
            this.messageConfirmation.setText(this.quantite.getText() + " produits ajoutés au panier.");
        }

        this.quantite.setText("0");
    }
}
