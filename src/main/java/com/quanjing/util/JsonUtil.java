package com.quanjing.util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.quanjing.util.stereotype.JsonFilter;
import com.quanjing.util.stereotype.OutPutEnum;

/**
 * 
 * @author 
 *
 */
public class JsonUtil {
    //TODO 需要修改mvc的json输出判断是从哪里来的
    static class FastJsonFilter implements PropertyPreFilter {
        private  int type;
        public  FastJsonFilter(){
            type = 0; //0 from web 1 from client
        }
        public  FastJsonFilter(int type){
            this.type=type;
        }
        
        
        public boolean apply(JSONSerializer jsonSerializer, Object o, String s) {
            try {
                Field field = o.getClass().getDeclaredField(s);
                if(field == null) return true;
                JsonFilter jf = field.getAnnotation(JsonFilter.class);
                if(jf != null){
                    OutPutEnum oen = jf.value();
                    if(oen == OutPutEnum.ALL_NO_WRITE)
                        return false;
                    if(type == 0)    {
                        if(oen == OutPutEnum.WEB_NO_WRITE)
                            return false;
                    }
                    else{
                        if(oen == OutPutEnum.CLIENT_NO_WRITE)
                            return false;
                    }
                }
            } catch (NoSuchFieldException e) {
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return true;
            }
            return true;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
    
    
    public static String toJsonString(Object obj,int type) {
        return JSON.toJSONString(obj, new JsonUtil.FastJsonFilter(type),
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.WriteDateUseDateFormat);
    }
    
	/**
	 * 将实体类转化为json字符串
	 * 此类将会自动清除null的
	 * @param obj
	 * @return
	 */
	public static String toJsonString(Object obj) {
       
        return JSON.toJSONString(obj, new JsonUtil.FastJsonFilter(),
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.WriteDateUseDateFormat);
      
    }
	
	/**
	 * 将实体类转化为json字符串
	 * 写入class信息，反序列化时用
	 * 此类将会自动清除null的
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) {
        return JSON.toJSONString(obj, new JsonUtil.FastJsonFilter(),
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.DisableCircularReferenceDetect);
      
    }
	
	/***
	 * 转换成对象
	 * @param jsonString
	 * @param pojoClass
	 * @return
	 */
	public static <T> T  getObject(String jsonString, Class<T> pojoClass) {
		try{
			
			 return JSON.parseObject(jsonString, pojoClass);
		}catch(Exception e){
			return null;
		}

	}
	
	
	public static JSONObject  getObject(String jsonString) {
		try{
			 return JSON.parseObject(jsonString);
		}catch(Exception e){
			return null;
		}

	}
	
	
	public static <T> List<T>  getList(String jsonString, Class<T> pojoClass) {
		try{
			 return JSON.parseArray(jsonString, pojoClass);
		}catch(JSONException e){
			return null;
		}

	}
	
	/***
	 * 转换成map
	 * @param jsonString
	 * @return
	 */
	public static  Map<String,Object> getMap4Json(String jsonString) {
        return  JSON.parseObject(jsonString,new TypeReference<Map<String, Object>>() {});
	}
	
	

       
       
   
}
