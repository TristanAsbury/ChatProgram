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

    public ConnectionToClient(Socket socket, Vector<ConnectionToClient> ctcs){
        try {
            talker = new Talker(socket);
            id = talker.receive();  //Receives the first message which will be the id from the client
            System.out.println("[Connection To Client] Client id is: " + id);

            this.ctcs = ctcs;       //Set our reference to the set of CTCS to the one we passed
        } catch (IOException io){
            System.out.println("Problem connecting to client...");
        }

        receiving = true;           //Set receiving to true.
        new Thread(this).start();   //Start the separate thread
    }

    public void run(){
        while(receiving){
            receive();
        }
    }

    private void receive(){
        try{
            String msg = talker.receive();  //Receive the text
            broadcast(msg);
            System.out.println("[CTC " + id + "] Received: " + msg);
        } catch (IOException io){
            System.out.println("[CTC " + id + "] Problem receiving message from client.");
            receiving = false;  //Stop trying to receive messages
            ctcs.remove(this);  //Remove this ctc from the list of ctcs so the server stops trying to send messages through us!
        }
    }

    private void broadcast(String msg){
        for(ConnectionToClient ctc : ctcs){ //Use a for each loop
            if(ctc != this){                //If the ctc we are looking at isn't us
                ctc.send(id + ": " + msg);  //Send the message to that ctc
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