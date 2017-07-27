package com.quanjing.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class HttpClientUtil {
	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	
	private static ReloadableResourceBundleMessageSource messageSource;
	
	/**
	 * 提交http请求
	 * 
	 * @param sendType  提交类型 get post
	 * @param methodUrl 功能url（访问服务器域名之后的部分）
	 * @param paramMap  参数name-vlaue集合
	 */
	public static String sendHttpRequest(String sendType, String url, Map<String, Object> paramMap){
		 return sendHttpRequest(sendType, url, paramMap, null,null);
	}
	
	public static String sendHttpRequest(String sendType, String url, Map<String, Object> paramMap,String charset){
		 return sendHttpRequest(sendType, url, paramMap, charset,null);
	}
	
	public static String sendHttpRequest(String sendType, String url, Map<String, Object> paramMap,String charset,Integer timeout){
		String response =null;
		HttpClient client = new DefaultHttpClient(); 
		//开发环境
		//client.getHostConfiguration().setHost(url, port, protocol);
		//设置编码格式
		client.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8"); 
		//提交方法
		HttpRequestBase method = null;
		//判断访问方式
		//使用 POST 方式提交数据
		if(paramMap==null){
			paramMap=new HashMap();
		}
		if("post".equals(sendType)) {
			method = getPostMethod(url, paramMap);    
			//使用 POST 方式提交数据
		} else {
			method = getGetMethod(url, paramMap);    
		}
		method.getParams().setParameter("http.protocol.content-charset",HTTP.UTF_8);  
		if(timeout==null){
			RequestConfig config = RequestConfig.custom()
				    .setConnectionRequestTimeout(8000).setConnectTimeout(6000)
				    .setSocketTimeout(5000).build();
			method.setConfig(config);
		}else{
			RequestConfig config = RequestConfig.custom().setSocketTimeout(timeout)  
			        .setConnectTimeout(timeout)  
				    .setConnectionRequestTimeout(timeout).build();
			method.setConfig(config);
		}
		
		
		try {
			//提交请求
			HttpResponse httpresponse=client.execute(method);
			logger.info("\n "+url+"------		:" + httpresponse.getStatusLine());
			
			if(httpresponse.getStatusLine().getStatusCode()==200){
				HttpEntity entity = httpresponse.getEntity();
				// 获取返回数据             HttpEntity entity = httpresponse.getEntity(); 
				if(charset!=null){
					response = EntityUtils.toString(entity, charset); 
				}else{//默认is08859-1
					response = EntityUtils.toString(entity); 
				}
				
				if (entity != null) { 
					entity.consumeContent(); 
				} 
				
				//返回的信息
				logger.debug("\n response	-----------		" + response);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("\n HttpClientUtil ： --------------	" + e.getMessage());
		}finally{
			//释放请求连接
			method.releaseConnection();
		}
		return response;
	}
	
	/**
	 * 提供post方法
	 * 
	 * @return HttpMethod
	 */
	private static HttpPost  getPostMethod(String methodUrl,Map<String, Object> paramMap){
		HttpPost  post = new HttpPost (methodUrl);
		post.getParams().setParameter("http.protocol.content-charset",HTTP.UTF_8);  
		post.getParams().setParameter(HTTP.CONTENT_ENCODING, HTTP.UTF_8);  
		post.getParams().setParameter(HTTP.CHARSET_PARAM, HTTP.UTF_8);  
		post.getParams().setParameter(HTTP.DEFAULT_PROTOCOL_CHARSET, HTTP.UTF_8);  
		post.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");  
		List<NameValuePair> paramPair = new ArrayList<NameValuePair>();   
		//定义数组下标
		int i = 0;
		for(Map.Entry<String, Object> entryMap : paramMap.entrySet()) {
			if(entryMap.getValue()!=null){
				NameValuePair nvPair = new BasicNameValuePair(entryMap.getKey(), entryMap.getValue().toString());
				paramPair.add(nvPair) ;
				i++;
			}
		}
		
		try {
			post.setEntity(new UrlEncodedFormEntity(paramPair, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}  
		
		return post;
   	}
	
	/**
	 * 提供get方法
	 * 
	 * @return HttpMethod
	 */
	private static  HttpGet  getGetMethod(String methodUrl,Map<String, Object> paramMap){
		//定义参数串
		StringBuffer paramStr = new StringBuffer("");
		if(!paramMap.isEmpty()){
			paramStr.append("?");
			for(Map.Entry<String, Object> entryMap : paramMap.entrySet()) {
				paramStr.append(entryMap.getKey()).append("=").append(entryMap.getValue().toString().replaceAll(" ", "%20")).append("&");
			}
			paramStr.deleteCharAt(paramStr.length()-1);
		}
		
		return new  HttpGet (methodUrl + paramStr);
	}
	
	@Autowired
	public void setMessageSource(
			ReloadableResourceBundleMessageSource messageSource) {
		HttpClientUtil.messageSource = messageSource;
	}
	
	public static boolean download(String url, String dst) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet(url);
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				HttpEntity entity = response.getEntity();
				InputStream in = entity.getContent();
				long length = entity.getContentLength();
				if (length <= 0) {
					logger.info("下载文件不存在！" + url);
					return false;
				}
				OutputStream out = new FileOutputStream(new File(dst));
				saveTo(in, out);
				return true;
			} finally {
				response.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpclient.close();
		}
		return true;
	}
	
	public static void saveTo(InputStream in, OutputStream out) throws Exception {
		byte[] data = new byte[1024 * 1024];
		int index = 0;
		while ((index = in.read(data)) != -1) {
			out.write(data, 0, index);
		}
		in.close();
		out.close();
	}
	
}
