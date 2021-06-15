package pageAdmin;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DonneesProduits {

    private final Integer produit_id;
    private final StringProperty produit_nom;
    private final StringProperty produit_categorie;
    private final StringProperty produit_marque;
    private final Integer produit_poids;
    private final Double produit_prix;
    private final Double produit_prix_kg;
    private final Integer produit_nombreStock;
    private final StringProperty produit_description;

    public DonneesProduits(int id, String nom, String categorie, int nbStock, String marque, int poids, double prix, double prixKg, String description){

        this.produit_id = id;
        this.produit_nom = new SimpleStringProperty(nom);
        this.produit_categorie = new SimpleStringProperty(categorie);
        this.produit_marque = new SimpleStringProperty(marque);
        this.produit_poids = poids;
        this.produit_prix = prix;
        this.produit_prix_kg = prixKg;
        this.produit_nombreStock = nbStock;
        this.produit_description = new SimpleStringProperty(description);
    }

    public Integer getProduit_id() {
        return produit_id;
    }

    public String getProduit_nom() {
        return produit_nom.get();
    }

    public StringProperty produit_nomProperty() {
        return produit_nom;
    }

    public void setProduit_nom(String produit_nom) {
        this.produit_nom.set(produit_nom);
    }

    public String getProduit_categorie() {
        return produit_categorie.get();
    }

    public StringProperty produit_categorieProperty() {
        return produit_categorie;
    }

    public void setProduit_categorie(String produit_categorie) {
        this.produit_categorie.set(produit_categorie);
    }

    public String getProduit_marque() {
        return produit_marque.get();
    }

    public StringProperty produit_marqueProperty() {
        return produit_marque;
    }

    public void setProduit_marque(String produit_marque) {
        this.produit_marque.set(produit_marque);
    }

    public Integer getProduit_poids() {
        return produit_poids;
    }

    public Double getProduit_prix() {
        return produit_prix;
    }

    public Double getProduit_prix_kg() {
        return produit_prix_kg;
    }

    public Integer getProduit_nombreStock() {
        return produit_nombreStock;
    }

    public String getProduit_description() {
        return produit_description.get();
    }

    public StringProperty produit_descriptionProperty() {
        return produit_description;
    }

    public void setProduit_description(String produit_description) {
        this.produit_description.set(produit_description);
    }
}
