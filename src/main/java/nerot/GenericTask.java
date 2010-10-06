package nerot;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.util.Date;
import java.io.IOException;
import java.lang.reflect.*;

/**
 * Use reflection to call an object generically and store the results in the Store.
 */
public class GenericTask extends BaseTask {

    private String key;
    private Object actor;
    private String method;
    private Object[] args;
    
    public void execute() {    
        try {
            Object retobj = null;
            if (actor instanceof Class) {
                Method m = getMethodObject((Class)actor);
                m.setAccessible(true);
                retobj = m.invoke(m, args);
            }
            else {
                Method m = getMethodObject(actor.getClass());
                m.setAccessible(true);
                retobj = m.invoke(actor, args);
            }
            getStore().set(key, retobj);
            System.err.println("stored object for key: " + key);
        } catch (Throwable t) {
            System.err.println("failed to set object for key: " + key);
            t.printStackTrace();
        }
    }
    
    private Method getMethodObject(Class c) throws Throwable {
        int found = 0;
        Method result = null;
        Method[] methods = c.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            //System.err.println("Checking reflected method name '" + method.getName() + "' vs. supplied method name '" + getMethod() + "'");
            if (method.getName().equals(getMethod()) && isEquivalent(toClassArray(args), method.getParameterTypes())) {
                if (result != null) {
                    found++;
                }
                result = method;
                //System.err.println("Matched method!");
            }
        }
        
        if (found > 1) {
            System.err.println("Warning: " + found + " method signatures matched specified method!");
        }
        
        return result;
    }
    
    private Class[] toClassArray(Object[] o) {
        Class[] result = null;
        if (o != null) {
            result = new Class[o.length];
            for (int i=0; i<o.length; i++) {
                Object obj = o[i];
                if (obj != null) { 
                    result[i] = obj.getClass();
                }
            }
        }
        //System.err.println("toClassArray results:");
        //debugArray(result);
        return result;
    }
    
    /*
    private void debugArray(Object[] o) {
        if (o != null) {
            System.err.print("[");
            for (int i=0; i<o.length; i++) {
                if (i!=0) {
                    System.err.print(",");
                }
                System.err.print(o[i]);
            }
            System.err.println("]");
        }
        else {
            System.err.print(o);
        }
    }
    */
    
    private boolean isEquivalent(Class[] c1, Class[] c2) {
        if ((c1 == null || c1.length == 0) && (c2 == null || c2.length == 0)) {
            return true;
        }
        else if (c1 != null && c2 != null && c1.length == c2.length) {
            for (int i=0; i<c1.length; i++) {
                Class c1c = toPrimitiveIfWrapperClass(c1[i]);
                Class c2c = toPrimitiveIfWrapperClass(c2[i]);
                if (!c1c.getName().equals(c2c.getName())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private Class toPrimitiveIfWrapperClass(Class c) {
        Class result = c;
        if (c == Byte.class) {
            result = byte.class;
        } else if (c == Short.class) {
            result = short.class;
        } else if (c == Integer.class) {
            result = int.class;
        } else if (c == Long.class) {
            result = long.class;
        } else if (c == Float.class) {
            result = float.class;
        } else if (c == Double.class) {
            result = double.class;
        } else if (c == Boolean.class) {
            result = boolean.class;
        } else if (c == Character.class) {
            result = char.class;
        }
        return result;
    }
    
    /*
    private void print(Type t) {
        System.err.println("" + t);
        Method[] methods = t.getClass().getDeclaredMethods();
        for (int i=0; i < methods.length; i++) {
            Method m = methods[i];
            System.err.println(m.getName());
        }
    }
    */

    /**
     * Key for the value stored in Nerot's store.
     */
    public String getKey() {
        return key;
    }
    
    /**
     * Set key for the return Object in Nerot's store.
     */
    public void setKey(String key) {
        this.key = key;
    }
    
    /**
     * Get the instance or class to call.
     */
    public Object getActor() {
        return actor;
    }
    
    /**
     * Set the instance or class to call.
     */
    public void setActor(Object actor) {
        this.actor = actor;
    }
    
    /**
     * Get the method name to call on the actor.
     */
    public String getMethod() {
        return method;
    }
    
    /**
     * Set the method name to call on the actor.
     */
    public void setMethod(String method) {
        this.method = method;
    }
    
    /**
     * Get the arguments to use when calling specified method on the actor.
     */
    public Object[] getArgs() {
        return args;
    }
    
    /**
     * Set the arguments to use when calling specified method on the actor.
     */
    public void setArgs(Object[] args) {
        this.args = args;
    }
    
    /**
     * Set the arguments to use when calling method on the actor.
     */
    public void setActor(Object[] args) {
        this.args = args;
    }
}