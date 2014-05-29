package com.techblogon.serviceexample;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {
	TestThread ringer;
	//Chronometer chronometer;
	private WakeLock screenWakeLock;
	DevicePolicyManager mDPM;
	ComponentName mAdminName;
	
	private final int REQUEST_ENABLE = 1;
	
	boolean chronometerRunning = false;
	boolean onResumeCalled = false;
	boolean initialized = false;
	Ringtone r;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("yolo", "onCreateCalled");
		
		if (!initialized){
			initializeServices();
			initialized = true;
		}
		
		boolean shouldRing = readIntentSetCaller();
		
		//chronometer = (Chronometer) findViewById(R.id.chronometer1);
		if (shouldRing){
			startRinging();
		}
	}
	
	public void initializeServices(){
		mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
		mAdminName = new ComponentName(this, MyAdmin.class);
		
		KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		lock.disableKeyguard();
		screenWakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
			     PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
		
		screenWakeLock.acquire();
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		
		deviceAdminSetup();
		showNotification();
		
		Log.d("yolo", "starting service");
		startService(new Intent(this, MyService.class));
		Log.d("yolo", "service started... starting ringer");
	}
	
	public boolean readIntentSetCaller(){
		Intent intent = getIntent();
		
		String name = intent.getStringExtra("name");
		if (name != null && name.length() != 0){
			TextView caller = (TextView) findViewById(R.id.textView1);
			caller.setText(name);
		}
		
		return intent.getBooleanExtra("shouldRing", false);
	}
	
	public void showNotification(){
		// prepare intent which is triggered if the
		// notification is selected

		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		// build notification
		// the addAction re-use the same intent to keep the example short
		Notification n  = new Notification.Builder(this)
		.setTicker("Listener Running")
		.setContentTitle("Listening")
		.setContentText("Hello")
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentIntent(pIntent).getNotification();
		n.flags = Notification.FLAG_ONGOING_EVENT;
		    
		  
		NotificationManager notificationManager = 
		  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, n); 
	}
	
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
	
	public void deviceAdminSetup(){
		if (!mDPM.isAdminActive(mAdminName)) {
			// try to become active – must happen here in this activity, to get result
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "text stuff");
			startActivityForResult(intent, REQUEST_ENABLE);

		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus){
		super.onWindowFocusChanged(hasFocus);
		Log.d("yolo", "onwindowfocuschanged " + hasFocus);
		if (hasFocus){
			startRinging();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void startRinging(){
		r.play();
	}
	
	public void stopRinging(){
		r.stop();
	}
	
	public void endCallClickListener(View v){
		if (chronometerRunning){
			//chronometer.stop();
			chronometerRunning = false;
		}
		stopRinging();

		lockScreen();
	}
	
	public void lockScreen(){
		//if (!mDPM.isAdminActive(mAdminName)) {
			// try to become active – must happen here in this activity, to get result
		//	Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		//	intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
		//	intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "text stuff");
		//	startActivityForResult(intent, REQUEST_ENABLE);

		//} else {
		// Already is a device administrator, can do security operations now.
		screenWakeLock.release();
		mDPM.lockNow();
		//}
	}
	
	public void takeCallClickListener(View v){
		//chronometer.setBase(SystemClock.elapsedRealtime());
        //chronometer.start();
        chronometerRunning = true;
       
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
 
		stopRinging();
	}
	
	public static class MyAdmin extends DeviceAdminReceiver {
		// implement onEnabled(), onDisabled(), …
	}

}