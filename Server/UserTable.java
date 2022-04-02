package Server;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;

public class UserTable extends Hashtable{

    

    //This constructor is for a table that loads from a file, ONLY USED IF A FILE EXISTS
    public UserTable(DataInputStream dis) throws IOException {
        int numUsers = dis.readInt();   //Gets number of users in the file
        
        for(int i = 0; i < numUsers; i++){
            User tmpUser = new User(dis);
        }
    }


    //If a file doesn't exist, then use this constructor
    public UserTable(){
       
    }
}
