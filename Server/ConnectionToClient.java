// package Server;

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
        this.username = "Pending";
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

    public void setReceiving(boolean b){
        this.receiving = b;
    }

    private void receive(){
        try{
            String msg = talker.receive();  //Receive the text
            System.out.println("[CTC " + username + "] Received: " + msg);
            handleMessage(msg);
        } catch (IOException io){
            System.out.println("[CTC " + username + "] Problem receiving message from client.");
            if(!username.equals("Pending")){    //If there is a user logged in
                server.userStatusChange(username, false);   //Change the status
            }
            receiving = false;  //Stop trying to receive messages
            ctcs.remove(this);  //Remove this ctc from the list of ctcs so the server stops trying to send messages through us!
        }
    }

    private void handleMessage(String msg) {
        if(msg.equals("USER_REGISTER")){        //If the user is registering
            try {
                String usernameInput = talker.receive(); //Blocks our run method, so we don't have to worry about battling to receive the username and password   
                String passwordInput = talker.receive(); 

                if(!server.userExists(usernameInput)){   //If the user doesn't exist, we're good
                    this.username = usernameInput;      //set CTC username to the username
                    server.addUser(usernameInput, passwordInput, this); //Add the user to server users
                    server.userStatusChange(username, true);
                    send("REGISTER_SUCCESS");
                } else {                                //If the user does exist, however, then we must send a message back and wait
                    send("REGISTER_ERROR_UE");      //Sends register error "user exists"
                }
            } catch (IOException io){
                System.out.println("Error getting user credentials.");
            }

        } else if (msg.equals("USER_LOGIN")){               //If the user is logging in
            try {
                String usernameInput = talker.receive(); //Blocks our run method, so we don't have to worry about battling to receive the username and password   
                String passwordInput = talker.receive(); 
                
                if(!server.accountExists(usernameInput, passwordInput)){
                    send("LOGIN_ERROR");            //If the account doesn't exist, send an error
                } else {
                    //We must check if the user is already logged in
                    this.username = usernameInput;  //Set the username so we can use it for later
                    User checkUser = server.users.get(this.username);

                    if(checkUser.ctc != null){
                        checkUser.ctc.setReceiving(false);      //Make that ctc stop receiving messages, fixes a few things
                        checkUser.ctc.send("FORCE_LOGOUT"); //Will force the already open client to close
                    }

                    server.setUserCTC(this.username, this); //Set user ctc
                    server.userStatusChange(this.username, true);   //Set user online
                    send("LOGIN_SUCCESS");          //Send our login success message to allow the user to create the window
                    server.sendBuddies(this.username);   //Send the buddies
                    server.sendToDos(this.username);     //Send all pending messages or whatever else
                }
                
            } catch (IOException io ){
                System.out.println("Error getting user credentials.");       
            }

        } else if (msg.startsWith("OUTGOING_BUDDYREQ")){        //If the user just send a buddy request
            String buddyName = msg.split(" ")[1];                //Get the buddy name
            if(server.userExists(buddyName)){                           //If the user exists
                server.sendBuddyRequest(username, buddyName);           //Send the request to the user
            } else {
                send("BUDDYREQ_ERROR_BNE");                         //If the user doesn't exist, send back to the user that they dont
            }

        } else if(msg.startsWith("BUDDYREQ_ACCEPT")){           //If the user accepts the friend request
            String senderUser = msg.split(" ")[1];              //Get the senderUser (the user who sent the friend request)
            String receiverUser = username;                             //Get the receiver user(the user who received the friend request)
            server.sendBuddy(receiverUser, senderUser);                 //Use our sever method to send each other the buddies

        } else if (msg.startsWith("OUTGOING_MSG")){             //If the user is sending a message
            String[] parts = msg.split(" ");                    //Get the parts
            String toUsername = parts[1];                               //Get the recipient of the message
            User toUser = server.users.get(toUsername);                 //Get the actual user object
            String message = msg.substring(parts[0].length() + parts[1].length() + 1);  //Get the message

            if(toUser.ctc != null){                                     //If user is online
                toUser.ctc.send("INCOMING_MSG " + username + " " + message);    //Send the message to the recipient
            } else {
                toUser.toDo.add("INCOMING_MSG " + username + " " + message);    //Else, add to pending messages
            }

        } else if (msg.startsWith("LOGOUT")){                   //If the user logs out
            server.userStatusChange(username, false);           //Send the status change to all their buddies
            ctcs.remove(this);
            receiving = false;
        } else if (msg.startsWith("FILE_SEND_REQUEST")){        //If the user wants to send a file
            String[] parts = msg.split(" ");                    //Get the parts
            
            String toUsername = parts[1];                               //Get the recipient of the file
            User toUser = server.users.get(toUsername);                 //Get the recipient user object

            String fileName = parts[2];                                 //Get the file name
            String fileLength = parts[3];                               //Get the number of bytes

            if(toUser != null){                                         //If the recipient exists
                if(toUser.ctc != null){                                 //If user is online
                    toUser.ctc.send("ASK_FILE_REQUEST " + username + " " + fileName + " " + fileLength);
                } else {
                    send("FILE_SEND_ERROR_UO");                     //Send error back to user
                }
            }

        } else if(msg.startsWith("ACCEPTED_FILE_REQUEST")){         //If the user accepts the file send request
            String[] parts = msg.split(" ");                        //split
            String toUsername = parts[1];                                   //The to username (the actual initiator)

            User toUser = server.users.get(toUsername);                     //Get the initiator sender
            if(toUser != null){
                if(toUser.ctc != null){
                    toUser.ctc.send("START_FILE_TRANSFER " + username + " " + socket.getInetAddress().toString().substring(1) + " " + 1111);
                } else {
                    send("FILE_SEND_ERROR_UO");
                }
            }

        } else if(msg.startsWith("DENIED_FILE_REQUEST")){
            String senderUsername = msg.split(" ")[1];
            User senderUser = server.users.get(senderUsername);

            if(senderUser != null){
                if(senderUser.ctc != null){
                    senderUser.ctc.send("FILE_SEND_ERROR_UO");
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