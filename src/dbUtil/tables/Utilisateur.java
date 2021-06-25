package dbUtil.tables;

import dbUtil.dbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class Utilisateur {

    private final int id;
    private final String username;
    private final String password;
    private final String role;
    private final int pointFidelite;
    private Collection<Commande> historiqueCommande;

    public Utilisateur(int id, String nom, String mdp, String role, int points) throws SQLException {

        this.id = id;
        this.username = nom;
        this.password = mdp;
        this.role = role;
        this.pointFidelite = points;
        historiqueCommande = new ArrayList<>();
        initHistoriqueCommande();
    }

    private void initHistoriqueCommande() throws SQLException {

        Connection conn = dbConnection.getConnection();
        PreparedStatement pr = null;
        ResultSet rs = null;

        try{
            pr = conn.prepareStatement("SELECT * FROM commande WHERE ID_User = ?");
            pr.setInt(1, this.id);

            rs = pr.executeQuery();

            while(rs.next()){

                historiqueCommande.add(new Commande(rs.getInt(1), rs.getInt(2)));
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }

        finally{
            conn.close();
            pr.close();
            rs.close();
        }
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public int getPointFidelite() {
        return pointFidelite;
    }

    public Collection<Commande> getHistoriqueCommande() {
        return historiqueCommande;
    }
}
