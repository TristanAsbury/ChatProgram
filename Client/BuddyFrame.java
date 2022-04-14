package Client;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.event.MouseInputListener;

import java.awt.event.*;
import java.util.Hashtable;
import java.awt.*;

public class BuddyFrame extends JFrame implements ActionListener, MouseListener {
    ConnectionToServer cts;

    String username;    //The username at the top
    JList<Buddy> buddyList;
    DefaultListModel<Buddy> buddyModel;
    
    JButton addBuddyButton;

    Hashtable<String, ChatBox> buddyChatBoxes;

    public BuddyFrame(ConnectionToServer cts){
        this.cts = cts;
        this.buddyChatBoxes = new Hashtable<String, ChatBox>();
        
        buddyModel = new DefaultListModel<Buddy>();
        buddyList = new JList<Buddy>(buddyModel);
        buddyList.addMouseListener(this);

        addBuddyButton = new JButton("Add Buddy");
        addBuddyButton.addActionListener(this);
        add(buddyList, BorderLayout.CENTER);
        add(addBuddyButton, BorderLayout.SOUTH);

        setupFrame();
    }

    public void setUsername(String username){
        this.username = username;
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
        }
    }

    public void mouseClicked(MouseEvent e){
        if(e.getClickCount() == 2){
            Point mousePos = e.getPoint();
            int buddyIndex = buddyList.locationToIndex(mousePos);
            
            if(buddyIndex >= 0){
                String buddyName = buddyList.getModel().getElementAt(buddyIndex).username;
                ChatBox chatBox = buddyChatBoxes.get(buddyName);
                
                if(chatBox != null){
                    chatBox.requestFocus();
                } else {
                    ChatBox buddyChatBox = new ChatBox(buddyName, cts);
                    buddyChatBoxes.put(buddyName, buddyChatBox);
                }
            }
        }
    }

    public void sendMessage(String buddyName, String msg){
        ChatBox buddyChatBox = buddyChatBoxes.get(buddyName);
        if(buddyChatBox == null){
            buddyChatBox = new ChatBox(buddyName, cts);
            buddyChatBoxes.put(buddyName, buddyChatBox);
        }
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
        setTitle("Logged In: " + username);
        setVisible(false);
    }
}
