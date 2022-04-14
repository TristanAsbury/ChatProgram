package Client;

import javax.swing.event.*;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import java.awt.event.*;
import javax.swing.*;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.BorderLayout;

class ChatBox extends JDialog implements ActionListener, DocumentListener {
    String buddyName;
    ConnectionToServer cts;
    String username;
    boolean isClosed;

    JButton sendButton;
    JTextField inputField;
    JEditorPane chatPane;

    JPanel inputPanel;

    public ChatBox(String buddyName, ConnectionToServer cts){
        this.buddyName = buddyName;
        this.cts = cts;
        this.isClosed = false;

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        inputField = new JTextField(40);

        chatPane = new JEditorPane();
        chatPane.setEditable(false);
        chatPane.setContentType("text/html");
        chatPane.setText(
            "<div>Chatting with: "+ buddyName + "</div>"
        );

        inputPanel = new JPanel();
        inputPanel.add(inputField);
        inputPanel.add(sendButton);

        add(chatPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setupDialog();
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == sendButton){
            cts.send("OUTGOING_MSG " + buddyName + " " + inputField.getText());
            addText(inputField.getText(), 1);
        }
    }

    public void insertUpdate(DocumentEvent e){

    }

    public void changedUpdate(DocumentEvent e){

    }

    public void removeUpdate(DocumentEvent e){

    }

    public boolean isClosed(){
        return this.isClosed;
    }

    public void addText(String txt, int side){
        HTMLDocument doc;
        Element html;
        Element body;

        doc = (HTMLDocument)chatPane.getDocument();
        html = doc.getRootElements()[0];
        body = html.getElement(1);

        try {
            String htmlText = "";
            if(side == 0){
                htmlText = "<div><p align=\"left\">" + txt + "</p></div>";
            } else {
                htmlText = "<div><p align=\"right\">" + txt + "</p></div>";
            }
            doc.insertBeforeEnd(body, htmlText);
            chatPane.setCaretPosition(chatPane.getDocument().getLength());
        } catch (Exception e){
            System.out.println("Problem adding text to pane!");
        }
    }

    private void setupDialog(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize(600, 400);
        setLocation((int)d.getWidth()/2, (int)d.getHeight()/2);
        setTitle(buddyName);
        setVisible(true);
    }

}