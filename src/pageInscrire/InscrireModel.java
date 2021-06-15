package pageInscrire;

import dbUtil.dbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InscrireModel {
    Connection connection;

    public InscrireModel(){
        try{
            this.connection = dbConnection.getConnection();
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }

        if(this.connection == null){
            System.exit(1);
        }
    }

    // Retourne vrai si le nom d'utilisateur passé en paramètre est déjà utilisé dans la BDD
    // On ne souhaite pas permettre aux clients de se nommer pareil qu'un admin par eux-mêmes
    public boolean existe(String user) throws Exception{
        PreparedStatement pr = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM login where username = ?";

        try{
            pr = this.connection.prepareStatement(sql);
            pr.setString(1, user);

            rs = pr.executeQuery();

            return rs.next();
        }
        catch(SQLException ex){
            return false;
        }

        finally{
            pr.close();
            rs.close();
        }
    }
}

