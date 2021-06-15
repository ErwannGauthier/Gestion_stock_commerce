package pageAdmin.controlesUtilisateurs;

public enum ajouterUtilisateurOptions {

    Admin, Client;

    private ajouterUtilisateurOptions(){
    }

    public String value(){
        return name();
    }

    public static ajouterUtilisateurOptions fromValue(String v){ return valueOf(v); }
}
