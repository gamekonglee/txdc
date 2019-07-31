package bc.yxdc.com.global;

import android.widget.Toast;

import bc.yxdc.com.constant.Constant;
import bc.yxdc.com.manager.NetworkStateManager;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by gamekonglee on 2018/9/5.
 */

public class Apiclient {
    /**
     * 检查网络是否联网
     */
    public static boolean hashkNewwork() {

        boolean hasNetwork = NetworkStateManager.instance().isNetworkConnected();
        if (!hasNetwork) {
            Toast.makeText(IssApplication.getInstance(), "您的网络连接已中断", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public static void getUser(Callback callback){
        OkHttpClient okHttpClient=new OkHttpClient();
        String url=Constant.URL_USER;
        Request request=new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newBuilder().build().newCall(request).enqueue(callback);
    }

}
