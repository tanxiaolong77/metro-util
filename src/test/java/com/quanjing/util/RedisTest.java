package com.quanjing.util;

import com.quanjing.util.RedisUtil;
import com.quanjing.util.constant.RedisKey;

public class RedisTest {

	public static void main(String[] args) {

		RedisUtil redis=new RedisUtil("redis.properties");
		//redis.initialShardedPool();
		
//		System.out.println(redis.ZREM(RedisKey.quanzi_all, "753878144779480"));
//		
//		
//		System.out.println(redis.del(RedisKey.action_full+"753878144779480"));
//		System.out.println(redis.getData(RedisKey.action_full+"753878144779480"));
		
		
		

	}

}
