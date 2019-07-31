package bc.yxdc.com.ui.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.CBPageAdapter;
import com.bigkoo.convenientbanner.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseFragment;
import bc.yxdc.com.bean.Ad;
import bc.yxdc.com.bean.FilterAttr;
import bc.yxdc.com.bean.FilterPrice;
import bc.yxdc.com.bean.GoodsBean;
import bc.yxdc.com.bean.GoodsCategoryBean;
import bc.yxdc.com.bean.High_quality_goods;
import bc.yxdc.com.bean.Hot_goods;
import bc.yxdc.com.bean.SaleTimeBean;
import bc.yxdc.com.bean.Time;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.inter.IScrollViewListener;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.CouponListHomeActivity;
import bc.yxdc.com.ui.activity.diy.ProgrammerActivity;
import bc.yxdc.com.ui.activity.goods.GoodsXsQgActivity;
import bc.yxdc.com.ui.activity.goods.CategoryGoodsListActivity;
import bc.yxdc.com.ui.activity.goods.GoodsSearchByImgActivity;
import bc.yxdc.com.ui.activity.goods.GoodsWeiHuoActivity;
import bc.yxdc.com.ui.activity.goods.GoodsXsTmActivity;
import bc.yxdc.com.ui.activity.goods.ProDetailActivity;
import bc.yxdc.com.ui.activity.goods.SelectGoodsActivity;
import bc.yxdc.com.ui.activity.user.MessageHomeActivity;
import bc.yxdc.com.ui.view.EndOfGridView;
import bc.yxdc.com.utils.ImageUtil;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import bc.yxdc.com.utils.UniversalUtil;
import bc.yxdc.com.utils.bitmap.Base64BitmapUtils;
import bc.yxdc.com.view.ObservableScrollView;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by gamekonglee on 2018/8/14.
 */

public class HomeMainFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    //首页数据
    private static final int TYPE_HOME_TYPE = 0;
    //限时抢购
    private static final int TYPE_PROMOTE = 2;
    //分类
    private static final int TYPE_HOME_CATEGARY = 1;
    //类型
    private static final int CATE_TYPE=4;
    //风格
    private static final int CATE_STYPE=5;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 43422;
    private static final int TYPE_HOME_RM = 23;
    //空间
    private  int CATE_SPACE=9;
    //新品
    private static final int TYPE_HOME_NEWS = 3;
    //现货
    private static final int TYPE_HOME_XIANHUO = 6;
    //尾货
    private static final int TYPE_HOME_WEIHUO=7;
    //猜你喜欢
    private static final int TYPE_HOME_LIKE = 8;
    //筛选
    private static final int TYPE_HOME_FILTER = 10;
    private static final int TAKE_PHOTO = 100;
    private static final int PERMISSION_MANAGE_DOCUMENTS = 3243;
    private static final int TYPE_SEARCH_IMG = 11;

    @BindView(R.id.ll_xianhuo)
    LinearLayout ll_xianhuo;
    @BindView(R.id.convenientBanner)
    ConvenientBanner mConvenientBanner;
    @BindView(R.id.tv_cd_day) TextView tv_cd_day;@BindView(R.id.tv_cd_hour) TextView tv_cd_hour;@BindView(R.id.tv_cd_min) TextView tv_cd_min;@BindView(R.id.tv_cd_seconds) TextView tv_cd_secodes;
    @BindView(R.id.ll_newgoods)LinearLayout ll_newgoods;
    @BindView(R.id.ll_factory_sell)
    LinearLayout ll_factory_sell;
    @BindView(R.id.rl_search)
    RelativeLayout rl_search;
    @BindView(R.id.tv_home_space)
    TextView tv_home_space;
//    @BindView(R.id.tv_home_style)
//    TextView tv_home_style;
    @BindView(R.id.tv_home_type)
    TextView tv_home_type;
    @BindView(R.id.gv_type)
    GridView gv_type;
    //尾货
    @BindView(R.id.tv_tail)
    TextView tv_tail;
    //新品
    @BindView(R.id.tv_news_list)
    TextView tv_news_list;
    //优惠券
    @BindView(R.id.tv_coupon)
    TextView tv_coupon;
    //设计
    @BindView(R.id.tv_desgin)
    TextView tv_design;
    @BindView(R.id.rl_xsqg)
    RelativeLayout rl_xsqg;
    @BindView(R.id.rl_temai)
    RelativeLayout rl_temai;
    @BindView(R.id.tv_newgoods_all)
    TextView tv_newgoods_all;
    @BindView(R.id.tv_xianhuo_all)
    TextView tv_xianhuo_all;
    @BindView(R.id.sc_home)ObservableScrollView sc_home;
    @BindView(R.id.tv_tail_all)TextView tv_tail_all;
    @BindView(R.id.ll_tail)LinearLayout ll_tail;
    @BindView(R.id.rl_home_top) View rl_home_top;
    @BindView(R.id.gv_cnxh)GridView gv_cnxh;
    @BindView(R.id.gv_rm)GridView gv_rm;
    @BindView(R.id.drawerlayout)DrawerLayout drawerlayout;
    @BindView(R.id.fl_filter)View fl_filter;
    @BindView(R.id.lv_filter_type)ListView lv_filter_type;
    @BindView(R.id.ll_weihuo)LinearLayout ll_weihuo;
    private List<GoodsBean >xianhuo_goods;
    private List<Ad> ad;
