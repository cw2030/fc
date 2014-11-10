package f.c.common.aop;

import java.lang.reflect.Method;
import java.util.List;

import f.c.common.services.ILoggerService;
import f.c.common.services.LoggerService;

public class InterceptorChain implements IInterceptorChain{
    protected Method callingMethod;

    protected Object args[];

    protected Object callingObj;

    protected Object returnValue;

    protected List<IMethodInterceptor> beforeList;
    
    protected List<IMethodInterceptor> afterList;

    private static ILoggerService LOG = LoggerService.getLog(InterceptorChain.class);

    public InterceptorChain(Object obj,
                            Method method,
                            List<IMethodInterceptor> bfList,
                            Object[] args,
                            List<IMethodInterceptor> afList) {
        this.callingObj = obj;
        this.callingMethod = method;
        this.args = args;
        this.beforeList = bfList;
        this.afterList = afList;
    }

    /**
     * 继续执行下一个拦截器,如果已经没有剩下的拦截器,则执行原方法
     * 
     * @return 拦截器链本身
     * @throws Throwable
     *             下层拦截器或原方法抛出的一切异常
     */
    public InterceptorChain doChain() throws Throwable {
        for (IMethodInterceptor mi : beforeList) {
            mi.filter(this);
        }
        invoke();
        for (IMethodInterceptor mi : beforeList) {
            mi.filter(this);
        }
        return this;

    }

    /**
     * 执行原方法,一般情况下不应该直接被调用
     * 
     * @throws Throwable
     *             原方法抛出的一切异常
     */
    public void invoke() throws Throwable {
        this.returnValue = callingMethod.invoke(callingObj, args);
    }

    /**
     * 获取返回值
     * 
     * @return 返回值
     */
    public Object getReturn() {
        return returnValue;
    }

    /**
     * 正在被调用的Method
     */
    public Method getCallingMethod() {
        return callingMethod;
    }

    /**
     * 方法调用的参数数组,如果你要改变参数,那么必须保证参数类型与方法参数兼容.
     */
    public Object[] getArgs() {
        return args;
    }

    public Object getCallingObj() {
        return callingObj;
    }

}
