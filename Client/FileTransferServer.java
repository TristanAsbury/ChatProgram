package Client;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;


public class FileTransferServer implements Runnable {

    ServerSocket servSock;
    DataInputStream dis;
    FileOutputStream fos;
    long totalFileSize;

    public FileTransferServer(Long fileSize, String fileName) throws IOException {
        System.out.println("Created file transfer server");
        fos = new FileOutputStream(new File(fileName));
        servSock = new ServerSocket(1111);
        new Thread(this).start();
        this.totalFileSize = fileSize;
    }

    public void run(){
        Socket inputSocket = null;
        try {
            System.out.println("Waiting for connection...");
            inputSocket = servSock.accept();
            System.out.println("Accepted socket!");
        } catch (IOException io){            
            System.out.println("Very bad!");
        }

        try{
            dis = new DataInputStream(inputSocket.getInputStream());    //Create the input stream (from the network)
            byte[] buffer = new byte[128];                              //Create empty buffer
            int numBytesRead = dis.read(buffer);                        //Read bytes into buffer
            long totalBytesRead = (long)numBytesRead;
            fos.write(buffer, 0, numBytesRead);                     //Write those bytes into the file

            do {
                numBytesRead = dis.read(buffer);                        //Read bytes in from server
                totalBytesRead += (long)numBytesRead;
                fos.write(buffer, 0, numBytesRead);                 //Write to file
            } while (totalBytesRead < totalFileSize);                     //Keep receiving bytes until we have received all

            System.out.println("Done receiving file");

            dis.close();
            fos.close();

        } catch (IOException io){
            JOptionPane.showMessageDialog(null, "Error receiving file...", "File error.", JOptionPane.ERROR_MESSAGE);
        }

    }
}
