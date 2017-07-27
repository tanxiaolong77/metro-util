package com.quanjing.util;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class BeanFactory implements ApplicationContextAware{

	private static ApplicationContext ac;
    
	
	public static <T> T getBean(Class<T> clazz){
		return ac.getBean(clazz);
	}
	
	public static Object getBean(String beanName){
		return ac.getBean(beanName);
	}
	
	
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		this.ac=arg0;		
	}
	
	public static String getMessage(String key) {
		String str=null;
		try{
			str= ac.getMessage(key,null, Locale.SIMPLIFIED_CHINESE);
		}catch(Exception e){
		}
		return str;
	}

}
