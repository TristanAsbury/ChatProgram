package Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Hashtable;

public class UserTable extends Hashtable<String, User>{

    

    //This constructor is for a table that loads from a file, ONLY USED IF A FILE EXISTS
    public UserTable(DataInputStream dis) throws IOException {
        int numUsers = dis.readInt();   //Gets number of users in the file
        
        for(int i = 0; i < numUsers; i++){
            User tmpUser = new User(dis);
        }
    }

    public void addUser(String username){

    }

    public void setStatus(String username, boolean online){
        if(online){
            
        }
    }


    //If a file doesn't exist, then use this constructor
    public UserTable(){
       
    }
}
