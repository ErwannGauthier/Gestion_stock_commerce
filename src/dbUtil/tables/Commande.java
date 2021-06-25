package dbUtil.tables;

public class Commande {

    private final int id_user;
    private final int id_panier;

    public Commande(int id_user, int id_panier){

        this.id_user = id_user;
        this.id_panier = id_panier;
    }

    public int getId_user() {
        return id_user;
    }

    public int getId_panier() {
        return id_panier;
    }
}
