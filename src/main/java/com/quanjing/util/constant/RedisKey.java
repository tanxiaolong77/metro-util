package com.quanjing.util.constant;

public class RedisKey {
	
	/**
	 * 用户昵称头像信息
	 */
	public final static String user_show = "usershow_";

	/**
	 * 用户登陆信息
	 */
	public final static String user_login = "user_login_";

	
	/**
	 * redis里存储的视频md5值，主要用于判断图片重复
	 */
	public final static String vedio_md5_map = "vedio_md5";

	
	/**
	 * 系统密钥
	 */
	public final static String secret_key = "metro-user_secretKey";

}
