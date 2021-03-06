package com.techblogon.serviceexample;

import android.media.Ringtone;

public class RingThread extends Thread{

	boolean ring = true;
	Ringtone r;
	
	public RingThread(Ringtone r){
		this.r = r;
	}
	
	@Override
	public void run(){
		super.run();
		int counter = 0;
		while (ring){
			counter++;
			if (counter < 10){
				try {
					r.play();
					Thread.sleep(2000);
					System.out.println("done sleeping");
					r.stop();
					Thread.sleep(1000);
				} catch (InterruptedException ex){
					
				}
			} else {
				ring = false;
			}
		}
	}
	
	public void setRing(boolean ring){
		this.ring = ring;
		
		if (!ring){
			r.stop();
		}
	}
}
