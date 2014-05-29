package com.techblogon.serviceexample;

import android.content.Context;
import android.os.Vibrator;

public class VibrateThread implements Runnable{
	
	boolean running = true;
	Context context;
	
	public VibrateThread(Context context){
		this.context = context;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (running){
			((Vibrator)context.getSystemService(context.VIBRATOR_SERVICE)).vibrate(1000);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex){
				
			}
		}
	}
	
	public void setRunning(boolean running){
		this.running = running;
	}

}
