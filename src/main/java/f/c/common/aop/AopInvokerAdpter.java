package f.c.common.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import f.c.common.services.ILoggerService;
import f.c.common.services.LoggerService;

/**
 * AOP拦截处理，包括事务代理。
 * @author user
 *
 */
public class AopInvokerAdpter implements InvocationHandler {
    
    protected ILoggerService LOG = LoggerService.getLog(AopInvokerAdpter.class);
    protected Object target;
    protected Map<String,Method> methods = new HashMap<String, Method>();
    
    protected Map<String,InterceptorChain> interceptors = new HashMap<String, InterceptorChain>();
    public AopInvokerAdpter(Object target,Map<String,Method> methods,Map<String,InterceptorChain> interceptors){
        this.target = target;
        this.methods = methods;
        this.interceptors = interceptors;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        String fName = method.toGenericString();
        try{
            if(methods.containsKey(fName)){
                if(interceptors.containsKey(fName)){
                    return interceptors.get(fName).doChain().getReturn();
                }else{
                    return method.invoke(target, args);
                }
            }
            LOG.error("Can't find class:{} method{}", target.getClass(),fName);
        }catch(Exception e){
            LOG.error("Aop invoke error:", e);
        }
        return null;
    }

}
