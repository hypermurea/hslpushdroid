package com.hypermurea.hslpushdroid.gcm;

import android.content.Context;

public class DevelopmentGCMRegistrationService implements GCMRegistrationService {
	
	public void registerForGcmMessaging(Context ctx) {
		GCMIntentService.broadcastRegistrationId(ctx, "dummy-reg-id");
	}

}
