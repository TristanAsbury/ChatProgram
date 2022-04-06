package Server;

import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Vector;

public class Server {
    UserTable users;
    Vector<ConnectionToClient> orphanConnections;   //The connections that aren't confirmed to be actual users on the server
    ServerSocket servSocket;

    public Server() throws IOException{
        System.out.println("Creating socket...");
        servSocket = new ServerSocket(1234);    //Declare the server socket, will throw an exception if gone wrong

        try {
            System.out.println("Loading userfile...");

            //Try opening a file named "usefile.txt", which contains the user data
            FileInputStream fis = new FileInputStream("userfile.txt");
            DataInputStream dis = new DataInputStream(fis);
            users = new UserTable(dis);
        } catch (IOException io){
            System.out.println("Couldn't find userfile. Creating a new one...");

            //If there was a problem opening the file, this means that there is no user data, so we create the file.
            FileOutputStream fos = new FileOutputStream("userfile.txt");
            DataOutputStream bos = new DataOutputStream(fos);
            users = new UserTable();    //Start new user table.
        }

        orphanConnections = new Vector<ConnectionToClient>();
    }

    public void startAcceptingConnections(){
        System.out.println("Starting to accept client connections...");
        while(true){
            try {
                Socket tmpSocket = servSocket.accept();    //Waits until a client sends a connection request
                System.out.println("Accepted connection...");
                ConnectionToClient tmpCTC = new ConnectionToClient(tmpSocket, this, orphanConnections);
                orphanConnections.add(tmpCTC);
            } catch (IOException io){
                System.out.println("Couldn't connect to client");
            }
        }
    }

    public void addUser(String username, String password, ConnectionToClient ctc){
        User newUser = new User(username, password, ctc);
        users.put(username, newUser);
    }

    public boolean userExists(String username){
        boolean userExists = false;
        if(users.get(username) != null){
            userExists = true;
        }
        return userExists;
    }

    public boolean loginExists(String username, String password){
        boolean loginExistent = false;
        User tmpUser = users.get(username);
        if(tmpUser != null){            // If the hashtable returns a user object
            if(tmpUser.password.equals(password)){  // If the passwords match
                loginExistent = true;   // Return login success
            }
        }
        return loginExistent;
    }
}
