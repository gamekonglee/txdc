package bc.yxdc.com.ui.activity.goods;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.core.app.CoreComponentFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.m7.imkfsdk.KfStartHelper;
import com.moor.imkf.model.entity.CardInfo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.CartList;
import bc.yxdc.com.bean.Goods;
import bc.yxdc.com.bean.GoodsBean;
import bc.yxdc.com.bean.GoodsListResult;
import bc.yxdc.com.bean.ProductResult;
import bc.yxdc.com.bean.ShopCartResult;
import bc.yxdc.com.bean.Spec_goods_price;
import bc.yxdc.com.bean.User;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.listener.IParamentChooseListener;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.service.MyChatService;
import bc.yxdc.com.ui.activity.buy.ConfirmOrderActivity;
import bc.yxdc.com.ui.activity.buy.ShoppingCartActivity;
import bc.yxdc.com.ui.activity.diy.DiyActivity;
import bc.yxdc.com.ui.activity.home.MainActivity;
import bc.yxdc.com.ui.activity.user.LoginActivity;
import bc.yxdc.com.ui.fragment.DetailGoodsFragmemt;
import bc.yxdc.com.ui.fragment.IntroduceGoodsFragment;
import bc.yxdc.com.ui.fragment.ParameterFragment;
import bc.yxdc.com.ui.fragment.SunImageFragment;
import bc.yxdc.com.ui.view.popwindow.SelectParamentPopWindow;
import bc.yxdc.com.utils.AppUtils;
import bc.yxdc.com.utils.IntentUtil;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.ScannerUtils;
import bc.yxdc.com.utils.ShareUtil;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/9/13.
 */

public class ProDetailActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    private static final int TYPE_PRO_DETAIL = 1;
    private static final int TYPE_BUY = 3;
    private static final int TYPE_PRO_PC = 4;
    public static boolean isJuHao;
    public static List<Spec_goods_price> spec_goods_prices;
    public int mProductId=0;
    public int mOrderId=1;
    public String mOrderid="";
    private LinearLayout  parament_ll, callLl, go_photo_Ll;
    private Button toDiyBtn, toCartBtn;
    private ImageView share_iv;
    public String mProperty = "";
    public String mPropertyValue = "";
    public int mPrice = 0;
    private RelativeLayout shopping_cart_Ll;
    public static  boolean isXianGou;
//    public com.alibaba.fastjson.JSONObject mProductObject;
    private ImageView iv_home;
    public LinearLayout tuijian_ll;
    private Button toBuyBtn;
    private TextView product_tv, detail_tv, parament_tv, sun_image_tv;
    private ViewPager container_vp;
    private Intent mIntent;
    private LinearLayout title_ll, product_ll, detail_ll, main_ll, sun_image_ll;
    private View product_view, detail_view, parament_view, sun_image_view;
    private ImageView collectIv;
    private RelativeLayout main_rl;
    private TextView unMessageReadTv;
    private TextView tuijian_tv;
    private JSONObject mAddressObject;
    private ProductContainerAdapter mContainerAdapter;
    private List<Fragment> mFragments;
    private SelectParamentPopWindow mPopWindow;
    private View tuijian_view;
    public static ProductResult goods;
    private IntroduceGoodsFragment introFragment;
    public int mCount;
    public LinearLayout ll_pro_top;
    public RelativeLayout rl_pro_top;
//    public boolean isQiangGou;

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mProductId = intent.getIntExtra(Constance.product, 0);
        mOrderid = intent.getStringExtra(Constance.order_id);
