package com.hufford.designs;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.swing.JOptionPane;

public class IPCServer {

	public static void main(String[] args) {
		String name = "";
		String ringer = "on";
		String heartBeat = "";
		if (args.length >= 1){
			name = args[0];
		}
		if (args.length >= 2){
			ringer = args[1];
		}
		if (args.length == 3){
			heartBeat = args[2];
		}
	      try {
	         ServerSocket srvr = new ServerSocket(1234);
	         srvr.setSoTimeout(5000);
	         Socket skt = srvr.accept();
	         System.out.print("Server has connected!\n");
	         PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
	         out.print(name + ":" + ringer + ":" + heartBeat);
	         out.flush();
	         out.close();
	         skt.close();
	         srvr.close();
	      } catch (SocketTimeoutException e){
	    	  JOptionPane.showMessageDialog(null, "The message server has shut down unexpectedly, please revert to backup cue. When you have time, please restart the message server.");
	      }
	      catch(Exception e) {
	         JOptionPane.showMessageDialog(null, "There was an error sending the message. Please revert to backup cue");
	         e.printStackTrace();
	      }
	}

}
