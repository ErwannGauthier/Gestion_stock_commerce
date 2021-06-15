package pageLogin;

public enum option {
    Admin, Client;

    private option(){

    }

    public String value(){
        return name();
    }

    public static option fromValue(String v){
        return valueOf(v);
    }
}
