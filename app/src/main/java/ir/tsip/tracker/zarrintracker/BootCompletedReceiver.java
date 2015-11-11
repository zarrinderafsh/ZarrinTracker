package ir.tsip.tracker.zarrintracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {
    public BootCompletedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ServiceManager.StartService(context,SendDataService.class);
        ServiceManager.StartService(context,LocationListener.class);
    }
}
