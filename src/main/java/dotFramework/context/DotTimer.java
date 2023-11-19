package dotFramework.context;

import java.lang.reflect.InvocationTargetException;
import java.util.TimerTask;
import java.lang.reflect.Method;

public class DotTimer extends TimerTask {
    private Object serviceObject;
    private Method scheduledMethod;

    public DotTimer(Object serviceObject, Method scheduledMethod) {
        this.serviceObject = serviceObject;
        this.scheduledMethod = scheduledMethod;
    }

    public void run() {
        try {
            scheduledMethod.invoke(serviceObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
