package com.quanjing.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Response;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Tuple;

/**
 * 
 * @author
 * @see <a href=\"http://redis.readthedocs.org/en/latest/index.html\">http://redis.readthedocs.org/en/latest/index.html</a>
 */
@Component("redis")
public class RedisUtil {
	
	@Value("${redis.list}")
	private  String serverList="";
	
	private static ShardedJedisPool shardedJedisPool;
	
	@Value("${exception.receive:fengss@quanjing.com}")
	private String receive;
	
	private Logger logg=LoggerFactory.getLogger(RedisUtil.class);
	
	@PostConstruct
	private void initial() {
		initialShardedPool(null);
	}
	
	public void initialShardedPool(String confFileName) {
		  if(shardedJedisPool!=null){
			  return;
		  }
		   // 池基本配置
		  JedisPoolConfig config = new JedisPoolConfig();
		  config.setMaxIdle(30);
		  config.setMaxWaitMillis(10000);
		  config.setTestOnBorrow(false);
		  config.setTestOnReturn(false);
		  
		  
		   // Shard链接
		  List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		  Properties properties = new Properties();  
	        try {
	        	String[] arr = null;
	        	if(confFileName!=null){
	        		properties.load(this.getClass().getResourceAsStream("/"+confFileName));
					arr=properties.getProperty("redis.list").split(",");
	        	}else{
	        		arr=serverList.split(",");
	        		
	        	}
				
				for(String s:arr){
					logg.info("redis ShardIn:"+s);
					String[]info=s.split(":");
					if(info[1].indexOf("-")!=-1){
						String[] ports=info[1].split("-");
						int min=Integer.valueOf(ports[0]);
						int max=Integer.valueOf(ports[1].trim());
						for(int i=min;i<=max;i++){
							shards.add(new JedisShardInfo(info[0],i));
						}
					}else{
						shards.add(new JedisShardInfo(info[0],Integer.valueOf(info[1])));
					}
					
				}
	        } catch (IOException e) {
				logg.error("redis  error",e);  
			}  
		  
		   // 构造池
		   shardedJedisPool = new ShardedJedisPool(config, shards);
    }
	
	
	private void shutdown() {
		shardedJedisPool.destroy();
	}
	
	public RedisUtil(){
	}
	
	public RedisUtil(String configFile){
		initialShardedPool(configFile);
	}

	
	
	public JedisCommands getConnection(){
		ShardedJedis jedis=null;          
		try {
			jedis=shardedJedisPool.getResource();
		} catch (Exception e) {     
			logg.error("redis error",e);
		}          
		return jedis;    
	}
	
	public ShardedJedis getJedis(){
		ShardedJedis jedis=null;          
		try {
			jedis=shardedJedisPool.getResource();
		} catch (Exception e) {              
			logg.error("redis error",e);      
		}          
		return jedis;    
	}
	
	public void returnResource(Object jedis) {          
		if (null != jedis) {              
			try {                  
				//shardedJedisPool.returnResourceObject((ShardedJedis) jedis);
				ShardedJedis j = (ShardedJedis) jedis;
				j.close();
			} catch (Exception e) {
				logg.error("redis error",e);        
			}          
		}      
	}  
	
	
	
	public boolean setData(String key,String value,int seconds) {
		ShardedJedis redis = null;
		try {                  
			redis = getJedis();
			ShardedJedisPipeline pip = redis.pipelined();
			pip.set(key, value);
			pip.expire(key, seconds);
			pip.sync();
			return true;              
		} catch (Exception e) {
			logg.error("redis setData error",e);
			sendRedisErrorEmail("set data error, key="+key+",value=" + value + ", seconds="+seconds, e);
		} finally {
			returnResource(redis);
		}
		return false;      
	}
	
	public boolean setData(String key,String value) {
		JedisCommands redis = null;
		try {                  
			//ShardedJedis jedis=getConnectionEx();   
			redis = getConnection();
			redis.set(key,value);                  
			return true;              
		} catch (Exception e) {
			logg.error("redis setData error",e);
			sendRedisErrorEmail("set data error, key="+key+",value=" + value, e);
		} finally {
			returnResource(redis);
		}       
		return false;      
	}
	
