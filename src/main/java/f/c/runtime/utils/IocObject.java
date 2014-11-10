package f.c.runtime.utils;

import java.util.List;

/**
 * 可注入的IOC对象
 * 
 * @author user
 * 
 */
public class IocObject {

    private Class<?> cls;

    private boolean singleton;

    private IocEvent events;

    private List<IocField> fields;

    public Class<?> getCls() {
        return cls;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public IocEvent getEvents() {
        return events;
    }

    public void setEvents(IocEvent events) {
        this.events = events;
    }

    public List<IocField> getFields() {
        return fields;
    }

    public void setFields(List<IocField> fields) {
        this.fields = fields;
    }
}
