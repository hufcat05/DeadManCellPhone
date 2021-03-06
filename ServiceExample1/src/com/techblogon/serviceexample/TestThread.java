package com.techblogon.serviceexample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class TestThread implements Runnable{
	public static final String SERVERIP = "192.168.1.4"; //your computer IP address
    public static final int SERVERPORT = 4444;
	
    BufferedReader in;
    Context context;
    
	public TestThread(Context context){
		this.context = context;
	}

	@Override
	public void run() {
		try {
			InetAddress serverAddr = InetAddress.getByName(SERVERIP);
			Log.d("yolo", "outside while");
			while (true){
			
				Socket socket = null;
				String serverMessage = null;
				try {
					//create a socket to make the connection with the server
					Log.d("yolo", "Attempting Connection");
	                socket = new Socket(serverAddr, SERVERPORT);
	               Log.d("yolo", "Connection Successful");
	                //receive the message which the server sends back
	                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	                
	                if (in != null){
	        			serverMessage = in.readLine();
	        		}
	                
	                if (serverMessage != null){
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
	                socket.close();
	                in.close();
				} catch (ConnectException ex){
					Log.d("yolo","Failed");
				} 
			}
		} catch (UnknownHostException e){
			Log.e("yolo", "Exception", e);
		} catch (IOException ex){
			Log.e("yolo", "Exception", ex);
		}
	}
	
}
