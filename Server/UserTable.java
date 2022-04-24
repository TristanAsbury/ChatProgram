package Server;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

public class UserTable extends Hashtable<String, User>{

    //This constructor is for a table that loads from a file, ONLY USED IF A FILE EXISTS
    public UserTable(DataInputStream dis) throws IOException {
        System.out.println("Loading users");
        int numUsers = dis.readInt();   //Gets number of users in the file
        System.out.println("Num of users: " + numUsers);

        for(int i = 0; i < numUsers; i++){
            User tmpUser = new User(dis);
            put(tmpUser.username, tmpUser);
        }
    }

    public void addUser(String username){

    }

    public void setStatus(String username, boolean online){
        if(online){
            
        }
    }

    public void saveTable(DataOutputStream dos) throws IOException {
        Enumeration<String> e = keys();

        //Must save num of users
        dos.writeInt(size());

        while(e.hasMoreElements()){
            User user = get(e.nextElement());
            System.out.println("[SERVER] Saving " + user.username);
            user.store(dos);
        }
    }

    //If a file doesn't exist, then use this constructor
    public UserTable(){
       
    }
}
