package Client;

public class Buddy {
    String username;
    boolean online;

    public String toString(){
        String returnString = "";
        if(online){
            returnString += "*";
        }
        return returnString + username;
    }
}
