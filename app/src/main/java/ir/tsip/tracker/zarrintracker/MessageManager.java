package ir.tsip.tracker.zarrintracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 10/31/2015.
 */
public class MessageManager {
    private static List<String> _Message = new ArrayList<String>();

    public static void SetMessage(String Mes)
    {
        //_Message.add(Mes);
    }

    public static String GetMessage()
    {
        String Ret="";
        if(_Message.size() >0)
            Ret = _Message.remove(0);
        return  Ret;
    }

}