//    private List<Promotion_goods >promotion_goods;
    private List<High_quality_goods> high_quality_goods;
    private List<Hot_goods> hot_goods;
    private List<String> paths;
    private CountDownTimer countDownTimer;
    private QuickAdapter<GoodsBean> promotion_goodsQuickAdapter;
    private List<Time> promoteBeans;
    private QuickAdapter<GoodsCategoryBean> categoryBeanQuickAdapter;
    private List<GoodsCategoryBean> categoryBeans;
    private int currentTypePostion=1;
    private List<GoodsBean> newGoodsBeans;
    private boolean isNotFirst;
    private RelativeLayout.LayoutParams rlp;
    private QuickAdapter<GoodsBean> adapterCnxh;
    private List<GoodsBean> goodsCnxhBeans;
    private Unbinder unbinder;
    private QuickAdapter filterAdapter;
    private List<FilterAttr> filterAttrs;
    private QuickAdapter searchGoodsAdapter;
    private String base64;
    private List<GoodsBean> goodsSearchBeans;
    private QuickAdapter adapterRm;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main_home,null);
        unbinder = ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void initUI() {

        rlp = (RelativeLayout.LayoutParams) mConvenientBanner.getLayoutParams();
        int mScreenWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
        rlp.width = mScreenWidth;
        rlp.height = (int) (mScreenWidth * (239f / 375f));
        mConvenientBanner.setLayoutParams(rlp);
//        promotion_goods=new ArrayList<>();
        promoteBeans=new ArrayList<>();
        ad=new ArrayList<>();
        high_quality_goods=new ArrayList<>();
        xianhuo_goods=new ArrayList<>();
        hot_goods=new ArrayList<>();
        categoryBeans = new ArrayList<>();
        promotion_goodsQuickAdapter = new QuickAdapter<GoodsBean>(getActivity(), R.layout.item_promotion) {
            @Override
            protected void convert(BaseAdapterHelper helper, GoodsBean item) {
                helper.setText(R.id.tv_name,item.getGoods_name());
                helper.setText(R.id.tv_price,"¥"+item.getShop_price()+"");
                helper.setText(R.id.tv_sold,"已售"+item.getSales_sum()+"件");
                ImageView iv_img=helper.getView(R.id.iv_img);
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getGoods_id(),iv_img,IssApplication.getImageLoaderOption());
            }
        };

//        gv_news_goods.setAdapter(promotion_goodsQuickAdapter);
//        gv_news_goods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent=new Intent(getActivity(), ProDetailActivity.class);
//                intent.putExtra(Constance.product,newGoodsBeans.get(position).getGoods_id());
//                startActivity(intent);
//            }
//        });
        categoryBeanQuickAdapter = new QuickAdapter<GoodsCategoryBean>(getActivity(), R.layout.item_home_type) {
            @Override
            protected void convert(BaseAdapterHelper helper, GoodsCategoryBean item) {
                ImageView view=helper.getView(R.id.iv_img);
                TextView tv_more=helper.getView(R.id.tv_more);
                if(helper.getPosition()==8||helper.getPosition()==categoryBeans.size()-1){
                    view.setVisibility(View.GONE);
                    helper.setVisible(R.id.tv_more,true);
                    int width= (UIUtils.getScreenWidth(getActivity())-UIUtils.dip2PX(30))/3;
                    tv_more.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (width*70/111.5)));
                }else {
                    view.setVisibility(View.VISIBLE);
                    helper.setVisible(R.id.tv_more,false);
                    int width= (UIUtils.getScreenWidth(getActivity())-UIUtils.dip2PX(30))/3;
                    view.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (width*70/111.5)));
                    ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST+item.getIndex_image(),view,IssApplication.getImageLoaderOption());
                }


            }
        };
        adapterCnxh = new QuickAdapter<GoodsBean>(getActivity(), R.layout.item_goods) {
            @Override
            protected void convert(BaseAdapterHelper helper, GoodsBean item) {
                helper.setText(R.id.name_tv,item.getGoods_name());
                helper.setText(R.id.price_tv,"¥"+item.getShop_price()+"");
                helper.setText(R.id.tv_sold, "已售"+item.getSales_sum()+"件");
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getGoods_id(),((ImageView)helper.getView(R.id.imageView)), IssApplication.getImageLoaderOption());

            }
        };
        adapterRm = new QuickAdapter<Hot_goods>(getActivity(), R.layout.item_goods) {
            @Override
            protected void convert(BaseAdapterHelper helper, Hot_goods item) {
                helper.setText(R.id.name_tv,item.getGoods_name());
                helper.setText(R.id.price_tv,"¥"+item.getShop_price()+"");
                helper.setText(R.id.tv_sold, "已售"+item.getSales_sum()+"件");
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getGoods_id(),((ImageView)helper.getView(R.id.imageView)), IssApplication.getImageLoaderOption());

            }
        };
        gv_type.setAdapter(categoryBeanQuickAdapter);
        gv_cnxh.setAdapter(adapterCnxh);
        gv_rm.setAdapter(adapterRm);
        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(getActivity(),drawerlayout,R.string.open,R.string.close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerlayout.addDrawerListener(actionBarDrawerToggle);
        filterAdapter = new QuickAdapter<FilterAttr>(getActivity(), R.layout.item_filter) {
            @Override
            protected void convert(BaseAdapterHelper helper, final FilterAttr itemAttr) {
                helper.setText(R.id.tv_name,itemAttr.getName());
                QuickAdapter<FilterPrice> filterPriceQuickAdapter=new QuickAdapter<FilterPrice>(getActivity(),R.layout.item_filter_item) {
                    @Override
                    protected void convert(BaseAdapterHelper helper, FilterPrice item) {
                        helper.setText(R.id.tv_filter_item,item.getName());
                        if(itemAttr.getCurrent()==helper.getPosition()){
                            helper.setBackgroundRes(R.id.tv_filter_item,R.drawable.bg_corner_red_empty);
                            helper.setTextColor(R.id.tv_filter_item,getResources().getColor(R.color.theme_red));
                        }else {
                            helper.setBackgroundRes(R.id.tv_filter_item,R.drawable.bg_efefef_corner_15);
                            helper.setTextColor(R.id.tv_filter_item,getResources().getColor(R.color.tv_333333));
                        }
                    }
                };

                GridView gv_item=helper.getView(R.id.gv_item);
                gv_item.setAdapter(filterPriceQuickAdapter);
                filterPriceQuickAdapter.replaceAll(itemAttr.getItem());
                UIUtils.initGridViewHeight(gv_item);
                gv_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        itemAttr.setCurrent(position);
                        filterAdapter.notifyDataSetChanged();

                    }
                });
            }
        };
        lv_filter_type.setAdapter(filterAdapter);

        getHome();
        getXianHuo();
        getPromotion();
        getNewGoods();
        getTail();
        getCategory();
        getCnXh();
