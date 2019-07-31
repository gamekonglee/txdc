package bc.yxdc.com.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by gamekonglee on 2018/9/11.
 */

public class IntentUtil {
    public static void startActivity(Context context, Class clzz, boolean flag) {
        Intent intent = new Intent(context, clzz);
        ((Activity) context).startActivity(intent);
        if (flag) {
            ((Activity) context).finish();
        }
    }
    public static void startActivity(Activity activity, Class clzz, boolean flag) {
        Intent intent = new Intent(activity, clzz);
        (activity).startActivity(intent);
        if (flag) {
            (activity).finish();
        }
    }
}
