package dbUtil;

import java.sql.*;

public class trouveID {

    private static final int borneMinViandesPoissons = 10000;
    private static final int borneMaxViandesPoissons = 20000;
    private static final int borneMinFruitsLegumes = 20000;
    private static final int borneMaxFruitsLegumes = 30000;
    private static final int borneMinFrais = 30000;
    private static final int borneMaxFrais = 40000;
    private static final int borneMinSurgele = 40000;
    private static final int borneMaxSurgele = 50000;
    private static final int borneMinFeculents = 50000;
    private static final int borneMaxFeculents = 60000;
    private static final int borneMinConserves = 60000;
    private static final int borneMaxConserves = 70000;
    private static final int borneMinHygiene = 70000;
    private static final int borneMaxHygiene = 80000;
    private static final int borneMinBoissons = 80000;
    private static final int borneMaxBoissons = 90000;

    private static Connection conn;

    // Retourne le nombre d'utilisateur présent dans la table login.
    public static int nbUtilisateur(){
        try{
            // Calcul le nombre d'utilisateur créé et le retourne.
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM login");
            rs.next();
            return rs.getInt(1);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    // Retourne un ID non utilisé dans la table login.
    public static int trouveIdUtilisateur(){
        try{

            // Créé une connection à la BDD.
            conn = dbConnection.getConnection();

            int idDispo = nbUtilisateur();

            // Execute la commande SQL entre "" sur la BDD.
            PreparedStatement stmt = conn.prepareStatement("SELECT userID FROM login where userID = ?");
            stmt.setInt(1, idDispo);
            ResultSet rs = stmt.executeQuery();

            // Tant que la commande à un résultat alors...
            while(rs.next()){
                idDispo++;
                stmt.setInt(1, idDispo);
                rs = stmt.executeQuery();
            }

            // Referme la connection à la BDD.
            conn.close();
            return idDispo;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Retourne le nombre de produit dans la catégorie (identifiée grâce à ses ID minimum et maximum).
    public static int nbProduitsCategorie(int borneMin, int borneMax){
        try{
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM produit WHERE produit_id > ? and produit_id < ?");
            stmt.setInt(1, borneMin);
            stmt.setInt(2, borneMax);
            ResultSet rs = stmt.executeQuery();
            return rs.getInt(1);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    // Retourne un ID non utilisé dans la catégorie passée en paramètre.
    public static int trouveIdProduit(String categorie) throws SQLException {

        conn = dbConnection.getConnection();
        int res = -1;
        int nbProdCat;

        switch(categorie){
            case "Viandes/Poissons":
                nbProdCat = nbProduitsCategorie(borneMinViandesPoissons, borneMaxViandesPoissons);
                res = trouveIdProduit(borneMinViandesPoissons, nbProdCat, borneMaxViandesPoissons);
                break;

            case "Fruits/Legumes":
                nbProdCat = nbProduitsCategorie(borneMinFruitsLegumes, borneMaxFruitsLegumes);
                res = trouveIdProduit(borneMinFruitsLegumes, nbProdCat, borneMaxFruitsLegumes);
                break;

            case "Frais":
                nbProdCat = nbProduitsCategorie(borneMinFrais, borneMaxFrais);
                res = trouveIdProduit(borneMinFrais, nbProdCat, borneMaxFrais);
                break;

            case "Surgele":
                nbProdCat = nbProduitsCategorie(borneMinSurgele, borneMaxSurgele);
                res = trouveIdProduit(borneMinSurgele, nbProdCat, borneMaxSurgele);
                break;

            case "Feculents":
                nbProdCat = nbProduitsCategorie(borneMinFeculents, borneMaxFeculents);
                res = trouveIdProduit(borneMinFeculents, nbProdCat, borneMaxFeculents);
                break;

            case "Conserves":
                nbProdCat = nbProduitsCategorie(borneMinConserves, borneMaxConserves);
                res = trouveIdProduit(borneMinConserves, nbProdCat, borneMaxConserves);
                break;

            case "Hygiene":
                nbProdCat = nbProduitsCategorie(borneMinHygiene, borneMaxHygiene);
                res = trouveIdProduit(borneMinHygiene, nbProdCat, borneMaxHygiene);
                break;

            case "Boissons":
                nbProdCat = nbProduitsCategorie(borneMinBoissons, borneMaxBoissons);
                res = trouveIdProduit(borneMinBoissons, nbProdCat, borneMaxBoissons);
                break;
        }

        conn.close();
        return res;
    }

    // Retourne un ID non utilisé dans la catégorie passée en paramètre (identifiée grâce à son ID minimale et maximale).
    //      Quasiment même procédé que pour la fonction trouveIdUtilisateur()
    public static int trouveIdProduit(int borneMin, int nbProduit, int borneMax){

        try{
            int idDispo = borneMin + nbProduit;
            PreparedStatement stmt = conn.prepareStatement("SELECT produit_id FROM produit where produit_id > ? and produit_id < ?");
            stmt.setInt(1, idDispo);
            stmt.setInt(2, borneMax);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                idDispo++;
                stmt.setInt(1, idDispo);
                rs = stmt.executeQuery();
            }

            return idDispo+1;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
