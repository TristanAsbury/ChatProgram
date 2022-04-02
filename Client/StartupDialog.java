package Client;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class StartupDialog extends JDialog implements DocumentListener, ActionListener{
    
    ConnectionToServer cts;

    //Login & Register Buttons
    JButton loginButton;
    JButton registerButton;

    //Username & Password Fields
    JTextField usernameField;
    JTextField passwordField;

    //Other? Layouts, JPanels, Labels
    JLabel usernameLabel;
    JLabel passwordLabel;

    GroupLayout inputLayout;
    JPanel inputPanel;
    JPanel buttonPanel;

    public StartupDialog(ConnectionToServer cts){
        this.cts = cts;
        setupUI();
        setupDialog();
    }

    private void setupUI(){

        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        usernameField = new JTextField(20);
        passwordField = new JTextField(20);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);

        //Creat the actual input panel
        inputPanel = new JPanel();

        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        
        //Create the actual button panel
        buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        inputLayout = new GroupLayout(inputPanel);
        inputPanel.setLayout(inputLayout);
        inputLayout.setAutoCreateGaps(true);
        inputLayout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = inputLayout.createSequentialGroup();
        hGroup.addGroup(inputLayout.createParallelGroup().
                    addComponent(usernameLabel)
                    .addComponent(passwordLabel));
        hGroup.addGroup(inputLayout.createParallelGroup().
                    addComponent(usernameField)
                    .addComponent(passwordField));
        inputLayout.setHorizontalGroup(hGroup);
        GroupLayout.SequentialGroup vGroup = inputLayout.createSequentialGroup();
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(usernameLabel)
                    .addComponent(usernameField));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(passwordLabel)
                    .addComponent(passwordField));
        inputLayout.setVerticalGroup(vGroup);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    public void insertUpdate(DocumentEvent e){
        //If the username or password fields are changed, check if they are valid
        if(e.getDocument() == usernameField.getDocument() || e.getDocument() == passwordField.getDocument()){
            loginButton.setEnabled(usernameField.getText().trim() != "" && passwordField.getText().trim() != "");
            registerButton.setEnabled(usernameField.getText().trim() != "" && passwordField.getText().trim() != "");
        }
    }

    public void removeUpdate(DocumentEvent e){ 
        //If the username or password fields are changed, check if they are valid
        if(e.getDocument() == usernameField.getDocument() || e.getDocument() == passwordField.getDocument()){
            loginButton.setEnabled(usernameField.getText().trim() != "" && passwordField.getText().trim() != "");
            registerButton.setEnabled(usernameField.getText().trim() != "" && passwordField.getText().trim() != "");
        }
    }

    public void changedUpdate(DocumentEvent e){ }

    public void actionPerformed(ActionEvent e){
        //
    }

    private void setupDialog(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize(400, 200);
        setLocation((int)d.getWidth()/2, (int)d.getHeight()/2);
        setTitle("Living Things");
        setVisible(true);
    }
}
