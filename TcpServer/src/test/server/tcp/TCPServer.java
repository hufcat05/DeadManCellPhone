package test.server.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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
public class TCPServer extends Thread {
 
    public static final int SERVERPORT = 4444;
    private boolean running = false;
    private PrintWriter mOut;
    private OnMessageReceived messageListener;
    private static Socket client;
    private boolean heartBeatReceived = true;
    static ServerBoard frame;
    int counter = 0;
    int disconnectCounter = 0;
 
    public static void main(String[] args) {
 
        //opens the window where the messages will be received and sent
        frame = new ServerBoard();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
            	try {
            		client.close();
            	} catch (IOException ex){
            		
            	}
            }
        });
    }
 
    /**
     * Constructor of the class
     * @param messageListener listens for the messages
     */
    public TCPServer(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }
 
    /**
     * Method to send the messages from server to client
     * @param message the message sent by the server
     */
    public void sendMessage(String message){
        if (mOut != null && !mOut.checkError()) {
        	if (message.contains("heartbeat")){
    			if (!heartBeatReceived){
    				counter++;
    			}
    			heartBeatReceived = false;
    		}
            mOut.println(message);
            mOut.flush();
        }
    }
 
    @Override
    public void run() {
    	System.out.println("In run");
        super.run();
        while (true){
	        running = true;
	        heartBeatReceived = true;
	        counter = 0;
	 
	        try {
	            messageListener.messageReceived("Waiting for device connection...");
	
	            //create a server socket. A server socket waits for requests to come in over the network.
	            ServerSocket serverSocket = new ServerSocket(SERVERPORT);
	            
	            //create client socket... the method accept() listens for a connection to be made to this socket and accepts it.
	            Socket client = serverSocket.accept();
	            messageListener.messageReceived("Device connection established. Listening on port: " + SERVERPORT + "...");
	            
	            //starting heartbeat
	            Thread heartBeatThread = new Thread(new ConnectionThread(this));
	            heartBeatThread.start();
	            
	            try {
	 
	                //sends the message to the client
	                mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
	 
	                //read the message received from client
	                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	 
	                //in this while we wait to receive messages from client (it's an infinite loop)
	                //this while it's like a listener for messages
	                while (running) {
	                	//*****************************************************
	                	// This block listens for commands from the local .jar file launched by Cue Labs 
	                	
	                	try {
	                		
	                		Socket skt = new Socket("localhost", 1234);
	                		
	                        BufferedReader localIn = new BufferedReader(new InputStreamReader(skt.getInputStream()));
	                        messageListener.messageReceived("Cue Server Connnected");
	                        String commandMessage = null;
	                        //System.out.println("checking localin");
	                      
	                       
	                        commandMessage = localIn.readLine();
	                       
	                    	
	                    	if (commandMessage != null && messageListener != null){
	              
	                			messageListener.messageReceived("Sending Message: " + commandMessage);
	                			sendMessage(commandMessage);
	                    
	                    		skt.close();
	                    		localIn.close();
	                    	}
	                	} catch (ConnectException ex){
	                		
	                	}
	                	
	                	//*********************************************************
	                	
	                	//*********************************************************
	                	//This block listens for responses from the device
	                	String message = null;
	                	if (in.ready()){
	                		message = in.readLine();
	                	}
	                  
	                    if (message != null && messageListener != null) {
	                        //call the method messageReceived from ServerBoard class
	                    	if (!heartBeatReceived){
	                    		if (message.contains("heartbeat")){
	                    			heartBeatReceived = true;
	                    			counter = 0;
	                    		}
	                    	}
	                        messageListener.messageReceived("Phone " + disconnectCounter + ": " + message);
	                    } 
	                    
	                    
	                    //***********************************************************
	                    //This alerts if the counter has increased to 3 meaning the device hasn't responded in 90 seconds
	                    //setting running = false breaks the while loop and restarts the server
	                    if (counter == 1){
	                    	deviceDisconnectAlert();
	                    	running = false;
	                    }
	                }
	 
	            } catch (Exception e) {
	                messageListener.messageReceived("Error: " + e.getMessage());
	            } finally {
	            	counter = 0;
	               client.close();
	               serverSocket.close();
	               mOut.close();
	               heartBeatThread.stop();
	               serverSocket = null;
	               client = null;
	               mOut = null;
	               messageListener.messageReceived("Connection Closed. Goodnight");
	               disconnectCounter++;
	            }
	 
	        } catch (Exception e) {
	           messageListener.messageReceived("Error: " + e.getMessage());
	        }
        }
    }
    
 
    public void setClient(Socket client){
    	this.client = client;
    }
    
    public void setHeartBeatReceived(boolean heartBeatReceived){
    	this.heartBeatReceived = heartBeatReceived;
    }
    
    public boolean getHeartBeatReceived(){
    	return heartBeatReceived;
    }
    
    public void deviceDisconnectAlert(){
    	messageListener.messageReceived("Device has disconnected, an alert will be sent if it reconnects");
    	messageListener.messageReceived("Restarting Server...");
    }
    
    //Declare the interface. The method messageReceived(String message) will must be implemented in the ServerBoard
    //class at on startServer button click
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
    
 
}