	public boolean setData(String key,Map<String,String> map,int seconds) {
		ShardedJedis redis = null;
		try {                  
			redis = getJedis();
			ShardedJedisPipeline pip = redis.pipelined();
			pip.hmset(key, map);
			pip.expire(key, seconds);
			pip.sync();
			return true;              
		} catch (Exception e) {
			logg.error("redis setData error",e); 
			sendRedisErrorEmail("set map error,"+getMapKV(map) + ", seconds=" + seconds, e);                         
		} finally {
			returnResource(redis);
		}
		return false;      
	}
	
	/**
	 * 插入map时不设置过期时间
	 * @param redis
	 */
	public boolean setData(String key,Map<String,String> map) {
		JedisCommands redis = null;
		try {
			redis = getConnection();
			String ret=redis.hmset(key, map);
			return true;              
		} catch (Exception e) {
			logg.error("redis setData error",e);
			sendRedisErrorEmail("set map error,"+getMapKV(map), e);                                           
		} finally {
			returnResource(redis);
		}
		return false;      
	}
	
	public boolean setMapData(String key,String field,String value) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			jedis.hset(key, field, value);
			 return true;
		} catch (Exception e) {
			logg.error("redis setMapData error",e);
			sendRedisErrorEmail("set map error,field="+field + ", value=" + value, e);                                       
		} finally {      
			returnResource(jedis);
		}
		return false;      
	}
	
	
	
	
	
	public Long getInc(String key,int num) {
		Long value = null;              
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			value=jedis.incrBy(key, num);
			return value;              
		} catch (Exception e) {
			logg.error("redis getInc error",e);
			sendRedisErrorEmail("getInc error, key="+ key + ", num="+num, e);                                  
		} finally {      
			returnResource(jedis);
		}
		return value;      
	}
	
	public Long mapInc(String key,String field,int num) {
		Long value = null;              
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			value=jedis.hincrBy(key, field, num);
			return value;              
		} catch (Exception e) {
			logg.error("redis hincrBy("+key+") error",e);
			sendRedisErrorEmail("hincrBy error, key="+key+", field=" + field + ", num=" + num, e);                               
		} finally {      
			returnResource(jedis);
		}
		return value;      
	}
	
	
	public String getData(String key) {
		String value = null;              
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			value=jedis.get(key);
			return value;              
		} catch (Exception e) {
			logg.error("redis get("+key+") error",e);                                
		} finally {      
			returnResource(jedis);
		}
		return value;      
	}
	
	public Map<String,String> getMap(String key) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.hgetAll(key);
		} catch (Exception e) {
			logg.error("redis hgetAll("+key+") error",e);                           
		} finally {      
			returnResource(jedis);
		}
		return null;      
	}
	
	
	public String getMapData(String key,String field) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.hget(key, field);
		} catch (Exception e) {
			logg.error("redis hget error",e);                                  
		} finally {
			returnResource(jedis);
		}
		return null;      
	}
	
	public long removeMapData(String key,String field) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.hdel(key, field);
		} catch (Exception e) {
			logg.error("redis removeMapData error",e); 
			sendRedisErrorEmail("remove map error, key="+ key + ", field=" + field, e);                                 
		} finally {
			returnResource(jedis);
		}
		return 0;      
	}
	
	/**
	 * pipline 批量查询
	 * @param key
	 * @param fields
	 * @return
	 */
	public List<String> getMapData(String key,String... fields) {
		List<String> ret=new ArrayList();
		
		ShardedJedis redis = null;
		try {                  
			redis = getJedis();
			ShardedJedisPipeline pip = redis.pipelined();
			for(String f:fields){
				ret.add(pip.hget(key, f).get());
			}
			pip.sync();
			return ret;
		} catch (Exception e) {
			logg.error("redis  getMapData error",e);                                  
		} finally {
			returnResource(redis);
		}
		return null;      
	}
	
	/**
	 * pipline 批量查询
	 * @param keys
	 * @param field
	 * @return
	 */
	public List<String> getBatchMapData(String field,String... keys) {
		List<String> ret=new ArrayList();
		
		ShardedJedis redis = null;
		try {                  
			redis = getJedis();
			ShardedJedisPipeline pip = redis.pipelined();
			List<Response> responses = new ArrayList(keys.length);  
			for(String key:keys){
				responses.add(pip.hget(key, field));
			}
			pip.sync();
			for (Response<String> resp : responses) {  
            	ret.add(resp.get());  
            } 
			return ret;
		} catch (Exception e) {
			logg.error("redis  getBatchMapData error",e);                                  
		} finally {
			
			returnResource(redis);
		}
		return null;      
	}
	
	
	
	public List<String> getList(String key) {
		JedisCommands jedis = null;
		try {
			jedis = getJedis();
			return jedis.lrange(key, 0, -1);
		} catch (Exception e) {
			logg.error("redis getList error",e);                                  
		} finally {      
			returnResource(jedis);
		}
		return null;      
	}
	
	
	public List<String> getDatas(String keys[]) {
		List<String> ret=new ArrayList();
		ShardedJedis jedis = null;
		try {                  
			jedis = getJedis();
			ShardedJedisPipeline pipeline = jedis.pipelined();
			List<Response> responses = new ArrayList(keys.length);  
            for (String key : keys) {  
                responses.add(pipeline.get(key));  
            }
            pipeline.sync();  
            for (Response<String> resp : responses) {  
            	ret.add(resp.get());  
            }  
			
			return ret;
		} catch (Exception e) {
			logg.error("redis getDatas error",e);                                  
		} finally {      
			returnResource(jedis);
		}
		return null;      
	}
	
	public List<String> getMapDatas(String key[],String field) {
		ShardedJedis jedis = null;
		List<String> ret=new ArrayList();
		try {
			jedis = getJedis();
			ShardedJedisPipeline pl = jedis.pipelined();
			for(String f:key){
				ret.add(pl.hget(f, field).get());
			}
			pl.sync();
			return ret;
		} catch (Exception e) {
			logg.error("redis  getMapDatas error",e);                                  
		} finally {      
			returnResource(jedis);
		}
		return null;      
	}
	
	
	public Set<String> hKeys(String key) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.hkeys(key);
		} catch (Exception e) {
			logg.error("redis hkeys error",e);    
			return null;
		} finally {      
			returnResource(jedis);
		}
	}
	
	
	
	public void expire(String key,int seconds) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			jedis.expire(key, seconds);              
		} catch (Exception e) {
			logg.error("redis expire  error",e);                                  
		} finally {      
			returnResource(jedis);
		}
	}
	
	
	public long addToSet(String setName,String... value) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.sadd(setName,value);                 
		} catch (Exception e) {
			logg.error("redis  sadd error",e);
			sendRedisErrorEmail("sadd error, key="+setName + ", element="+value, e);                                  
		} finally {      
			returnResource(jedis);
		}
		return 0;
	}
	
	public void removeSet(String setName,String key) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			jedis.srem(setName,key);
		} catch (Exception e) {
			logg.error("redis srem  error",e); 
			sendRedisErrorEmail("srem error, key="+setName+", element="+key, e);                                 
		} finally {      
			returnResource(jedis);
		}
	}

	public boolean ismember(String setName,String key) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.sismember(setName,key);
		} catch (Exception e) {
			logg.error("redis ismember error",e);                                  
		} finally {      
			returnResource(jedis);
		}
		return false;
	}
	
	
	public Set<String>  smembers(String setName) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.smembers(setName);
		} catch (Exception e) {
			logg.error("redis smembers error",e);                                  
		} finally {      
			returnResource(jedis);
		}
		return null;
	}
	
	public List<String>  srandmember(String key,int count) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.srandmember(key, count);
		} catch (Exception e) {
			logg.error("redis srandmember error",e);                                  
		} finally {      
			returnResource(jedis);
		}
		return null;
	}
	
	public ScanResult<String> sscan(String key,String cursor) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.sscan(key, cursor);
		} catch (Exception e) {
			logg.error("redis sscan error",e);                                  
		} finally {      
			returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 
	 * @param key
	 * @param val
	 * @return
	 * Return value
		the length of the list after the push operations.
	 * @throws Exception 
	 */
	public long lpush(String key,String... val) throws Exception {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.lpush(key, val);
			
		} catch (Exception e) {
			logg.error("redis lpush error",e);
			sendRedisErrorEmail("lpush error, key=" + key+", values="+getStringArrVal(val), e);    
			throw e;
		} finally {      
			returnResource(jedis);
		}
	}
	
	public List lrange(String key,int start,int end) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.lrange(key, start, end);
		} catch (Exception e) {
			logg.error("redis lrange("+key+") error",e);    
			return null;
		} finally {      
			returnResource(jedis);
		}
	}
	
	public long lrem(String key,int count,String value) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.lrem(key, count, value);
		} catch (Exception e) {
			logg.error("redis lrem("+key+") error",e);
			sendRedisErrorEmail("lrem error, key="+key+",value="+value+",count="+count, e);    
			return 0;
		} finally {      
			returnResource(jedis);
		}
	}
	
	public long llen(String key) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.llen(key);
		} catch (Exception e) {
			logg.error("redis llen  error",e);    
			return 0;
		} finally {      
			returnResource(jedis);
		}
	}
	
	
	public long rpush(String key,String... val) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.rpush(key, val);
			
		} catch (Exception e) {
			logg.error("redis rpush error",e);
			sendRedisErrorEmail("rpush error, key="+key+", values="+getStringArrVal(val), e);    
			return 0;
		} finally {      
			returnResource(jedis);
		}
	}
	
	public String lpop(String key) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.lpop(key);
			
		} catch (Exception e) {
			logg.error("redis  lpop error",e);    
			return null;
		} finally {      
			returnResource(jedis);
		}
	}
	

	public String rpop(String key) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.rpop(key);
			
		} catch (Exception e) {
			logg.error("redis  rpop error",e);    
			return null;
		} finally {      
			returnResource(jedis);
		}
	}
	
	/**
	 * 对一个列表进行修剪(trim)，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
	 * @param key
	 * @param start
	 * @param end
	 */
	public void ltrim(String key,long start,long end) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			jedis.ltrim(key, start, end);
			
		} catch (Exception e) {
			logg.error("redis ltrim error",e);
			sendRedisErrorEmail("ltrim error, key="+key+",start="+start+",end="+end, e);    
		} finally {      
			returnResource(jedis);
		}
	}
	
	public long sadd(String key,String val) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.sadd(key,val);
			
		} catch (Exception e) {
			logg.error("redis  sadd error",e);
			sendRedisErrorEmail("sadd error, key="+key+", value="+val, e);    
			return 0;
		} finally {      
			returnResource(jedis);
		}
	}
	
	public long scard(String key) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.scard(key);
		} catch (Exception e) {
			logg.error("redis  scard error",e);    
			return 0;
		} finally {      
			returnResource(jedis);
		}
	}
	
	
	public boolean sismember(String key,String member) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.sismember(key, member);
			
		} catch (Exception e) {
			logg.error("redis sismember("+key+") error",e);    
			return false;
		} finally {      
			returnResource(jedis);
		}
	}

	public long srem(String key,String val) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.srem(key,val);
			
		} catch (Exception e) {
			logg.error("redis srem error",e);
			sendRedisErrorEmail("srem error, key="+key + ", value="+val, e);    
			return 0;
		} finally {      
			returnResource(jedis);
		}
	}
	
	
	
	public long zadd(String key,String val,double score) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.zadd(key,score, val);
			
		} catch (Exception e) {
			logg.error("redis zadd error",e);
			sendRedisErrorEmail("zadd error, key="+key+", value="+val+", score="+score, e);    
			return 0;
		} finally {      
			returnResource(jedis);
		}
	}
	
	
	public long zadd(String key,Map<String,Double> map) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.zadd(key,map);
			
		} catch (Exception e) {
			logg.error("redis zadd("+key+") error",e);
			sendRedisErrorEmail("redis zadd("+key+") error",e);    
			return 0;
		} finally {      
			returnResource(jedis);
		}
	}
	
	public Double zincrby(String key,Double increment,String member ) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.zincrby(key, increment, member);
			
		} catch (Exception e) {
			logg.error("redis zincrby("+key+") error",e);
			sendRedisErrorEmail("redis zincrby("+key+") error",e);    
			return 0D;
		} finally {      
			returnResource(jedis);
		}
	}
	
	
	
	 

	public Long zcount(String key,double min ,double max) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.zcount(key, min, max);
			
		} catch (Exception e) {
			logg.error("redis zcount("+key+") error",e);    
			return null;
		} finally {      
			returnResource(jedis);
		}
	}
	
	/**
	 * 返回有序集 key 的基数。
	 * @param key
	 * @return
	 * 当 key 不存在时，返回 0 。
	 */
	public Long zcard(String key) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.zcard(key);
			
		} catch (Exception e) {
			logg.error("redis zcard("+key+") error",e);    
			return null;
		} finally {      
			returnResource(jedis);
		}
	}
	
	public Double zscore(String key,String member) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.zscore(key,member);
			
		} catch (Exception e) {
			logg.error("redis zscore("+key+") error",e);    
			return null;
		} finally {      
			returnResource(jedis);
		}
	}
	
	
	 /**
	  * 返回有序集key中，指定区间内的成员。
	  * @param key
	  * @param start
	  *  0表示有序集第一个成员
	  * @param end
	  * @return
	  */
	public Set<String> zrange(String key,int start ,int end) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.zrange(key, start, end);
		} catch (Exception e) {
			logg.error("redis zrange("+key+") error",e);    
			return null;
		} finally {      
			returnResource(jedis);
		}
	}
	
	public Set<String> zrevrange(String key,int start ,int end) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.zrevrange(key, start, end);
		} catch (Exception e) {
			logg.error("redis zrevrange("+key+") error",e);  
			return null;
		} finally {      
			returnResource(jedis);
		}
	}
		
	public boolean exists (String key) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.exists(key);
			
		} catch (Exception e) {
			logg.error("redis exists("+key+") error",e);    
			return false;
		} finally {      
			returnResource(jedis);
		}
	}
		
	
	public Set<String> zrevrangeByScore(String key,double min ,double max,int count) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.zrevrangeByScore(key, max, min, 0, count);
			
		} catch (Exception e) {
			logg.error("redis  zrevrangeByScore("+key+") error",e);    
			return null;
		} finally {      
			returnResource(jedis);
		}
	}
	
	public Set<String> zrangeByScore(String key,double min ,double max,int count) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.zrangeByScore(key, min, max, 0, count);
			
		} catch (Exception e) {
			e.printStackTrace();  
			return null;
		} finally {      
			returnResource(jedis);
		}
	}
	
	public long zremrangeByRank (String key,int start,int end) {
		JedisCommands jedis = null;
		try {
			jedis = getConnection();
			return jedis.zremrangeByRank(key, start, end);
			
		} catch (Exception e) {
			logg.error("redis zremrangeByRank "+key+" error",e);
			sendRedisErrorEmail("redis zremrangeByRank "+key+" error",e);    
			return 0;
		} finally {      
			returnResource(jedis);
		}
	}
		
	
		public long del(String key){
			JedisCommands jedis = null;
			try {
				jedis = getConnection();
				long c=jedis.del(key);
				return c;
			} catch (Exception e) {
				logg.error("redis del "+key+" error",e);
				sendRedisErrorEmail("redis del "+key+" error",e);  
				return 0;
			}finally{
				returnResource(jedis);
			}
		}
		
		
		public long ZREM(String key,String member){
			JedisCommands jedis = null;
			try {
				jedis = getConnection();
				long c=jedis.zrem(key, member);
				return c;
			} catch (Exception e) {
				logg.error("redis ZREM  error(key="+key+",member="+member+")",e);
				sendRedisErrorEmail("redis ZREM  error(key="+key+",member="+member+")", e);  
				return 0;
			}finally{
				returnResource(jedis);
			}
		}
		
		public Long zrank (String key,String member) {
			JedisCommands jedis = null;
			try {
				jedis = getConnection();
				return jedis.zrank(key, member);
			} catch (Exception e) {
				logg.error("redis zRank "+key+" error",e);    
				return null;
			} finally {      
				returnResource(jedis);
			}
		}
		
		public Set<Tuple> zrangeWithScore(String key,int start ,int end) {
			JedisCommands jedis = null;
			try {
				jedis = getConnection();
				return jedis.zrangeWithScores(key, start, end);
			} catch (Exception e) {
				logg.error("redis zrangeWithScore("+key+") error",e);    
				return null;
			} finally {      
				returnResource(jedis);
			}
		}
		
		private void sendRedisErrorEmail(String params, Exception e) {
			String exception = getExceptionAllinformation(e);
			if (receive != null) {
				EmailUtil.getInstance().send("redis error-"+IPUtil.getLocalIp4(), params + "\r\n" + exception, receive.split(","));
			}
		}
		
		private String getMapKV(Map<String, String> map) {
			if (map == null) {
				return "";
			}
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				sb.append("key="+entry.getKey()+",value="+entry.getValue()+"\r\n");
			}
			return sb.toString();
		}
		
		private String getStringArrVal(String[] params) {
			if (params == null) {
				return "";
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < params.length; i++) {
				sb.append(params[i]);
				if (i != params.length - 1) {
					sb.append(",");
				}
			}
			return sb.toString();
		}
		
		private String getExceptionAllinformation(Exception ex) {
			String sOut = ex.getMessage();
			StackTraceElement[] trace = ex.getStackTrace();
			for (StackTraceElement s : trace) {
				sOut += "\t " + s + "\r\n";
			}
			return sOut;
		}
} 

