package Server;

import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Vector;

public class Server {
    UserTable users;
    Vector<ConnectionToClient> orphanConnections;
    
    ServerSocket servSocket;


    public Server() throws IOException{
        servSocket = new ServerSocket(1234);    //Declare the server socket, will throw an exception if gone wrong

        try {
            //Try opening a file named "usefile.txt", which contains the user data
            FileInputStream fis = new FileInputStream("userfile.txt");
            DataInputStream dis = new DataInputStream(fis);
            users = new UserTable(dis);
        } catch (IOException io){
            //If there was a problem opening the file, this means that there is no user data, so we create the file.
            FileOutputStream fos = new FileOutputStream("userfile.txt");
            DataOutputStream bos = new DataOutputStream(fos);
            users = new UserTable();    //Start new user table.
        }

        
    }

    public void startAcceptingConnections(){
        while(true){
            try {
                Socket socket = servSocket.accept();    //Waits until a client sends a connection request
            } catch (IOException io){
                System.out.println("Couldn't connect to client");
            }
        }
    }
}
