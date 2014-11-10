package f.c.common.aop;

/**
 * 所有拦截器均要实现此接口
 * @author user
 *
 */
public interface IMethodInterceptor {
    void filter(IInterceptorChain chain) throws Throwable;
}
