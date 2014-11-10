package f.c.runtime.utils;

import java.util.HashMap;
import java.util.Map;

import f.c.utils.Strings;

public class IocEvent {

    Map<String,String> events = new HashMap<String,String>();
    
    public void addEvent(String event,String value){
        if(!Strings.isBlank(event)){
            events.put(event, value);
        }
    }
    
    public boolean isContainsEvent(String event){
        if(!Strings.isBlank(event)){
            return events.containsKey(event);
        }
        return false;
    }
    
}
