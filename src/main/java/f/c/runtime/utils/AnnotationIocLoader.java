package f.c.runtime.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import f.c.common.annotation.Inject;
import f.c.common.annotation.IocBean;
import f.c.common.reflect.ReflectUtil;
import f.c.common.services.ILoggerService;
import f.c.common.services.LoggerService;
import f.c.utils.Strings;

/**
 * IOC加载器
 * 
 * @author user
 * 
 */
class AnnotationIocLoader {

    public static AnnotationIocLoader INSTANCE = new AnnotationIocLoader();
    private ILoggerService LOG = LoggerService.getLog(AnnotationIocLoader.class);

    private HashMap<String, IocObject> map = new HashMap<String, IocObject>();

    private AnnotationIocLoader() {}

    public HashMap<String, IocObject> load(Set<Class<?>> clsSet) {

        for (Class<?> cls : clsSet) {
            IocBean iocBean = cls.getAnnotation(IocBean.class);
            if (iocBean != null) {
                LOG.debug("Found a Class with Ioc-Annotation : {}", cls);

                String beanName = iocBean.name();
                if (Strings.isBlank(beanName)) {
                    beanName = Strings.lowerFirst(cls.getSimpleName());
                }
                if (map.containsKey(beanName)) {
                    LOG.warn("Duplicate beanName={}, by {} !!  Have been define by {} !!", beanName, cls, map.get(beanName));
                    continue;
                }
                IocObject ioc = new IocObject();
                ioc.setCls(cls);
                IocEvent iocEvt = new IocEvent();
                String event = null;
                event = iocBean.fetch();
                if (!Strings.isBlank(event)) {
                    iocEvt.addEvent("fetch", iocBean.fetch());
                }
                event = iocBean.create();
                if (!Strings.isBlank(event)) {
                    iocEvt.addEvent("create", iocBean.create());
                }
                ioc.setSingleton(iocBean.singleton());

                // 处理字段(以@Inject方式,位于字段)
                List<IocField> fieldList = new ArrayList<IocField>();
                Field[] fields = ReflectUtil.gtField(cls);
                for (Field feild : fields) {
                    Inject inject = feild.getAnnotation(Inject.class);
                    if (inject == null) {
                        continue;
                    }
                    IocField iocField = new IocField();
                    if (!Strings.isBlank(inject.name())) {
                        iocField.setName(inject.name());
                    } else {
                        iocField.setName(Strings.lowerFirst(feild.getType().getSimpleName()));
                    }
                    iocField.setFeild(feild);
                    fieldList.add(iocField);
                }
                map.put(beanName, ioc);
            }
        }
        return map;
    }
}
