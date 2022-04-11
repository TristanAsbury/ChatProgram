package Client;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class AddBuddyDialog extends JDialog implements DocumentListener, ActionListener{
    
    ConnectionToServer cts;

    //Login & Register Buttons
    JButton sendButton;
    JButton cancelButton;

    //Username & Password Fields
    JTextField usernameField;
    String sendersUsername;

    //Other? Layouts, JPanels, Labels
    JLabel usernameLabel;
    JLabel passwordLabel;

    GroupLayout inputLayout;
    JPanel inputPanel;
    JPanel buttonPanel;

    BuddyFrame buddyFrame;

    public AddBuddyDialog(ConnectionToServer cts, String sendersUsername){
        this.cts = cts;
        this.sendersUsername = sendersUsername;

        setupUI();
        setupDialog();
    }

    private void setupUI(){

        usernameLabel = new JLabel("Buddy Username:");
        usernameField = new JTextField(20);
        usernameField.getDocument().addDocumentListener(this);

        sendButton = new JButton("Send Request");
        sendButton.addActionListener(this);
        sendButton.setEnabled(false);

        //Creat the actual input panel
        inputPanel = new JPanel();

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setEnabled(false);
        
        //Create the actual button panel
        buttonPanel = new JPanel();
        buttonPanel.add(sendButton);
        buttonPanel.add(cancelButton);

        inputLayout = new GroupLayout(inputPanel);
        inputPanel.setLayout(inputLayout);
        inputLayout.setAutoCreateGaps(true);
        inputLayout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = inputLayout.createSequentialGroup();
        hGroup.addGroup(inputLayout.createParallelGroup().
                    addComponent(usernameLabel));
        hGroup.addGroup(inputLayout.createParallelGroup().
                    addComponent(usernameField));
        inputLayout.setHorizontalGroup(hGroup);
        GroupLayout.SequentialGroup vGroup = inputLayout.createSequentialGroup();
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(usernameLabel)
                    .addComponent(usernameField));
        inputLayout.setVerticalGroup(vGroup);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    public void insertUpdate(DocumentEvent e){
        //If the username or password fields are changed, check if they are valid
        if(e.getDocument() == usernameField.getDocument()){
            sendButton.setEnabled(!usernameField.getText().contains(" ")
            && usernameField.getText().trim() != "" 
            && usernameField.getText().trim() != sendersUsername);
        }
    }

    public void removeUpdate(DocumentEvent e){ 
        //If the username or password fields are changed, check if they are valid
        if(e.getDocument() == usernameField.getDocument()){
            sendButton.setEnabled(!usernameField.getText().contains(" ")
            && usernameField.getText().trim() != "" 
            && usernameField.getText().trim() != sendersUsername);
        }
    }

    public void changedUpdate(DocumentEvent e){ }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == sendButton){
            cts.send("OUTGOING_BUDDYREQ " + usernameField.getText());
        }

        if(e.getSource() == cancelButton){
            dispose();
        }
    }

    private void setupDialog(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize(400, 200);
        setLocation((int)d.getWidth()/2, (int)d.getHeight()/2);
        setTitle("Login");
        setVisible(true);
    }
}
