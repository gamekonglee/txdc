package bc.yxdc.com.ui.activity.home;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.gson.Gson;
import com.m7.imkfsdk.KfStartHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pgyersdk.Pgy;
import com.pgyersdk.crash.PgyCrashManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.CartList;
import bc.yxdc.com.bean.OverlayAd;
import bc.yxdc.com.bean.ShopCartResult;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.Apiclient;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.MyWebViewActivity;
import bc.yxdc.com.ui.activity.goods.GoodsSearchResultActivity;
import bc.yxdc.com.ui.activity.goods.SelectGoodsActivity;
import bc.yxdc.com.ui.fragment.CartFragment;
import bc.yxdc.com.ui.fragment.ClassifyFragment;
import bc.yxdc.com.ui.fragment.GoodsListFragment;
import bc.yxdc.com.ui.fragment.HomeMainFragment;
import bc.yxdc.com.ui.fragment.MineMainFragment;
import bc.yxdc.com.ui.fragment.ProgrammerFragment;
import bc.yxdc.com.utils.AppUtils;
import bc.yxdc.com.utils.CommonUtil;
import bc.yxdc.com.utils.ImageUtil;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.NetWorkUtils;
import bc.yxdc.com.utils.UIUtils;
import bc.yxdc.com.view.TextViewPlus;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    private static final int TAKE_PHOTO = 100;
    private static final int TYPE_HOME = 2;
    @BindView(R.id.tv_tab_home)
    TextViewPlus tv_home;
    @BindView(R.id.tv_tab_classify)
    TextViewPlus tv_classify;
    @BindView(R.id.tv_tab_palette)
    TextViewPlus tv_tab_patelette;
    @BindView(R.id.tv_tab_shopping)
    TextViewPlus tv_tab_shopping;
    @BindView(R.id.tv_tab_me)
    TextViewPlus tv_tab_me;
    @BindView(R.id.unMessageReadTv)
    TextView unMessageReadTv;
    public static int currentTab;
    private HomeMainFragment mHomeFragment;
    private Fragment currentFragmen;
    private MineMainFragment mMineFragment;
    private GoodsListFragment classifyFragment;
    private CartFragment mCartFragment;
    private String mAppVersion;
    private int pager=2;
    private long exitTime;
    private ProgrammerFragment programmerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setStatuTextColor(this, Color.WHITE);
