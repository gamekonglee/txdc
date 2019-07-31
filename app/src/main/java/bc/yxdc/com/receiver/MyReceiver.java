package bc.yxdc.com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * Created by gamekonglee on 2018/12/3.
 */

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "pushreceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationBarEvent notificationBarEvent = new NotificationBarEvent();

        if(intent!=null&&intent.getExtras()!=null){
            Bundle bundle=intent.getExtras();
            String regisId=JPushInterface.getRegistrationID(context);
            if(!TextUtils.isEmpty(regisId)){
            MyShare.get(context).putString(Constance.RegistrationID,regisId);
            }
            LogUtils.logE("receive",regisId+","+intent.getExtras().getString(JPushInterface.EXTRA_REGISTRATION_ID));
            //      通知栏点击事件监听
//      通知栏收到消息时触发


            /**
             * 极光推送
             */
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            LogUtils.logE(TAG, "JPush用户注册成功");
            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            LogUtils.logE(TAG, "接受到推送下来的自定义消息");

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            LogUtils.logE(TAG, "接受到推送下来的通知");
                notificationBarEvent.receivingNotification(context,bundle.getString(JPushInterface.EXTRA_EXTRA));

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            LogUtils.logE(TAG, "用户点击打开了通知");
                notificationBarEvent.openNotification(context,bundle.getString(JPushInterface.EXTRA_EXTRA));
            }
        }



    }
}
