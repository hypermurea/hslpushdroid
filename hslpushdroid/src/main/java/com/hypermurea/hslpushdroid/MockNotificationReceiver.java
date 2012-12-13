package com.hypermurea.hslpushdroid;

import roboguice.receiver.RoboBroadcastReceiver;

import com.google.inject.Inject;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.os.Bundle;

public class MockNotificationReceiver extends RoboBroadcastReceiver {
	
	@Inject DisruptionNotifier disruptionNotifier;
	
	@Override
	public void handleReceive(Context ctx, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			String from = bundle.getString("from");
			String message = bundle.getString("message");
			disruptionNotifier.wakeDeviceAndNotify(ctx, from, message);
		} catch (Exception e) {
			Toast.makeText(ctx, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

}

