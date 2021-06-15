package pageAdmin;

import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import pageAdmin.controlesProduits.AjouterProduitController;
import pageAdmin.controlesProduits.ModifierProduitController;
import pageAdmin.controlesUtilisateurs.AjouterUtilisateurController;
import pageAdmin.controlesUtilisateurs.ModifierUtilisateurController;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private TableView<DonneesUtilisateurs> userTable;

    @FXML
    private TableColumn<DonneesUtilisateurs, Integer> userIDCol;

    @FXML
    private TableColumn<DonneesUtilisateurs, String> userUsernameCol;

    @FXML
    private TableColumn<DonneesUtilisateurs, String> userPasswordCol;

    @FXML
    private TableColumn<DonneesUtilisateurs, String> userRoleCol;

    @FXML
    private TableColumn<DonneesUtilisateurs, Integer> userPointFideliteCol;

    @FXML
    private TableView<DonneesProduits> prodTable;

    @FXML
    private TableColumn<DonneesProduits, Integer> prodIDCol;

    @FXML
    private TableColumn<DonneesProduits, String> prodNomCol;

    @FXML
    private TableColumn<DonneesProduits, String> prodCategorieCol;

    @FXML
    private TableColumn<DonneesProduits, Integer> prodStockCol;

    @FXML
    private TableColumn<DonneesProduits, String> prodMarqueCol;

    @FXML
    private TableColumn<DonneesProduits, Integer> prodPoidsCol;

    @FXML
    private TableColumn<DonneesProduits, Double> prodPrixCol;

    @FXML
    private TableColumn<DonneesProduits, Double> prodPrixKGCol;

    @FXML
    private TableColumn<DonneesProduits, String> prodDescriptionCol;

    private ObservableList<DonneesUtilisateurs> donneesUtilisateurs;
    private ObservableList<DonneesProduits> donneesProduits;

    private final Connection conn = dbConnection.getConnection();
    public AdminController() throws SQLException {
    }

    public void initialize(URL url, ResourceBundle rb){

        chargerDonneesUtilisateurs();
        chargerDonneesProduits();
    }

    public void chargerDonneesUtilisateurs(){

        try{
            String userRequete = "SELECT * FROM login";

            this.donneesUtilisateurs = FXCollections.observableArrayList();

            ResultSet rs = conn.createStatement().executeQuery(userRequete);
            while(rs.next()){
                this.donneesUtilisateurs.add(new DonneesUtilisateurs(rs.getInt(1), rs.getString(2),rs.getString(3),rs.getString(4),rs.getInt(5)));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        this.userIDCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        this.userUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        this.userPasswordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        this.userRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        this.userPointFideliteCol.setCellValueFactory(new PropertyValueFactory<>("pointFidelite"));

        this.userTable.setItems(null);
        this.userTable.setItems(this.donneesUtilisateurs);
    }

    public void chargerDonneesProduits(){
        try{

            String prodRequete = "SELECT * FROM produit";

            this.donneesProduits = FXCollections.observableArrayList();

            ResultSet rs = conn.createStatement().executeQuery(prodRequete);
            while(rs.next()){
                this.donneesProduits.add(new DonneesProduits(rs.getInt(1), rs.getString(2),rs.getString(3),rs.getInt(4),rs.getString(5), rs.getInt(6), rs.getDouble(7), rs.getDouble(8), rs.getString(9)));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        this.prodIDCol.setCellValueFactory(new PropertyValueFactory<>("produit_id"));
        this.prodNomCol.setCellValueFactory(new PropertyValueFactory<>("produit_nom"));
        this.prodCategorieCol.setCellValueFactory(new PropertyValueFactory<>("produit_categorie"));
        this.prodStockCol.setCellValueFactory(new PropertyValueFactory<>("produit_nombreStock"));
        this.prodMarqueCol.setCellValueFactory(new PropertyValueFactory<>("produit_marque"));
        this.prodPoidsCol.setCellValueFactory(new PropertyValueFactory<>("produit_poids"));
        this.prodPrixCol.setCellValueFactory(new PropertyValueFactory<>("produit_prix"));
        this.prodPrixKGCol.setCellValueFactory(new PropertyValueFactory<>("produit_prix_kg"));
        this.prodDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("produit_description"));

        this.prodTable.setItems(null);
        this.prodTable.setItems(this.donneesProduits);
    }

    @FXML
    private void rafraichir(){
        chargerDonneesUtilisateurs();
        chargerDonneesProduits();
    }

    @FXML
    private void ajouterUtilisateur(){

        try{
            Stage ajouterStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Pane root = loader.load(getClass().getResource("/pageAdmin/controlesUtilisateurs/AjouterUtilisateur.fxml").openStream());

            AjouterUtilisateurController ajouterUtilisateurController = loader.getController();

            Scene scene = new Scene(root);
            ajouterStage.setScene(scene);
            ajouterStage.setTitle("Commerce.io - Ajouter utilisateur");
            ajouterStage.setResizable(false);
            ajouterStage.show();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void modifierUtilisateur(){

        if(userTable.getSelectionModel().getSelectedItem() != null){
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/pageAdmin/controlesUtilisateurs/ModifierUtilisateur.fxml"));

                Stage stageModifierUser = new Stage();
                stageModifierUser.setScene(new Scene(loader.load()));

                ModifierUtilisateurController modifierUtilisateurController = loader.getController();
                modifierUtilisateurController.initialiseTextField(
                        userTable.getSelectionModel().getSelectedItem().getUserID(),
                        userTable.getSelectionModel().getSelectedItem().getUsername(),
                        userTable.getSelectionModel().getSelectedItem().getPassword(),
                        userTable.getSelectionModel().getSelectedItem().getRole(),
                        userTable.getSelectionModel().getSelectedItem().getPointFidelite()
                );

                stageModifierUser.setTitle("Commerce.io - Modifier utilisateur");
                stageModifierUser.setResizable(false);
                stageModifierUser.show();
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        else{
            String message = "Vous devez sélectionner un utilisateur pour pouvoir le modifier.";
            Alert infoImpossible = new Alert(Alert.AlertType.INFORMATION, message, new ButtonType[0]);
            infoImpossible.setTitle("Action impossible");
            infoImpossible.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            infoImpossible.show();
        }
    }

    @FXML
    private void supprimerUtilisateur(){

        // Si un utilisateur est séléctionné alors...
        if(userTable.getSelectionModel().getSelectedItem() != null){

            // On ne peut pas supprimer l'admin Principal, il possède l'userID n°1.
            if(userTable.getSelectionModel().getSelectedItem().getUserID() == 1){

                String message = "Vous ne pouvez pas supprimer cet utilisateur !";
                Alert infoImpossible = new Alert(Alert.AlertType.INFORMATION, message, new ButtonType[0]);
                infoImpossible.setTitle("Action impossible");
                infoImpossible.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                infoImpossible.show();
            }
            else{

                // Crée une fenetre de confirmation
                String message = "Êtes-vous sûr de vouloir supprimer cet utilisateur ?";
                ButtonType b1 = new ButtonType("Annuler");
                ButtonType b2 = new ButtonType("Supprimer");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, b1, b2);
                alert.setTitle("Attention");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

                // Affiche la fenetre et attend le choix de l'utilisateur
                alert.showAndWait().ifPresent((reponse) -> {

                    // Si bouton Supprimer selectionné alors...
                    if (reponse == b2) {

                        // Supprime l'utilisateur
                        try{

                            String sql = "DELETE FROM login WHERE userID = ?";
                            PreparedStatement stmt = conn.prepareStatement(sql);

                            // Indique l'ID de l'utilisateur selectionné
                            stmt.setInt(1, userTable.getSelectionModel().getSelectedItem().getUserID());

                            stmt.execute();
                        }
                        catch (SQLException ex){
                            ex.printStackTrace();
                        }

                        chargerDonneesUtilisateurs();
                        chargerDonneesProduits();
                    }
                    else {
                        alert.close();
                    }
                });
            }
        }
        else{
            String message = "Vous devez sélectionner un utilisateur pour pouvoir le supprimer.";
            Alert infoImpossible = new Alert(Alert.AlertType.INFORMATION, message, new ButtonType[0]);
            infoImpossible.setTitle("Action impossible");
            infoImpossible.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            infoImpossible.show();
        }
    }

    @FXML
    private void ajouterProduit(){

        try{
            Stage ajouterStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Pane root = loader.load(getClass().getResource("/pageAdmin/controlesProduits/AjouterProduit.fxml").openStream());

            AjouterProduitController ajouterProduitController = loader.getController();

            Scene scene = new Scene(root);
            ajouterStage.setScene(scene);
            ajouterStage.setTitle("Commerce.io - Ajouter produit");
            ajouterStage.setResizable(false);
            ajouterStage.show();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void modifierProduit(){

        if(prodTable.getSelectionModel().getSelectedItem() != null){
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/pageAdmin/controlesProduits/ModifierProduit.fxml"));

                Stage stageModifierProd = new Stage();
                stageModifierProd.setScene(new Scene(loader.load()));

                ModifierProduitController modifierProduitController = loader.getController();
                modifierProduitController.initialiseTextField(
                        prodTable.getSelectionModel().getSelectedItem().getProduit_id(),
                        prodTable.getSelectionModel().getSelectedItem().getProduit_nom(),
                        prodTable.getSelectionModel().getSelectedItem().getProduit_marque(),
                        prodTable.getSelectionModel().getSelectedItem().getProduit_categorie(),
                        prodTable.getSelectionModel().getSelectedItem().getProduit_nombreStock(),
                        prodTable.getSelectionModel().getSelectedItem().getProduit_poids(),
                        prodTable.getSelectionModel().getSelectedItem().getProduit_prix(),
                        prodTable.getSelectionModel().getSelectedItem().getProduit_prix_kg(),
                        prodTable.getSelectionModel().getSelectedItem().getProduit_description()
                );

                stageModifierProd.setTitle("Commerce.io - Modifier produit");
                stageModifierProd.setResizable(false);
                stageModifierProd.show();
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        else{
            String message = "Vous devez sélectionner un produit pour pouvoir le modifier.";
            Alert infoImpossible = new Alert(Alert.AlertType.INFORMATION, message, new ButtonType[0]);
            infoImpossible.setTitle("Action impossible");
            infoImpossible.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            infoImpossible.show();
        }

    }

    @FXML
    private void supprimerProduit(){

        // Si un produit est séléctionné alors...
        if(prodTable.getSelectionModel().getSelectedItem() != null){

            // Crée une fenetre de confirmation
            String message = "Êtes-vous sûr de vouloir supprimer ce produit ?";
            ButtonType b1 = new ButtonType("Annuler");
            ButtonType b2 = new ButtonType("Supprimer");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, b1, b2);
            alert.setTitle("Attention");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

            //Affiche la fenetre et attend le choix de l'utilisateur
            alert.showAndWait().ifPresent((reponse) -> {

                // Si bouton Supprimer selectionné alors...
                if (reponse == b2) {

                    // Supprime le produit
                    try{

                        String sql = "DELETE FROM produit WHERE produit_id = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);

                        // Indique l'ID du produit selectionné
                        stmt.setInt(1, prodTable.getSelectionModel().getSelectedItem().getProduit_id());

                        stmt.execute();
                    }
                    catch (SQLException ex){
                        ex.printStackTrace();
                    }

                    // Recharge les données pour afficher les modifications apportées
                    chargerDonneesUtilisateurs();
                    chargerDonneesProduits();
                }
                else {
                    alert.close();
                }
            });
        }
        else{
            String message = "Vous devez sélectionner un produit pour pouvoir le supprimer.";
            Alert infoImpossible = new Alert(Alert.AlertType.INFORMATION, message, new ButtonType[0]);
            infoImpossible.setTitle("Action impossible");
            infoImpossible.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            infoImpossible.show();
        }
    }
}