//        getRm();
        getFilter();
        rl_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SelectGoodsActivity.class));
            }
        });
        gv_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent = new Intent(getActivity(), CategoryGoodsListActivity.class);
                String categoriesId = categoryBeans.get(position).getId();
                if(position==categoryBeans.size()-1||position==8){
                    categoriesId=categoryBeans.get(position).getParent_id();
                }
                mIntent.putExtra(Constance.categories, categoriesId);
                JSONArray jsonArray=new JSONArray();
                for(int i=0;i<categoryBeans.size();i++){
                    jsonArray.add(new Gson().toJson(categoryBeans.get(i),GoodsCategoryBean.class));
                }
                mIntent.putExtra(Constance.categoryList,jsonArray);
                mIntent.putExtra(Constance.name,currentTypePostion==1?"分类":"空间");
                getActivity().startActivity(mIntent);
            }
        });
        gv_cnxh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MobclickAgent.onEvent(getActivity(),"product",""+goodsCnxhBeans.get(position).getGoods_name());
                Intent mIntent = new Intent(getActivity(), ProDetailActivity.class);
                int categoriesId = goodsCnxhBeans.get(position).getGoods_id();
                mIntent.putExtra(Constance.product, categoriesId);
                getActivity().startActivity(mIntent);
            }
        });
        gv_rm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MobclickAgent.onEvent(getActivity(),"product",""+hot_goods.get(position).getGoods_name());
                Intent mIntent = new Intent(getActivity(), ProDetailActivity.class);
                int categoriesId = hot_goods.get(position).getGoods_id();
                mIntent.putExtra(Constance.product, categoriesId);
                getActivity().startActivity(mIntent);
            }
        });
        sc_home.setScrollViewListener(new IScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView observableScrollView, int x, int y, int oldx, int oldy) {
                if(tv_news_list!=null){
                    int []location=new int[2];
                    tv_news_list.getLocationOnScreen(location);
//                LogUtils.logE("tv_news_list",""+location[1]);
                    if(location[1]<UIUtils.dip2PX(65)){
                        rl_home_top.setBackgroundResource(R.drawable.bg_frag_top);
                    }else {
                        rl_home_top.setBackgroundColor(getResources().getColor(R.color.white));
                        int al= (int) (Math.abs(location[1]-UIUtils.dip2PX(65)-rlp.height)*1.0f/(UIUtils.dip2PX(65)+rlp.height)*255f);
                        if(al>200)al=200;
//                        if(al<30)al=30;
//                    LogUtils.logE("alpha",""+al);
                        rl_home_top.getBackground().setAlpha(al);
                    }
                }
            }
        });
        tv_home_space.setOnClickListener(this);
