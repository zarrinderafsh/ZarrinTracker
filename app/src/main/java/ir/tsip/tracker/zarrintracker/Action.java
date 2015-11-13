package ir.tsip.tracker.zarrintracker;

import android.content.res.Resources;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by morteza on 2015-11-12.
 */
public class Action {
    public static Object run(String ClassName,String methodName,Class<?>[] ParamsType,Object[] Params) throws ClassNotFoundException {
        try {
            Class<?> clazz = Class.forName(ClassName);
            Constructor<?> ctor = clazz.getConstructor(String.class);
            Object object = ctor.newInstance(new Object[]{});
            Object ret = object.getClass().getMethod(methodName,ParamsType).invoke(Params);
            return  ret;
        }
        catch (Exception ex)
        {

        }
        return null;
    }
}
