package Server;

import java.io.*;
import java.net.*;

public class Talker {
    private BufferedReader bis;
    private DataOutputStream dos;
    private String id;

    public Talker(Socket socket, String id) throws IOException{
        bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dos = new DataOutputStream(socket.getOutputStream());
        System.out.println("[Talker] Created streams from socket!");
        this.id = id;
    }

    public Talker(Socket socket) throws IOException{
        bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dos = new DataOutputStream(socket.getOutputStream());
        System.out.println("[Talker] Created streams from socket!");
        this.id = "pending";
    }

    public String receive() throws IOException{
        String msg = bis.readLine();
        System.out.println("[Talker "+id+"] RECEIVED: " + msg);
        return msg;
    }

    public void send(String msg) throws IOException{
        dos.writeBytes(msg + '\n');
        System.out.println("[Talker "+id+"] SENT: " + msg);
    }
}