//        tv_home_style.setOnClickListener(this);
        tv_home_type.setOnClickListener(this);
        tv_tail.setOnClickListener(this);
        tv_news_list.setOnClickListener(this);
        tv_coupon.setOnClickListener(this);
        tv_design.setOnClickListener(this);
        rl_xsqg.setOnClickListener(this);
        tv_newgoods_all.setOnClickListener(this);
        tv_xianhuo_all.setOnClickListener(this);
        tv_tail_all.setOnClickListener(this);
        rl_temai.setOnClickListener(this);



    }

    private void getRm() {
        misson(TYPE_HOME_RM, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    goodsCnxhBeans = new ArrayList<>();
                    JSONObject result=res.getJSONObject(Constance.result);
                    JSONArray jsonArray=result.getJSONArray(Constance.favourite_goods);
                    for (int i=0;i<jsonArray.length();i++){
                        goodsCnxhBeans.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(),GoodsBean.class));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(goodsCnxhBeans !=null&& goodsCnxhBeans.size()>0){
                                adapterCnxh.replaceAll(goodsCnxhBeans);
                                UIUtils.initGridViewHeight(gv_cnxh);
                                if(!isNotFirst){
                                    sc_home.scrollTo(0,0);
                                    isNotFirst=true;
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void getFilter() {
        misson(TYPE_HOME_FILTER, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                JSONObject jsonObject=new JSONObject(result);
                if(jsonObject!=null&&jsonObject.getJSONObject(Constance.result)!=null){
                    filterAttrs = new ArrayList<>();
                    JSONObject resultObj=jsonObject.getJSONObject(Constance.result);
                    JSONArray specArray=resultObj.getJSONArray(Constance.filter_spec);
                    JSONArray attrArray=resultObj.getJSONArray(Constance.filter_attr);
                    JSONArray priceArray=resultObj.getJSONArray(Constance.filter_price);
                    if(specArray!=null&&specArray.length()>0){
                        for(int i=0;i<specArray.length();i++){
                            filterAttrs.add(new Gson().fromJson(specArray.getJSONObject(i).toString(),FilterAttr.class));
                        }
                        for(int i=0;i<attrArray.length();i++){
                            filterAttrs.add(new Gson().fromJson(attrArray.getJSONObject(i).toString(),FilterAttr.class));
                        }
                        FilterAttr filterAttr=new FilterAttr();
                        filterAttr.setName("价格");
                        List<FilterPrice> filterPrices=new ArrayList<>();
                        for(int i=0;i<priceArray.length();i++){
                            filterPrices.add(new Gson().fromJson(priceArray.getJSONObject(i).toString(),FilterPrice.class));
                        }
                        filterAttr.setItem(filterPrices);
                        filterAttrs.add(filterAttr);
                    }
//                    GoodsListResult goodsListResult=new Gson().fromJson(jsonObject.getJSONObject(Constance.result).toString(),GoodsListResult.class);
//                    List<GoodsBean> temp=goodsListResult.getGoods_list();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            filterAdapter.notifyDataSetChanged();
                            filterAdapter.replaceAll(filterAttrs);
                            UIUtils.initListViewHeight(lv_filter_type);
                        }
                    });
                }
            }
        });
    }

    private void getCnXh() {
        misson(TYPE_HOME_LIKE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    goodsCnxhBeans = new ArrayList<>();
                    JSONObject result=res.getJSONObject(Constance.result);
                    JSONArray jsonArray=result.getJSONArray(Constance.favourite_goods);
                    for (int i=0;i<jsonArray.length();i++){
                        goodsCnxhBeans.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(),GoodsBean.class));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(goodsCnxhBeans !=null&& goodsCnxhBeans.size()>0){
                                adapterCnxh.replaceAll(goodsCnxhBeans);
                                UIUtils.initGridViewHeight(gv_cnxh);
                                if(!isNotFirst){
                                    sc_home.scrollTo(0,0);
                                    isNotFirst=true;
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void getHome() {
        misson(TYPE_HOME_TYPE, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
//
                LogUtils.logE("home",result);
                JSONObject jsonObject=new JSONObject(result);
                JSONObject resultObject=jsonObject.getJSONObject(Constance.result);
                JSONArray ads=resultObject.getJSONArray(Constance.ad);
                for(int i=0;i<ads.length();i++){
                    ad.add(new Gson().fromJson(ads.getJSONObject(i).toString(),Ad.class));
                }
                JSONArray pgds=resultObject.getJSONArray(Constance.promotion_goods);
//                for(int i=0;i<pgds.length();i++){
//                    promotion_goods.add(new Gson().fromJson(pgds.getJSONObject(i).toString(),Promotion_goods.class));
//                }
                JSONArray hqgs=resultObject.getJSONArray(Constance.high_quality_goods);
                for(int i=0;i<hqgs.length();i++){
                    high_quality_goods.add(new Gson().fromJson(hqgs.getJSONObject(i).toString(),High_quality_goods.class));
                }
//                JSONArray newsgs=resultObject.getJSONArray(Constance.new_goods);
//                for(int i=0;i<newsgs.length();i++){
//                    new_goods.add(new Gson().fromJson(newsgs.getJSONObject(i).toString(),New_goods.class));
//                }
                JSONArray hotsg=resultObject.getJSONArray(Constance.hot_goods);
                for(int i=0;i<hotsg.length();i++){
                    hot_goods.add(new Gson().fromJson(hotsg.getJSONObject(i).toString(),Hot_goods.class));
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getAd();
//                    setCountDown();
//                        getNews();
                        getFractory();
                        if(hot_goods !=null&& hot_goods.size()>0){
                            adapterRm.replaceAll(hot_goods);
                            UIUtils.initGridViewHeight(gv_rm);
                                sc_home.scrollTo(0,0);
                        }


                    }
                });

            }
        });
    }

    private void getXianHuo() {
        misson(TYPE_HOME_XIANHUO, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    JSONArray jsonArray=res.getJSONObject(Constance.result).getJSONArray(Constance.goods_list);
                    for(int i=0;i<jsonArray.length();i++){
                        xianhuo_goods.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(),GoodsBean.class));
                    }
                    if(getActivity()==null||getActivity().isFinishing()){
                        return;
                    }

                        getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getNews();
                        }
                    });

                }

            }
        });
    }

    private void getPromotion() {
        misson(TYPE_PROMOTE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                LogUtils.logE("promote",res.toString());
                final JSONObject promotesArray=res.getJSONObject(Constance.result);

                promoteBeans = new ArrayList<>();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(promotesArray!=null&&promotesArray.length()>0){
                            SaleTimeBean saleTimeBean=new Gson().fromJson(promotesArray.toString(),SaleTimeBean.class);
                            promoteBeans = saleTimeBean.getTime();
//                            for(int i=0;i<promotesArray.length();i++){
//                                promoteBeans.add(new Gson().fromJson(promotesArray.getJSONObject(i).toString(),PromoteBean.class));
//                            }
                            long currentMoment= System.currentTimeMillis()/1000L;
                            long endMonet=0;
//                            int currnet=0;
                            for(int i=0;i<promoteBeans.size();i++){
                            long start=promoteBeans.get(i).getStart_time();
                            long end=promoteBeans.get(i).getEnd_time();
                            if(currentMoment<start){
                                endMonet=start;
                                break;
                            }else if(currentMoment>=start&&currentMoment<end){
                                endMonet=end;
                                break;
                            }else {
                                continue;
                            }
                            }
//                            Time proGoods=promoteBeans.get(0);
//                            if(proGoods!=null){
                                Long timeStamp=endMonet;
                                Date date= null;
                                Date date2=null;
                                date = new Date(timeStamp*1000L);
                                date2 =new Date();
                                long timeDis=date.getTime()-date2.getTime();
                                countDownTimer = new CountDownTimer(timeDis,1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        millisUntilFinished=millisUntilFinished/1000L;
                                        int day= (int) (millisUntilFinished/60/60/24);
                                        int hour= (int) ((millisUntilFinished-day*24*60*60)/60/60);
                                        int min=(int)((millisUntilFinished-day*24*60*60-hour*60*60))/60;
                                        int secodes=(int)((millisUntilFinished-day*24*60*60-hour*60*60-min*60));
                                        if(tv_cd_day==null)return;
                                        tv_cd_day.setText((day<10?(""+day):(day+"")));
                                        tv_cd_hour.setText((hour<10?("0"+hour):(hour+"")));
                                        tv_cd_min.setText((min<10?("0"+min):(min+"")));
                                        tv_cd_secodes.setText((secodes<10?("0"+secodes):(secodes+"")));

                                    }
                                    @Override
                                    public void onFinish() {

                                    }
                                };
                                countDownTimer.start();
//                            }
//                            promotion_goodsQuickAdapter.replaceAll(promoteBeans);
                        }

                    }
                });

            }
        });
    }

    private void getNewGoods() {
        misson(TYPE_HOME_NEWS, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    newGoodsBeans = new ArrayList<>();
                    JSONArray jsonArray=res.getJSONArray(Constance.result);
                    for (int i=0;i<jsonArray.length();i++){
                        newGoodsBeans.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(),GoodsBean.class));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(newGoodsBeans!=null&&newGoodsBeans.size()>0){
                                ll_newgoods.removeAllViews();
                                int width=(UIUtils.getScreenWidth(getActivity())-UIUtils.dip2PX(35))/3;
                                int height=UIUtils.dip2PX(195-20);
                                for(int i=0;i<newGoodsBeans.size();i++){
                                    View view=View.inflate(getActivity(),R.layout.item_promotion,null);
                                    ImageView iv_img=view.findViewById(R.id.iv_img);
                                    TextView tv_name=view.findViewById(R.id.tv_name);
                                    TextView tv_price=view.findViewById(R.id.tv_price);
                                    TextView tv_sold=view.findViewById(R.id.tv_sold);
                                    tv_name.setText(newGoodsBeans.get(i).getGoods_name());
                                    tv_price.setText("¥"+newGoodsBeans.get(i).getShop_price());
                                    tv_sold.setText("已售"+newGoodsBeans.get(i).getSales_sum()+"件");
                                    ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+newGoodsBeans.get(i).getGoods_id(),iv_img,IssApplication.getImageLoaderOption());
                                    LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(width,height);
                                    layoutParams.setMargins(10,0,0,0);
                                    final int finalI = i;
                                    view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent=new Intent(getActivity(),ProDetailActivity.class);
                                            intent.putExtra(Constance.product,newGoodsBeans.get(finalI).getGoods_id());
                                            startActivity(intent);
                                        }
                                    });
                                    ll_newgoods.addView(view,layoutParams);
                                }
                            }

