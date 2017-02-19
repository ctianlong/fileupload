package com.ctl.fileupload.listener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.ctl.fileupload.utils.FileUploadProperties;
@WebListener()
public class FileUploadListener implements ServletContextListener {
	
	public FileUploadListener() {
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		InputStream in = getClass().getClassLoader().getResourceAsStream("/upload.properties");
		Properties properties = new Properties();
		try {
			properties.load(in);
			for(Map.Entry<Object, Object> prop: properties.entrySet()){
				String propertyName = (String) prop.getKey();
				String propertyValue = (String) prop.getValue();
				FileUploadProperties.getInstance().setProperty(propertyName, propertyValue);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
