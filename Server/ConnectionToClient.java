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

    public ConnectionToClient(Socket socket, Server server){
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
            handleMessage(msg);
            System.out.println("[CTC " + id + "] Received: " + msg);
        } catch (IOException io){
            System.out.println("[CTC " + id + "] Problem receiving message from client.");
            receiving = false;  //Stop trying to receive messages
            ctcs.remove(this);  //Remove this ctc from the list of ctcs so the server stops trying to send messages through us!
        }
    }

    private void handleMessage(String msg){
        if(msg.equals("USER_REGISTER")){
            System.out.println("User registering");
            
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