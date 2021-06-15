package pageAdmin.controlesProduits;

public enum ajouterProduitOptions {
    Viandes_Poissons, Fruits_Legumes, Frais, Surgelé, Féculents, Conserves, Hygiène, Boissons;

    private ajouterProduitOptions(){
    }

    public String value(){
        return name();
    }

    public static ajouterProduitOptions fromValue(String v){ return valueOf(v); }
}
