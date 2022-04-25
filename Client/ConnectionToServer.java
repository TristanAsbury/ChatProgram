// package Client;

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
        currentThread = new Thread(this);
        currentThread.start();
    }

    public void stopThread(){
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

        if(msg.startsWith("BUDDY_INCOMING")){               //This is called when a user is receiving a buddy
            String[] parts = msg.split(" ");                //Get the parts
            buddyFrame.addBuddy(new Buddy(parts[1], Boolean.parseBoolean(parts[2])));   //Add buddy to the buddyModel
        
        } else if(msg.startsWith("INCOMING_BUDDYREQ")){     //If a user is requesting to be a buddy
            String requestersUsername = msg.split(" ")[1];  //Get the requesters username

            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    int option = JOptionPane.showConfirmDialog(null, "Accept " + requestersUsername, "Incoming Buddy Request", JOptionPane.YES_NO_OPTION);  //Open joptionpane to get yes or no answer
                    if(option == JOptionPane.YES_OPTION){
                        send("BUDDYREQ_ACCEPT " + requestersUsername);
                    } else {
                        send("BUDDYREQ_DENY " + requestersUsername);
                    }
                }
            });
            
        } else if(msg.startsWith("BUDDYREQ_ERROR_BNE")) {   //If the user doesn't exist (Buddy Not Exist)
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    JOptionPane.showMessageDialog(null, "User doesn't exist...");
                }
            });

        }else if(msg.startsWith("INCOMING_MSG")){       //INCOMING MESSAGE
            String[] parts = msg.split(" ");            //Get the parts
            String buddyUsername = parts[1];                    //Get the username that sent it
            String chatMsg = msg.substring(parts[0].length() + parts[1].length() + 1);  //Get the actual message

            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    buddyFrame.receiveMessage(buddyUsername, chatMsg);      //Receive message in  buddyframe
                }
            });

        } else if(msg.startsWith("BUDDY_STATUS")){      //Buddy status update
            String[] parts = msg.split(" ");            //Get the parts
            String buddyName = parts[1];                        //Get the buddy username
            boolean online = Boolean.parseBoolean(parts[2]);    //Get if they are online

            for(int i = 0; i < buddyFrame.buddyModel.size(); i++){  //Go through all of our friends
                if(buddyFrame.buddyModel.get(i).username.equals(buddyName)){    //If we found the buddy
                    buddyFrame.buddyModel.set(i, new Buddy(buddyName, online)); //set him to a new buddy (online/offline)
                }
            }

        } else if(msg.startsWith("ASK_FILE_REQUEST")){  //When a user receives a file send request
            String[] parts = msg.split(" ");            //Get the parts
            String fromUsername = parts[1];                     //Get the username who is sending the file
            String fileName = parts[2];                         //Get the file name
            String fileLength = parts[3];                       //Get the size of the file in bytes
            String question = "Accept " + fileName + " from " + fromUsername + "?" + " File size: " + fileLength + " bytes";

            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    int confirmation = JOptionPane.showConfirmDialog(null, question);
            
                    if(confirmation == JOptionPane.YES_OPTION){
                        send("ACCEPTED_FILE_REQUEST " + fromUsername);  //Tell the server we accepted the file send request
                        try {
                            FileTransferServer fts = new FileTransferServer(Long.parseLong(fileLength), fileName);
                        } catch (IOException io){
                            SwingUtilities.invokeLater(new Runnable(){
                                public void run(){
                                    JOptionPane.showMessageDialog(null, "Error receiving file...");
                                }
                            });
                        }
                    } else {
                        send("DENIED_FILE_REQUEST " + fromUsername);
                    }        
                }
            });
            
        } else if(msg.startsWith("START_FILE_TRANSFER")){
            String[] parts = msg.split(" ");
            String toUser = parts[1];
            String ipAddress = parts[2];
            int port = Integer.parseInt(parts[3]);
            try {
                Thread.sleep(1000); //Sleep for a second to wait for the server to start
                buddyFrame.buddyChatBoxes.get(toUser).startFileTransfer(ipAddress, port);
            } catch (InterruptedException ie){
                System.out.println("Problem sleeping");
            }
        }

        else if(msg.startsWith("FILE_SEND_ERROR_UO")){
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    JOptionPane.showMessageDialog(null, "User offline. Cannot send file.");
                }
            });
        }

        else if(msg.startsWith("FORCE_LOGOUT")){
            System.exit(0);
        }

        else if(msg.startsWith("BUDDYREQ_OFFLINE")){
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    JOptionPane.showMessageDialog(null, "User is offline. They will be notified when they log in.");
                }
            });
        }
    }

    public ConnectionToServer getDuplicateCTS(){
        return new ConnectionToServer(this.talker);
    }
}
