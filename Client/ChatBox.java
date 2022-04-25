// package Client;

import javax.swing.event.*;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

class ChatBox extends JDialog implements ActionListener, DocumentListener, DropTargetListener {
    String buddyName;
    ConnectionToServer cts;
    String username;
    boolean isClosed;

    JButton sendButton;
    JTextField inputField;
    JEditorPane chatPane;
    JScrollPane mainPane;

    DropTarget dropTarget;

    JPanel inputPanel;
    boolean sendingFile;

    java.util.List<File> files;
    DefaultListModel<String> fileNames;

    public ChatBox(String buddyName, ConnectionToServer cts){
        this.buddyName = buddyName;
        this.cts = cts;
        isClosed = false;
        sendingFile = false;

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        sendButton.setEnabled(false);

        inputField = new JTextField(40);
        inputField.getDocument().addDocumentListener(this);

        chatPane = new JEditorPane();
        chatPane.setEditable(false);
        chatPane.setContentType("text/html");
        chatPane.setText(
            "<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>"
        );

        mainPane = new JScrollPane(chatPane);

        dropTarget = new DropTarget(chatPane, this);

        inputPanel = new JPanel();
        inputPanel.add(inputField);
        inputPanel.add(sendButton);

        add(mainPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setupDialog();
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == sendButton){
            cts.send("OUTGOING_MSG " + buddyName + " " + inputField.getText());
            addText(buddyName, inputField.getText(), 1);
            inputField.setText("");
            inputField.requestFocus();
        }
    }

    public void insertUpdate(DocumentEvent e){ 
        if(e.getDocument() == inputField.getDocument()){
            sendButton.setEnabled(!(inputField.getText().trim().equals("") || inputField.getText().length() <= 0));
        }
    }

    public void removeUpdate(DocumentEvent e){
        if(e.getDocument() == inputField.getDocument()){
            sendButton.setEnabled(!(inputField.getText().trim().equals("") || inputField.getText().length() <= 0));
        }
    }
    
    public void changedUpdate(DocumentEvent e){ }

    public boolean isClosed(){
        return this.isClosed;
    }

    public void addText(String buddyName, String txt, int side){
        HTMLDocument doc;
        Element html;
        Element body;

        doc = (HTMLDocument)chatPane.getDocument();
        html = doc.getRootElements()[0];
        body = html.getElement(1);

        try {
            String htmlText = "";
            if(side == 0){
                htmlText = "<div><p style=\"font-family: sans-serif; position: absolute; color: orange;\" align=\"left\">" + buddyName + ": " + txt + "</p></div>";
            } else {
                htmlText = "<div><p style=\"font-family: sans-serif; position: absolute; font-weight: bold; color: blue;\" align=\"right\">" + txt + " :You</p></div>";
            }
            doc.insertBeforeEnd(body, htmlText);
            chatPane.setCaretPosition(chatPane.getDocument().getLength());
        } catch (Exception e){
            System.out.println("Problem adding text to pane!");
        }
    }

    public void dragEnter(DropTargetDragEvent dtde) {
        chatPane.setBackground(new Color(123,123,123));
    }

    public void dragExit(DropTargetEvent dte) {
        chatPane.setBackground(new Color(255, 255, 255));
    }

    public void dragOver(DropTargetDragEvent dtde) { }


    public void dropActionChanged(DropTargetDragEvent dtde) { }

    public void drop(DropTargetDropEvent dtde) {
        chatPane.setBackground(new Color(255, 255, 255));
        
        files = null;
        fileNames = new DefaultListModel<String>();
        Transferable transferableData;
        
        transferableData = dtde.getTransferable();

        try {
            if(transferableData.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                files = (java.util.List<File>)transferableData.getTransferData(DataFlavor.javaFileListFlavor);
            }
        }catch(UnsupportedFlavorException uf){
            System.out.println("Unsupported file");
        }catch(IOException io){
            System.out.println("Bad io exception.");
        }

        if(files.size() > 1){
            System.out.println("File name: " + files.get(0).getName());
        } else {
            //EXAMPLE: FILE_SEND_REQUEST john file.txt 1031261278
            //Server sees this, redirects to john
            cts.send("FILE_SEND_REQUEST " + buddyName + " " + files.get(0).getName() + " " + files.get(0).length());
        }
    }

    public void startFileTransfer(String ipAddress, int port){
        System.out.println("Starting file transfer to " + buddyName + " with address: " + ipAddress + " on port " + port);
        try {
            Socket outSocket = new Socket(ipAddress, port);
            
            byte[] buffer = new byte[128];                              //Create empty buffer;

            File file = files.get(0);                               //get the file

            InputStream fis = new FileInputStream(files.get(0));    //Create the file input stream (take in file)
            OutputStream os = outSocket.getOutputStream();                  //Get the output stream

            long totalFileSize = file.length();                 //Get the filesize
            int numBytesRead = fis.read(buffer);                //Get the number of bytes read
            long totalBytesRead = numBytesRead;                 //Create the total bytes read
            os.write(buffer, 0, numBytesRead);              //Write those bytes
            
            do {
                numBytesRead = fis.read(buffer);                //Read bytes
                totalBytesRead += numBytesRead;                 //Add those bytes
                os.write(buffer, 0, numBytesRead);         //Write to file
            } while (totalBytesRead < totalFileSize);           //Do this while we still have bytes to read

            System.out.println("Done sending file");        //
            
            os.close();             //Close the output stream
            fis.close();            //Close the file stream
            outSocket.close();      //Close the socket
        } catch (IOException io){

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
        setResizable(false);
    }
}