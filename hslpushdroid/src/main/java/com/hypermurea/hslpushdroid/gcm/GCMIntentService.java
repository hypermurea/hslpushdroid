package com.hypermurea.hslpushdroid.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.hypermurea.hslpushdroid.R;

public class GCMIntentService extends GCMBaseIntentService {
	
	private static final String TAG ="hslpushdroid.GCMBaseIntentService";
	
	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub	
		Log.e(TAG, "onError invoked");
	}

	@Override
	protected void onMessage(Context ctx, Intent intent) {
		Log.e(TAG, "onMessage invoked");
		
		final String TRAFFIC_DISRUPTION_DESCRIPTION = "desc";
		wakeUpNotification(ctx, "HSLPush", intent.getExtras().getString(TRAFFIC_DISRUPTION_DESCRIPTION));
	}
	
	private void wakeUpNotification(Context ctx, String from, String message) {
		// credit where credit is due:
		// http://www.stonetrip.com/developer/wiki/index.php?title=Android_Intents,_Notifications_and_External_Applications
		
	    // Set pm to Power Service so that we can access the power options on device
        PowerManager powerManager = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);        

        // Check if the screen itself is off or not. If it is then this code below will FORCEFULLY wake up the device even if it is put into sleep mode by user
        if(!powerManager.isScreenOn()) {
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
            PowerManager.WakeLock cpuWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
        	
            wakeLock.acquire(10000);
            cpuWakeLock.acquire(10000);
        }
        
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, new Intent(), 0);
        Notification notification = new Notification(R.drawable.icon, message, System.currentTimeMillis());
        notification.setLatestEventInfo(ctx, from, message, contentIntent);     
       
        notification.flags = Notification.FLAG_INSISTENT;
        final int NOTIFICATION_ID = 1234;
        notificationManager.notify(NOTIFICATION_ID, notification);
	}

	@Override
	protected void onRegistered(Context ctx, String registrationId) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onRegistered invoked");
		broadcastRegistrationId(ctx, registrationId);
	}
	
	public static void broadcastRegistrationId(Context ctx, String registrationId) {
		Intent intent = new Intent();
		intent.setAction(GCMRegistrationService.REGISTERED_ACTION);
		intent.putExtra(GCMRegistrationService.REGISTRATION_ID, registrationId);
		Log.d(TAG, "registrationId acquired: " + registrationId);
		ctx.sendBroadcast(intent);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onUnregistered invoked");
	}

}
