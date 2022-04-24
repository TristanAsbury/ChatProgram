package Server;

import java.io.*;
import java.net.*;
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

            
            FileInputStream fis = new FileInputStream("userfile.txt");  //Try opening a file named "usefile.txt", which contains the user data
            DataInputStream dis = new DataInputStream(fis);
            users = new UserTable(dis);
            dis.close();
            fis.close();

        } catch (IOException io){
            System.out.println("Couldn't find userfile. Creating a new one...");

            
            FileOutputStream fos = new FileOutputStream("userfile.txt");    //If there was a problem opening the file, this means that there is no user data, so we create the file.
            DataOutputStream dos = new DataOutputStream(fos);
            dos.close();
            fos.close();
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

    public void sendBuddies(String username){
        User user = users.get(username);    //User that is receiving the buddy updates

        for(int i = 0; i < user.buddies.size(); i++){       // Go through that users friends
            User buddy = users.get(user.buddies.get(i));    // Get the user
            user.ctc.send("BUDDY_INCOMING " + buddy.username + " " + (buddy.ctc != null)); // Will send: "BUDDY_INCOMING bob123 false" meaning bob123 is offline
        }
    }

    public void sendToDos(String username){
        User user = users.get(username);

        for(int i = user.toDo.size() - 1; i >= 0; i--){
            user.ctc.send(user.toDo.elementAt(i));
            user.toDo.remove(i);
        }
    }

    public void sendBuddy(String receiverUser, String requesterUser){
        User receiver = users.get(receiverUser);
        User sender = users.get(requesterUser);

        receiver.buddies.add(sender.username);
        sender.buddies.add(receiver.username);

        System.out.println(receiver.username + " is " + (receiver.online ? " online " : " offline "));
        System.out.println(sender.username + " is " + (sender.online ? " online " : " offline "));
        saveUsers();

        //If the receiver (the one who was sent the request) is online, send him the user
        if(receiver.ctc != null){
            receiver.ctc.send("BUDDY_INCOMING " + sender.username + " " + sender.online);
        }

        if(sender.ctc != null){
            sender.ctc.send("BUDDY_INCOMING " + receiver.username + " " + receiver.online);
        }
    }

    public void addUser(String username, String password, ConnectionToClient ctc){
        User newUser = new User(username, password, ctc);
        users.put(username, newUser);

        System.out.println("Num users: " + users.size());

        saveUsers();
    }

    private void saveUsers(){
        try {
            FileOutputStream fos = new FileOutputStream("userfile.txt");
            DataOutputStream dos = new DataOutputStream(fos);

            users.saveTable(dos);

            dos.close();
            fos.close();
        } catch (IOException io){
            System.out.println("Error saving users...");
        }
    }

    public void setUserCTC(String username, ConnectionToClient ctc){
        users.get(username).ctc = ctc;
    }

    public void userStatusChange(String username, boolean online){
        User user = users.get(username);  //The user whos status is changing
        user.setOnline(online);   // Set their status
        
        if(!online){    //If they go offline, set their CTC to null...
            user.ctc = null;
        }

        for(int i = 0; i < user.buddies.size(); i++){ //Go through each one of the buddies
            User buddy = users.get(user.buddies.get(i));  //Get the current buddy
            if(buddy.ctc != null){  //Are they online?
                System.out.println("Sending status to: " + buddy.username); //Send the status if so
                buddy.ctc.send("BUDDY_STATUS " + username + " " + online);
            }
        }
    }

    public boolean userExists(String username){
        boolean userExists = false;
        if(users.get(username) != null){
            userExists = true;
        }
        return userExists;
    }

    public boolean accountExists(String username, String password){
        boolean loginExistent = false;
        User tmpUser = users.get(username);
        if(tmpUser != null){            // If the hashtable returns a user object
            if(tmpUser.password.equals(password)){  // If the passwords match
                loginExistent = true;   // Return login success
            }
        }
        return loginExistent;
    }
    
    public void sendBuddyRequest(String sender, String receiver){
        User senderUser = users.get(sender);
        User receiverUser = users.get(receiver);
        
        if(receiverUser.ctc != null){    //If the user is online
            receiverUser.ctc.send("INCOMING_BUDDYREQ " + sender);
        } else {                                //If the user is offline
            receiverUser.addToDo("INCOMING_BUDDYREQ " + sender);
            if(senderUser.ctc != null){
                senderUser.ctc.send("BUDDYREQ_OFFLINE");
            }
        }
    }
}
