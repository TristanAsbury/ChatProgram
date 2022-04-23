package Client;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Server.Server;

public class FileTransferServer implements Runnable {

    ServerSocket servSock;
    DataInputStream dis;
    FileOutputStream fos;
    Long totalFileSize;

    public FileTransferServer(Long fileSize, String fileName) throws IOException {
        fos = new FileOutputStream(new File(fileName));
        servSock = new ServerSocket(1111);
        new Thread(this).run();
        this.totalFileSize = fileSize;
    }

    public void run(){
        Socket inputSocket = null;
        try {
            inputSocket = servSock.accept();
        } catch (IOException io){            
            System.out.println("Very bad!");
        }

        try{
            dis = new DataInputStream(inputSocket.getInputStream());    //Create the input stream (from the network)
            byte[] buffer = new byte[128];                              //Create empty buffer
            int numBytesRead = dis.read(buffer);                        //Read bytes into buffer
            fos.write(buffer, 0, numBytesRead);                     //Write those bytes into the file

            do {
                numBytesRead = dis.read(buffer);                        //Read bytes in from server
                fos.write(buffer, 0, numBytesRead);                 //Write to file
            } while (numBytesRead < totalFileSize);      //Keep receiving bytes until we have received all

        } catch (IOException io){
            System.out.println("Problem receiving file...");
        }
    }
}
