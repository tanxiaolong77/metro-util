package com.quanjing.util;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.quanjing.util.stereotype.QueryCached;




/**
 * 
 * record service data to redis
 * 
 * @author haisheng xie
 * 
 */
public class StorageMethodResult implements MethodInterceptor {

	private static Logger logger = LoggerFactory.getLogger(StorageMethodResult.class);
	
	@Autowired
	@Qualifier("redis")
	private RedisUtil redisUtil;

	/**
	 * aop record data to redis
	 * when you query some data,return data if that is in redis else <br>
	 * this will query in mysql and record result in redis
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {

		String className = invocation.getThis().getClass().getName();// 

		Object[] args = invocation.getArguments();// get param
		logger.debug("$$  " + invocation.getMethod() + " will be executed.");
		QueryCached replication = null;//
		
		
		if (invocation.getMethod().isAnnotationPresent(QueryCached.class)) {//
			replication = invocation.getMethod().getAnnotation(QueryCached.class);
			
		} else {//call 
			return invocation.proceed();
		}
		String key = "";
        if(!StringUtils.isEmpty(replication.keyPreFix())){
             key = replication.keyPreFix() + "_" +JsonUtil.toJsonString(args);
        }
        else if(!StringUtils.isEmpty(replication.key())){
             key =  replication.key();
        }
        else{
            key = className+"_"+invocation.getMethod().getName()+"_"+ JsonUtil.toJsonString(args);
        }
        
		String json  = redisUtil.getData(key);
		
		if(json != null){
			logger.debug(className+" invoke method:"+invocation.getMethod().getName()+" cache return ^_^");
			Class retClass=invocation.getMethod().getReturnType();
			if(retClass.isAssignableFrom(List.class)&&replication.retclass()!=null){
				return JsonUtil.getList(json,replication.retclass());
			}else{
				return JsonUtil.getObject(json,invocation.getMethod().getReturnType());
			}
										
		}else{
			Object ret = invocation.proceed();
			if(ret!=null){
				if(replication.timeout()>0){
					redisUtil.setData(key, JsonUtil.toJson(ret), replication.timeout());
				}else{
					redisUtil.setData(key, JsonUtil.toJson(ret));
				}
			}
			
			
			return ret;			
		}		
	}
}
