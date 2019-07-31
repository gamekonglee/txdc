package bc.yxdc.com.base;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import androidx.core.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;


import java.lang.reflect.Field;

import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.utils.AppUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.UIUtils;
import bc.yxdc.com.view.SpotsDialog;
import okhttp3.Callback;

/**
 * Created by gamekonglee on 2018/8/10.
 */

public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initData();
        initUI();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            //取消状态栏透明
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //添加Flag把状态栏设为可绘制模式
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
//            window.setStatusBarColor(color);
            //设置系统状态栏处于可见状态
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                //设置状态栏文字颜色及图标为深色
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    protected abstract void initData();
    public abstract void initUI();
    public abstract void getData(int type,Callback callback);
//    public abstract void onErrorRefreshUI();
    public void misson(int type,Callback callback){
        getData(type,callback);
    }

    public void goBack(View v){
        finish();
    }
//    public void setStatuTextColor(Activity activity, int color) {
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
//            Window window = activity.getWindow();
//            //取消状态栏透明
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //添加Flag把状态栏设为可绘制模式
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            //设置状态栏颜色
//            window.setStatusBarColor(color);
//            //设置系统状态栏处于可见状态
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
//                //设置状态栏文字颜色及图标为深色
//                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            }
//        }
//    }
public void setStatuTextColor(Activity activity, int color) {
    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
        Window window = activity.getWindow();
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(color);
        //设置系统状态栏处于可见状态
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            //设置状态栏文字颜色及图标为深色
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
    public static void setFullScreenColor(int color,Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 生成一个状态栏大小的矩形
//            View statusView = createStatusView(activity, color);
            // 添加 statusView 到布局中
//            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
//            decorView.addView(statusView);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
//            rootView.setFitsSystemWindows(true);
//            rootView.setClipToPadding(true);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                try {
                    Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                    Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                    field.setAccessible(true);
                    field.setInt(activity.getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
                } catch (Exception e) {}
            }
        }
    }
//    public static void setFullScreenColor(Activity activity){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
////            // 设置状态栏透明
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
////            // 生成一个状态栏大小的矩形
////            View statusView = createStatusView(activity, color);
//            // 添加 statusView 到布局中
////            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
////            decorView.addView(statusView);
//            // 设置根布局的参数
//            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
////            rootView.setFitsSystemWindows(true);
////            rootView.setClipToPadding(true);
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//                try {
//                    Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
//                    Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
//                    field.setAccessible(true);
//                    field.setInt(activity.getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
//                } catch (Exception e) {}
//            }
//        }
//    }
    public void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
//                Window window = activity.getWindow();
//                WindowManager.LayoutParams attributes = window.getAttributes();
//                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
////                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
//                attributes.flags |= flagTranslucentStatus;
////                attributes.flags |= flagTranslucentNavigation;
//                window.setAttributes(attributes);
                // Android5.0之后的沉浸式状态栏写法
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                // 两个标志位要结合使用，表示让应用的主体内容占用系统状态栏的空间
                // 第三个标志位可让底部导航栏变透明View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            }
        }
    }

    /**
     * 设置状态栏颜色
     * @param activity 需要设置的activity
     * @param color 状态栏颜色值
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity, color);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusView);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            //取消状态栏透明
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //添加Flag把状态栏设为可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(color);
            //设置系统状态栏处于可见状态
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                //设置状态栏文字颜色及图标为深色
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            //让view不根据系统窗口来调整自己的布局
            ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                ViewCompat.setFitsSystemWindows(mChildView, true);
                ViewCompat.requestApplyInsets(mChildView);

            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(activity.getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (Exception e) {}
        }
        setStatusBarDarkIcon(activity.getWindow(),true);
    }

    public static boolean setStatusBarDarkIcon(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
                Log.e("MeiZu", "setStatusBarDarkIcon: failed");
            }
        }
        return result;
    }
    /**
     * 获取并绑定点击
     *
     * @param id id
     */
    @SuppressWarnings("unchecked")
    protected <T extends View> T getViewAndClick(@IdRes int id) {
        T v = getView(id);
        v.setOnClickListener(this);
        return v;
    }
    /**
     * 获取 控件
     *
     * @param id 行布局中某个组件的id
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(@IdRes int id) {
        return (T) findViewById(id);
    }
    /**
     * 生成一个和状态栏大小相同的矩形条
     * @param activity 需要设置的activity
     * @param color 状态栏颜色值
     * @return 状态栏矩形条
     */
    private static View createStatusView(Activity activity, int color) {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }

    @Override
    public void onClick(View v) {

    }

    private boolean showDialog;// 显示加载对话框
    private boolean isDestroy;
    /**
     * 显示加载对话框
     *
     * @param showDialog 是否显示加载对话框
     */
    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }
    /**
     * 加载框文本
     */
    public void setShowDialog(String msg) {
        if (isDestroy)
            return;
        if (msg == null) {
            showDialog = false;
            return;
        }
        showDialog = true;
        if (mDialog == null) {
            setLoadingDialog(new SpotsDialog(this, msg));
        } else {
//            mDialog.setLoadMsg(msg);
        }
    }
    SpotsDialog mDialog;
    public void setLoadingDialog(SpotsDialog loadingDialog) {
        this.mDialog = loadingDialog;
    }

    /**
     * 隐藏加载框
     */
    public void hideLoading() {
        if (isDestroy)
            return;
        if (mDialog != null)
            mDialog.dismiss();
    }

    /**
     * 显示加载框
     */
    public void showLoading() {
        if (isDestroy)
            return;
        if (showDialog) {
            if (mDialog == null) {
                setLoadingDialog(new SpotsDialog(this));
            }
            try {
                mDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 判断是否有toKen
     */
    public Boolean isToken() {
        String token = MyShare.get(this).getString(Constance.TOKEN);
        if(AppUtils.isEmpty(token)){
            UIUtils.showLoginDialog(this);
            return true;
        }
        return false;
    }
}
