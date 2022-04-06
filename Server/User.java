package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public class User {
    
    ConnectionToClient ctc;
    boolean online;
    String username;
    String password;
    Vector<String> buddies;
    
    public User(DataInputStream dis) throws IOException {
        buddies = new Vector<String>();

        this.username = dis.readUTF();
        this.password = dis.readUTF();
        int buddyCount = dis.readInt();
        for(int i = 0; i < buddyCount; i++){
            buddies.add(dis.readUTF());
        }
    }

    public void store(DataOutputStream dos) throws IOException {
        dos.writeUTF(username);
        dos.writeUTF(password);
        dos.writeInt(buddies.size());
        for(int i = 0; i < buddies.size(); i++){
            dos.writeUTF(buddies.elementAt(i));
        }
    }

    public void setOnline(boolean online){
        this.online = online;
    }
}
