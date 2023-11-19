package dotFramework.context;

import dotFramework.annotations.*;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DotContext {

    public final static HashMap<String, Object> serviceObjects = new HashMap<>();

    public void initApp(){
        // scan all service classes
        List<Class<?>> serviceClasses = getClassesAnnotatedWith(Service.class);

        //
        initiateClasses(serviceClasses);

        performAllDI();
    }


    public Object getBeanOfType(Class<?> classType){
        // the class implements an interface
        List<Object> objectsOfType = new ArrayList<Object>();
        try {
            for (Object theServiceClass : serviceObjects.values()) {
                Class<?>[] interfaces = theServiceClass.getClass().getInterfaces();

                for (Class<?> theInterface : interfaces) {
                    if (theInterface.getName().contentEquals(classType.getName()))
                        objectsOfType.add(theServiceClass);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!objectsOfType.isEmpty()) return objectsOfType.get(0);


        // the class does not implement an interface
        try {
            for (Object theClass : serviceObjects.values()) {
                //check class without interface
                if (theClass.getClass().getName().equals(classType.getName()))
                    return theClass;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public Object getBeanOfName(String beanName){
        return serviceObjects.get(beanName);
    }

    // get all classes annotated with a certain annotation
    private List<Class<?>> getClassesAnnotatedWith(Class<? extends Annotation> annotationClass){
        Reflections reflections = new Reflections("application");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(annotationClass);
        return annotatedClasses.stream().toList();
    }

    // instantiate a list of classes and add them
    private void initiateClasses(List<Class<?>> classes){
      for(Class<?> serviceClass:classes){
          try {
              serviceObjects.put(serviceClass.getName(),serviceClass.getDeclaredConstructor().newInstance());
          } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
              throw new RuntimeException(e);
          }
      }
    }

    private Object performConstructorDI(Object serviceObject) {
      try{
          Constructor<?> [] constructors = serviceObject.getClass().getDeclaredConstructors();
          for(Constructor<?> constructor: constructors){
              if(constructor.isAnnotationPresent(Autowired.class)){
                  Class<?> parameter = constructor.getParameterTypes()[0];
                  Object parameterInstance = getBeanOfType(parameter);
                  Object serviceClassInstance = constructor.newInstance(parameterInstance);
                  System.out.println(serviceClassInstance);
                  serviceObjects.put(serviceClassInstance.getClass().getName(), serviceClassInstance);
                  return serviceClassInstance;
              }
          }

      }catch (Exception e){
          e.printStackTrace();
      }
          return null;
    }


    private void performFieldInjection(Object serviceObject){
       try {
           for(Field field:serviceObject.getClass().getDeclaredFields()){
               if(field.isAnnotationPresent(Autowired.class)){
                   Object injectableObject;
                   // has qualifier
                   if(field.isAnnotationPresent(Qualifier.class)){
                       Qualifier annotation = field.getAnnotation(Qualifier.class);
                       String name = annotation.name();
                       injectableObject = getBeanOfName(name);
                   }

                   // has no qualifier
                   else{
                        Class<?> classType = field.getType();
                        injectableObject = getBeanOfType(classType);
                   }

                   field.setAccessible(true);
                   field.set(serviceObject, injectableObject);
               }
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    private void performMethodInjection(Object serviceObject){
        try{
            for(Method method: serviceObject.getClass().getDeclaredMethods()){
                if(method.isAnnotationPresent(Autowired.class)){
                    Class<?>[] methodParameters = method.getParameterTypes();
                    Class<?> injectableParameterType = methodParameters[0];
                    //get the object instance of this type
                    Object instance = getBeanOfType(injectableParameterType);
                    //do the injection
                    method.invoke(serviceObject, instance);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void performValueInjection(Object serviceObject, Properties properties){
       try{
           for (Field field : serviceObject.getClass().getDeclaredFields()) {
               if (field.isAnnotationPresent(Value.class)) {
                   // get the type of the field
                   Class<?> theFieldType = field.getType();
                   if (field.getType().getName().contentEquals("java.lang.String")) {
                       // get attribute value
                       String attrValue = field.getAnnotation(Value.class).name();
                       // get the property value
                       String toBeInjectedString = properties.getProperty(attrValue);
                       // do the injection
                       field.setAccessible(true);
                       field.set(serviceObject, toBeInjectedString);
                   }
               }
           }
       }catch (Exception e){
e.printStackTrace();
       }
    }



    public int getCronRate(String cron) {
        String[] splitresult = cron.split(" ");
        String secondsString = splitresult[0];
        String minutesString = splitresult[1];
        int seconds = Integer.parseInt(secondsString);
        int minutes = Integer.parseInt(minutesString);
        return (minutes * 60 + seconds) *1000;
    }

    private void scheduleMethods(Object serviceObject){
        Method[] methods = serviceObject.getClass().getDeclaredMethods();
        for(Method method:methods){
            if(method.isAnnotationPresent(Scheduled.class)){
                Scheduled scheduled = method.getAnnotation(Scheduled.class);
                // get the name of the Qualifier annotation
                int rate = scheduled.fixedRate();

                String cron = scheduled.cron();

                Timer timer = new Timer();


                if (rate > 0)  timer.scheduleAtFixedRate(new DotTimer(serviceObject, method), 0, rate);

                if (!Objects.equals(cron, "")) {
                    int cronrate = getCronRate(cron);
                    if (cronrate > 0)
                        timer.scheduleAtFixedRate(new DotTimer(serviceObject, method), 0, cronrate);
                }

            }
        }
    }
    public void performAllDI(){
        Properties properties = ConfigurationFileReader.getConfigProperties();
        for(Object serviceObject:serviceObjects.values()){
            Object newServiceObject = performConstructorDI(serviceObject);
            if(newServiceObject != null) serviceObject = newServiceObject;

            performFieldInjection(serviceObject);
            performMethodInjection(serviceObject);
            performValueInjection(serviceObject,properties);
            scheduleMethods(serviceObject);
        }

    }
}
