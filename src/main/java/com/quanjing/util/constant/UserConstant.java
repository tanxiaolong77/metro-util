package com.quanjing.util.constant;


public class UserConstant {
	/**
	 * redis中找回密码验证短信对应key后缀
	 */
	public final static String SENDMSGFINDPWD_SUFFIX = "_msgByFindPwd";
	/**
	 * redis中手机号注册验证短信对应key后缀
	 */
	public final static String SENDMSGREGPHONE_SUFFIX = "_msgByRegPhone";
	
	
	public final static String SENDMSGLogin_SUFFIX = "_msgBylogin";
	
	
	
	/**
	 * redis中图片分类
	 */
	public final static String Image_CATE = "Image_CATE";
	/**
	 * redis中图片根分类
	 */
	public final static String Image_CATE_ROOT = "ImageCATE_ROOT";
	
	

	public final static String User_collect = "User_collect_";

	public final static String User_collect_set = "user_collect_set_";
	
	

	
	
	
	/**
	 * 通过注册手机找回密码
	 */
	public static final String FINDPWD_BYPHONE = "findPwdByPhone";
	
	/**
	 * redis中手机号注册验证码时效
	 */
	public final static int SENDMSGREGPHONE_TIME = 60*5;
	
	/**
	 * redis中手机号修改密码验证码时效
	 */
	public final static int FINDPWDBYPHONEFUNCTION_TIME = 60*5;
	
	/**
	 * redis中用户登录记录ticket时效(单位:s)
	 */
	public final static int USER_TICKET_TIME = 60*30;
	
	
	public final static int COOKIE_TIME = 2*365*24*60*60;
	
	
	
	/**
	 * 用户密码最小长度
	 */
	public static final int PWD_MINLEN = 6;
	/**
	 * 用户密码最大长度
	 */
	public static final int PWD_MAXLEN = 16;
	/**
	 * 用户真实姓名最大长度
	 */
	public static final int REALNAMEMAXLENGTH = 20;
	
}
