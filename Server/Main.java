package Server;

import java.io.IOError;
import java.io.IOException;
import java.net.*;

class Main {
    public static void main(String[] args){
        try {
            System.out.println("Creating server...");
            Server mainServer = new Server();
            mainServer.startAcceptingConnections(); //If the server was constructed successfully, then start accepting user connections

        } catch (IOException e) {
            System.out.println("Problem starting server. Exiting.");
            System.exit(0);
        }
    }
}