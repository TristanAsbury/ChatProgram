package Client;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JList;
import java.awt.event.*;
import java.util.Hashtable;
import java.awt.*;

public class BuddyFrame extends JFrame implements ActionListener, MouseListener {
    ConnectionToServer cts;

    String username;    //The username at the top
    JList<Buddy> buddyList;
    DefaultListModel<Buddy> buddyModel;
    
    JButton addBuddyButton;
    JButton logoutButton;

    JPanel buttonPanel;

    Hashtable<String, ChatBox> buddyChatBoxes;

    public BuddyFrame(ConnectionToServer cts){
        this.cts = cts;
        this.buddyChatBoxes = new Hashtable<String, ChatBox>();
        
        buttonPanel = new JPanel();

        buddyModel = new DefaultListModel<Buddy>();
        buddyList = new JList<Buddy>(buddyModel);
        buddyList.addMouseListener(this);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);
        buttonPanel.add(logoutButton);

        addBuddyButton = new JButton("Add Buddy");
        addBuddyButton.addActionListener(this);
        add(buddyList, BorderLayout.CENTER);
        buttonPanel.add(addBuddyButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setupFrame();
    }

    public void setUsername(String username){
        this.username = username;
        setTitle("Logged In: " + username);
    }

    public void addBuddy(Buddy buddy){
        buddyModel.addElement(buddy);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == addBuddyButton){
            boolean goodToAddBuddy = true;
            String addBuddyName = JOptionPane.showInputDialog(null, "Input Buddy Username: "); //Create new add buddy dialog

            if(addBuddyName == null || addBuddyName.trim().equals("") || addBuddyName.equals(username)){     //If the name is nothing, or our own name, its bad
                //Dont do anything
            } else {
                for(int i = 0; i < buddyModel.size(); i++){              //Go through all buddies
                    if(addBuddyName.equals(buddyModel.get(i).username)){ //Is this buddy the buddy we are gonna add
                        JOptionPane.showMessageDialog(null, "Already a buddy!", "Existing Buddy!", JOptionPane.ERROR_MESSAGE); //If so, error
                        goodToAddBuddy = false;
                    }
                }

                if(goodToAddBuddy){
                    cts.send("OUTGOING_BUDDYREQ " + addBuddyName);  // Else, add the buddy
                }
            }
        } else if (e.getSource() == logoutButton){                  //If the user pressed log out
            cts.stopThread();                                       //Stop the cts thread
            cts.send("LOGOUT");                                //Send LOGOUT from ctc
            StartupDialog newDialog = new StartupDialog(this);      //Create a new Dialog

            for(int i = 0; i < buddyModel.size(); i++){
                ChatBox buddyChatBox = buddyChatBoxes.get(buddyModel.get(i).username);
                if(buddyChatBox != null){
                    buddyChatBox.dispose();
                }
            }
            setVisible(false);                                  //Make the buddy list invisible
        }
    }

    public void mouseClicked(MouseEvent e){
        if(e.getClickCount() == 2){                                 //If the user double clicks a user
            Point mousePos = e.getPoint();                          //Get position of mouse
            int buddyIndex = buddyList.locationToIndex(mousePos);   //Get the index at the position of the mouse
            
            if(buddyIndex >= 0){
                String buddyName = buddyList.getModel().getElementAt(buddyIndex).username;  //Get the buddy at the index
                ChatBox buddyChatBox = buddyChatBoxes.get(buddyName);                       //Get the chatbox for that buddy
                
                if(buddyChatBox == null || !buddyChatBox.isDisplayable()){                  //If that chatbox is null, or is invisible
                    buddyChatBox = new ChatBox(buddyName, cts);                             //Create a new chatbox
                    buddyChatBoxes.put(buddyName, buddyChatBox);                            //Assign it to that buddy
                }
                buddyChatBox.requestFocus();                        //Request the focus
            }
        }
    }

    public void receiveMessage(String buddyName, String msg){       //When sending a message
        ChatBox buddyChatBox = buddyChatBoxes.get(buddyName);       //Get the buddy chatbox
        if(buddyChatBox == null || !buddyChatBox.isDisplayable()){  //If that box isn't existent
            buddyChatBox = new ChatBox(buddyName, cts);     //Create a new chatbox
            buddyChatBoxes.put(buddyName, buddyChatBox);    //Assign the chatbox to a buddy
        }
        buddyChatBox.requestFocus();
        buddyChatBox.addText(buddyName, msg, 0);
    }

    public void mousePressed(MouseEvent e){ }

    public void mouseEntered(MouseEvent e){ } 

    public void mouseReleased(MouseEvent e){ }

    public void mouseExited(MouseEvent e){  }

    private void setupFrame(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize(300, 700);
        setLocation((int)d.getWidth()/2, (int)d.getHeight()/2);
        setTitle("Logged In: waiting...");
        setVisible(false);
        setResizable(false);
    }
}
