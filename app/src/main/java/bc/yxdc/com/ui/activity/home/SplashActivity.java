package bc.yxdc.com.ui.activity.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.OverlayAd;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/9/11.
 */

public class SplashActivity extends BaseActivity{

    private View ll_bg;
    private Animation animation;
    private ImageView iv_bg;
    private int count;
    private TimerSchedule mTimerSc;
    private TextView tv_countDown;
    private TextView tv_jump;

    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        ll_bg = findViewById(R.id.ll_bg);
        iv_bg = findViewById(R.id.iv_bg);
        tv_countDown = findViewById(R.id.tv_countdown);
        tv_jump = findViewById(R.id.tv_jump);
        mTimerSc = new TimerSchedule();
        count = 4;

        tv_jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTimerSc !=null) mTimerSc.cancel();
//                boolean remember= MyShare.get(SplashActivity.this).getBoolean(Constance.apply_remember);
//                if(!remember){
//                    showDialog();
//                }else {
//                    startAct();
//                }
                Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        animation = new AlphaAnimation(0,1);
        animation.setDuration(1500);
        animation.setFillAfter(true);

        misson(0, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                if(res!=null&&res.getJSONObject(Constance.result)!=null){
                    JSONArray openAdv=res.getJSONObject(Constance.result).getJSONArray(Constance.openAdv);
                    if(openAdv!=null||openAdv.length()>0){
                    int index=new Random().nextInt(openAdv.length());
                    ImageLoader.getInstance().loadImage(NetWorkConst.API_HOST + openAdv.getJSONObject(index).getString(Constance.ad_code), new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            doAnimation();
                        }

                        @Override
                        public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_bg.setImageBitmap(bitmap);
                                    doAnimation();
                                }
                            });

                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });
                    }else {
                        doAnimation();
                    }
                }else {
                    doAnimation();
                }
            }
        });
//        misson(0, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                doAnimation();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                JSONObject res=new JSONObject(response.body().string());
//                if(res!=null&&res.getJSONObject(Constance.result)!=null){

//                final OverlayAd overlayAd=new Gson().fromJson(res.getJSONObject(Constance.result).getJSONObject(Constance.overlayAd).toString(),OverlayAd.class);

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(overlayAd!=null){
//                            ImageLoader.getInstance().loadImage(NetWorkConst.API_HOST + overlayAd.getAd_code(), new ImageLoadingListener() {
//                                @Override
//                                public void onLoadingStarted(String s, View view) {
//
//                                }
//
//                                @Override
//                                public void onLoadingFailed(String s, View view, FailReason failReason) {
//                                    doAnimation();
//
//                                }
//
//                                @Override
//                                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
////                                    ll_bg.setBackground(new BitmapDrawable(bitmap));
////                                    iv_bg.setVisibility(View.GONE);
////                                    ll_bg.setOnClickListener(new View.OnClickListener() {
////                                        @Override
////                                        public void onClick(View v) {
////                                            try{
////                                                Uri uri=Uri.parse(overlayAd.getAd_link());
////                                                Intent intent=new Intent(Intent.ACTION_VIEW);
////                                                intent.setData(uri);
////                                                startActivity(intent);
////                                                finish();
////                                            }catch (Exception e){
////
////                                            }
////
////                                        }
////                                    });
//                                    doAnimation();
//
//                                }
//
//                                @Override
//                                public void onLoadingCancelled(String s, View view) {
//                                    doAnimation();
//
//                                }
//                            });
//                    doAnimation();
//                        }else {
//                            doAnimation();
//                        }
//                    }
//                });
//            }else {
//                    doAnimation();
//                }
//            }
//        });

    }
    public class TimerSchedule extends TimerTask {
        @Override
        public void run() {
            count--;
            if(count==0){
                handler.sendEmptyMessage(1);

            }else {
                handler.sendEmptyMessage(0);
            }

        }
    }

    private void doAnimation() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ll_bg.setVisibility(View.VISIBLE);
                ll_bg.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    if(mTimerSc!=null)new Timer().schedule(mTimerSc, 0,1000);
                                }catch (Exception e){

                                }
                            }
                        }.start();

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });

    }
    public static int countDown=0;
    public static int finishEnd=1;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==countDown){
                tv_countDown.setText(count+"s");
            }else if(msg.what==finishEnd){
            Intent intent=new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            }
        }
    };
    @Override
    public void getData(int type, Callback callback) {
        OkHttpUtils.getHomePage(callback);

    }
}
