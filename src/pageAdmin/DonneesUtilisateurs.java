package pageAdmin;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DonneesUtilisateurs {

    private final Integer userID;
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty role;
    private final int pointFidelite;

    public DonneesUtilisateurs(int userID, String username, String password, String role, int pointFidelite){

        this.userID = userID;
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.role = new SimpleStringProperty(role);
        this.pointFidelite = pointFidelite;
    }

    public Integer getUserID() {
        return userID;
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getRole() {
        return role.get();
    }

    public StringProperty roleProperty() {
        return role;
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public int getPointFidelite() {
        return pointFidelite;
    }
}
