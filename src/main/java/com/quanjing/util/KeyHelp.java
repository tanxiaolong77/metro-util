package com.quanjing.util;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;


public class KeyHelp {

	private static  RSAPublicKey pubKey ;  
	private static  RSAPrivateKey priKey;
	
	private static KeyHelp _instance=null;
	private KeyHelp(){
	}
	
	public static KeyHelp  getInstance(RedisUtil redis) throws Exception{
		if(_instance==null){
			_instance=new KeyHelp();
			 Map<String, Object> keyMap=new HashMap<String, Object>();
			keyMap=RSAUtils.getKeys();
			 //生成公钥和私钥  
	        RSAPublicKey publicKey = (RSAPublicKey) keyMap.get(RSAUtils.PUBLIC_KEY);  
	        RSAPrivateKey privateKey = (RSAPrivateKey) keyMap.get(RSAUtils.PRIVATE_KEY);  
	          
	        //模  
	        String modulus = publicKey.getModulus().toString();  
	        //公钥指数  
	        String public_exponent = publicKey.getPublicExponent().toString();  
	        //私钥指数  
	        String private_exponent = privateKey.getPrivateExponent().toString();  
	       
	    	if(redis!=null){
	    		if(redis.exists("ticket_rsa")){
	    			Map<String,String> map=redis.getMap("ticket_rsa");
	    			modulus=map.get("modulus");
	    			public_exponent=map.get("public_exponent");
	    			private_exponent=map.get("private_exponent");
	    		}else{
	    			Map<String,String> map=new HashMap();
	    			map.put("modulus", modulus);
	    			map.put("public_exponent", public_exponent);
	    			map.put("private_exponent", private_exponent);
	    			redis.setData("ticket_rsa", map);
	    		}
	    		
	    	}
	       
	    	
	        //使用模和指数生成公钥和私钥  
	         pubKey = RSAUtils.getPublicKey(modulus, public_exponent);  
	         priKey = RSAUtils.getPrivateKey(modulus, private_exponent);  
	       
		}
		return _instance;
	}
	
	
	public String encode(String str) throws Exception{
		String mi = RSAUtils.encryptByPublicKey(str, pubKey);  
		return mi;
	}
	
	
	public String decode(String mi) throws Exception{
		String ming = RSAUtils.decryptByPrivateKey(mi, priKey); 
		return ming;
	}

}
