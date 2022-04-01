package Client;

//package Client;

import java.io.*;
import java.net.*;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ConnectionToServer implements Runnable {
    private Talker talker;
    private Socket socket;
    private JLabel messageLabel;
    private String id;

    boolean keepReceiving;
    
    public ConnectionToServer(String id, JLabel messageLabel){
        try {
            socket = new Socket("localhost", 1234); //Try sending a request to the server
            this.messageLabel = messageLabel;       //If that succeeded, then set our references
            this.id = id;                           //
            this.keepReceiving = true;              //We will be receiving
        } catch (IOException io){
            System.out.println("[Connection To Server] Couldn't establish a connection to the server. Exiting");
            JOptionPane.showMessageDialog(null, "Error connecting to the server. Exiting.");    //Show message dialog if there was a problem, and exit the program.
            System.exit(0);
        }

        try {
            talker = new Talker(socket, id);
        } catch (IOException io){
            JOptionPane.showMessageDialog(null, "Error connecting to the server. The program will exit!");  //Show message dialog if there was a problem, and exit the program.
            System.exit(0);
        }

        new Thread(this).start();
    }

    public void run(){
        send(id);   //Sends the id to the server

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

    //Wrapper method
    public void send(String msg){
        try {
            talker.send(msg);
        } catch (IOException io){
            JOptionPane.showMessageDialog(null, "Error sending message to server. The program will exit!"); //Show message dialog if there was a problem, and exit the program.
            System.exit(0);
        }
    }

    private void handleMessage(String msg){
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                messageLabel.setText(msg);
            }
        });
    }
}
