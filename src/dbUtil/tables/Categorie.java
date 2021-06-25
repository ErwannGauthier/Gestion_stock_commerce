package dbUtil.tables;

public class Categorie {

    private final String nom;
    private final int borneMin;
    private final int borneMax;

    public Categorie(String nom, int borneMin, int borneMax){

        this.nom = nom;
        this.borneMin = borneMin;
        this.borneMax = borneMax;
    }

    public String getNom() {
        return nom;
    }

    public int getBorneMin() {
        return borneMin;
    }

    public int getBorneMax() {
        return borneMax;
    }
}
