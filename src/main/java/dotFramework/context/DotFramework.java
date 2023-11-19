package dotFramework.context;

import dotFramework.annotations.Autowired;

import java.lang.reflect.Field;

public class DotFramework {
    public static void run(Class applicationClass) {
        // create the context
        DotContext dotContext = new DotContext();
        dotContext.initApp();
        try {
            // create instance of the application class
            Object applicationObject =  applicationClass.getDeclaredConstructor().newInstance();
            // find annotated fields
            for (Field field : applicationObject.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    // get the type of the field
                    Class<?> theFieldType = field.getType();
                    // get the object instance of this type
                    Object instance = dotContext.getBeanOfType(theFieldType);
                    // do the injection
                    field.setAccessible(true);
                    field.set(applicationObject, instance);
                }
            }
            //call the run() method
            if (applicationObject instanceof Runnable)
                ((Runnable)applicationObject).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
