package com.quanjing.util.stereotype;

import java.lang.annotation.*;

/**
 * @ClassName: NoLog
 * @Description: 当不需要用LogInterceptor拦截的时候，请标记此注解
 * @author mengkaixuan
 * @Date 2014年7月17日 下午6:32:37
 * @Version
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoLog {

}
