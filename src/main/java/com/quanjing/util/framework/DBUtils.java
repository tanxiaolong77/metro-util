package com.quanjing.util.framework;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.quanjing.util.RedisUtil;

import redis.clients.jedis.JedisCommands;

/**
 * DB基础类
 * @author haisheng
 *
 */
@Service("dbUtils")
public class DBUtils {
	
	@Autowired
	@Qualifier("redis")
	private RedisUtil redisUtil;
	private final static long twepoch = 1425082458070L;
	
	 private final static int workerIdBits = 5;
	 public final static long maxWorkerId = -1L ^ -1L << workerIdBits;
	 private final static int sequenceBits = 10;
	 public final static long maxSequence = -1L ^ -1L << sequenceBits;

     private final static long workerIdShift = sequenceBits;

     private final static int timestampLeftShift = sequenceBits + workerIdBits;

	 
	private static AtomicLong sid = new AtomicLong(1);
	
	private  int workerid= 1;
	
	public DBUtils(){
	}
	//private static DBUtils dbUtils = null;
	
	public DBUtils(int workerId){
		if (workerId > this.maxWorkerId || workerId < 0) {
		   throw new IllegalArgumentException(String.format(
		     "worker Id can't be greater than %d or less than 0",
		     this.maxWorkerId));
		  }
		  this.workerid = workerId;
	}

	
	/**
	 *  
	 * @return
	 */
	private long IncID(){
		Long id = (long) 1;
		JedisCommands redis = null;
        try {
            redis = redisUtil.getConnection();
            id = redis.incr("AutoID");
            if(id >= maxSequence){
                redis.set("AutoID", "0");
            }
            sid=  new AtomicLong(id);
        }catch (Exception e){
            sid.addAndGet(1);
            if(sid.get() > maxSequence)sid=  new AtomicLong(0);
            return sid.get();
        }finally {
        	if(redis!=null){
        		redisUtil.returnResource(redis);
        	}
        	
        }
		return id;
	}
	
	
	/**
	 * 获得唯一ID 数据库的主键
     * Snowflake ID
	 * @return
	 */
	public long CreateID(){
		long iid = this.IncID();		
		long id = System.currentTimeMillis()-twepoch;		
		id = (id<<timestampLeftShift)|(workerid<<workerIdShift)|iid;
		return id;
	}
	
	
	public static void main(String[] args) {
		DBUtils dbu=new DBUtils();
		System.out.println(dbu.CreateID());
		
		System.out.println((1404546697561990L>>15)+1425082458070L);
	}
}
