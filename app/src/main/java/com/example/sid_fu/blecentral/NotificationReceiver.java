package com.example.sid_fu.blecentral;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sid_fu.blecentral.activity.MainFrameForStartServiceActivity;
import com.example.sid_fu.blecentral.ui.activity.HomeActivity;
import com.example.sid_fu.blecentral.utils.SystemUtils;

/**
 * Created by Administrator on 2016/8/10.
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //判断app进程是否存活
        if(SystemUtils.isAppAlive(context, "com.example.sid_fu.blecentral")){
            //如果存活的话，就直接启动DetailActivity，但要考虑一种情况，就是app的进程虽然仍然在
            //但Task栈已经空了，比如用户点击Back键退出应用，但进程还没有被系统回收，如果直接启动
            //DetailActivity,再按Back键就不会返回MainActivity了。所以在启动
            //DetailActivity前，要先启动MainActivity。
            Log.i("NotificationReceiver", "the app process is alive");
            Intent mainIntent = new Intent(context, HomeActivity.class);
            //将MainAtivity的launchMode设置成SingleTask, 或者在下面flag中加上Intent.FLAG_CLEAR_TOP,
            //如果Task栈中有MainActivity的实例，就会把它移到栈顶，把在它之上的Activity都清理出栈，
            //如果Task栈不存在MainActivity实例，则在栈顶创建
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent detailIntent = new Intent(context, MainFrameForStartServiceActivity.class);
            detailIntent.putExtra("DB_ID", intent.getExtras().getInt("id"));
            detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent[] intents = {mainIntent, detailIntent};

            context.startActivities(intents);
//            SharedPreferences.getInstance().remove(Constants.LAST_DEVICE_ID);
//            context.startActivity(mainIntent);
        }else {
            //如果app进程已经被杀死，先重新启动app，将DetailActivity的启动参数传入Intent中，参数经过
            //SplashActivity传入MainActivity，此时app的初始化已经完成，在MainActivity中就可以根据传入
            // 参数跳转到DetailActivity中去了
            Log.i("NotificationReceiver", "the app process is dead");
            Intent launchIntent = context.getPackageManager().
                    getLaunchIntentForPackage("com.example.sid_fu.blecentral");
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//            Bundle args = new Bundle();
//            args.putString("name", "电饭锅");
//            args.putString("price", "58元");
//            args.putString("detail", "这是一个好锅, 这是app进程不存在，先启动应用再启动Activity的");
//            launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
            context.startActivity(launchIntent);
        }
    }
}