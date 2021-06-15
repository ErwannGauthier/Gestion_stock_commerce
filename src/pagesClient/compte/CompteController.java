package pagesClient.compte;

import dbUtil.dbConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompteController {

    private int userID;

    @FXML
    private Label username;

    @FXML
    private Label mdp;

    @FXML
    private Label fidelite;

    public void recupID(int id){

        this.userID = id;
    }

    public void afficheDonnees(){
        try{
            Connection conn = dbConnection.getConnection();
            PreparedStatement pr = conn.prepareStatement("SELECT * FROM login WHERE userID = ?");
            pr.setInt(1, this.userID);

            ResultSet rs = pr.executeQuery();

            this.username.setText(rs.getString(2));
            this.mdp.setText(rs.getString(3));
            this.fidelite.setText(String.valueOf(rs.getInt(5)));

            rs.close();
            pr.close();
            conn.close();
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }
}