//                            if(newGoodsBeans !=null&& newGoodsBeans.size()>0){
//                                promotion_goodsQuickAdapter.replaceAll(newGoodsBeans);
//                                if(!isNotFirst){
//                                    sc_home.scrollTo(0,0);
//                                    isNotFirst=true;
//                                }
//                            }
                        }
                    });
                }

            }
        });
    }

    private void getTail() {
        misson(TYPE_HOME_WEIHUO, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    JSONArray jsonArray=res.getJSONArray(Constance.result);
                    final List<GoodsBean> goodsWeiHuoBeans = new ArrayList<>();
                    for(int i=0;i<jsonArray.length();i++){
                        goodsWeiHuoBeans.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(),GoodsBean.class));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(goodsWeiHuoBeans!=null&&goodsWeiHuoBeans.size()>0){
                                ll_weihuo.setVisibility(View.VISIBLE);
                                ll_tail.removeAllViews();
                                for(int i=0;i<goodsWeiHuoBeans.size();i++){
                                    View view=View.inflate(getActivity(),R.layout.item_news_goods,null);
                                    ImageView iv_img=view.findViewById(R.id.iv_img);
                                    TextView tv_name=view.findViewById(R.id.tv_name);
                                    TextView tv_price=view.findViewById(R.id.tv_price);
                                    TextView tv_sold=view.findViewById(R.id.tv_sold);
                                    tv_name.setText(goodsWeiHuoBeans.get(i).getGoods_name());
                                    tv_price.setText("¥"+goodsWeiHuoBeans.get(i).getShop_price());
                                    tv_sold.setText("已售"+goodsWeiHuoBeans.get(i).getSales_sum()+"件");
                                    ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+goodsWeiHuoBeans.get(i).getGoods_id(),iv_img,IssApplication.getImageLoaderOption());
                                    LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    layoutParams.setMargins(0,0,10,0);
                                    final int finalI = i;
                                    view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent=new Intent(getActivity(),ProDetailActivity.class);
                                            intent.putExtra(Constance.product,goodsWeiHuoBeans.get(finalI).getGoods_id());
                                            startActivity(intent);
                                        }
                                    });
                                    ll_tail.addView(view,layoutParams);
                                }
                            }else {
                                ll_weihuo.setVisibility(View.GONE);
                            }

                        }
                    });}
            }
        });
    }

    private void getCategory() {
        misson(TYPE_HOME_CATEGARY, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                categoryBeans=new ArrayList<>();
                if(res.getInt(Constance.status)==1){
                    JSONArray results=res.getJSONArray(Constance.result);
                    if(results!=null&&results.length()>0){
                        for(int i=0;i<results.length()&&i<9;i++){
                            categoryBeans.add(new Gson().fromJson(results.getJSONObject(i).toString(),GoodsCategoryBean.class));
                            if(categoryBeans.get(i).getName().contains("空间")){
                                CATE_SPACE= Integer.parseInt(categoryBeans.get(i).getId());
                            }
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                categoryBeanQuickAdapter.replaceAll(categoryBeans);
                                gv_type.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.getScreenWidth(getActivity())*260/375));
                                if(!isNotFirst){
                                    sc_home.scrollTo(0,0);
                                    isNotFirst=true;
                                }

                            }
                        });
                    }
                }
            }
        });
    }

    private void getFractory() {

    }

    private void getNews() {
        if(xianhuo_goods!=null&&xianhuo_goods.size()>0){
            ll_xianhuo.removeAllViews();
            for(int i=0;i<xianhuo_goods.size();i++){
                View view=View.inflate(getActivity(),R.layout.item_news_goods,null);
                ImageView iv_img=view.findViewById(R.id.iv_img);
                TextView tv_name=view.findViewById(R.id.tv_name);
                TextView tv_price=view.findViewById(R.id.tv_price);
                TextView tv_sold=view.findViewById(R.id.tv_sold);
                tv_name.setText(xianhuo_goods.get(i).getGoods_name());
                tv_price.setText("¥"+xianhuo_goods.get(i).getShop_price());
                tv_sold.setText("已售"+xianhuo_goods.get(i).getSales_sum()+"件");
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+xianhuo_goods.get(i).getGoods_id(),iv_img,IssApplication.getImageLoaderOption());
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,0,10,0);
                final int finalI = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(),ProDetailActivity.class);
                        intent.putExtra(Constance.product,xianhuo_goods.get(finalI).getGoods_id());
                        startActivity(intent);
                    }
                });
                ll_xianhuo.addView(view,layoutParams);
            }
        }
    }


    private void getAd() {
        paths = new ArrayList<>();
        for(int i=0;i<ad.size();i++){
            paths.add(ad.get(i).getAd_code());
        }
        mConvenientBanner.setPages(
                new CBViewHolderCreator<NetworkImageHolderView>() {
                    @Override
                    public NetworkImageHolderView createHolder() {
                        return new NetworkImageHolderView();
                    }
                }, paths);
        mConvenientBanner.setPageIndicator(new int[]{R.drawable.dot_unfocuse, R.drawable.dot_focuse});

    }

    @Override
    public void getData(int type, Callback callback) {
        switch (type){
            case TYPE_HOME_TYPE:
                OkHttpUtils.getHomePage(callback);
                break;
            case TYPE_PROMOTE:
                OkHttpUtils.getSaleTime(callback);
                break;
            case TYPE_HOME_CATEGARY:
                switch (currentTypePostion){
                    case 1:
                        OkHttpUtils.getGoodsCategory(0+"",callback);
                        break;
                    case 2:
                        OkHttpUtils.getGoodsCategory(CATE_SPACE+"",callback);
                        break;
//                    case 3:
//                        OkHttpUtils.getGoodsCategory(CATE_SPACE+"",callback);
//                        break;

                }
                break;
            case TYPE_HOME_NEWS:
                OkHttpUtils.getHomeNewGoods(1,callback);
                break;
            case TYPE_HOME_XIANHUO:
                OkHttpUtils.getGoodsList("","","","","",1,callback);
                break;
            case TYPE_HOME_WEIHUO:
                OkHttpUtils.getGoodsWeiHuo(callback);
                break;
            case TYPE_HOME_LIKE:
                OkHttpUtils.getLikeGoods(1,callback);
                break;
//            case TYPE_HOME_RM:
//                OkHttpUtils.getGoodsList();
//                break;
            case TYPE_HOME_FILTER:
                OkHttpUtils.getGoodsList("","","","","",1,callback);
                break;
            case TYPE_SEARCH_IMG:
                OkHttpUtils.searchSimliarImg(base64,callback);
                break;

        }


    }

    @Override
    protected void initData() {

    }
    @OnClick({R.id.rl_camera,R.id.rl_top_type,R.id.tv_ensure,R.id.tv_reset})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_home_space:
                currentTypePostion = 2;
                getCategory();
                changeTYPE();
                break;
            case R.id.tv_home_type:
                currentTypePostion=1;
                getCategory();
                changeTYPE();
                break;
