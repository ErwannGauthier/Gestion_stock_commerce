package pagesClient;

import dbUtil.dbConnection;
import dbUtil.tables.Categorie;
import dbUtil.tables.Produit;
import dbUtil.tables.Utilisateur;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import pageAccueil.AccueilController;
import pagesClient.categories.CategorieController;
import pagesClient.compte.CompteController;
import pagesClient.panier.PanierController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;

public class MenuClient {

    private final Utilisateur client;
    private final HashMap<Produit, Integer> panier;
    private final Collection<Produit> listeProduits;
    private final Menu compte;
    private final Menu menuPanier;
    private final Menu categories;
    private final MenuBar menu;

    public MenuClient(Utilisateur client, HashMap<Produit, Integer> panier, Collection<Produit> liste){

        this.client = client;
        this.panier = panier;
        this.listeProduits = liste;

        this.compte = new Menu("Mon compte");
        initCompte();

        this.menuPanier = new Menu("Mon panier");
        initPanier();

        this.categories = new Menu("Catégories");
        initCategories();

        this.menu = new MenuBar(this.compte, this.menuPanier, this.categories);
        this.menu.setLayoutX(0);
        this.menu.setLayoutY(0);
        this.menu.setPrefSize(850, 25);
    }

    public MenuBar getMenuBar(){
        return this.menu;
    }

    private void initCompte(){

        if(this.client.getId() > 0){
            MenuItem donnees = new MenuItem("Mes données");
            donnees.setOnAction(ActionEvent -> ouvreFenetreCompte());
            this.compte.getItems().add(donnees);
        }

        MenuItem deconnexion = new MenuItem("Me déconnecter");
        deconnexion.setOnAction(ActionEvent -> {
            ouvreFenetreLogin();
            fermerFenetre();
        });
        this.compte.getItems().add(deconnexion);
    }

    private void initPanier(){

        MenuItem consulterPanier = new MenuItem("Consulter");
        consulterPanier.setOnAction(ActionEvent -> {
            ouvreFenetrePanier();
            fermerFenetre();
        });
        this.menuPanier.getItems().add(consulterPanier);
    }

    private void initCategories(){

        // MenuItem fenetre principale des catégories
        MenuItem menuCategorie = new MenuItem("Menu Principal");
        menuCategorie.setOnAction(ActionEvent -> {
            ouvreFenetrePrincipale();
            fermerFenetre();
        });
        this.categories.getItems().add(menuCategorie);

        // Liste des catégories --> Pour chacune faire un menuItem
        try{
            Connection conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM categorie");

            while(rs.next()){
                creerMenuItemCategorieAuto(rs.getString(1), rs.getInt(2), rs.getInt(3));
            }

            rs.close();
            conn.close();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void fermerFenetre(){
        Stage stage = (Stage) this.menu.getScene().getWindow();
        stage.close();
    }

    // Fonction pour MenuItem donnees de this.compte
    private void ouvreFenetreCompte(){
        try{

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/compte/Compte.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            CompteController compteController = loader.getController();
            // Envoie le client dans le CompteController
            compteController.init(this.client);
            compteController.afficheDonnees();

            stage.setTitle("Commerce.io - Mon compte");
            stage.setResizable(false);
            stage.show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    // Fonction pour MenuItem deconnexion de this.compte
    private void ouvreFenetreLogin(){

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
    }

    // Fonction pour MenuItem consulterPanier de this.menuPanier
    private void ouvreFenetrePanier(){

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

        fermerFenetre();
    }

    // Fonction pour MenuItem menuCategorie de this.categorie
    private void ouvreFenetrePrincipale(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/client.fxml"));

            Stage stageClient = new Stage();
            stageClient.setScene(new Scene(loader.load()));

            ClientController clientController = loader.getController();
            // Envoie le client et son panier à la page Client
            clientController.recupDonnees(this.client, this.panier, this.listeProduits);

            stageClient.setTitle("Commerce.io - Accueil Client");
            stageClient.setResizable(false);
            stageClient.show();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    // Fonction pour creer les MenuItems de this.categorie
    private void creerMenuItemCategorieAuto(String nom, int borneMin, int borneMax){

        Categorie categorie = new Categorie(nom, borneMin, borneMax);
        MenuItem catego = new MenuItem(categorie.getNom());
        catego.setOnAction(ActionEvent -> {
            ouvreFenetreCategorie(categorie);
            fermerFenetre();
        });

        this.categories.getItems().add(catego);
    }

    // Fonction pour creerMenuItemCategorieAuto() de this.categorie
    private void ouvreFenetreCategorie(Categorie categorie){
        try{

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/categories/Categorie.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            CategorieController categorieController = loader.getController();
            // Envoie les données éssentielles et la catégorie séléctionnée
            categorieController.initDonnees(this.client, this.panier, this.listeProduits, categorie);

            stage.setTitle("Commerce.io - Catégorie " + categorie.getNom());
            stage.setResizable(false);
            stage.show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}

