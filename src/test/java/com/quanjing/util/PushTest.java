package com.quanjing.util;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.quanjing.util.JPushUtil;
import com.quanjing.util.TimeUtil;

public class PushTest {
	
	public final static boolean test = true;
	public final static Long sendAndroidUser = 1135712l;
	public final static Long sendIosUser = 1074090L;
	public static Long sendUser = sendIosUser;
	
	
	public static void main(String[] args) {
		
		JPushUtil jutil = new JPushUtil("a520157cdde590ae544a61a3", "eba77a431f7ad4efbd98c0c6",false);
//		if (test) {
//			jutil = new JPushUtil("a520157cdde590ae544a61a3", "eba77a431f7ad4efbd98c0c6",false);
//		}
		
		//圈子
		send(jutil , "1043182539998819" , "2" , "圈子内容","旅行，让生活更美好！上海南站欢迎您！");
		//用户图片
		//send(jutil , "1043200104367722" , "3" , "用户图片","图片标题");
		
		//活动
		//send(jutil , "34" , "8" , "活动内容","活动标题");
		//图片分类
		//sendImageCategory(jutil , "京味" , "7" , "图片分类内容","图片分类标题");
		//发现-图文故事
		//send(jutil , "1020" , "6" , "优雅的糕点内容","优雅的糕点");
	}
	
	//发送圈子、活动、图片
	public static void send(JPushUtil jutil , String dateId , String dateType , String content , String alert) {
		Map<String, String> extras = new HashMap<String, String>();
		extras.put("act", "5");
		extras.put("actTime", TimeUtil.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
		extras.put("dataId", dateId);
		extras.put("dataType", dateType);
		extras.put("content",content);
		//String title = "推送测试标题";
		List<Long> li=new ArrayList();
		li.add(sendUser);
		boolean boo = jutil.sendWithAlias(li, extras, alert);
		 System.out.println(boo);
	}
	//发送图片分类
	public static void sendImageCategory(JPushUtil jutil , String imageTypeName , String dateType , String content , String alert) {
		Map<String, String> extras = new HashMap<String, String>();
		extras.put("act", "5");
		extras.put("actTime", TimeUtil.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
		extras.put("data", imageTypeName);
		extras.put("dataType", dateType);
		extras.put("content",content);
		//String title = "推送测试标题";
		List<Long> li=new ArrayList();
		li.add(sendUser);
		 boolean boo = jutil.sendWithAlias(li, extras, alert);
		 System.out.println(boo);
	}
}