//            case R.id.tv_home_style:
//                currentTypePostion=2;
//                getCategory();
//                changeTYPE();
//                break;
            case R.id.tv_tail:
            case R.id.tv_tail_all:
                startActivity(new Intent(getActivity(), GoodsWeiHuoActivity.class));
                break;
            case R.id.tv_news_list:
            case R.id.tv_newgoods_all:
                Intent intent=new Intent(getActivity(),SelectGoodsActivity.class);
                intent.putExtra(Constance.isNew,true);
                intent.putExtra(Constance.text,"新品预售");
                startActivity(intent);
                break;
            case R.id.tv_coupon:
                if(isToken()){
                    return;
                }
                startActivity(new Intent(getActivity(), CouponListHomeActivity.class));
                break;
            case R.id.tv_desgin:
                startActivity(new Intent(getActivity(), ProgrammerActivity.class));
                break;
            case R.id.rl_xsqg:
                startActivity(new Intent(getActivity(), GoodsXsQgActivity.class));
                break;
            case R.id.rl_temai:
                startActivity(new Intent(getActivity(), GoodsXsTmActivity.class));
                break;
            case R.id.tv_xianhuo_all:
                Intent intent2=new Intent(getActivity(),SelectGoodsActivity.class);
                intent2.putExtra(Constance.isNew,true);
                intent2.putExtra(Constance.isXianHuo,true);
                intent2.putExtra(Constance.text,"现货专区");
                startActivity(intent2);
                break;
            case R.id.rl_camera:
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    requestCameraPermission();
//                requestPermissions(new String[]{Manifest.permission.MANAGE_DOCUMENTS},PERMISSION_MANAGE_DOCUMENTS);
                }else {
                intent=new Intent(getActivity(), GoodsSearchByImgActivity.class);
                startActivityForResult(intent,TAKE_PHOTO);
                }
                break;
            case R.id.rl_top_type:
