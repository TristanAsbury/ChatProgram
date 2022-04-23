package Server;

//package Server;

import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class ConnectionToClient implements Runnable {
    private Talker talker;
    private Vector<ConnectionToClient> ctcs;
    private String username;
    private boolean receiving;
    private Server server;
    private Socket socket;

    public ConnectionToClient(Socket socket, Server server, Vector<ConnectionToClient> ctcs){
        this.server = server;
        this.receiving = true;
        this.ctcs = ctcs;
        this.username = "Pending...";
        this.socket = socket;
        
        try {
            this.talker = new Talker(socket);
        } catch (IOException io){
            ctcs.remove(this);
            receiving = false;
        }

        new Thread(this).start();
    }

    public void run(){
        while(receiving){
            receive();
        }
    }

    private void receive(){
        try{
            String msg = talker.receive();  //Receive the text
            System.out.println("[CTC " + username + "] Received: " + msg);
            handleMessage(msg);
        } catch (IOException io){
            System.out.println("[CTC " + username + "] Problem receiving message from client.");
            server.userStatusChange(username, false);
            receiving = false;  //Stop trying to receive messages
            ctcs.remove(this);  //Remove this ctc from the list of ctcs so the server stops trying to send messages through us!
        }
    }

    private void handleMessage(String msg) {
        if(msg.equals("USER_REGISTER")){
            System.out.println("User registering");
            try {
                String usernameInput = talker.receive();
                String passwordInput = talker.receive();

                if(!server.userExists(usernameInput)){   //If the user doesn't exist, we're good
                    server.addUser(usernameInput, passwordInput, this);
                    this.username = usernameInput;
                    send("REGISTER_SUCCESS");
                    server.sendBuddies(this.username);
                    server.sendToDos(this.username);
                } else { //If the user does exist, however, then we must send a message back and wait
                    send("REGISTER_ERROR_UE");  //Sends register error "user exists"
                }
            } catch (IOException io){
                System.out.println("Error getting user credentials.");
            }
        } else if (msg.equals("USER_LOGIN")){
            System.out.println("User attempting to log in...");
            try {
                String usernameInput = talker.receive();
                String passwordInput = talker.receive();
                
                if(!server.accountExists(usernameInput, passwordInput)){
                    send("LOGIN_ERROR");
                } else {
                    this.username = usernameInput;  //Set the username so we can use it for later
                    server.setUserCTC(this.username, this); //Set user ctc
                    server.userStatusChange(this.username, true);   //Set user online
                    send("LOGIN_SUCCESS");
                    server.sendBuddies(this.username);
                    server.sendToDos(this.username);
                }
                
            } catch (IOException io ){
                System.out.println("Error getting user credentials.");       
            }
        } else if (msg.startsWith("OUTGOING_BUDDYREQ")){
            String buddyName = msg.split(" ")[1];
            System.out.println("Checking to see if user: " + buddyName + " exists...");
            if(server.userExists(buddyName)){
                System.out.println("User exists! Sending reqest!");
                server.sendBuddyRequest(username, buddyName);
            }
        } else if(msg.startsWith("BUDDYREQ_ACCEPT")){
            String senderUser = msg.split(" ")[1];
            String receiverUser = username;

            server.sendBuddy(receiverUser, senderUser);
        } else if (msg.startsWith("OUTGOING_MSG")){
            String[] parts = msg.split(" ");
            String toUsername = parts[1];
            String message = msg.substring(parts[0].length() + parts[1].length() + 1);
            
            User toUser = server.users.get(toUsername);
            if(toUser.ctc != null){   //If user is online
                toUser.ctc.send("INCOMING_MSG " + username + " " + message);
            } else {
                toUser.toDo.add("INCOMING_MSG " + username + " " + message);
            }
        } else if (msg.startsWith("LOGOUT")){
            server.userStatusChange(username, false);
        } else if (msg.startsWith("FILE_SEND_REQUEST")){
            String[] parts = msg.split(" ");
            String toUsername = parts[1];
            String fileName = parts[2];
            String fileLength = parts[3];

            User toUser = server.users.get(toUsername);

            if(toUser != null){
                if(toUser.ctc != null){ //If user is online
                    toUser.ctc.send("ASK_FILE_REQUEST " + username + " " + fileName + " " + fileLength);
                } else {
                    send("FILE_SEND_ERROR_UO"); //Send error back to user
                }
            }
        } else if(msg.startsWith("ACCEPTED_FILE_REQUEST")){
            String[] parts = msg.split(" ");
            String toUsername = parts[1];

            User toUser = server.users.get(toUsername);
            if(toUser != null){
                if(toUser.ctc != null){
                    toUser.ctc.send("START_FILE_TRANSFER " + username + " " + socket.getInetAddress().toString().substring(1) + " " + 1111);
                }
            }
        }
    }

    //Wrapper method
    public void send(String msg){
        try {
            talker.send(msg);   //Uses the talker method to send the message
        } catch (IOException io){
            System.out.println("Problem sending message...");
            server.userStatusChange(username, false);
            ctcs.remove(this);  //If there is a problem, remove ourself from the list of ctcs so the server doesn't send messages to us (we are forgotten :( )
        }
    }
}