package f.c.common.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ReflectUtil {

    public static Field[] gtField(Class<?> cls){
        Class<?> cc = cls;
        Map<String,Field> map = new HashMap<String,Field>();
        while (null != cc && cc != Object.class) {
            Field[] fs = cc.getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                Field f = fs[i];
                int m = f.getModifiers();
                if (Modifier.isStatic(m))
                    continue;
                if (Modifier.isFinal(m))
                    continue;
                if (f.getName().startsWith("this$"))
                    continue;
                if (!Modifier.isStatic(m))
                    continue;
                if (map.containsKey(fs[i].getName()))
                    continue;

                map.put(fs[i].getName(), fs[i]);
            }
            cc = cc.getSuperclass();
        }
        
        return map.values().toArray(new Field[map.size()]);
    }
}