//        isQiangGou = intent.getBooleanExtra(Constance.qianggou,false);
        isJuHao=false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mProductId=intent.getIntExtra(Constance.product,0);
        mContainerAdapter = new ProductContainerAdapter(getSupportFragmentManager());
        container_vp.setAdapter(mContainerAdapter);
        selectProductType(R.id.product_ll);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initUI() {
        setContentView(R.layout.activity_product_detail);
        setStatuTextColor(this, Color.WHITE);
        setFullScreenColor(Color.TRANSPARENT,this);
        EventBus.getDefault().register(this);
        product_ll = getViewAndClick(R.id.product_ll);
        detail_ll = getViewAndClick(R.id.detail_ll);
        parament_ll = getViewAndClick(R.id.parament_ll);
        sun_image_ll = getViewAndClick(R.id.sun_image_ll);
        tuijian_ll = getViewAndClick(R.id.tuijian_ll);
        callLl = getViewAndClick(R.id.callLl);
        shopping_cart_Ll = getViewAndClick(R.id.shopping_cart_Ll);
        toDiyBtn = getViewAndClick(R.id.toDiyBtn);
        toCartBtn = getViewAndClick(R.id.toCartBtn);
        share_iv = getViewAndClick(R.id.share_iv);
        go_photo_Ll = getViewAndClick(R.id.go_photo_Ll);
        iv_home = getViewAndClick(R.id.iv_home);
        toBuyBtn = getViewAndClick(R.id.toBuyBtn);
        product_view = findViewById(R.id.product_view);
        detail_view = findViewById(R.id.detail_view);
        parament_view = findViewById(R.id.parament_view);
        sun_image_view = findViewById(R.id.sun_image_view);
        tuijian_view = findViewById(R.id.tuijian_view);
        product_tv = (TextView) findViewById(R.id.product_tv);
        detail_tv = (TextView) findViewById(R.id.detail_tv);
        parament_tv = (TextView) findViewById(R.id.parament_tv);
        sun_image_tv = (TextView) findViewById(R.id.sun_image_tv);
        tuijian_tv = findViewById(R.id.tuijian_tv);
        unMessageReadTv = (TextView) findViewById(R.id.unMessageReadTv);
        container_vp = (ViewPager) findViewById(R.id.container_vp);

        mContainerAdapter = new ProductContainerAdapter(getSupportFragmentManager());
        container_vp.setAdapter(mContainerAdapter);
        container_vp.setOnPageChangeListener(this);
        container_vp.setCurrentItem(0);
        title_ll = (LinearLayout) findViewById(R.id.title_ll);
        product_ll = (LinearLayout) findViewById(R.id.product_ll);
        main_ll = (LinearLayout) findViewById(R.id.main_ll);
        detail_ll = (LinearLayout) findViewById(R.id.detail_ll);
//        parament_ll = (LinearLayout) findViewById(R.id.parament_ll);
        sun_image_ll = (LinearLayout) findViewById(R.id.sun_image_ll);

        main_rl = (RelativeLayout) findViewById(R.id.main_rl);
        collectIv = (ImageView) findViewById(R.id.collectIv);
        ll_pro_top = findViewById(R.id.ll_pro_top);
        rl_pro_top = findViewById(R.id.rl_pro_top);
        ImageView iv_share=findViewById(R.id.iv_share);
        iv_share.setOnClickListener(this);

        container_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    mFragments.get(position).onStart();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        misson(TYPE_PRO_PC, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                JSONObject res=new JSONObject(response.body().string());
//                LogUtils.logE("thumb",res.toString());
//
//            }
//        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getCartMun();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.product_ll:
                selectProductType(R.id.product_ll);
                break;
            case R.id.detail_ll:
                selectProductType(R.id.detail_ll);
                break;
//            case R.id.parament_ll:
//                selectProductType(R.id.parament_ll);
//                break;
            case R.id.sun_image_ll:
                selectProductType(R.id.sun_image_ll);
                break;
            case R.id.tuijian_ll:
                selectProductType(R.id.tuijian_ll);
                break;
            case R.id.callLl:
                if (!isToken()){
                    //适配8.0service
                    NotificationManager notificationManager = (NotificationManager) IssApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationChannel mChannel = null;
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                   startService(new Intent(this, MyChatService.class));
//                    }
                    String icon=NetWorkConst.IMAGE_URL+ProDetailActivity.goods.getGoods().getGoods_id();
                    LogUtils.logE("icon",icon);
                    String goodsName=goods.getGoods().getGoods_name();
                    LogUtils.logE("goodsName",goodsName);
                    String price="¥"+goods.getGoods().getShop_price();
                    LogUtils.logE("price",price);
                    String content=goods.getSpec_goods_price().get(IntroduceGoodsFragment.currentAttrPosi).getKey_name();
                    LogUtils.logE("content",content);
                    String url="http://www.tianxiadengcang.com/Mobile/Goods/goodsInfo/id/"+goods.getGoods().getGoods_id()+".html";
                    LogUtils.logE("url",url);
                    CardInfo ci = null;
                    try {
                        ci = new CardInfo(URLEncoder.encode(icon,"utf-8") ,URLEncoder.encode(goodsName ,"utf-8"),URLEncoder.encode(price,"utf-8" ),URLEncoder.encode(content,"utf-8"), URLEncoder.encode(url,"utf-8"));

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    final KfStartHelper helper = new KfStartHelper(ProDetailActivity.this);
                    User user=new Gson().fromJson(IssApplication.mUserBean,User.class);
                    helper.setCard(ci);
                    if(user==null){
                        OkHttpUtils.getUserInfo(MyShare.get(ProDetailActivity.this).getString(Constance.TOKEN), MyShare.get(ProDetailActivity.this).getString(Constance.user_id), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                MyToast.show(ProDetailActivity.this,"登录状态失效");
                                startActivity(new Intent(ProDetailActivity.this, LoginActivity.class));
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final JSONObject jsonObject=new JSONObject(response.body().string());
                                if(jsonObject!=null&&jsonObject.getJSONObject(Constance.result)!=null){
                                    final User user=new Gson().fromJson(jsonObject.getJSONObject(Constance.result).toString(),User.class);
                                    if(TextUtils.isEmpty(user.getNickname())){
                                        helper.initSdkChat("6f555230-9f0b-11e9-a2f8-cb23e96e098b",user.getMobile(),user.getUser_id()+"");
                                    }else {
                                        helper.initSdkChat("6f555230-9f0b-11e9-a2f8-cb23e96e098b",user.getNickname(),user.getUser_id()+"");
                                    }

                                }
                            }
                        });
                    }else {
                        if(TextUtils.isEmpty(user.getNickname())){
                            helper.initSdkChat("6f555230-9f0b-11e9-a2f8-cb23e96e098b",user.getMobile(),user.getUser_id()+"");
                        }else {
                            helper.initSdkChat("6f555230-9f0b-11e9-a2f8-cb23e96e098b",user.getNickname(),user.getUser_id()+"");
                        }
                    }


                    //设置card
                    //设置参数初始化
                }
//                    sendCall("尝试连接聊天服务..请连接?");
                break;
            case R.id.shopping_cart_Ll:
                if (!isToken())
                    getShoopingCart();
                break;
            case R.id.toDiyBtn:
                GoDiyProduct();
                break;
            case R.id.toCartBtn:
                if (!isToken())
                    GoShoppingCart();
                break;
            case R.id.share_iv:
            case R.id.iv_share:
                setShare();
//                IntentUtil.startActivity(this,ShareProductActivity.class,false);
                break;
            case R.id.go_photo_Ll:
                if (!isToken())
                    GoDiyProduct();
                break;
            case R.id.iv_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.toBuyBtn:
                if (!isToken())
                    toBuy();
                break;
        }
    }

    private void toBuy() {
        if (AppUtils.isEmpty(goods))
            return;
        if (goods.getGoods_spec_list().size() == 0) {
//            sendGoShoppingCart(goods.getGoods().getGoods_id() + "", "0", 1);
            mProperty="0";
            mCount = 1;
            buyNow();

        } else {
            selectParament();
        }
    }

    public  void buyNow() {
        misson(TYPE_BUY, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final JSONObject jsonObject=new JSONObject(response.body().string());
                LogUtils.logE("buynow",jsonObject.toString()+"");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                    MyToast.show(ProDetailActivity.this,jsonObject.getString(Constance.msg));
//                    }
//                });
//                if(jsonObject.getInt(Constance.status)==1){
//                    Intent intent=new Intent(ProDetailActivity.this, ConfirmOrderActivity.class);
//                    intent.putExtra(Constance.isbuy,true);
//                    intent.putExtra(Constance.item_id,mProperty+"");
//                    intent.putExtra(Constance.result,jsonObject.getJSONObject(Constance.result).toString());
//                    startActivity(intent);
//                }
                Intent intent=new Intent(ProDetailActivity.this, ConfirmOrderActivity.class);
                intent.putExtra(Constance.isbuy,true);
                intent.putExtra(Constance.item_id,mProperty+"");

                intent.putExtra(Constance.goods_id,goods.getGoods().getGoods_id()+"");
                intent.putExtra(Constance.count,mCount+"");
//                intent.putExtra(Constance.result,jsonObject.getJSONObject(Constance.result).toString());
                startActivity(intent);

            }
        });
    }

    @Override
    public void getData(int type, Callback callback) {
        if(type==TYPE_PRO_DETAIL){
            String token=MyShare.get(this).getString(Constance.token);
            String user_id=MyShare.get(this).getString(Constance.user_id);
//            if(TextUtils.isEmpty(token)||TextUtils.isEmpty(user_id)){
//                MyToast.show(this,"登录状态失效");
//                startActivity(new Intent(this, LoginActivity.class));
//                finish();
//                return;
//            }
            OkHttpUtils.getGoodsDetail(user_id,token,mProductId,callback);
        }else if(type==TYPE_BUY){
            String token=MyShare.get(this).getString(Constance.token);
            String user_id=MyShare.get(this).getString(Constance.user_id);
            if(TextUtils.isEmpty(token)||TextUtils.isEmpty(user_id)){
                MyToast.show(this,"登录状态失效");
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            OkHttpUtils.buyGoodsNow(user_id,token,goods.getGoods().getGoods_id(),mProperty,mCount,callback);
        }else if(type==TYPE_PRO_PC){
            OkHttpUtils.getGoodsThumb(mProductId,callback);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class ProductContainerAdapter extends FragmentPagerAdapter {


        public ProductContainerAdapter(FragmentManager fm) {
            super(fm);
            initFragment();
        }

        private void initFragment() {
            mFragments = new ArrayList<>();
            Bundle bundle = new Bundle();
            bundle.putString(Constance.product, mProductId + "");
//            if(getIntent().hasExtra(Constance.is_xiangou)){
//                ProDetailActivity.isXianGou=true;
//            }else {
//                ProDetailActivity.isXianGou=false;
//            }
//            bundle.putBoolean(Constance.is_xiangou,ProDetailActivity.isXianGou);
            introFragment = new IntroduceGoodsFragment();
            introFragment.setArguments(bundle);
            introFragment.setmListener(new IntroduceGoodsFragment.ScrollListener() {
                @Override
                public void onScrollToBottom(int currPosition) {
                    if (currPosition == 0) {
                        title_ll.setVisibility(View.GONE);
//                        sun_image_ll.setVisibility(View.VISIBLE);
                        product_ll.setVisibility(View.VISIBLE);
                        detail_ll.setVisibility(View.VISIBLE);
                        tuijian_ll.setVisibility(View.VISIBLE);
                    } else {
                        title_ll.setVisibility(View.VISIBLE);
                        product_ll.setVisibility(View.GONE);
//                        sun_image_ll.setVisibility(View.GONE);
                        detail_ll.setVisibility(View.GONE);
                        tuijian_ll.setVisibility(View.GONE);
                    }
                }
            });


            DetailGoodsFragmemt detailFragment = new DetailGoodsFragmemt();
            detailFragment.setArguments(bundle);

            ParameterFragment parameterFragment = new ParameterFragment();
            parameterFragment.setArguments(bundle);

            SunImageFragment sunImageFragment = new SunImageFragment();
            sunImageFragment.setArguments(bundle);


            mFragments.add(introFragment);
            mFragments.add(detailFragment);
            mFragments.add(parameterFragment);
            mFragments.add(sunImageFragment);

        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

    }

    /**
     * 购物车数量显示
     */
    public void getCartMun() {
        String userid=MyShare.get(this).getString(Constance.user_id);
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

                       ShopCartResult  result = new Gson().fromJson(jsonObject.getJSONObject(Constance.result).toString(), ShopCartResult.class);
                        List<CartList >cartList = result.getCartList();

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

    /**
     * 产品详情不同选择
     *
     * @param type
     */
    public void selectProductType(int type) {
        sun_image_tv.setTextColor(getResources().getColor(R.color.txt_black));
        tuijian_tv.setTextColor(getResources().getColor(R.color.txt_black));
        detail_tv.setTextColor(getResources().getColor(R.color.txt_black));
        product_tv.setTextColor(getResources().getColor(R.color.txt_black));
//        detail_tv.setBackgroundResource((R.drawable.bg_goods_detail_intro_corner_normal));
//        product_tv.setBackgroundResource((R.drawable.bg_goods_detail_intro_corner_left_normal));
//        tuijian_tv.setBackgroundResource(R.drawable.bg_goods_detail_intro_corner_normal);
//        sun_image_tv.setBackgroundResource(R.drawable.bg_goods_detail_intro_corner_right_normal);
        product_view.setVisibility(View.GONE);
        detail_view.setVisibility(View.GONE);
        tuijian_view.setVisibility(View.GONE);
        switch (type) {
            case R.id.product_ll:
//                product_view.setVisibility(View.VISIBLE);
//                product_tv.setBackgroundResource((R.drawable.bg_goods_detail_intro_corner_left));
                product_tv.setTextColor(getResources().getColor(R.color.theme_red));
                product_view.setVisibility(View.VISIBLE);
                container_vp.setCurrentItem(0, true);
                break;
            case R.id.detail_ll:
//                detail_view.setVisibility(View.VISIBLE);
//                detail_tv.setBackgroundResource((R.drawable.bg_goods_detail_intro_corner));
                detail_tv.setTextColor(getResources().getColor(R.color.theme_red));
                detail_view.setVisibility(View.VISIBLE);
                container_vp.setCurrentItem(1, true);
                break;
            case R.id.tuijian_ll:
                tuijian_tv.setTextColor(getResources().getColor(R.color.theme_red));
                tuijian_view.setVisibility(View.VISIBLE);
//                tuijian_tv.setBackgroundResource(R.drawable.bg_goods_detail_intro_corner);
                container_vp.setCurrentItem(2, true);
                break;
            case R.id.sun_image_ll:
//                sun_image_view.setVisibility(View.VISIBLE);
                sun_image_tv.setTextColor(getResources().getColor(R.color.theme_red));
                sun_image_view.setVisibility(View.VISIBLE);
//                sun_image_tv.setBackgroundResource(R.drawable.bg_goods_detail_intro_corner_right);
                container_vp.setCurrentItem(3, true);
                break;

        }

    }
    /**
     * 购物车
     */
    public void getShoopingCart() {
        IntentUtil.startActivity(this, ShoppingCartActivity.class, false);
    }
    /**
     * 马上配配看
     */
    public void GoDiyProduct() {
        if (AppUtils.isEmpty(goods)) {
            MyToast.show(this, "还没加载完毕，请稍后再试");
            return;
        }
        if(goods.getSpec_goods_price().size()==0){
            mIntent = new Intent(this, DiyActivity.class);
            mIntent.putExtra(Constance.product, new Gson().toJson(goods.getGoods(),Goods.class));
            mIntent.putExtra(Constance.property, mProperty);
            IssApplication.mSelectProducts.add(goods.getGoods());
            startActivity(mIntent);
        }else {
            introFragment.showParamSelectDialog(true);
        }

    }


    /**
     * 加入购物车
     */
    public void GoShoppingCart() {

        if (AppUtils.isEmpty(goods))
            return;
        if (goods.getSpec_goods_price().size() == 0) {
            sendGoShoppingCart(goods.getGoods().getGoods_id() + "", "0", 1);
        } else {
            selectParament();
        }
    }

    private void sendGoShoppingCart(String id, String property, int count) {
        String token= MyShare.get(this).getString(Constance.token);
        String user_id=MyShare.get(this).getString(Constance.user_id);
        OkHttpUtils.addToShopCart(id,property,count,user_id,token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    String resStr=response.body().string();
                    final JSONObject jsonObject=new JSONObject(resStr);
                    int status=jsonObject.getInt(Constance.status);
                    LogUtils.logE("CartAdd",resStr);
                    if(status==1){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyToast.show(ProDetailActivity.this,jsonObject.getString(Constance.msg));
                            }
                        });

                    }
            }
        });
    }

    /*
     * 选择参数
     */
    public void selectParament() {
        introFragment.showParamSelectDialog(false);
//        if (AppUtils.isEmpty(goods))
//            return;
//        mPopWindow = new SelectParamentPopWindow(this, goods);
//        mPopWindow.onShow(main_ll);
//        mPopWindow.setListener(new IParamentChooseListener() {
//            @Override
//            public void onParamentChanged(String text, Boolean isGoCart, String property, int mount, int price) {
//                if (!AppUtils.isEmpty(text)) {
//                    mProperty = property;
//                    mPrice = price;
//                    mPropertyValue = text;
//                }
//                if (isGoCart == true) {
////                    setShowDialog(true);
////                    setShowDialog("正在加入购物车中...");
////                    showLoading();
////                    sendGoShoppingCart(mProductId + "", property, mount);
//                }
//
//                EventBus.getDefault().post(Constance.PROPERTY);
//            }
//        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(String  type){
        getCartMun();
    }
    public void setShare() {

        if (AppUtils.isEmpty(goods)) {
            MyToast.show(this, "还没加载完毕，请稍后再试");
            return;
        }
        showShareDialog();
//        mIntent = new Intent(this, GoodsShareActivity.class);
//
//        mIntent.putExtra(Constance.product, new Gson().toJson(goods,ProductResult.class));
//        mIntent.putExtra(Constance.property, mProperty);
//        mIntent.putExtra(Constance.property_name,introFragment.mPropertyName);
//        startActivity(mIntent);
    }

    private void showShareDialog() {
        final Dialog dialog= UIUtils.showBottomInDialog(this,R.layout.dialog_share,UIUtils.dip2PX(500));
//        ImageView iv_img=dialog.findViewById(R.id.iv_img);
//        TextView tv_name=dialog.findViewById(R.id.tv_name);
//        TextView tv_price=dialog.findViewById(R.id.tv_price);
        final ImageView iv_code=dialog.findViewById(R.id.iv_code);
        ImageView iv_dismiss=dialog.findViewById(R.id.iv_dismiss);
        TextView tv_save=dialog.findViewById(R.id.tv_save);
        final String url=NetWorkConst.API_HOST+"api/goods/goodsSharePoster?id="+goods.getGoods().getGoods_id()+"&item_id="+goods.getSpec_goods_price().get(IntroduceGoodsFragment.currentAttrPosi).getItem_id()+"&prom_id="+goods.getGoods().getProm_id()+"&prom_type="+goods.getGoods().getProm_type();
        ImageLoader.getInstance().displayImage(url,iv_code);
//        tv_name.setText(goods.getGoods().getGoods_name());
//        tv_price.setText("¥"+goods.getGoods().getShop_price());
        iv_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bitmap bitmap=UIUtils.view2Bitmap(iv_code);

                MyToast.show(ProDetailActivity.this,"保存成功！");
                dialog.dismiss();
                ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        String mShareImagePath = ScannerUtils.saveImageToGallery02(ProDetailActivity.this, bitmap, ScannerUtils.ScannerType.RECEIVER);
                        ShareUtil.shareWxPic(ProDetailActivity.this,"",bitmap,true);

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });


            }
        });


    }
}
