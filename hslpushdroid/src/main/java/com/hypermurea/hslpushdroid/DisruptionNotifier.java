package com.hypermurea.hslpushdroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;

public class DisruptionNotifier {
	
	private SharedPreferences preferences;
	
	public DisruptionNotifier(SharedPreferences preferences) {
		this.preferences = preferences;
	}
	
	
	public void wakeDeviceAndNotify(Context ctx, String from, String message) {
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
        notification.defaults = Notification.DEFAULT_ALL;
        
        preferences.edit().putString(ApplicationModule.LAST_DISRUPTION_PREFERENCE, message).commit();
        
        // TODO This should probably be a setting
        //notification.flags = Notification.FLAG_INSISTENT;
        
        final int NOTIFICATION_ID = 1234;
        notificationManager.notify(NOTIFICATION_ID, notification);
	}

}
