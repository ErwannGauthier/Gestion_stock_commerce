package pagesClient.compte;

import dbUtil.tables.Utilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CompteController {

    private Utilisateur client;

    @FXML
    private Label username;

    @FXML
    private Label mdp;

    @FXML
    private Label fidelite;

    public void init(Utilisateur client){

        this.client = client;
    }

    public void afficheDonnees(){

        this.username.setText(client.getUsername());
        this.mdp.setText(client.getPassword());
        this.fidelite.setText(
                String.valueOf(client.getPointFidelite())
        );
    }
}