//                if(drawerlayout.isDrawerOpen(fl_filter)){
//                    drawerlayout.closeDrawer(fl_filter);
//                }else {
//                    drawerlayout.openDrawer(fl_filter);
//                }
                if(isToken()){
                    return;
                }
                startActivity(new Intent(getActivity(), MessageHomeActivity.class));
                break;
            case R.id.tv_reset:
                for (int i=0;i<filterAttrs.size();i++){
                    filterAttrs.get(i).setCurrent(0);
                }
                filterAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_ensure:
                drawerlayout.closeDrawers();
                intent=new Intent(getActivity(),SelectGoodsActivity.class);
                startActivity(intent);
        }
    }

    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    ||ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
                    ||ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }else {
                Intent intent=new Intent(getActivity(), GoodsSearchByImgActivity.class);
                startActivityForResult(intent,TAKE_PHOTO);
//                showToast("权限已申请");
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_MANAGE_DOCUMENTS){
            Intent intent=new Intent(getActivity(), GoodsSearchByImgActivity.class);
            startActivityForResult(intent,TAKE_PHOTO);
        }else if(requestCode==MY_PERMISSIONS_REQUEST_CAMERA){
            Intent intent=new Intent(getActivity(), GoodsSearchByImgActivity.class);
            startActivityForResult(intent,TAKE_PHOTO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TAKE_PHOTO&&resultCode==200){
            String path=data.getStringExtra(Constance.path);
//            LogUtils.logE("path",path);
//            Intent intent=new Intent(getActivity(), GoodsSearchResultActivity.class);
//            intent.putExtra(Constance.path,path);
//            startActivity(intent);

            final Dialog dialog=UIUtils.showBottomInDialog(getActivity(),R.layout.dialog_search_img,UIUtils.getScreenHeight(getActivity())-200);
            EndOfGridView gv_goods=dialog.findViewById(R.id.gv_goods);
            ImageView iv_close=dialog.findViewById(R.id.iv_close);
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            searchGoodsAdapter = new QuickAdapter<GoodsBean>(getActivity(), R.layout.item_goods) {
                @Override
                protected void convert(BaseAdapterHelper helper, GoodsBean item) {
                    helper.setText(R.id.name_tv,item.getGoods_name());
                    helper.setText(R.id.price_tv,item.getShop_price()+"");
                    helper.setText(R.id.tv_sold, "已售"+item.getSales_sum()+"件");
                    ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getGoods_id(),((ImageView)helper.getView(R.id.imageView)), IssApplication.getImageLoaderOption());
                }
            };
            gv_goods.setAdapter(searchGoodsAdapter);
            ImageLoader.getInstance().loadImage(  path, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    base64 = Base64BitmapUtils.bitmapToBase64(bitmap);
                    misson(TYPE_SEARCH_IMG, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final JSONObject jsonObject=new JSONObject(response.body().string());
                            LogUtils.logE("goodsResult",jsonObject.toString());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(jsonObject==null||jsonObject.getJSONArray(Constance.result)==null||jsonObject.getJSONArray(Constance.result).length()==0)
                                    {
                                        MyToast.show(getActivity(),"没有搜索到相关结果");
                                        return;
                                    } else {
                                        goodsSearchBeans = new ArrayList<>();
                                        goodsSearchBeans =new Gson().fromJson(jsonObject.getJSONArray(Constance.result).toString(),new TypeToken<List<GoodsBean>>(){}.getType());
                                        searchGoodsAdapter.replaceAll(goodsSearchBeans);
                                    }
                                }
                            });

                        }
                    });
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
            gv_goods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MobclickAgent.onEvent(getActivity(),"product",""+goodsSearchBeans.get(position).getGoods_name());
                    Intent intent=new Intent(getActivity(),ProDetailActivity.class);
                    intent.putExtra(Constance.product,goodsSearchBeans.get(position).getGoods_id());
                    startActivity(intent);
                }
            });

        }
    }

    private void changeTYPE() {
        tv_home_type.setTextColor(getActivity().getResources().getColor(R.color.tv_333333));
//        tv_home_style.setTextColor(getActivity().getResources().getColor(R.color.tv_333333));
        tv_home_space.setTextColor(getActivity().getResources().getColor(R.color.tv_333333));
        tv_home_space.setCompoundDrawables(null,null,null,null);
//        tv_home_style.setCompoundDrawables(null,null,null,null);
        tv_home_type.setCompoundDrawables(null,null,null,null);

        Drawable drawable= getResources().getDrawable(R.drawable.bg_line);
/// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

        switch (currentTypePostion){
            case 1:
                tv_home_type.setCompoundDrawables(null,null,null,drawable);
                tv_home_type.setTextColor(getActivity().getResources().getColor(R.color.theme_red));
                break;
            case 2:
//                tv_home_style.setCompoundDrawables(null,null,null,drawable);
//                tv_home_style.setTextColor(getActivity().getResources().getColor(R.color.theme_red));
                tv_home_space.setCompoundDrawables(null,null,null,drawable);
                tv_home_space.setTextColor(getActivity().getResources().getColor(R.color.theme_red));
                break;
            case 3:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    class NetworkImageHolderView implements CBPageAdapter.Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, String data) {

            imageView.setImageBitmap(ImageUtil.getBitmapById(getActivity(), R.drawable.bg_default));
            ImageLoader.getInstance().displayImage(data, imageView,((IssApplication)getActivity().getApplication()).getImageLoaderOption());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转到知道的网址
//                    String link = ad.get(position).getAd_link();
//                    if (!AppUtils.isEmpty(link)) {
//                        Intent mIntent = new Intent();
//                        mIntent.setAction(Intent.ACTION_VIEW);
//                        mIntent.setData(Uri.parse(link));
//                        startActivity(mIntent);
//                    }

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        setPause();
    }

    public void setResume() {
        // 开始自动翻页
        mConvenientBanner.startTurning(UniversalUtil.randomA2B(3000, 5000));
    }

    public void setPause() {
        // 停止翻页
        mConvenientBanner.stopTurning();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //    String result="{\n" +
//                        "    \"status\":1,\n" +
//                        "    \"msg\":\"获取成功\",\n" +
//                        "    \"result\":{\n" +
//                        "        \"promotion_goods\":[\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"119\",//商品id\n" +
//                        "                \"goods_name\":\"小米旗舰店正品手机平板通用迷你充电宝 移动电源10000毫安大容量\",//商品名称\n" +
//                        "                \"price\":\"59.00\",//促销价格\n" +
//                        "                \"end_time\":\"1464624000\"//促销截止时间戳\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"1\",\n" +
//                        "                \"goods_name\":\"Apple iPhone 6s Plus 16G 玫瑰金 移动联通电信4G手机\",\n" +
//                        "                \"price\":\"423.00\",\n" +
//                        "                \"end_time\":\"1472918400\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"141\",\n" +
//                        "                \"goods_name\":\"三星 Galaxy A9高配版 (A9100) 精灵黑 全网通4G手机 双卡双待\",\n" +
//                        "                \"price\":\"465.00\",\n" +
//                        "                \"end_time\":\"1472918400\"\n" +
//                        "            }\n" +
//                        "        ],\n" +
//                        "        \"high_quality_goods\":[\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"148\",//商品id\n" +
//                        "                \"goods_name\":\"1111111\",//商品名称\n" +
//                        "                \"shop_price\":\"3.00\"//商品价格\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"1\",\n" +
//                        "                \"goods_name\":\"Apple iPhone 6s Plus 16G 玫瑰金 移动联通电信4G手机\",\n" +
//                        "                \"shop_price\":\"200.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"155\",\n" +
//                        "                \"goods_name\":\"22222\",\n" +
//                        "                \"shop_price\":\"0.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"154\",\n" +
//                        "                \"goods_name\":\"11111\",\n" +
//                        "                \"shop_price\":\"0.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"153\",\n" +
//                        "                \"goods_name\":\"测试\",\n" +
//                        "                \"shop_price\":\"0.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"152\",\n" +
//                        "                \"goods_name\":\"11111111111\",\n" +
//                        "                \"shop_price\":\"0.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"145\",\n" +
//                        "                \"goods_name\":\"\",\n" +
//                        "                \"shop_price\":\"0.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"143\",\n" +
//                        "                \"goods_name\":\"haier海尔 BC-93TMPF 93升单门冰箱\",\n" +
//                        "                \"shop_price\":\"699.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"142\",\n" +
//                        "                \"goods_name\":\"海尔（Haier）BCD-251WDGW 251升 无霜两门冰箱（白色）\",\n" +
//                        "                \"shop_price\":\"2699.00\"\n" +
//                        "            }\n" +
//                        "        ],\n" +
//                        "        \"new_goods\":[\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"155\",\n" +
//                        "                \"goods_name\":\"22222\",\n" +
//                        "                \"shop_price\":\"0.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"154\",\n" +
//                        "                \"goods_name\":\"11111\",\n" +
//                        "                \"shop_price\":\"0.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"153\",\n" +
//                        "                \"goods_name\":\"测试\",\n" +
//                        "                \"shop_price\":\"0.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"152\",\n" +
//                        "                \"goods_name\":\"11111111111\",\n" +
//                        "                \"shop_price\":\"0.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"145\",\n" +
//                        "                \"goods_name\":\"\",\n" +
//                        "                \"shop_price\":\"0.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"104\",\n" +
//                        "                \"goods_name\":\"小米手机5,十余项黑科技，很轻狠快\",\n" +
//                        "                \"shop_price\":\"1999.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"103\",\n" +
//                        "                \"goods_name\":\"珂兰钻石 18K金90分效果群镶钻石戒指 倾城 需定制\",\n" +
//                        "                \"shop_price\":\"1999.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"77\",\n" +
//                        "                \"goods_name\":\"红衣主角2015冬装新款走秀款大气简约黄色羊毛呢大衣.\",\n" +
//                        "                \"shop_price\":\"500.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"76\",\n" +
//                        "                \"goods_name\":\"韩文2015冬装新款女装红色双排扣配腰带毛呢大衣.\",\n" +
//                        "                \"shop_price\":\"289.00\"\n" +
//                        "            }\n" +
//                        "        ],\n" +
//                        "        \"hot_goods\":[\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"149\",\n" +
//                        "                \"goods_name\":\"111111111111\",\n" +
//                        "                \"shop_price\":\"11.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"1\",\n" +
//                        "                \"goods_name\":\"Apple iPhone 6s Plus 16G 玫瑰金 移动联通电信4G手机\",\n" +
//                        "                \"shop_price\":\"200.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"155\",\n" +
//                        "                \"goods_name\":\"22222\",\n" +
//                        "                \"shop_price\":\"0.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"154\",\n" +
//                        "                \"goods_name\":\"11111\",\n" +
//                        "                \"shop_price\":\"0.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"153\",\n" +
//                        "                \"goods_name\":\"测试\",\n" +
//                        "                \"shop_price\":\"0.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"152\",\n" +
//                        "                \"goods_name\":\"11111111111\",\n" +
//                        "                \"shop_price\":\"0.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"143\",\n" +
//                        "                \"goods_name\":\"haier海尔 BC-93TMPF 93升单门冰箱\",\n" +
//                        "                \"shop_price\":\"699.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"142\",\n" +
//                        "                \"goods_name\":\"海尔（Haier）BCD-251WDGW 251升 无霜两门冰箱（白色）\",\n" +
//                        "                \"shop_price\":\"2699.00\"\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"goods_id\":\"141\",\n" +
//                        "                \"goods_name\":\"三星 Galaxy A9高配版 (A9100) 精灵黑 全网通4G手机 双卡双待\",\n" +
//                        "                \"shop_price\":\"3499.00\"\n" +
//                        "            }\n" +
//                        "        ],\n" +
//                        "        \"ad\":[\n" +
//                        "            {\n" +
//                        "                \"ad_link\":\"http://dev.tpshop.cn/index.php?m=Home&c=Goods&a=goodsInfo&id=1\",//广告位链接\n" +
//                        "                \"ad_name\":\"首页banner轮播1\",//广告位名称\n" +
//                        "                \"ad_code\":\"http://dev.tpshop.cn/Public/upload/ad/2016/07-25/5795b12d78f4f.jpg\"//广告图\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"ad_link\":\"http://dev.tpshop.cn/index.php?m=Home&c=Goods&a=goodsInfo&id=1\",\n" +
//                        "                \"ad_name\":\"首页banner轮播2\",\n" +
//                        "                \"ad_code\":\"http://dev.tpshop.cn/Public/upload/ad/2016/07-25/5795ab303cb65.jpg\"\n" +
//                        "            }\n" +
//                        "        ]\n" +
//                        "    }\n" +
//                        "}";
}
