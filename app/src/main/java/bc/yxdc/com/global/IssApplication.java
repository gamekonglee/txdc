package bc.yxdc.com.global;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.pgyersdk.crash.PgyCrashManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.bean.Goods;
import bc.yxdc.com.bean.GoodsBean;
import bc.yxdc.com.bean.SceneBean;
import bc.yxdc.com.utils.ImageLoadProxy;
import bc.yxdc.com.utils.MyShare;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by gamekonglee on 2018/9/5.
 */

public class IssApplication extends Application{
    private static Context instance;
    public static boolean isClassify;
    public static String imagePath;
    public static File cameraPath;
    private static DisplayImageOptions defaultOptions;
    private static ImageLoaderConfiguration config;
    private static DisplayImageOptions options;
    public static List<Goods> mSelectProducts=new ArrayList<>();
    public static int mCartCount;
    public static String mUserBean;
    public static int mLightIndex=0;
    public static List<SceneBean> mSelectScreens=new ArrayList<>();
//    public static JSONObject mUserObject;

    public static Context getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        mSelectProducts=new ArrayList<>();
        initImageLoader();
        ImageLoadProxy.initImageLoader(instance);
        PgyCrashManager.register(this);
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        /**
         * 注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，也需要在App代码中调
         * 用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，
         * UMConfigure.init调用中appkey和channel参数请置为null）。
         */
//        IMChatManager.getInstance().init(this,);
        try {
        UMConfigure.init(this, "5cf87757570df36b5e00090f", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "");

        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        // 支持在子进程中统计自定义事件
        UMConfigure.setProcessEvent(true);
        }catch (Exception e){
        }

//        IMChatManager.getInstance().init(getApplicationContext(),
//                "receiverAction",
//                "6f555230-9f0b-11e9-a2f8-cb23e96e098b",
//                "username ",
//                "userId");
    }



    //初始化网络图片缓存库
    private void initImageLoader(){
        //网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    public  static DisplayImageOptions getImageLoaderOption() {
        if(defaultOptions==null) {
            defaultOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565)
                    .cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY)
                    .cacheOnDisk(true).build();
        }
        // default
// .memoryCache(new LruMemoryCache((int) (6 * 1024 * 1024)))
// Remove
        if(config ==null){
            config = new ImageLoaderConfiguration.Builder(
                    getInstance())
                    .threadPoolSize(4)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .denyCacheImageMultipleSizesInMemory()
// .memoryCache(new LruMemoryCache((int) (6 * 1024 * 1024)))
                    .memoryCache(new LruMemoryCache(5 * 1024 * 1024))
                    .memoryCacheSize((int) (2 * 1024 * 1024))
                    .memoryCacheSizePercentage(25)
                    .diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(200)
                    .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                    .defaultDisplayImageOptions(defaultOptions).writeDebugLogs() // Remove
                    .build();
        }
// Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        if(options ==null){
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.bg_default) // resource or
                    // drawable
                    .showImageForEmptyUri(R.drawable.bg_default) // resource or
                    // drawable
                    .showImageOnFail(R.drawable.bg_default) // resource or
                    .cacheInMemory(true)// drawable
                    .resetViewBeforeLoading(false) // default
                    .delayBeforeLoading(1000).cacheInMemory(true) // default
                    .cacheOnDisk(true) // default
                    .considerExifParams(false) // default
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                    .bitmapConfig(Bitmap.Config.RGB_565) // default
                    .displayer(new SimpleBitmapDisplayer()) // default
                    .handler(new Handler()) // default
                    .build();
        }
        return options;
    }
}
