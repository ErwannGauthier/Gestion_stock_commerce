package pagesClient.categories;

import dbUtil.tables.Categorie;
import dbUtil.tables.Produit;
import dbUtil.tables.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pagesClient.MenuClient;
import pagesClient.categories.produits.ProduitController;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class CategorieController {

    private Utilisateur client;
    private HashMap<Produit, Integer> panier;
    private Categorie categorie;
    private Collection<Produit> listeProduits;

    @FXML
    private AnchorPane fenetre;

    public void initDonnees(Utilisateur client, HashMap<Produit, Integer> pan, Collection<Produit> liste, Categorie cat) {

        this.client = client;
        this.panier = pan;
        this.categorie = cat;

        this.listeProduits = liste;
        afficheListe();

        MenuClient menu = new MenuClient(this.client, this.panier, this.listeProduits);
        this.fenetre.getChildren().add(menu.getMenuBar());
    }

    public void afficheListe(){

        Image img;
        int posX = 20;
        int posY = 45;
        Iterator<Produit> it = this.listeProduits.iterator();
        boolean peutAfficher;

        while(it.hasNext()){
            Produit produit = it.next();
            peutAfficher = true;

            if(produit.getId() > this.categorie.getBorneMin() && produit.getId() < this.categorie.getBorneMax()) {

                // Si l'utilisateur à déjà tout le stock d'un produit dans son panier, il ne peut plus le voir
                if(this.panier.containsKey(produit) && this.panier.get(produit) >= produit.getNbStock()){
                    peutAfficher = false;
                }
                else if(produit.getNbStock() <= 0){
                    peutAfficher = false;
                }

                if(peutAfficher == true){

                    try{
                        String nomCat = this.categorie.getNom();
                        if(nomCat.equals("Viandes/Poissons")){
                            nomCat = "Viandes_Poissons";
                        }
                        else if (nomCat.equals("Fruits/Legumes")){
                            nomCat = "Fruits_Legumes";
                        }
                        img = new Image("/images/categories/" + nomCat + "/" + produit.getId() + ".jfif", 100, 100, false, false);
                    }
                    catch(Exception ex){
                        img = new Image("/images/categories/notFound.jfif", 100, 100, false, false);
                    }
                    ImageView imageView = new ImageView(img);
                    imageView.setX(posX);
                    imageView.setY(posY);

                    Button bouton = new Button("Commander");
                    bouton.setMinSize(100, 20);
                    bouton.setLayoutX(posX);
                    bouton.setLayoutY(posY + 100);
                    bouton.setOnAction(ActionEvent -> {
                        ouvrePageProduit(produit);
                    });

                    this.fenetre.getChildren().addAll(imageView, bouton);

                    posX = posX + 120;
                    if(posX > 740){
                        posX = 20;
                        posY = posY + 150;
                    }
                }
            }
        }
    }

    public void ouvrePageProduit(Produit produit){

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pagesClient/categories/produits/Produit.fxml"));

            Stage stageProduit = new Stage();
            stageProduit.setScene(new Scene(loader.load()));

            ProduitController produitController = loader.getController();

            produitController.initialiseDonnees(this.client, this.panier, this.listeProduits, produit, this.categorie);

            stageProduit.setTitle("Commerce.io - Produit");
            stageProduit.setResizable(false);
            stageProduit.show();

            fermeFenetre();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private void fermeFenetre(){
        Stage stage = (Stage) this.fenetre.getScene().getWindow();
        stage.close();
    }
}
