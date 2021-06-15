package pagesClient.panier;

import dbUtil.dbConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pageAccueil.AccueilController;
import pagesClient.ClientController;
import pagesClient.categories.CategorieController;
import pagesClient.categories.produits.Produit;
import pagesClient.compte.CompteController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class PanierController {

    @FXML
    private Pane fenetre;

    private int idClient;
    private int pointFidelite;
    private Collection<Produit> listeProduits;
    private HashMap<Produit, Integer> panier;
    private double prixTotalCommande = 0;
    private Button payer;
    private Label restePayer;
    private CheckBox payerFidel;

    public PanierController() {
    }

    public void initDonnees(int id, Collection<Produit> list, HashMap<Produit, Integer> pan) throws SQLException {

        this.idClient = id;
        this.listeProduits = list;
        this.panier = pan;
        if(this.idClient > 0){
            this.pointFidelite = recupPointFideliteClient();
        }
        else{
            this.pointFidelite = 0;
        }

        affichePanier();
    }

    public void affichePanier() {

        if(this.panier.size() > 0){

            Image img;
            int posX = 20;
            int posY = 20;
            Iterator<Produit> itProd = this.panier.keySet().iterator();
            Iterator<Integer> itNb = this.panier.values().iterator();

            while(itProd.hasNext()){

                Produit prod = itProd.next();
                Integer nbCommande = itNb.next();

                try{
                    img = new Image("/images/categories/" + retourneCategorie(prod.getCategorie()) + "/" + prod.getId() + ".jfif", 100, 100, false, false);
                }
                catch(Exception ex){
                    img = new Image("/images/categories/notFound.jfif", 100, 100, false, false);
                }

                ImageView imageView = new ImageView(img);
                imageView.setX(posX);
                imageView.setY(posY);

                Label nom = new Label(prod.getNom());
                nom.setPrefSize(400, 20);
                nom.setLayoutX(posX + 110);
                nom.setLayoutY(posY + 10);

                Label quantite = new Label("Quantité : " + nbCommande.toString());
                quantite.setPrefSize(200, 20);
                quantite.setLayoutX(posX + 110);
                quantite.setLayoutY(posY + 40);

                double prixProd = nbCommande * prod.getPrix();
                this.prixTotalCommande = this.prixTotalCommande + prixProd;
                Label prix = new Label("Prix : " + prixProd + " €");
                prix.setPrefSize(200, 20);
                prix.setLayoutX(posX + 310);
                prix.setLayoutY(posY + 40);

                Button bouton = new Button("Retirer");
                bouton.setLayoutX(posX + 430);
                bouton.setLayoutY(posY + 70);
                bouton.setOnAction(ActionEvent -> {
                    try {
                        retireProduitPanier(prod);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                });

                this.fenetre.getChildren().addAll(imageView, nom, quantite, prix, bouton);

                posY = posY + 120;
            }

            Label prixTotal = new Label("Prix total à payer : " + new DecimalFormat("##.##").format(this.prixTotalCommande) + " €");
            prixTotal.setPrefSize(200, 20);
            prixTotal.setLayoutX(posX);
            prixTotal.setLayoutY(posY + 20);

            this.payer = new Button("Payer");
            this.payer.setLayoutX(posX + 430);
            this.payer.setLayoutY(posY + 20);
            this.payer.setOnAction(ActionEvent -> payerCommande());

            this.fenetre.getChildren().addAll(prixTotal, payer);

            if(this.idClient > 0){
                this.payerFidel = new CheckBox();
                this.payerFidel.setLayoutX(posX);
                this.payerFidel.setLayoutY(posY + 70);
                this.payerFidel.setOnAction(ActionEvent -> {
                    try {
                        payerAvecFidelite();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                });

                Label pointsFidelite = new Label("Utiliser mes points de fidélité.");
                pointsFidelite.setPrefSize(200, 20);
                pointsFidelite.setLayoutX(posX + 40);
                pointsFidelite.setLayoutY(posY + 70);

                restePayer = new Label();
                restePayer.setPrefSize(200, 20);
                restePayer.setLayoutX(posX);
                restePayer.setLayoutY(posY + 90);

                this.fenetre.getChildren().addAll(payerFidel, pointsFidelite, restePayer);
            }
        }
        else{
            Label label = new Label("Votre panier est vide.");
            label.setPrefSize(575, 25);
            label.setLayoutX(0);
            label.setLayoutY(175);
            label.setAlignment(Pos.CENTER);

            this.fenetre.getChildren().add(label);
        }
    }

    public void retireProduitPanier(Produit p) throws SQLException {
        this.panier.remove(p);
        fermeFenetre();

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
    }

    public void videPanier(){
        this.panier.clear();
    }

    public String retourneCategorie(String categorie){

        if(categorie.equals("Viandes/Poissons")) {
            categorie = "viandes_poissons";
        }
        else if(categorie.equals("Fruits/Legumes")) {
            categorie = "fruits_legumes";
        }

        return categorie;
    }

    public void retireProduitStock(){

        try{
            Connection conn = dbConnection.getConnection();

            Iterator<Produit> itProd = this.panier.keySet().iterator();
            Iterator<Integer> itNb = this.panier.values().iterator();

            while(itProd.hasNext()) {

                Produit prod = itProd.next();
                Integer qttCommande = itNb.next();

                // Requête
                String sql = "UPDATE produit SET produit_nombreStock=? WHERE produit_id=?";

                // Prépare la requête avec des valeurs vides à la place des ? en attendant qu'ils soit remplacés
                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setInt(1, prod.getNbStock() - qttCommande);

                stmt.setInt(2, prod.getId());

                // execute la requete
                stmt.execute();
            }

            // Ferme la connexion à la BDD
            conn.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void ajouteFidelite(){

        try{
            Connection conn = dbConnection.getConnection();

            // Requête
            String sql = "UPDATE login SET pointFidelite=? WHERE userID=?";

            // Prépare la requête avec des valeurs vides à la place des ? en attendant qu'ils soit remplacés
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, (int) (this.pointFidelite - payerAvecFidelite() + this.prixTotalCommande/10));

            stmt.setInt(2, this.idClient);

            // execute la requete
            stmt.execute();

            // Ferme la connexion à la BDD
            conn.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void payerCommande(){

       retireProduitStock();
       if(this.idClient > 0) {
           ajouteFidelite();
       }
       videPanier();

       ouvrePrincipaleVide();

       try{

           FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/panier/Remerciement.fxml"));

           Stage stage = new Stage();
           stage.setScene(new Scene(loader.load()));

           RemerciementController remerciementController = loader.getController();

           stage.setTitle("Commerce.io - Remerciement");
           stage.setResizable(false);
           stage.show();

       } catch (IOException e) {
           e.printStackTrace();
       }
    }

    public void ouvrePrincipaleVide(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/client.fxml"));

            Stage stageClient = new Stage();
            stageClient.setScene(new Scene(loader.load()));

            ClientController clientController = loader.getController();
            // Envoie l'ID du client et le panier à la page Client
            clientController.recupID(this.idClient);

            stageClient.setTitle("Commerce.io - Accueil Client");
            stageClient.setResizable(false);
            stageClient.show();

            fermeFenetre();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public int recupPointFideliteClient() throws SQLException {
        Connection conn = dbConnection.getConnection();

        try{
            // Requête
            String sql = "SELECT pointFidelite FROM login WHERE userID = ?";

            // Prépare la requête avec des valeurs vides à la place des ? en attendant qu'ils soit remplacés
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, this.idClient);

            // execute la requete
            ResultSet rs = stmt.executeQuery();

            return rs.getInt(1);
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        finally{
            // Ferme la connexion à la BDD
            conn.close();
        }

        return 0;
    }

    public int payerAvecFidelite() throws SQLException {

        int pointUtilise = 0;

        if(this.payerFidel.isSelected()){

            if(this.prixTotalCommande >= this.pointFidelite){
                pointUtilise = this.pointFidelite;
            }
            else{
                pointUtilise = (int) (this.pointFidelite - this.prixTotalCommande);
            }
            double resteAPayer = this.prixTotalCommande - pointUtilise;
            if(resteAPayer <= 0){
                resteAPayer = 0;
            }
            this.restePayer.setText("Reste à payer : " + new DecimalFormat("##.##").format(resteAPayer) + " €");
            this.payer.setLayoutY(this.restePayer.getLayoutY());
        }
        else{
            this.restePayer.setText("");
            this.payer.setLayoutY(this.restePayer.getLayoutY() - 70);
        }

        return pointUtilise;
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
