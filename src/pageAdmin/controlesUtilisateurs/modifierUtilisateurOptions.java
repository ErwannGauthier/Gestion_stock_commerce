package pageAdmin.controlesUtilisateurs;

public enum modifierUtilisateurOptions {

    Admin, Client;

    private modifierUtilisateurOptions(){
    }

    public String value(){
        return name();
    }

    public static modifierUtilisateurOptions fromValue(String v){ return valueOf(v); }
}
