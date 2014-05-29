package com.techblogon.serviceexample;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends IntentService{

	private static final String TAG = "MyService";
	UDPMessenger messenger;
	private TCPClient mTcpClient;
	Notification n;
	NotificationManager notificationManager;
	
	public MyService(){
		super("");
		messenger = new UDPMessenger(this);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Intent notIntent = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, notIntent, 0);

		// build notification
		// the addAction re-use the same intent to keep the example short
		n  = new Notification.Builder(this)
		.setTicker("Listener Running")
		.setContentTitle("Listening")
		.setContentText("Hello")
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentIntent(pIntent).getNotification();
		n.flags = Notification.FLAG_ONGOING_EVENT;
		
		startForeground(7336, n);
		//messenger.startMessageReceiver();
		
		mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message) {
                //this method calls the onProgressUpdate
                //publishProgress(message);
            }
        }, this);
        Thread thread = new Thread(mTcpClient);
        thread.start();

		Log.d(TAG, "onStart");
	//Note: You can start a new thread and use it for long background processing from here.
	}
	
	public void showNotification(){
		// prepare intent which is triggered if the
		// notification is selected

		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		// build notification
		// the addAction re-use the same intent to keep the example short
		n  = new Notification.Builder(this)
		.setTicker("Listener Running")
		.setContentTitle("Listening")
		.setContentText("Hello")
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentIntent(pIntent).getNotification();
		n.flags = Notification.FLAG_ONGOING_EVENT;
		    
		  
		notificationManager = 
		  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, n); 
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
		if (notificationManager != null){
			notificationManager.cancelAll();
		}
		Log.d(TAG, "onDestroy");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
	}
}
