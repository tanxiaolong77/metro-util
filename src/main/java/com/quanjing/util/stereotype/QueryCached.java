package com.quanjing.util.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * using for redis to record cached
 * @author haishengxie
 *
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryCached {	  
	public int timeout() default 60*30;//秒
    //完整的key
    public String key() default "";
    //key前缀，如果不为空用这个
    public String keyPreFix() default "";
	public Class retclass() default Object.class;
}
