package pagesClient.categories.produits;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pageAccueil.AccueilController;
import pagesClient.ClientController;
import pagesClient.categories.CategorieController;
import pagesClient.compte.CompteController;
import pagesClient.panier.PanierController;

import java.io.IOException;
import java.sql.SQLException;
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

    private int idClient;
    private HashMap<Produit, Integer> panier;
    private Collection<Produit> listeProduits;
    private Produit produit;
    private String nomCategorie;

    public void initialiseDonnees(int id, HashMap<Produit, Integer> pan, Collection<Produit> liste, Produit p, String nomCat){

        this.idClient = id;
        this.panier = pan;
        this.listeProduits = liste;
        this.produit = p;
        this.nomCategorie = nomCat;

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

        String nomCat = this.nomCategorie;
        Image img;
        if(nomCat.equals("Viandes et Poissons")){
            nomCat = "viandes_poissons";
        }
        else if(nomCat.equals("Fruits et Legumes")){
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

    // ------- FONCTIONS POUR MENU -------
    // MON COMPTE
    @FXML
    private void consulterCompte(){

        if(this.idClient > 0){
            try{

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/compte/Compte.fxml"));

                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));

                CompteController compteController = loader.getController();
                // Envoie l'ID du client dans le CompteController
                compteController.recupID(this.idClient);
                compteController.afficheDonnees();

                stage.setTitle("Commerce.io - Mon compte");
                stage.setResizable(false);
                stage.show();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deconnexion(){

        try{

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pageAccueil/Accueil.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            AccueilController accueilController = loader.getController();

            stage.setTitle("Commerce.io - Accueil");
            stage.setResizable(false);
            stage.show();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        fermeFenetre();
    }

    // MON PANIER
    @FXML
    private void consulterPanier(){
        try{

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/panier/Panier.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            PanierController panierController = loader.getController();
            panierController.initDonnees(this.idClient, this.listeProduits, this.panier);

            stage.setTitle("Commerce.io - Panier");
            stage.setResizable(false);
            stage.show();
        }
        catch(IOException | SQLException e){
            e.printStackTrace();
        }

        fermeFenetre();
    }

    // CATEGORIES
    private static final int borneMinViandesPoissons = 10000;
    private static final int borneMaxViandesPoissons = 20000;
    private static final int borneMinFruitsLegumes = 20000;
    private static final int borneMaxFruitsLegumes = 30000;
    private static final int borneMinFrais = 30000;
    private static final int borneMaxFrais = 40000;
    private static final int borneMinSurgele = 40000;
    private static final int borneMaxSurgele = 50000;
    private static final int borneMinFeculents = 50000;
    private static final int borneMaxFeculents = 60000;
    private static final int borneMinConserves = 60000;
    private static final int borneMaxConserves = 70000;
    private static final int borneMinHygiene = 70000;
    private static final int borneMaxHygiene = 80000;
    private static final int borneMinBoissons = 80000;
    private static final int borneMaxBoissons = 90000;

    @FXML
    public void ouvrePrincipale(){

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/client.fxml"));

            Stage stageClient = new Stage();
            stageClient.setScene(new Scene(loader.load()));

            ClientController clientController = loader.getController();
            // Envoie l'ID du client et le panier à la page Client
            clientController.recupDonnees(this.idClient, this.panier, this.listeProduits);

            stageClient.setTitle("Commerce.io - Accueil Client");
            stageClient.setResizable(false);
            stageClient.show();

            fermeFenetre();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    public void ouvreViandesPoissons(){
        fenetreGlobale("Viandes et Poissons", borneMinViandesPoissons, borneMaxViandesPoissons);
        fermeFenetre();
    }

    @FXML
    public void ouvreFruitsLegumes(){
        fenetreGlobale("Fruits et Legumes", borneMinFruitsLegumes, borneMaxFruitsLegumes);
        fermeFenetre();
    }

    @FXML
    public void ouvreFrais(){
        fenetreGlobale("Frais", borneMinFrais, borneMaxFrais);
        fermeFenetre();
    }

    @FXML
    public void ouvreSurgele(){
        fenetreGlobale("Surgele", borneMinSurgele, borneMaxSurgele);
        fermeFenetre();
    }

    @FXML
    public void ouvreFeculents(){
        fenetreGlobale("Feculents", borneMinFeculents, borneMaxFeculents);
        fermeFenetre();
    }

    @FXML
    public void ouvreConserves(){
        fenetreGlobale("Conserves", borneMinConserves, borneMaxConserves);
        fermeFenetre();
    }

    @FXML
    public void ouvreHygiene(){
        fenetreGlobale("Hygiene", borneMinHygiene, borneMaxHygiene);
        fermeFenetre();
    }

    @FXML
    public void ouvreBoissons(){
        fenetreGlobale("Boissons", borneMinBoissons, borneMaxBoissons);
        fermeFenetre();
    }

    public void fermeFenetre(){
        Stage stage = (Stage) this.fenetre.getScene().getWindow();
        stage.close();
    }

    public void fenetreGlobale(String nomCat, int borneMin, int borneMax){
        try{

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/categories/Categorie.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            CategorieController categorieController = loader.getController();
            // Envoie des données permettant d'identifier la catégorie dans CategorieController
            categorieController.initDonnees(this.idClient, this.panier, this.listeProduits, nomCat, borneMin, borneMax);

            stage.setTitle("Commerce.io - Catégorie " + nomCat);
            stage.setResizable(false);
            stage.show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
