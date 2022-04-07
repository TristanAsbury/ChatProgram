package Client;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JList;
import java.awt.event.*;
import java.util.Hashtable;
import java.awt.Toolkit;
import java.awt.Dimension;

public class BuddyFrame extends JFrame implements ActionListener{
    ConnectionToServer cts;
    String username;
    JList buddies;
    DefaultListModel buddyModel;
    JButton addBuddyButton;

    Hashtable<String, ChatBox> buddyChatBoxes;

    public BuddyFrame(ConnectionToServer cts){
        this.cts = cts;
        this.buddyChatBoxes = new Hashtable<String, ChatBox>();
        
        setupFrame();
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void addBuddy(Buddy buddy){
        
    }

    public void actionPerformed(ActionEvent e){

    }

    private void setupFrame(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize(300, 700);
        setLocation((int)d.getWidth()/2, (int)d.getHeight()/2);
        setTitle("Buddies");
        setVisible(false);
    }
}
