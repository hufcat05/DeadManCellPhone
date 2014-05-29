package com.techblogon.serviceexample;

import android.util.Log;
import android.widget.Toast;

public class TestThread implements Runnable{
	
	MainActivity activity;
	boolean running = true;
	
	public TestThread(MainActivity activity){
		this.activity = activity;
	}

	@Override
	public void run() {
		Log.d("yolo", "In ringer");
		int x = 0;
		while (running){
			//Toast.makeText(activity, "My Service Started", Toast.LENGTH_LONG).show();
			try {
				x++;
				Log.d("yolo", "sleeping: " + x);
				Thread.sleep(2000L);
			} catch (InterruptedException ex){
				Log.d("yolo", "Exception");
			}
		}
		
	}
	
	public void stop(){
		running = false;
	}
	
	public void start(){
		running = true;
	}

}
