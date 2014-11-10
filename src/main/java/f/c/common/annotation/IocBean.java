package f.c.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface IocBean {
    
    /**
     * bean的唯一标识，默认是所属类的名字，第一个字母小写
     * @return
     */
    String name() default "";
    
    /**
     * 是否否单例
     * @return
     */
    boolean singleton() default true;
    
    /**
     * 当对象被Ioc容器创建后调用的方法
     */
    String create() default "";
    
    /**
     * 当对象被调用者从Ioc容器调出时触发的方法
     */
    String fetch() default "";
}
