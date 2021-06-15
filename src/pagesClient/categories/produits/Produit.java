package pagesClient.categories.produits;

public class Produit {

    private final int id;
    private final String nom;
    private final String categorie;
    private final int nbStock;
    private final String marque;
    private final int poids;
    private final double prix;
    private final double prixKg;
    private final String description;

    public Produit(int id, String nom, String cat, int stock, String marq, int poids, double prix, double prixKg, String desc){

        this.id = id;
        this.nom = nom;
        this.categorie = cat;
        this.nbStock = stock;
        this.marque = marq;
        this.poids = poids;
        this.prix = prix;
        this.prixKg = prixKg;
        this.description = desc;
    }

    public String toString(){
        return this.id + " " + this.nom + " " + this.categorie + " " + this.nbStock;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public int getNbStock() {
        return nbStock;
    }

    public String getMarque() {
        return marque;
    }

    public int getPoids() {
        return poids;
    }

    public double getPrix() {
        return prix;
    }

    public double getPrixKg() {
        return prixKg;
    }

    public String getDescription() {
        return description;
    }
}
