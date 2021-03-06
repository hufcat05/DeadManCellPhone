package com.techblogon.serviceexample;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity {
	//Chronometer chronometer;
	private WakeLock screenWakeLock;
	DevicePolicyManager mDPM;
	ComponentName mAdminName;
	
	private final int REQUEST_ENABLE = 1;
	
	boolean chronometerRunning = false;
	boolean onResumeCalled = false;
	boolean initialized = false;
	MediaPlayer mPlayer;
	RingThread ringThread;
	
	Ringtone r;

	
	//Initialization method
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("yolo", "onCreateCalled");
		
		//Modify buttons
		modifyButtons();
		
		//Starting admin and other basic services that need to be initialized
		if (!initialized){
			initializeServices();
			initialized = true;
		}
		
		//Reads incoming intent
		Log.d("yolo", "Reading intent");
		//boolean shouldRing = readIntentSetCaller();
		
		//chronometer = (Chronometer) findViewById(R.id.chronometer1);
		//if (shouldRing){
			//startRinging();
		//}
	}
	
	/**
	 * Modifies the buttons to have onTouch properties
	 */
	public void modifyButtons(){
		ImageButton answer = (ImageButton) findViewById(R.id.imageButton1);
		ImageButton hangup = (ImageButton) findViewById(R.id.imageButton2);
		
		answer.setOnTouchListener(new View.OnTouchListener() {

	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            if(event.getAction() == (MotionEvent.ACTION_UP)){
	                ((ImageButton)v).setImageResource(R.drawable.phone3);
	                takeCallClickListener(v);
	            }
	            else{
	            	 ((ImageButton)v).setImageResource(R.drawable.phone2pressed);
	            }
	            return true;
	        }
	    });
		
		hangup.setOnTouchListener(new View.OnTouchListener() {

	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            if(event.getAction() == (MotionEvent.ACTION_UP)){
	                ((ImageButton)v).setImageResource(R.drawable.hangup);
	                endCallClickListener(v);
	            }
	            else{
	            	 ((ImageButton)v).setImageResource(R.drawable.iconphonepressed);
	            }
	            return true;
	        }
	    });
	}
	
	public void initializeServices(){
		//Takes care of device admin (this is what allows us to lock the screen programmatically
		mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
		mAdminName = new ComponentName(this, MyAdmin.class);
		
		//Acquires a lock that bypasses HTC's lock screen. This way when the app receives a signal it displays the call screen and not the lock screen
		KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		lock.disableKeyguard();
		screenWakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
			     PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
		//Wakes up the screen and bypasses lock screen
		screenWakeLock.acquire();
		
		//Ensures that the app is full screen so that the notification bar cannot be viewed
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//This method takes care of setting up the ringtone
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		//More ringtone setup
		/*
		try {
			mPlayer = new MediaPlayer();
			mPlayer.setDataSource(this, notification);
			final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			mPlayer.setAudioStreamType(AudioManager.STREAM_RING);
			mPlayer.setLooping(true);
			mPlayer.prepareAsync();
			
		} catch (IOException ex){
			Log.d("yolo", "IOException loading ringtone: " + ex.getMessage());
		}
		*/
		//If necessary, requests device admin from user
		deviceAdminSetup();
		
		//Starts TCP listening service
		Log.d("yolo", "starting service");
		startService(new Intent(this, MyService.class));
		Log.d("yolo", "service started... starting ringer");
	}
	
	@Override
	public void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		Log.d("yolo","OnNewIntent called");
		//Acquires a lock that bypasses HTC's lock screen. This way when the app receives a signal it displays the call screen and not the lock screen
		KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		lock.disableKeyguard();
		screenWakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
			     PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
		//Wakes up the screen and bypasses lock screen
		screenWakeLock.acquire();
		
		//Ensures that the app is full screen so that the notification bar cannot be viewed
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		boolean shouldRing = readIntentSetCaller(intent);
		Log.d("yolo", "shouldRing: " + shouldRing);
		
		//***********************************************
		//Delete this section if phone rings by itself
		String name = intent.getStringExtra("name");
		
		if (name != null && name.contains("stop")){
			shouldRing = false;
		} else {
			shouldRing = true;
		}
		//************************************************
		if (shouldRing){
			startRinging();
		}
		
		intent.removeExtra("name");
	}
	
	/*
	 * Reads the incoming intent for the name of the caller, determines if the phone should start ringing
	 */
	public boolean readIntentSetCaller(){
		Intent intent = getIntent();
		
		String name = intent.getStringExtra("name");
		Log.d("yolo", "Name: " + name);
		if (name != null && name.length() != 0){
			if (name.contains("stop")){
				Log.d("yolo", "contained stop");
				stopRinging();
				lockScreen();
				return false;
			} else {
				TextView caller = (TextView) findViewById(R.id.textView1);
				caller.setText(name);
				return true;
			}
		} else {
			return false;
		}
	}
	
	/*
	 * Reads the incoming intent for the name of the caller, determines if the phone should start ringing
	 */
	public boolean readIntentSetCaller(Intent intent){
		
		String name = intent.getStringExtra("name");
		Log.d("yolo", "Name: " + name);
		if (name != null && name.length() != 0){
			if (name.contains("stop")){
				Log.d("yolo", "contained stop");
				stopRinging();
				lockScreen();
				return false;
			} else {
				TextView caller = (TextView) findViewById(R.id.textView1);
				caller.setText(name);
				return true;
			}
		} else {
			return false;
		}
	}
	
	
	/*
	 * Don't worry about this code, it's necessary, but nonsense
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (REQUEST_ENABLE == requestCode){
		if (resultCode == Activity.RESULT_OK) {
		// Has become the device administrator.
			Log.d("yolo", "result ok");
		} else {
		//Canceled or failed.
			Log.d("yolo", "result not ok");
		}
		}
	}
	
	/*
	 * Sets up device admin
	 */
	public void deviceAdminSetup(){
		if (!mDPM.isAdminActive(mAdminName)) {
			// try to become active � must happen here in this activity, to get result
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "text stuff");
			startActivityForResult(intent, REQUEST_ENABLE);

		}
	}
	
	/*
	 * Code that is run when the window has been turned on by the app. Starts ringing
	 * (non-Javadoc)
	 * @see android.app.Activity#onWindowFocusChanged(boolean)
	 */
	/*
	@Override
	public void onWindowFocusChanged(boolean hasFocus){
		super.onWindowFocusChanged(hasFocus);
		
		PowerManager pm = (PowerManager)
				getSystemService(Context.POWER_SERVICE);
				boolean isScreenOn = pm.isScreenOn();
		boolean shouldRing = readIntentSetCaller();
		Log.d("yolo", "shouldRing: " + shouldRing);
		Log.d("yolo", "onwindowfocuschanged " + hasFocus);
		if (hasFocus && isScreenOn){
			if (!r.isPlaying()){
				startRinging();
			} else { 
				stopRinging();
				lockScreen();
			}
		}
	}
	*/
	/*
	 * More nonsense code
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	/*
	 * Starts the ringer and vibrator
	 */
	public void startRinging(){
		ringThread = new RingThread(r);
		ringThread.start();
	}
	
	/*
	 * Stops the ringer and vibrator
	 */
	public void stopRinging(){
		if (ringThread != null){
			ringThread.setRing(false);
		}
	}
	//new comment
	/*
	 * Method that is called when the hangup button is pressed.
	 * Stops ringing and locks the screen
	 */
	public void endCallClickListener(View v){
		if (chronometerRunning){
			//chronometer.stop();
			chronometerRunning = false;
		}
		stopRinging();

		lockScreen();
	}
	
	/**
	 * Locks the screen
	 */
	public void lockScreen(){
		try {
			screenWakeLock.release();
		} catch (Exception e){
			
		}
		mDPM.lockNow();
		//}
	}
	
	/**
	 * Method that is called when the answer button is pressed
	 * Stops the ringing and locks the screen on.
	 * @param v
	 */
	public void takeCallClickListener(View v){
		//chronometer.setBase(SystemClock.elapsedRealtime());
        //chronometer.start();
        chronometerRunning = true;
       
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
 
		stopRinging();
	}
	
	public static class MyAdmin extends DeviceAdminReceiver {
		// implement onEnabled(), onDisabled(), �
	}

}