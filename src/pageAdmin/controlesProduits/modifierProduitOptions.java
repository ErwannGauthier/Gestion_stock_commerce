package pageAdmin.controlesProduits;

public enum modifierProduitOptions {

    Viandes_Poissons, Fruits_Legumes, Frais, Surgelé, Féculents, Conserves, Hygiène, Boissons;

    private modifierProduitOptions(){
    }

    public String value(){
        return name();
    }

    public static modifierProduitOptions fromValue(String v){ return valueOf(v); }
}
