package Client;

//package Client;
import java.io.*;
import java.net.*;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class ConnectionToServer implements Runnable {
    private Talker talker;
    private JLabel messageLabel;
    private String id;  //This is the ID of the user (username)
    BuddyFrame buddyFrame;

    boolean keepReceiving;
    
    public ConnectionToServer(Socket socket) throws IOException {
        talker = new Talker(socket); //Create talker
        this.keepReceiving = false;
    }

    public void startThread(){
        this.keepReceiving = true;
        new Thread(this).start();
    }

    public void run(){
        while(keepReceiving){
            try {
                String msg = talker.receive();
                handleMessage(msg);
            } catch (IOException io){
                JOptionPane.showMessageDialog(null, "Error receiving message from server. The program will exit!"); //Show message dialog if there was a problem, and exit the program.
                System.exit(0);
            }
        }
    }

    public void setBuddyFrame(BuddyFrame buddyFrame){
        this.buddyFrame = buddyFrame;
    }

    //Wrapper method
    public void send(String msg){
        try {
            talker.send(msg);
        } catch (IOException io){
            JOptionPane.showMessageDialog(null, "Error sending message to server. The program will exit!"); //Show message dialog if there was a problem, and exit the program.
        }
    }

    public String receive(){
        String retString = null;
        try {
            retString = talker.receive();
        } catch (IOException io){

        }
        return retString;
    }

    private void handleMessage(String msg){
        if(msg.startsWith("BUDDY_INCOMING")){
            String[] parts = msg.split(" ");
            buddyFrame.addBuddy(new Buddy(parts[1], Boolean.parseBoolean(parts[2])));
        } else if(msg.startsWith("INCOMING_BUDDYREQ")){
            String requestersUsername = msg.split(" ")[1];
            int option = JOptionPane.showConfirmDialog(null, "Accept " + requestersUsername, "Incoming Buddy Request", JOptionPane.YES_NO_OPTION);
            if(option == JOptionPane.YES_OPTION){
                send("BUDDYREQ_ACCEPT " + requestersUsername);
            } else {
                send("BUDDYREQ_DENY " + requestersUsername);
            }
        }
    }
}
