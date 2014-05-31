package test.server.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
 
/**
 * The class extends the Thread class so we can receive and send messages at the same time
 */
public class TCPServer_Copy{
 
    public static final int SERVERPORT = 4444;
    private static PrintWriter mOut;
    private Socket client;
    int counter = 0;
 
    public static void main(String[] args) {
 
    	establishConnection("Jack Johnson");
 
    }
 
    /**
     * Constructor of the class
     * @param messageListener listens for the messages
     */
    public TCPServer_Copy() {
    }
 
    /**
     * Method to send the messages from server to client
     * @param message the message sent by the server
     */
    public static void sendMessage(String message){
        if (mOut != null && !mOut.checkError()) {
            mOut.println(message);
            mOut.flush();
        }
    }
 
    public static void establishConnection(String message) {

        try {
            System.out.println("Waiting for device connection...");

            //create a server socket. A server socket waits for requests to come in over the network.
            ServerSocket serverSocket = new ServerSocket(SERVERPORT);
 
            //create client socket... the method accept() listens for a connection to be made to this socket and accepts it.
            Socket client = serverSocket.accept();
            System.out.println("Device connection established. Sending Message");
            
            try {
 
                //sends the message to the client
                mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
 
               sendMessage(message);
                	
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
               client.close();
               mOut.close();
               System.out.println("Connection Closed. Goodnight");
            }
 
        } catch (Exception e) {
           System.out.println("Error: " + e.getMessage());
        }
    }
    
 
    public void setClient(Socket client){
    	this.client = client;
    }
    
 
 
}