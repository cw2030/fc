package f.c.ioc;

import org.junit.Test;

import f.c.runtime.utils.Ioc;

public class IocTest {

    @Test
    public void iocTest(){
        Ioc ioc = Ioc.INSTANCE;
        Object b = ioc.getBean("testB");
        System.out.println(b);
    }
}
