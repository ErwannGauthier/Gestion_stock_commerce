package pagesClient.panier;

import dbUtil.dbConnection;
import dbUtil.tables.Produit;
import dbUtil.tables.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pagesClient.ClientController;
import pagesClient.MenuClient;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class PanierController {

    @FXML
    private Pane fenetre;
    @FXML
    private AnchorPane fenetre1;

    private Utilisateur client;
    private int pointFidelite;
    private Collection<Produit> listeProduits;
    private HashMap<Produit, Integer> panier;
    private double prixTotalCommande = 0;
    private Button payer;
    private Label restePayer;
    private CheckBox payerFidel;

    public PanierController() {
    }

    public void initDonnees(Utilisateur client, Collection<Produit> list, HashMap<Produit, Integer> pan) throws SQLException {

        this.client = client;
        this.listeProduits = list;
        this.panier = pan;
        this.pointFidelite = client.getPointFidelite();

        MenuClient menu = new MenuClient(this.client, this.panier, this.listeProduits);
        menu.getMenuBar().setPrefWidth(600);
        this.fenetre1.getChildren().add(menu.getMenuBar());

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

            if(this.client.getId() > 0){
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
            panierController.initDonnees(this.client, this.listeProduits, this.panier);

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

            stmt.setInt(2, this.client.getId());

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
       if(this.client.getId() > 0) {
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
            clientController.init(this.client);

            stageClient.setTitle("Commerce.io - Accueil Client");
            stageClient.setResizable(false);
            stageClient.show();

            fermeFenetre();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
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

    private void fermeFenetre(){
        Stage stage = (Stage) this.fenetre.getScene().getWindow();
        stage.close();
    }
}