//        setFullScreenColor(this);
//        fullScreen(this);
        //沉浸式状态栏
        setStatuTextColor(this, Color.WHITE);
        setFullScreenColor(Color.TRANSPARENT,this);
        ButterKnife.bind(this);
        currentTab = 0;
        EventBus.getDefault().register(this);
        initTab();
        getHomePage();
        PgyCrashManager.register(this);
        refreshCart(Constance.refreshCart);
        if(!isToken()){
//            final KfStartHelper helper = new KfStartHelper(MainActivity.this);
//            helper.initSdkChat("6f555230-9f0b-11e9-a2f8-cb23e96e098b", "测试2","97289000");

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        PgyCrashManager.unregister();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshCart(String type){
        if(type.equals(Constance.refreshCart)){
        String userid= MyShare.get(this).getString(Constance.user_id);
        String token=MyShare.get(this).getString(Constance.token);
        if(userid!=null&&token!=null){
            OkHttpUtils.getShoppingCart(userid, token, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    JSONObject jsonObject=new JSONObject(response.body().string());
                    if(jsonObject.getJSONObject(Constance.result)!=null){

                        ShopCartResult result = new Gson().fromJson(jsonObject.getJSONObject(Constance.result).toString(), ShopCartResult.class);
                        List<CartList> cartList = result.getCartList();

                        if (cartList!=null&&cartList.size()> 0) {

                            IssApplication.mCartCount = (int) result.getTotal_price().getNum();
                        } else {
                            IssApplication.mCartCount=0;
                        }
//                                EventBus.getDefault().post(Constance.CARTCOUNT);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (IssApplication.mCartCount == 0) {
                                    unMessageReadTv.setVisibility(View.GONE);
                                } else {
                                    unMessageReadTv.setVisibility(View.VISIBLE);
                                    unMessageReadTv.setText(IssApplication.mCartCount + "");
                                }
                            }
                        });


                    }}

            });
        }
        }
    }
    private void getHomePage() {
        misson(TYPE_HOME, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                if(res!=null&&res.getJSONObject(Constance.result)!=null){
                    JSONObject overlayObject=res.getJSONObject(Constance.result).getJSONObject(Constance.overlayAd);
                    if(overlayObject!=null){
                        final OverlayAd overlayAd=new Gson().fromJson(res.getJSONObject(Constance.result).getJSONObject(Constance.overlayAd).toString(),OverlayAd.class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(overlayAd!=null){
                                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                                            .displayer(new RoundedBitmapDisplayer(UIUtils.dip2PX(15))) // display rounded bitmap
                                            .build();
                                    final Dialog dialog=new Dialog(MainActivity.this,R.style.customDialog);
                                    dialog.setContentView(R.layout.dialog_home);
                                    final ImageView iv_img=dialog.findViewById(R.id.iv_img);
                                    ImageView iv_close=dialog.findViewById(R.id.iv_close);

                                    iv_close.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    iv_img.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent=new Intent(MainActivity.this, SelectGoodsActivity.class);
                                            intent.putExtra(Constance.isNew,true);
                                            intent.putExtra(Constance.text,"新品预售");
//                                        intent.putExtra(Constance.url,overlayAd.getAd_link());
                                            startActivity(intent);
                                        }
                                    });
                                    ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST + overlayAd.getAd_code(),iv_img,options, new ImageLoadingListener() {
                                        @Override
                                        public void onLoadingStarted(String s, View view) {

                                        }

                                        @Override
                                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                                        }

                                        @Override
                                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                            dialog.show();
                                        }

                                        @Override
                                        public void onLoadingCancelled(String s, View view) {

                                        }
                                    });


                                }
                            }
                        });
                    }

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendVersion();
        if(currentTab==4||currentTab==3||currentTab==2){
            if(isToken()){
                return;
            }
        }
        refreshUI();
    }

    @OnClick({R.id.tv_tab_me,R.id.tv_tab_home,R.id.tv_tab_palette,R.id.tv_tab_shopping,R.id.tv_tab_classify})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_tab_home:
                currentTab=0;
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeMainFragment();
                }
                break;
            case R.id.tv_tab_classify:
                currentTab=1;
                if(classifyFragment==null)classifyFragment = new GoodsListFragment();

                break;
            case R.id.tv_tab_palette:
