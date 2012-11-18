package com.hypermurea.hslpushdroid.gcm;

import android.content.Context;

public interface GCMRegistrationService {
	
	public static final String REGISTRATION_ID = "registrationId";
	public static final String REGISTERED_ACTION = "com.hypermurea.hslpushdroid.registered";
	
	public void registerForGcmMessaging(Context ctx);
}
