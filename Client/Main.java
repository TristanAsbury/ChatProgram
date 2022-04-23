package Client;

import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args){
        System.out.println("Connecting to server...");  
        BuddyFrame buddyFrame = null;
        StartupDialog startupDialog = new StartupDialog(buddyFrame);
    }
}