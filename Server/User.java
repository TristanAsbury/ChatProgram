package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public class User {
    
    public ConnectionToClient ctc;
    boolean online;
    String username;
    String password;
    Vector<String> buddies;

    Vector<String> toDo;
    
    public User(DataInputStream dis) throws IOException {
        buddies = new Vector<String>();
        toDo = new Vector<String>();

        this.username = dis.readUTF();
        this.password = dis.readUTF();

        int buddyCount = dis.readInt();
        for(int i = 0; i < buddyCount; i++){
            buddies.add(dis.readUTF());
        }

        int toDoCount = dis.readInt();
        for(int i = 0; i < toDoCount; i++){
            toDo.add(dis.readUTF());
        }
    }

    public void addToDo(String msg){
        toDo.add(msg);
    }

    public User(String username, String password, ConnectionToClient ctc){
        this.username = username;
        this.password = password;
        this.ctc = ctc;
        this.buddies = new Vector<String>();
        this.toDo = new Vector<String>();
        setOnline(true);
    }

    public void store(DataOutputStream dos) throws IOException {
        dos.writeUTF(username);
        dos.writeUTF(password);
        dos.writeInt(buddies.size());
        for(int i = 0; i < buddies.size(); i++){
            dos.writeUTF(buddies.elementAt(i));
        }

        dos.writeInt(toDo.size());
        for(int i = 0; i < toDo.size(); i++){
            dos.writeUTF(toDo.elementAt(i));
        }
    }

    public void setOnline(boolean online){
        this.online = online;
    }
}
