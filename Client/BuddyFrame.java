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
            //Creat new add buddy dialog (MAKE YOUR OWN CLASS)
            String addBuddyName = JOptionPane.showInputDialog(null, "Input Buddy Username: ");
            if(addBuddyName == null || addBuddyName.trim().equals("") || addBuddyName.equals(username)){
                //Do nothing
            } else {
                //Add buddy
                cts.send("OUTGOING_BUDDYREQ " + addBuddyName);
            }
        } else if (e.getSource() == logoutButton){
            cts.send("LOGOUT");
            StartupDialog newDialog = new StartupDialog(cts, this);
            this.setVisible(false);
        }
    }

    public void mouseClicked(MouseEvent e){
        if(e.getClickCount() == 2){
            Point mousePos = e.getPoint();
            int buddyIndex = buddyList.locationToIndex(mousePos);
            
            if(buddyIndex >= 0){
                String buddyName = buddyList.getModel().getElementAt(buddyIndex).username;
                ChatBox buddyChatBox = buddyChatBoxes.get(buddyName);
                
                if(buddyChatBox == null || !buddyChatBox.isDisplayable()){
                    buddyChatBox = new ChatBox(buddyName, cts);
                    buddyChatBoxes.put(buddyName, buddyChatBox);
                }
                buddyChatBox.requestFocus();
            }
        }
    }

    public void sendMessage(String buddyName, String msg){
        ChatBox buddyChatBox = buddyChatBoxes.get(buddyName);
        if(buddyChatBox == null || !buddyChatBox.isDisplayable()){
            buddyChatBox = new ChatBox(buddyName, cts);
            buddyChatBoxes.put(buddyName, buddyChatBox);
        }
        buddyChatBox.requestFocus();
        buddyChatBox.addText(msg, 0);
    }

    public void mousePressed(MouseEvent e){

    }

    public void mouseEntered(MouseEvent e){

    } 

    public void mouseReleased(MouseEvent e){

    }

    public void mouseExited(MouseEvent e){

    }

    

    private void setupFrame(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize(300, 700);
        setLocation((int)d.getWidth()/2, (int)d.getHeight()/2);
        setTitle("Logged In: waiting...");
        setVisible(false);
    }
}
