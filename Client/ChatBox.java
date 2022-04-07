package Client;

import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.*;

class ChatBox extends JDialog implements ActionListener, DocumentListener {
    String buddyName;
    ConnectionToServer cts;

    JButton sendButton;
    JTextField inputField;
    JEditorPane chatPane;

    public ChatBox(String buddyName, ConnectionToServer cts){
        this.buddyName = buddyName;
        this.cts = cts;

        sendButton = new JButton("Send");
        inputField = new JTextField(40);

        chatPane = new JEditorPane();
        
        setupDialog();
    }

    public void actionPerformed(ActionEvent e){

    }

    public void insertUpdate(DocumentEvent e){

    }

    public void changedUpdate(DocumentEvent e){

    }

    public void removeUpdate(DocumentEvent e){

    }

    private void setupDialog(){

    }

}