package dbUtil.tables;

public class Panier {

    private final int id_panier;
    private final int id_produit;
    private final int quantite;
    private final double prix;
    private final String categorie;

    public Panier(int id_panier, int id_produit, int quantite, double prix, String categorie){

        this.id_panier = id_panier;
        this.id_produit = id_produit;
        this.quantite = quantite;
        this.prix = prix;
        this.categorie = categorie;
    }

    public int getId_panier() {
        return id_panier;
    }

    public int getId_produit() {
        return id_produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public double getPrix() {
        return prix;
    }

    public String getCategorie() {
        return categorie;
    }
}
