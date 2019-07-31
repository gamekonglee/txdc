package bc.yxdc.com.receiver;

import android.content.Context;
import android.content.Intent;

import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.ui.activity.goods.GoodsXsQgActivity;
import bc.yxdc.com.ui.activity.goods.GoodsXsTmActivity;
import bc.yxdc.com.ui.activity.order.MyExpressActivity;
import bc.yxdc.com.utils.LogUtils;
import bocang.json.JSONObject;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by gamekonglee on 2018/12/21.
 */

public class NotificationBarEvent {
    //接受推送下来的通知，消息到达时触发
    public void receivingNotification(Context context, String message1) {
        String message = message1; //获取等到通知栏消息

//        MainActivity.mainActivity_instance.GetStoreUnreadMessages(); //获取商家未读消息

    }
//通知栏点击监听事件，用户点击时触发
    public void openNotification(Context context, String description) {


        String alert = description;
        LogUtils.logE("alter",alert);
        JSONObject jsonObject=new JSONObject(alert);
        String type=jsonObject.getString(Constance.type);
        JSONObject data=jsonObject.getJSONObject(Constance.data);
        if(type.equals("promotion")){
            int prom_type=data.getInt(Constance.prom_type);
            if(prom_type==1){
                Intent intent=new Intent(context, GoodsXsQgActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else if (prom_type==3){
                Intent intent=new Intent(context, GoodsXsTmActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }else if(type.equals("shipping")){
            Intent intent=new Intent(context, MyExpressActivity.class);
            intent.putExtra(Constance.order_id,data.getInt(Constance.order_id)+"");
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        //根据消息内容跳转到指定页面
    }
}
