package com.ctl.fileupload.utils;

import java.util.HashMap;
import java.util.Map;

public class FileUploadProperties {
	
	//Singleton
	private FileUploadProperties() {}
	
	private volatile static FileUploadProperties instance;
	
	public static FileUploadProperties getInstance() {
		if(instance == null){
			synchronized (FileUploadProperties.class) {
				if(instance == null)
					instance = new FileUploadProperties();
			}
		}
		return instance;
	}
	
	private Map<String, String> properties = new HashMap<>();
	
	public void setProperty(String propertyName, String propertyValue) {
		properties.put(propertyName, propertyValue);
	}
	
	public String getProperty(String propertyName) {
		return properties.get(propertyName);
	}

}
