package Server;

import java.io.DataInputStream;
import java.util.Vector;

public class User {
    
    ConnectionToClient ctc;
    String username;
    String password;

    Vector<String> buddies;
    
    public User(DataInputStream dis){
    }
}
