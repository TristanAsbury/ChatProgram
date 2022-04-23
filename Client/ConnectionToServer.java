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
    BuddyFrame buddyFrame;

    Thread currentThread;
    boolean keepReceiving;
    
    public ConnectionToServer(Socket socket) throws IOException {
        talker = new Talker(socket); //Create talker
        this.keepReceiving = false;
    }

    public ConnectionToServer(Talker talker){
        this.talker = talker;
        this.keepReceiving = false;
    }

    public void startThread(){
        this.keepReceiving = true;
        new Thread(this).start();
    }

    public void stopThread(){
        System.out.println("Stopping thread...");
        this.keepReceiving = false;
    }

    public void setBuddyFrame(BuddyFrame buddyFrame){
        this.buddyFrame = buddyFrame;
    }

    public void run(){
        while(this.keepReceiving){
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

    public String receive(){
        String retString = null;
        try {
            retString = talker.receive();
            System.out.println("[CTS] Received " + retString);
        } catch (IOException io){
            System.out.println("[CTS] Error receiving message");
        }
        return retString;
    }

    private void handleMessage(String msg){

            //INCOMING BUDDY
        if(msg.startsWith("BUDDY_INCOMING")){
            String[] parts = msg.split(" ");
            buddyFrame.addBuddy(new Buddy(parts[1], Boolean.parseBoolean(parts[2])));
        
            //INCOMING BUDDY REQUEST
        } else if(msg.startsWith("INCOMING_BUDDYREQ")){
            String requestersUsername = msg.split(" ")[1];
            int option = JOptionPane.showConfirmDialog(null, "Accept " + requestersUsername, "Incoming Buddy Request", JOptionPane.YES_NO_OPTION);
            if(option == JOptionPane.YES_OPTION){
                send("BUDDYREQ_ACCEPT " + requestersUsername);
            } else {
                send("BUDDYREQ_DENY " + requestersUsername);
            }

            //INCOMING MESSAGE
        } else if(msg.startsWith("INCOMING_MSG")){
            String[] parts = msg.split(" ");
            String buddyUsername = parts[1];
            String chatMsg = msg.substring(parts[0].length() + parts[1].length() + 1);
            System.out.println("Chat Message Received: " + chatMsg);

            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    buddyFrame.sendMessage(buddyUsername, chatMsg);
                }
            });
        } else if(msg.startsWith("BUDDY_STATUS")){
            String[] parts = msg.split(" ");
            String username = parts[1];
            boolean online = Boolean.parseBoolean(parts[2]);

            for(int i = 0; i < buddyFrame.buddyModel.size(); i++){
                if(buddyFrame.buddyModel.get(i).username.equals(username)){
                    buddyFrame.buddyModel.set(i, new Buddy(username, online));
                }
            }
        } else if(msg.startsWith("ASK_FILE_REQUEST")){
            String[] parts = msg.split(" ");
            String fromUsername = parts[1];
            String fileName = parts[2];
            String fileLength = parts[3];
            String question = "Accept " + fileName + " from " + fromUsername + "?" + " File size: " + fileLength;
            int confirmation = JOptionPane.showConfirmDialog(null, question);
            
            if(confirmation == JOptionPane.YES_OPTION){
                try {
                    FileTransferServer fts = new FileTransferServer(Long.parseLong(fileLength), fileName);
                } catch (IOException io){
                    JOptionPane.showMessageDialog(null, "Error receiving file...");
                }
                send("ACCEPTED_FILE_REQUEST " + fromUsername);  //Tell the server we accepted the file send request
            } else {
                send("DENIED_FILE_REQUEST");
            }
        } else if(msg.startsWith("START_FILE_TRANSFER")){
            String[] parts = msg.split(" ");
            String toUser = parts[1];
            String ipAddress = parts[2];
            int port = Integer.parseInt(parts[3]);
            buddyFrame.buddyChatBoxes.get(toUser).startFileTransfer(ipAddress, port);
        }
    }

    public ConnectionToServer getDuplicateCTS(){
        return new ConnectionToServer(this.talker);
    }
}
