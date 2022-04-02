package Client;

//package Client;

import java.io.*;
import java.net.*;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ConnectionToServer implements Runnable {
    private Talker talker;
    private JLabel messageLabel;
    private String id;  //This is the ID of the user (username)

    boolean keepReceiving;
    
    public ConnectionToServer(Socket socket) throws IOException {
        talker = new Talker(socket); //Create talker
    }

    public void startThread(){
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
