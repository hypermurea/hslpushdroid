package com.hypermurea.hslpushdroid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.res.Resources;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class EnvironmentConfig {

	@Inject 
	protected static Provider<Resources> resourcesProvider;
	
	private Properties properties;
	
	public EnvironmentConfig() {
		try {
			  InputStream rawResource = resourcesProvider.get().openRawResource(R.raw.development);
			  properties = new Properties();
			  properties.load(rawResource);
			} catch (Resources.NotFoundException e) {
			  System.err.println("Did not find raw resource: " + e);
			} catch (IOException e) {
			  System.err.println("Failed to open microlog property file");
			}		
	}
	
	public Properties getProperties() {
		return properties;
	}
	
}
