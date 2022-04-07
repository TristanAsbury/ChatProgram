package Client;

import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args){
        try {
            System.out.println("Connecting to server...");  
            Socket socket = new Socket("localhost", 1234);  //Will throw an IOException if connection is unsuccessful

            ConnectionToServer cts = new ConnectionToServer(socket);    //Will throw an IOException if connection is unsuccessful

            //If there is a successful connection.
            System.out.println("Connected to server... starting client");
            BuddyFrame buddyFrame = new BuddyFrame(cts);
            StartupDialog startupDialog = new StartupDialog(cts, buddyFrame);
            

        } catch (IOException io){
            JOptionPane.showMessageDialog(null, "Error connecting to server. Exiting.", "Error!", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
}