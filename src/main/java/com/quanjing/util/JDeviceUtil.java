package com.quanjing.util;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.device.DeviceClient;
import cn.jpush.api.device.TagListResult;

@Service("jpush_device")
public class JDeviceUtil {

	private String appKey;

	private String masterSecret;

	public DeviceClient dc;

	public JDeviceUtil() {
	}

	public JDeviceUtil(String appKey, String masterSecret) {
		this.appKey = appKey;
		this.masterSecret = masterSecret;
	}

	@PostConstruct
	private void initial() {
		Properties properties = new Properties();
		try {
			properties.load(this.getClass().getResourceAsStream("/jpush.properties"));
			appKey = properties.getProperty("appKey");
			masterSecret = properties.getProperty("masterSecret");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getDeviceClient() {
		dc = new DeviceClient(masterSecret, appKey);
	}

	/**
	 * @param maxRetryTimes
	 */
	public void getDeviceClient(int maxRetryTimes) {
		dc = new DeviceClient(masterSecret, appKey, maxRetryTimes);
	}

	/**
	 * 获取设备的tag列表
	 * @return
	 */
	public List<String> getTagList() {
		try {
			TagListResult list = dc.getTagList();
			return list.tags;
		} catch (APIConnectionException e) {
			e.printStackTrace();
		} catch (APIRequestException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		JDeviceUtil ju = new JDeviceUtil("bfe5efa7b229186953b08e1c", "08d075948a3112de26e6cf27");
		ju.getDeviceClient();
		List<String> list = ju.getTagList();
		System.out.println(list);
	}
}