//                MyToast.show(this,"该功能尚未开放");
                if(programmerFragment==null)programmerFragment = new ProgrammerFragment();
                currentTab=2;
                break;
            case R.id.tv_tab_shopping:
                if(isToken()){
                    return;
                }
                if(mCartFragment==null){
                    mCartFragment = new CartFragment();
                }
                currentTab=3;
                break;
            case R.id.tv_tab_me:
                if(isToken()){
                    return;
                }
                if (mMineFragment == null) {
                    mMineFragment = new MineMainFragment();
                }
                currentTab=4;
                break;
        }
                refreshUI();
    }

    public  void refreshUI() {

        resetDrawable();
        switch (currentTab){
            case 0:
                setDrawable(tv_home,R.mipmap.tab_icon_home_selected);
                addOrShowFragment(getSupportFragmentManager().beginTransaction(),mHomeFragment);
                break;
            case 1:
                setDrawable(tv_classify,R.mipmap.tab_icon_classification_selected);
                addOrShowFragment(getSupportFragmentManager().beginTransaction(),classifyFragment);
                break;
            case 2:
                setDrawable(tv_tab_patelette,R.mipmap.tab_design_selected);
                addOrShowFragment(getSupportFragmentManager().beginTransaction(),programmerFragment);
                break;
            case 3:
                setDrawable(tv_tab_shopping,R.mipmap.tab_icon_shop_selected);
                addOrShowFragment(getSupportFragmentManager().beginTransaction(),mCartFragment);
                break;
            case 4:
                setDrawable(tv_tab_me,R.mipmap.tab_icon_me_selected);
                addOrShowFragment(getSupportFragmentManager().beginTransaction(),mMineFragment);
                break;

        }
    }

    private void setDrawable(TextViewPlus tv_classify, int tab_icon_classification_selected) {
        Drawable drawable=getResources().getDrawable(tab_icon_classification_selected);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        tv_classify.setCompoundDrawables(null,drawable,null,null);
        tv_classify.setTextColor(getResources().getColor(R.color.theme_red));
    }

    private void resetDrawable() {
        Drawable drawable=getResources().getDrawable(R.mipmap.tab_icon_home_default);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        tv_home.setCompoundDrawables(null,drawable,null,null);
        tv_home.setTextColor(getResources().getColor(R.color.tv_home_default));
         drawable=getResources().getDrawable(R.mipmap.tab_icon_classification_default);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        tv_classify.setCompoundDrawables(null,drawable,null,null);
        tv_classify.setTextColor(getResources().getColor(R.color.tv_home_default));
        drawable=getResources().getDrawable(R.mipmap.tab_design_default);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        tv_tab_patelette.setCompoundDrawables(null,drawable,null,null);
        tv_tab_patelette.setTextColor(getResources().getColor(R.color.tv_home_default));
        drawable=getResources().getDrawable(R.mipmap.tab_icon_shop_default);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        tv_tab_shopping.setCompoundDrawables(null,drawable,null,null);
        tv_tab_shopping.setTextColor(getResources().getColor(R.color.tv_home_default));
        drawable=getResources().getDrawable(R.mipmap.tab_icon_me_default);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        tv_tab_me.setCompoundDrawables(null,drawable,null,null);
        tv_tab_me.setTextColor(getResources().getColor(R.color.tv_home_default));
    }

    /**
     * 添加或者显示碎片
     *
     * @param transaction
     * @param fragment
     */
    private void addOrShowFragment(FragmentTransaction transaction,
                                   Fragment fragment) {
        if (currentFragmen == fragment)
            return;

        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(currentFragmen)
                    .add(R.id.top_bar, fragment).commit();
        } else {
            transaction.hide(currentFragmen).show(fragment).commit();
        }

        currentFragmen = fragment;
    }
    @Override
    protected void initData() {
    }

    @Override
    public void initUI() {

    }

    @Override
    public void getData(int type, final Callback callback) {
        if(type==0){
//            Apiclient.getUser(callback);
        }else if(type==TYPE_HOME){
            OkHttpUtils.getHomePage(callback);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (pager == 2) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    exitTime = System.currentTimeMillis();

                    MyToast.show(this, R.string.back_desktop);
                } else {
                    finish();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 获取版本号
     */
    private void sendVersion(){
//        mNetWork.sendVersion(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ans = NetWorkUtils.doGet(NetWorkConst.VERSION_URL_CONTENT);
                final JSONObject jsonObject=new JSONObject(ans);
                mAppVersion = jsonObject.getString(Constance.version);
                if(AppUtils.isEmpty(mAppVersion)) return;
                String localVersion = CommonUtil.localVersionName(MainActivity.this);
                if ("-1".equals(mAppVersion)) {

                } else {
                    boolean isNeedUpdate = CommonUtil.isNeedUpdate(localVersion, mAppVersion);
                    if (isNeedUpdate){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                final Dialog dialog=new Dialog(MainActivity.this,R.style.customDialog);
                                dialog.setContentView(R.layout.dialog_update);
                                TextView tv_version=dialog.findViewById(R.id.tv_version);
                                TextView tv_upgrate=dialog.findViewById(R.id.tv_upgrate);
                                ImageView iv_close=dialog.findViewById(R.id.iv_close);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                iv_close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                tv_version.setText("V"+mAppVersion);
                                tv_upgrate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        Uri content_url = Uri.parse("http://app.08138.com/txdc.apk");
                                        intent.setData(content_url);
                                        startActivity(intent);
                                    }
                                });
                                dialog.show();
//                                UIUtils.showSingleWordDialog(MainActivity.this, "发现有新版本，是否更新？", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent intent = new Intent();
//                                        intent.setAction("android.intent.action.VIEW");
//                                        Uri content_url = Uri.parse("http://app.08138.com/txdc.apk");
//                                        intent.setData(content_url);
//                                        startActivity(intent);
//                                    }
//                                });

                            }
                        });


                    }
                }

            }
        }).start();

    }
    /**
     * 初始化底部标签
     */
    private void initTab() {
        if (mHomeFragment == null) {
            mHomeFragment = new HomeMainFragment();
        }
        if (!mHomeFragment.isAdded()) {
            // 提交事务
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.top_bar, mHomeFragment).commit();

            // 记录当前Fragment
            currentFragmen = mHomeFragment;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
