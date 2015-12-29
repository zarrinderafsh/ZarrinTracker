package ir.tsip.tracker.zarrintracker;

import java.lang.reflect.Method;

/**
 * Created by morteza on 2015-11-12.
 */
public class Action {
    public static Object run(String ClassName,String methodName,Class<?>[] ParamsType,Object[] Params) throws ClassNotFoundException {
        try {
            Class<?> clazz = Class.forName(ClassName);

            Method method = clazz.getMethod(methodName, ParamsType);
            Object ret = method.invoke(null,Params);
            return  ret;
        }
        catch (Exception ex)
        {
            ex.toString();
        }
        return null;
    }
}
