// package Client;

public class Buddy {
    String username;
    boolean online;

    public Buddy(){

    }

    public Buddy(String username, boolean online){
        this.username = username;
        this.online = online;
    }

    public String toString(){
        String returnString = "";
        if(online){
            returnString += "*";
        }
        return returnString + username;
    }
}
