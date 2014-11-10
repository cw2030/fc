package f.c.common.aop;

import java.lang.reflect.Method;

public interface IInterceptorChain {

    public Object getReturn();
    public Method getCallingMethod();
    public Object[] getArgs();
    public Object getCallingObj();
}
