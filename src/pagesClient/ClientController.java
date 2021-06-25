package pagesClient;

import dbUtil.dbConnection;
import dbUtil.tables.Categorie;
import dbUtil.tables.Produit;
import dbUtil.tables.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pagesClient.categories.CategorieController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class ClientController {

    private Utilisateur client;
    private HashMap<Produit, Integer> panier;
    private Collection<Produit> listeProduits;
    private Collection<Categorie> listeCategories;

    @FXML
    private AnchorPane fenetre;

    public void init(Utilisateur client){

        this.client = client;
        this.panier = new HashMap<>();
        this.listeProduits = new ArrayList<>();
        initialiseListeProduits();

        this.listeCategories = new ArrayList<>();
        initListeCategories();

        MenuClient menu = new MenuClient(this.client, this.panier, this.listeProduits);
        this.fenetre.getChildren().add(menu.getMenuBar());
    }

    public void recupDonnees(Utilisateur client, HashMap<Produit, Integer> panier, Collection<Produit> liste){

        this.client = client;
        this.panier = panier;
        this.listeProduits = liste;

        this.listeCategories = new ArrayList<>();
        initListeCategories();

        MenuClient menu = new MenuClient(this.client, this.panier, this.listeProduits);
        this.fenetre.getChildren().add(menu.getMenuBar());
    }

    private void initialiseListeProduits(){

        try{

            Connection conn = dbConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM produit");

            while(rs.next()){

                if(rs.getInt(4) > 0){

                    listeProduits.add(new Produit(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5), rs.getInt(6), rs.getDouble(7), rs.getDouble(8), rs.getString(9)));
                }
            }

            rs.close();
            conn.close();
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    private void initListeCategories(){

        try{

            Connection conn = dbConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM categorie");

            while(rs.next()){
                listeCategories.add(new Categorie(rs.getString(1), rs.getInt(2), rs.getInt(3)));
            }


            rs.close();
            conn.close();
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    private Categorie getCategorieByIndex(int index){
        int i = -1;
        Iterator<Categorie> it = this.listeCategories.iterator();
        Categorie categorie = new Categorie("", 0, 0);

        while(i != index && it.hasNext()){
            categorie = it.next();
            i++;
        }

        return categorie;
    }

    @FXML
    public void ouvreViandesPoissons(){
        fenetreGlobale(getCategorieByIndex(0));
        fermeFenetre();
    }

    @FXML
    public void ouvreFruitsLegumes(){
        fenetreGlobale(getCategorieByIndex(1));
        fermeFenetre();
    }

    @FXML
    public void ouvreFrais(){
        fenetreGlobale(getCategorieByIndex(2));
        fermeFenetre();
    }

    @FXML
    public void ouvreSurgele(){
        fenetreGlobale(getCategorieByIndex(3));
        fermeFenetre();
    }

    @FXML
    public void ouvreFeculents(){
        fenetreGlobale(getCategorieByIndex(4));
        fermeFenetre();
    }

    @FXML
    public void ouvreConserves(){
        fenetreGlobale(getCategorieByIndex(5));
        fermeFenetre();
    }

    @FXML
    public void ouvreHygiene(){
        fenetreGlobale(getCategorieByIndex(6));
        fermeFenetre();
    }

    @FXML
    public void ouvreBoissons(){
        fenetreGlobale(getCategorieByIndex(7));
        fermeFenetre();
    }

    public void fermeFenetre(){
        Stage stage = (Stage) this.fenetre.getScene().getWindow();
        stage.close();
    }

    public void fenetreGlobale(Categorie categorie){
        try{

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/categories/Categorie.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            CategorieController categorieController = loader.getController();
            // Envoie des données permettant d'identifier la catégorie dans CategorieController
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