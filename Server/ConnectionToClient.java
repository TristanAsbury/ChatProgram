package Server;

//package Server;

import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class ConnectionToClient implements Runnable {
    private Talker talker;
    private Vector<ConnectionToClient> ctcs;
    private String id;
    private boolean receiving;
    private boolean loggedIn;
    private Server server;

    public ConnectionToClient(Socket socket, Server server, Vector<ConnectionToClient> ctcs){
        this.server = server;
        this.receiving = true;
        this.ctcs = ctcs;
        
        try {
            this.talker = new Talker(socket);
        } catch (IOException io){
            ctcs.remove(this);
            receiving = false;
        }
        
        loggedIn = false;
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
            System.out.println("[CTC " + id + "] Received: " + msg);
            handleMessage(msg);
        } catch (IOException io){
            System.out.println("[CTC " + id + "] Problem receiving message from client.");
            receiving = false;  //Stop trying to receive messages
            ctcs.remove(this);  //Remove this ctc from the list of ctcs so the server stops trying to send messages through us!
        }
    }

    private void handleMessage(String msg){
        if(msg.equals("USER_REGISTER")){
            System.out.println("User registering");
            try {
                String username = talker.receive();
                String password = talker.receive();

                if(!server.userExists(username)){   //If the user doesn't exist, we're good
                    server.addUser(username, password, this);
                    send("REGISTER_SUCCESS");
                } else { //If the user does exist, however, then we must send a message back and wait
                    send("REGISTER_ERROR_UE");  //Sends register error "user exists"
                }
            } catch (IOException io){
                System.out.println("Error getting user credentials.");
            }
        }
    }

    //Wrapper method
    private void send(String msg){
        try {
            talker.send(msg);   //Uses the talker method to send the message
        } catch (IOException io){
            System.out.println("Problem sending message...");
            ctcs.remove(this);  //If there is a problem, remove ourself from the list of ctcs so the server doesn't send messages to us (we are forgotten :( )
        }
    }
}