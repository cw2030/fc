package f.c.runtime.utils;

import java.lang.reflect.Field;

public class IocField {

    private String name;
    private Field feild;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Field getFeild() {
        return feild;
    }
    public void setFeild(Field feild) {
        this.feild = feild;
    }
    
    
}
