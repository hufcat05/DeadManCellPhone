package com.techblogon.serviceexample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
 
public class TCPClient implements Runnable{
 
    private String serverMessage;
    public static final String SERVERIP = "192.168.1.4"; //your computer IP address
    public static final int SERVERPORT = 4444;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;
    Context context;
 
    PrintWriter out;
    BufferedReader in;
 
    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener, Context context) {
        mMessageListener = listener;
        this.context = context;
    }
 
    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }
 
    public void stopClient(){
        mRun = false;
    }
 
    @Override
    public void run() {
 
        mRun = true;
 
        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);
 
            Log.e("TCP Client", "C: Connecting...");
 
            Socket socket = null;
            try {
                //in this while the client listens for the messages sent by the server
                boolean connected = false;
                while (mRun) {
                	
                	try {
                		if (!connected){
	                		//create a socket to make the connection with the server
	                        socket = new Socket(serverAddr, SERVERPORT);
	                        //send the message to the server
	                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
	                        //receive the message which the server sends back
	                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	                        connected = true;
                		}
                		if (in != null){
                			serverMessage = in.readLine();
                		}
	 
	                    if (serverMessage != null && mMessageListener != null) {
	                        //call the method messageReceived from MyActivity class
	                    	if (serverMessage.contains("heartbeat")){
	                    		sendMessage("heartbeat");
	                    	} else {
	                    		Intent intent = new Intent(context, MainActivity.class);
		    					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    					
	                    		if (serverMessage.contains("off")){
	                    			intent.putExtra("name", "stop");
	                    			intent.putExtra("shouldRing", false);
	                    		} else {
			                    	String[] messageInfo = serverMessage.split(":");
			    					String name = messageInfo[messageInfo.length - 1];
			    					
			    					Log.d("yolo", "Message Received");
			    					intent.putExtra("name", name);
			    					intent.putExtra("shouldRing", true);
	                    		}
		    					context.startActivity(intent);
	                    	}
	                    }
	                    serverMessage = null;
                	} catch (ConnectException ex){
                		Log.d("yolo", "Connect Exception: " + ex.getMessage());
                	}
 
                }
 
            } catch (Exception e) {
 
                Log.e("TCP", "S: Error", e);
 
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }
 
        } catch (Exception e) {
 
            Log.e("TCP", "C: Error", e);
 
        }
 
    }
 
    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}
