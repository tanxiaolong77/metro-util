package com.quanjing.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.PushPayload.Builder;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

@Service("jpush")
public class JPushUtil {

	private static Logger logger = LoggerFactory.getLogger(JPushUtil.class);

	private String appKey;

	private String masterSecret;
	
	private boolean isProduct=true;

	public JPushClient pc;

	

	public JPushUtil() {
	}

	public JPushUtil(String appKey, String masterSecret,boolean isProduct) {
		this.appKey = appKey;
		this.masterSecret = masterSecret;
		this.isProduct=isProduct;
		pc = new JPushClient(masterSecret, appKey);
	}

	@PostConstruct
	private void initial() {
		Properties properties = new Properties();
		try {
			properties.load(this.getClass().getResourceAsStream("/jpush.properties"));
			
			appKey = properties.getProperty("appKey");
			masterSecret = properties.getProperty("masterSecret");
			
			pc = new JPushClient(masterSecret, appKey);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	public boolean sendNotification(String alert, String title, Map<String, String> extras, Collection<String> devices) {
		try {
			if (!StringUtils.isNullOrEmpty(alert) && !devices.isEmpty()) {
				
				Builder bd = PushPayload.newBuilder();
				/**
				 * 别名推送
				 */
				PushPayload pl  = bd.setPlatform(Platform.android_ios())
						.setAudience(Audience.alias(devices))
						.setNotification(Notification.android(alert, title, extras))
						.build();
				PushResult pr = pc.sendPush(pl);
				
				boolean result = pr.isResultOK();
				if (!result) {
					pl = bd.setPlatform(Platform.ios())
							.setAudience(Audience.alias(devices))
							.setNotification(Notification.ios(alert, extras))
							.build();
					pr = pc.sendPush(pl);
				}
				
				logger.info("devices:" + JsonUtil.toJsonString(devices) + "--alert:" + alert + "--title:" + title + "--extras:" + extras == null ? "" : JsonUtil.toJsonString(extras) + "--pushResult:" + pr.isResultOK());
				return pr.isResultOK();
			}
		} catch (APIConnectionException e) {
			e.printStackTrace();
		} catch (APIRequestException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	/**
	 * 推送到所有设备
	 * @param alert
	 * 通知内容
	 * @param data
	 * 附加数据
	 * @return
	 */
	public boolean sendNotificationWithAppKey(String alert,Map data) {
		try {
			if (!StringUtils.isNullOrEmpty(alert)) {
				
				Options opt=Options.newBuilder().build();
				opt.setApnsProduction(isProduct);
				PushPayload pl2=cn.jpush.api.push.model.PushPayload.newBuilder().setPlatform(Platform.ios()).setOptions(opt)
			            .setAudience(Audience.all())
			            .setNotification(Notification.ios(alert, data)).build();
						
				
				PushResult pr2 = pc.sendPush(pl2);
				logger.info("ios device------" + "alert:" + alert + "--pushResult:" + pr2.isResultOK());
						
				
				PushPayload pl1=cn.jpush.api.push.model.PushPayload.newBuilder().setPlatform(Platform.android())
	            .setAudience(Audience.all())
	            .setNotification(Notification.android(alert,null, data)).build();
				PushResult pr = pc.sendPush(pl1);
				logger.info("android device------" + "alert:" + alert + "--pushResult:" + pr.isResultOK());
				
				
						
				return pr.isResultOK();
			}
		} catch (APIConnectionException e) {
			e.printStackTrace();
		} catch (APIRequestException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean sendWithAlias(List alias,Map data,String alert) {
		try {
			if (!StringUtils.isNullOrEmpty(alert)) {
				
				Map map=new HashMap();
				map.put("platform", "all");
				Map audience=new HashMap();
				map.put("audience", audience);
				audience.put("alias", alias);
				
				Map notification=new HashMap();
				map.put("notification", notification);
				notification.put("alert", alert);
				Map android=new HashMap();
				notification.put("android", android);
				android.put("extras", data);
				Map ios=new HashMap();
				notification.put("ios", ios);
				ios.put("extras", data);
				
				Map options=new HashMap();
				map.put("options", options);
				options.put("apns_production", isProduct);
				
				PushResult pr = pc.sendPush(JsonUtil.toJson(map));
				logger.info("alert:" + alert + "--pushResult:" + pr.isResultOK());
				return pr.isResultOK();
			}
		} catch (APIConnectionException e) {
			e.printStackTrace();
		} catch (APIRequestException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	
	/**
	 * 推送到所有设备
	 * 
	 * @param alert
	 *            通知内容
	 * @return
	 */
	public boolean sendNotificationWithAppKey(String alert) {
		try {
			if (!StringUtils.isNullOrEmpty(alert)) {
				PushResult pr = pc.sendNotificationAll(alert);
				logger.info("all device------" + "alert:" + alert + "--pushResult:" + pr.isResultOK());
				return pr.isResultOK();
			}
		} catch (APIConnectionException e) {
			e.printStackTrace();
		} catch (APIRequestException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	
}
