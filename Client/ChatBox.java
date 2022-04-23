package Client;

import javax.swing.event.*;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.BorderLayout;
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

    java.util.List<File> files;
    DefaultListModel<String> fileNames;

    

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
                htmlText = "<div><p style=\"font-family: sans-serif; position: absolute; bottom: 0;\" align=\"left\">" + txt + "</p></div>";
            } else {
                htmlText = "<div><p style=\"font-family: sans-serif; position: absolute; bottom: 0;\" align=\"right\">" + txt + "</p></div>";
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


    public void dragEnter(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub
        System.out.println("Dragged into!");
    }


    public void dragOver(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub
        System.out.println("Dragged outta!");
    }


    public void dropActionChanged(DropTargetDragEvent dtde) { 

    }


    public void dragExit(DropTargetEvent dte) {
        if(dte.getSource() == dropTarget){

        }
    }

    public void drop(DropTargetDropEvent dtde) {
        System.out.println("DROPPED!");
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
        try {
            Socket outSocket = new Socket(ipAddress, port);
            
            byte[] buffer = new byte[128];

            File file = files.get(0);

            FileInputStream fis = new FileInputStream(files.get(0));

            long fileSize = file.length();
            long bytesRead = (long)fis.read(buffer);

            do {
                
            } while (bytesRead < fileSize);

            

        } catch (IOException io){

        }
        
    }



}