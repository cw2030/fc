package f.c.runtime.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import f.c.common.services.ILoggerService;
import f.c.common.services.LoggerService;

public class Ioc {

    public static Ioc INSTANCE = new Ioc();
    private Map<String,IocObject> map = new HashMap<String,IocObject>();
    private Map<String,Object> iocObjects = new HashMap<String, Object>();
    private ILoggerService LOG = LoggerService.getLog(Ioc.class);

    private Ioc() {
        String[] pkgs = new String[]{"com.wzp"};
        for (String pkg : pkgs) {
            map = AnnotationIocLoader.INSTANCE.load(Scans.INSTANCE.scanPackage(pkg));
            if (map.size() == 0) {
                LOG.warn("Scan complete ! Found {} classes in ({}) base-packages!",map.size(),pkg);
            }
        }
        init();
    }
    
    private void init(){
        IocObject iocObject = null;
        Class<?> cls = null;
        for(String name : map.keySet()){
            try{
                iocObject = map.get(name);
                cls = iocObject.getCls();
                Constructor<?>[] cst = cls.getConstructors();
                if(cst.length > 1){
                    LOG.error("Class ({}) have more than 1 constructor.", cls);
                    continue;
                }
                Class<?>[] ptypes = cst[0].getParameterTypes();
                if(ptypes != null){
                    if(ptypes.length > 0){
                        LOG.error("Class ({}) need a no parameter constructor.", cls);
                        continue;
                    }
                }
                iocObjects.put(name, cst[0].newInstance());
            }catch(Exception e){
                LOG.error("", e);
            }
        }
        try{
            for(String name : map.keySet()){
                iocObject = map.get(name);
                Object obj = iocObjects.get(name);
                Field field = null;
                for(IocField f : iocObject.getFields()){
                    if(!iocObjects.containsKey(f.getName())){
                        continue;
                    }
                    field = f.getFeild();
                    field.setAccessible(true);
                    field.set(obj, iocObjects.get(f.getName()));
                }
            }
        }catch(Exception e){
            LOG.error("", e);
        }
        
    }
    
    public Object getBean(String beanName){
        return iocObjects.get(beanName);
    }

}
