package f.c.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import f.c.common.enums.AopEnum;

/**
 * 给目标方法增加一个拦截器
 * @author user
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Aop {
    String name();
    AopEnum beforeOrAfter();
}
