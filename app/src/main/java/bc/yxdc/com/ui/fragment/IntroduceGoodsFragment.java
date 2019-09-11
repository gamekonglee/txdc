package bc.yxdc.com.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.CBPageAdapter;
import com.bigkoo.convenientbanner.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseFragment;
import bc.yxdc.com.bean.Activity;
import bc.yxdc.com.bean.DbGoodsBean;
import bc.yxdc.com.bean.Goods;
import bc.yxdc.com.bean.Goods_attr_list;
import bc.yxdc.com.bean.Goods_spec_list;
import bc.yxdc.com.bean.ProductResult;
import bc.yxdc.com.bean.Spec_goods_price;
import bc.yxdc.com.bean.Spec_list;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.db.DaoMaster;
import bc.yxdc.com.db.DaoSession;
import bc.yxdc.com.db.DbGoodsBeanDao;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.inter.IScrollViewListener;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.diy.DiyActivity;
import bc.yxdc.com.ui.activity.goods.ProDetailActivity;
import bc.yxdc.com.ui.activity.user.LoginActivity;
import bc.yxdc.com.ui.view.AutoLinefeedLayout;
import bc.yxdc.com.ui.view.MyProgressDialog;
import bc.yxdc.com.utils.AppUtils;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import bc.yxdc.com.view.ObservableScrollView;
import bc.yxdc.com.view.countdownview.CountdownView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/9/14.
 */

public class  IntroduceGoodsFragment extends BaseFragment implements View.OnClickListener, CountdownView.OnCountdownEndListener {
    private static final int TYPE_PRO_DETAIL = 0;
    private static final int TYPE_ADD_TO_CART = 1;
    private static final int TYPE_PRO_CONTENT = 3;
    private static final int TYPE_COLLECT = 4;
    private static final int TYPE_GOODS_ACTIVITY = 5;
    public String productId;
    private RelativeLayout collect_rl, rl_2;
    private ImageView top_iv;
    private ConvenientBanner mConvenientBanner;
    private List<String> paths = new ArrayList<>();
    private WebView mWebView;
    private TextView unPriceTv, proNameTv, proPriceTv;
    private TextView mParamentTv;
    private ImageView collect_iv;
    private int mIsLike = 0;
    private RelativeLayout rl_rl;
    private String mProperty;
    private CountdownView cv_countdownView;
    private LinearLayout time_ll;
//    private TextView time_title_tv;
    private Boolean isStartTimeBuy=false;
    private LinearLayout ll_comment;
    private LinearLayout rl_comment_cotent;
    private LinearLayout ll_dajia;
    private TextView tv_dajia;
    private View v_dajia;
    private LinearLayout ll_24rexiao;
    private TextView tv_24rexiao;
    private View v_24rexiao;
    private LinearLayout ll_tuijian;
    private int page;
    private int type;
    private TextView tv_collect;
    private TextView tv_more;
//    private TextView tv_time_price;
//    private TextView tv_time_oldprice;
    private ListView lv_paramter;
    private LinearLayout ll_paramter;
    private Goods mProductionObject;
    private String mCurrentPrice;
    private String mOldPrice;
    private TextView tv_paied;
    private TextView tv_store_count;
    private TextView tv_jiaohuoqi;
    private bocang.json.JSONArray spec_goods_array;
    private List<Spec_goods_price> spec_goods_prices;
    public static int currentAttrPosi=0;
    private int currentCount=1;
    private bocang.json.JSONArray gallery;
    private List<Goods_attr_list> goods_attr_lists;
    private RelativeLayout rl_param;
    private TextView tv_sale;
    private CountDownTimer countDownTimer;
    private boolean isFull;
    private Unbinder unbinder;
    @BindView(R.id.tv_qg_price)TextView tv_qg_price;
    @BindView(R.id.tv_has_got)TextView tv_has_got;
    @BindView(R.id.tv_qg_cd)TextView tv_qg_cd;
    @BindView(R.id.tv_qg_progress)TextView tv_qg_progress;
    @BindView(R.id.pb_qg)ProgressBar pb_qg;
    @BindView(R.id.rl_qg)RelativeLayout rl_qg;
    private List<Goods_spec_list> goods_spec_lists;
    private int urlPos;
    private int currentUrlPos;
    public String mPropertyName;
    private TextView tv_sale_title;
    private Activity activity;
    private int numColumn;
    private int warn_number;
    private TextView tv_zhibao;
    private ProgressDialog progressDialog;
    private MyProgressDialog myProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fm_product_introduce, null);
        unbinder = ButterKnife.bind(this,view);
        return view;

    }


    @Override
    public void initUI() {
        collect_rl = (RelativeLayout) getView().findViewById(R.id.collect_rl);
        rl_2 = (RelativeLayout) getView().findViewById(R.id.rl_2);
        collect_rl.setOnClickListener(this);
        rl_2.setOnClickListener(this);
        mConvenientBanner = (ConvenientBanner) getActivity().findViewById(R.id.convenientBanner);
        rl_rl = (RelativeLayout) getView().findViewById(R.id.rl_rl);
        mParamentTv = (TextView) getView().findViewById(R.id.type_tv);
        unPriceTv = (TextView) getView().findViewById(R.id.unPriceTv);
        proPriceTv = (TextView) getView().findViewById(R.id.proPriceTv);
        top_iv = (ImageView) getView().findViewById(R.id.top_iv);
//        final PullUpToLoadMore ptlm = (PullUpToLoadMore) getActivity().findViewById(R.id.ptlm);
//        ptlm.currPosition=0;
//        ptlm.setmListener(new PullUpToLoadMore.ScrollListener() {
//            @Override
//            public void onScrollToBottom(int currPosition) {
//                if (currPosition == 0) {
//                    if(mListener!=null) mListener.onScrollToBottom(0);
//                    top_iv.setVisibility(View.GONE);
//                } else {
//                    if(mListener!=null) mListener.onScrollToBottom(1);
//                    top_iv.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//        top_iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ptlm.scrollToTop();
//            }
//        });


        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        int mScreenWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
        mConvenientBanner = (ConvenientBanner) getActivity().findViewById(R.id.convenientBanner);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mConvenientBanner.getLayoutParams();
        rlp.width = mScreenWidth;
        rlp.height = mScreenWidth - 20;
        mConvenientBanner.setLayoutParams(rlp);
        ll_comment = getView().findViewById(R.id.ll_comment);
        rl_comment_cotent = getView().findViewById(R.id.rl_comment_cotent);
        ll_dajia = getView().findViewById(R.id.ll_dajia);
        tv_dajia = getView().findViewById(R.id.tv_dajia);
        v_dajia = getView().findViewById(R.id.v_dajia);
        ll_24rexiao = getView().findViewById(R.id.ll_24rexiao);
        tv_24rexiao = getView().findViewById(R.id.tv_24rexiao);
        v_24rexiao = getView().findViewById(R.id.v_24rexiao);
        ll_tuijian = getView().findViewById(R.id.ll_tuijian);
        tv_collect = getView().findViewById(R.id.tv_collect);
        tv_more = getView().findViewById(R.id.tv_more);
        lv_paramter = getView().findViewById(R.id.lv_paramter);
        ll_paramter = getView().findViewById(R.id.ll_paramter);
        ll_24rexiao.setOnClickListener(this);
        ll_dajia.setOnClickListener(this);
        mWebView = (WebView) getActivity().findViewById(R.id.webView);
        mWebView.setWebChromeClient(new WebChromeClient());
//        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.getSettings().setBlockNetworkImage(false);

        unPriceTv = (TextView) getActivity().findViewById(R.id.unPriceTv);
        proNameTv = (TextView) getActivity().findViewById(R.id.proNameTv);
        proPriceTv = (TextView) getActivity().findViewById(R.id.proPriceTv);
        mParamentTv = (TextView) getActivity().findViewById(R.id.type_tv);
        cv_countdownView = (CountdownView) getActivity().findViewById(R.id.cv_countdownView);
        collect_iv = (ImageView) getActivity().findViewById(R.id.collect_iv);
        time_ll =  getActivity().findViewById(R.id.time_ll);
        rl_rl = (RelativeLayout) getActivity().findViewById(R.id.rl_rl);
        tv_paied = getView().findViewById(R.id.tv_paied);
        tv_store_count = getView().findViewById(R.id.tv_store_count);
        tv_jiaohuoqi = getView().findViewById(R.id.tv_jiaohuoqi);
        rl_param = getView().findViewById(R.id.rl_param);
        tv_sale = getView().findViewById(R.id.tv_sale);
        tv_sale_title = getView().findViewById(R.id.tv_sale_title);
        ObservableScrollView msv=getView().findViewById(R.id.msv);
        tv_zhibao = getView().findViewById(R.id.tv_zhibao);
        isFull = true;
        msv.setScrollViewListener(new IScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView observableScrollView, int x, int y, int oldx, int oldy) {
            if(mConvenientBanner!=null){
                int []location=new int[2];
                mConvenientBanner.getLocationOnScreen(location);
//                LogUtils.logE("banner_location",""+location[1]);
                if(location[1]>0-UIUtils.dip2PX(100)){
                    ((ProDetailActivity)getActivity()).ll_pro_top.setVisibility(View.GONE);
                    ((ProDetailActivity)getActivity()).rl_pro_top.setVisibility(View.VISIBLE);
                    if(!isFull){
//                        ((ProDetailActivity)getActivity()).fullScreen(getActivity());
                        isFull=true;
                    }
                }else {
                    ((ProDetailActivity)getActivity()).ll_pro_top.setVisibility(View.VISIBLE);
                    ((ProDetailActivity)getActivity()).rl_pro_top.setVisibility(View.GONE);
                    if(isFull){
//                        ((ProDetailActivity)getActivity()).setColor(getActivity(), Color.WHITE);
                        isFull=false;
                    }
                    int al= (int) (Math.abs(location[1]+UIUtils.dip2PX(100))*1.0f/300f*255f);
                    if(al>255)al=255;
                    float fal=al/255f;
//                    LogUtils.logE("alpha",""+al);
                    ((ProDetailActivity)getActivity()).ll_pro_top.setAlpha(fal);
                }

            }
            }
        });
        rl_param.setOnClickListener(this);
        cv_countdownView.setOnCountdownEndListener(this);

        getDetail();
        getWebContent();
    }

    private void getWebContent() {
        misson(TYPE_PRO_CONTENT, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final bocang.json.JSONObject res=new bocang.json.JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    if(getActivity()!=null)getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bocang.json.JSONObject result=res.getJSONObject(Constance.result);
                            goods_attr_lists = new ArrayList<>();
                            bocang.json.JSONArray array=result.getJSONArray(Constance.goods_attr_list);
                            String content=result.getString(Constance.goods_content);
                            for(int i=0;i<array.length();i++){
                                goods_attr_lists.add(new Gson().fromJson(array.getJSONObject(i).toString(),Goods_attr_list.class));
                            }

                            EventBus.getDefault().postSticky(content);
                            setWebview(content);

                        }
                    });
                }

            }
        });
    }

    private void getActivityInfo() {
        misson(TYPE_GOODS_ACTIVITY, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                bocang.json.JSONObject res=new bocang.json.JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    final bocang.json.JSONObject result=res.getJSONObject(Constance.result);
                    if(getActivity()==null||getActivity().isFinishing()) {
                    return;
                    }
                        getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int prom_type=0;
                            if(result!=null){
                                prom_type=result.getInt(Constance.prom_type);
                            }
                            activity = new Gson().fromJson(result.toString(),Activity.class);
                            setActivityInfo(prom_type);
                        }
                    });
                }
            }
        });
    }

    private void getDetail() {
        misson(TYPE_PRO_DETAIL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res=response.body().string();
                LogUtils.logE("prodetail",res);
                final bocang.json.JSONObject jsonObject=new bocang.json.JSONObject(res);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bocang.json.JSONObject result=jsonObject.getJSONObject(Constance.result);
                        if(jsonObject.getInt(Constance.status)!=1){
                            UIUtils.showSingleWordDialog(getActivity(), jsonObject.getString(Constance.msg), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getActivity().finish();
                                }
                            });
                            return;
                        }
                        gallery = result.getJSONArray(Constance.gallery);
                        ProDetailActivity.goods=new Gson().fromJson(jsonObject.getJSONObject(Constance.result).toString(),ProductResult.class);
                        spec_goods_array = jsonObject.getJSONObject(Constance.result).getJSONArray(Constance.spec_goods_price);
                        goods_spec_lists = ProDetailActivity.goods.getGoods_spec_list();
                        parseRes(jsonObject.getJSONObject(Constance.result).getJSONObject(Constance.goods));

                    }
                });
            }
        });
    }

    private void setWebview(String htmlValue) {
        String html = htmlValue;
        String replace = html.replace("&lt;", "<");
        String replace1 = replace.replace("&gt;", ">");
        String replace2 = replace1.replace("&amp;", "&");
        html = replace2.replace("&quot;", "\"");

        html = html.replace("/public/upload/",  NetWorkConst.API_HOST_2+"/public/upload/");
        html = "<meta name=\"viewport\" content=\"width=device-width\">" + html;
        html=html.replace("<p>","");
        html=html.replace("</p>","");
        LogUtils.logE("webData",html);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                handler.proceed();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mWebView.getSettings()
                            .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }
            }
        });
//        mWebView.setWebViewClient(new WebViewClient(){
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
//
//            }});
        // android 5.0以上默认不支持Mixed Content
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mWebView.getSettings()
//                    .setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
//        }
//        mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        mWebView.loadData(html, "text/html;charset=utf-8", null);
    }

    private void parseRes(bocang.json.JSONObject res) {
//        ProductResult produResult=new Gson().fromJson(res.toString(),ProductResult.class);
        spec_goods_prices = new ArrayList<>();
        mProductionObject = new Gson().fromJson(res.toString(),Goods.class);
        mCurrentPrice = mProductionObject.getShop_price();
        if(IssApplication.isShowDiscount){
        mCurrentPrice=mProductionObject.getCost_price();
        }
        proPriceTv.setText("￥" + mCurrentPrice);
        Activity activityInfo=ProDetailActivity.goods.getActivity();
        if(spec_goods_array!=null&&spec_goods_array.length()>0){
            for(int i=0;i<spec_goods_array.length();i++){
                spec_goods_prices.add(new Gson().fromJson(spec_goods_array.getJSONObject(i).toString(),Spec_goods_price.class));
            }
            mProperty=spec_goods_prices.get(0).getItem_id()+"";
            getActivityInfo();
        }else {
        if(activityInfo!=null){
            setActivityInfo(activityInfo.getProm_type());
        }else {
            setActivityInfo(0);
        }
        }
//        if(((ProDetailActivity)getActivity()).isQiangGou){
//
//        }else {
//
//        }
        String sku=mProductionObject.getSku();
        if(TextUtils.isEmpty(sku)){
            sku="现货";
        }
        tv_jiaohuoqi.setText("交货期："+sku);
        mIsLike = mProductionObject.getIs_collect();
        final String productName = mProductionObject.getGoods_name();
        tv_paied.setText(mProductionObject.getSales_sum()+"人已付款");
        int height= UIUtils.dip2PX(60)+UIUtils.initListViewHeight(lv_paramter);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        ll_paramter.setLayoutParams(layoutParams);
        mOldPrice = mProductionObject.getShop_price();
        paths=new ArrayList<>();
        if (!AppUtils.isEmpty(gallery)) {
            for (int i = 0; i < gallery.length(); i++) {
                paths.add(gallery.getJSONObject(i).getString(Constance.image_url));
            }
        }

//        getWebView(value);
        mConvenientBanner.setPages(
                new CBViewHolderCreator<NetworkImageHolderView>() {
                    @Override
                    public NetworkImageHolderView createHolder() {
                        return new NetworkImageHolderView();
                    }
                }, paths);
        mConvenientBanner.setPageIndicator(new int[]{R.drawable.dot_unfocuse, R.drawable.dot_focuse});
        proNameTv.setText(productName);
        DecimalFormat df=new DecimalFormat("###.00");
        selectCollect();
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getActivity(), "my-db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DbGoodsBeanDao dbGoodsBeanDao=daoSession.getDbGoodsBeanDao();
        DbGoodsBean dbGoodsBean=new DbGoodsBean();
        dbGoodsBean.setId(mProductionObject.getGoods_id());
        dbGoodsBean.setG_id(Long.valueOf(mProductionObject.getGoods_id()));
        dbGoodsBean.setName(mProductionObject.getGoods_name());
        if(spec_goods_prices!=null&&spec_goods_prices.size()>currentAttrPosi){
        dbGoodsBean.setAttr(spec_goods_prices.get(currentAttrPosi).getItem_id()+"");
        }
        dbGoodsBean.setPrice(mProductionObject.getMarket_price());
        dbGoodsBean.setCurrent_price(mProductionObject.getShop_price());
        dbGoodsBean.setCreate_time(System.currentTimeMillis()+"");
        dbGoodsBean.setOriginal_img(mProductionObject.getOriginal_img());
//        dbGoodsBean.setOriginal_img(productObject.getJSONArray(Constance.photos).getJSONObject(0).getString(Constance.large));
        unPriceTv.setText("￥" + df.format(Double.parseDouble(mOldPrice)));
//        unPriceTv.setText("￥" + 00001);
        unPriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//        time_ll.setVisibility(View.GONE);
        cv_countdownView.setVisibility(View.GONE);
        tv_store_count.setText("工厂库存（"+mProductionObject.getStore_count()+"）");
        if(goods_attr_lists!=null&&goods_attr_lists.size()>0){
            for(int i=0;i<goods_attr_lists.size();i++){
                if(goods_attr_lists.get(i).getAttr_name().contains("售后")){
                    tv_zhibao.setText(goods_attr_lists.get(i).getAttr_value());
                    break;
                }
            }
        }
        try {
            if(isStartTimeBuy){
                proPriceTv.setText("￥" + mCurrentPrice);
                proPriceTv.setVisibility(View.GONE);
                unPriceTv.setVisibility(View.GONE);
//                dbGoodsBean.setCurrent_price(mCurrentPrice);
//                dbGoodsBean.setPrice(mOldPrice +"");
                ((RelativeLayout.LayoutParams)proNameTv.getLayoutParams()).setMargins(UIUtils.dip2PX(15),UIUtils.dip2PX(20),0,0);
                proNameTv.setTextSize(15);
            }else{
            }


        } catch (Exception e) {
            e.printStackTrace();
//            time_ll.setVisibility(View.GONE);
        }finally {
            dbGoodsBeanDao.insertOrReplace(dbGoodsBean);
        }
    }

    private void setActivityInfo(int type) {
        if(proPriceTv==null)return;
        proPriceTv.setVisibility(View.VISIBLE);
        rl_qg.setVisibility(View.GONE);
        tv_paied.setVisibility(View.VISIBLE);
//        rl_2.setVisibility(View.GONE);
        if(type==3){
            setSaleInfo();
        }else if(type==1){
            proPriceTv.setVisibility(View.GONE);
            tv_paied.setVisibility(View.GONE);
            rl_qg.setVisibility(View.VISIBLE);
//            Activity activity=ProDetailActivity.goods.getActivity();
            tv_qg_price.setText(""+activity.getProm_price());
            tv_has_got.setText("已抢"+activity.getVirtual_num()+"件");
            int total=activity.getProm_store_count();
            if(total==0){
                total=1;
            }
            int progress=activity.getVirtual_num()/total;
            pb_qg.setProgress(progress);
            tv_qg_progress.setText("已抢"+progress+"%");
            setCountDown();
        }else {
            time_ll.setVisibility(View.GONE);
        }
    }

    private void setCountDown() {
//        Activity activity=ProDetailActivity.goods.getActivity();
        long end_time=activity.getProm_end_time();
        Date date= null;
        Date date2=null;
        date = new Date(end_time*1000L);
        date2 =new Date();
        long timeDis=date.getTime()-date2.getTime();
        if(countDownTimer!=null)
        {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(timeDis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisUntilFinished=millisUntilFinished/1000L;
                int day= (int) (millisUntilFinished/60/60/24);
                int hour= (int) ((millisUntilFinished-day*24*60*60)/60/60);
                int min=(int)((millisUntilFinished-day*24*60*60-hour*60*60))/60;
                int secodes=(int)((millisUntilFinished-day*24*60*60-hour*60*60-min*60));
                if(tv_qg_cd!=null)
                tv_qg_cd.setText("距优惠结束："+(hour<10?("0"+hour):(hour+""))+"："+(min<10?("0"+min):(min+""))+"："+(secodes<10?("0"+secodes):(secodes+"")));
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    private void setSaleInfo() {
//        Activity activity=ProDetailActivity.goods.getActivity();
        long end_time=activity.getProm_end_time();
        time_ll.setVisibility(View.VISIBLE);
        Date date= null;
        Date date2=null;
        date = new Date(end_time*1000L);
        date2 =new Date();
        long timeDis=date.getTime()-date2.getTime();
        tv_sale_title.setText(activity.getData().get(0).getTitle()+" "+activity.getData().get(0).getContent());
        proPriceTv.setText("¥"+activity.getProm_price());
        if(countDownTimer!=null)
        {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(timeDis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisUntilFinished=millisUntilFinished/1000L;
                int day= (int) (millisUntilFinished/60/60/24);
                int hour= (int) ((millisUntilFinished-day*24*60*60)/60/60);
                int min=(int)((millisUntilFinished-day*24*60*60-hour*60*60))/60;
                int secodes=(int)((millisUntilFinished-day*24*60*60-hour*60*60-min*60));
                if(day>0){
                    tv_sale.setText("距优惠结束："+day+" 天 "+(hour<10?("0"+hour):(hour+""))+":"+(min<10?("0"+min):(min+""))+":"+(secodes<10?("0"+secodes):(secodes+"")));
                }else {
                    tv_sale.setText("距优惠结束："+(hour<10?("0"+hour):(hour+""))+":"+(min<10?("0"+min):(min+""))+":"+(secodes<10?("0"+secodes):(secodes+"")));
                }

            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }



    @Override
    public void getData(int type, Callback callback) {
        if(type==TYPE_PRO_DETAIL){
            String user_id= MyShare.get(getActivity()).getString(Constance.user_id);
            String token=MyShare.get(getActivity()).getString(Constance.token);
//            if(TextUtils.isEmpty(user_id)||TextUtils.isEmpty(token)){
//                MyToast.show(getActivity(),"请先登录");
//                startActivity(new Intent(getActivity(), LoginActivity.class));
//                return;
//            }
            OkHttpUtils.getGoodsDetail(user_id,token,Integer.parseInt(productId),callback);
        }else if(type==TYPE_ADD_TO_CART){
            String user_id= MyShare.get(getActivity()).getString(Constance.user_id);
            String token=MyShare.get(getActivity()).getString(Constance.token);
            if(TextUtils.isEmpty(user_id)||TextUtils.isEmpty(token)){
                MyToast.show(getActivity(),"请先登录");
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }
            if(spec_goods_prices!=null&&spec_goods_prices.size()>0){
            OkHttpUtils.addToShopCart(mProductionObject.getGoods_id()+"",spec_goods_prices.get(currentAttrPosi).getItem_id()+"",currentCount,user_id,token,callback);
            }else {
                OkHttpUtils.addToShopCart(mProductionObject.getGoods_id()+"","",currentCount,user_id,token,callback);
            }
        }else if(type==TYPE_PRO_CONTENT){
            OkHttpUtils.getGoodsContent(Integer.parseInt(productId),callback);
        }else if(type==TYPE_COLLECT){
            String user_id= MyShare.get(getActivity()).getString(Constance.user_id);
            String token=MyShare.get(getActivity()).getString(Constance.token);
            if(TextUtils.isEmpty(user_id)||TextUtils.isEmpty(token)){
                MyToast.show(getActivity(),"请先登录");
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }
            OkHttpUtils.collectGoods(mProductionObject.getGoods_id(),user_id,token,callback);
        }else if(type==TYPE_GOODS_ACTIVITY){
            OkHttpUtils.getGoodsActivity(mProductionObject.getGoods_id(),mProperty,callback);
        }
    }

    @Override
    protected void initData() {
        Bundle bundle=getArguments();
        if(bundle!=null){
            productId =  bundle.getString(Constance.product);
        }

    }

    private ScrollListener mListener;

    public void setmListener(ScrollListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_2:
                showParamSelectDialog(false);
                break;
            case R.id.rl_param:
                showParams();
                break;
            case R.id.collect_rl:
                setCollect();
                break;
        }
    }

    private void setCollect() {
        misson(TYPE_COLLECT, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final bocang.json.JSONObject res=new bocang.json.JSONObject(response.body().string());
                LogUtils.logE("collect",res.toString());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.show(getActivity(),""+res.getString(Constance.msg));
                            if(res.getInt(Constance.status)==1) {
                                getDetail();
                            }
                        }
                    });

            }
        });
    }

    private void showParams() {
        if(goods_attr_lists==null||goods_attr_lists.size()==0){
            MyToast.show(getActivity(),"数据加载中，请稍等");
            return;
        }
//        String[] propertyArray = new String[goods_spec_lists.get().getSpec_list().size()];
        Dialog dialog=UIUtils.showBottomInDialog(getActivity(),R.layout.dialog_param);
        ListView lv_param=dialog.findViewById(R.id.lv_paramter);
        QuickAdapter<Goods_attr_list> adapter=new QuickAdapter<Goods_attr_list>(getActivity(),R.layout.item_params) {
            @Override
            protected void convert(BaseAdapterHelper helper, Goods_attr_list item) {
                helper.setText(R.id.tv_key,item.getAttr_name());
                helper.setText(R.id.tv_value,item.getAttr_value()+"");
            }
        };
        lv_param.setAdapter(adapter);
        adapter.replaceAll(goods_attr_lists);
    }

    public  void showParamSelectDialog(boolean isDiy) {
        if(spec_goods_prices==null||spec_goods_prices.size()==0){
            return;
        }
        final Dialog dialog=UIUtils.showBottomInDialog(getActivity(),R.layout.dialog_attr_select);
        LogUtils.logE("has_goods","show");
        final ImageView iv_img=dialog.findViewById(R.id.iv_img);
        TextView tv_name=dialog.findViewById(R.id.tv_name);
        TextView tv_warn_number=dialog.findViewById(R.id.tv_warn_number);
        final TextView tv_price=dialog.findViewById(R.id.tv_price);
        final TextView tv_store_count_dialog=dialog.findViewById(R.id.tv_store_count);
        ImageView iv_dismiss=dialog.findViewById(R.id.iv_dismiss);
//        final TextView tv_attr=dialog.findViewById(R.id.tv_attr_name);
//        GridView gv_attr=dialog.findViewById(R.id.gv_attr);
        TextView tv_reduce=dialog.findViewById(R.id.tv_reduce);
        final TextView tv_add=dialog.findViewById(R.id.tv_add);
        final EditText tv_num=dialog.findViewById(R.id.et_num);
        Button btn_add_cart=dialog.findViewById(R.id.btn_add_cart);
        Button btn_buy_now=dialog.findViewById(R.id.btn_buy_now);
        final TextView tv_attr_tips=dialog.findViewById(R.id.tv_attr_tips);
        LinearLayout ll_buy=dialog.findViewById(R.id.ll_buy);
        Button btn_diy=dialog.findViewById(R.id.btn_diy);
        LinearLayout ll_skulist=dialog.findViewById(R.id.ll_skulist);
//
//        if (propertyList == null || propertyList.size() <= 0) {
//            System.out.println("propertyList==null");
//            TextView tv_auto = new TextView(getActivity());
//            tv_auto.setText(goodsDetail.getProductInfo().getName());
////                        tv_auto.setBackground(getDrawable(R.drawable.bg_corner_8c8e91));
////                        tv_auto.setTextColor(getResources().getColor(R.color.tv_232326));
//            tv_auto.setGravity(Gravity.CENTER);
//            tv_auto.setEnabled(true);
//            tv_auto.setPadding(UIUtils.dip2PX(8), UIUtils.dip2PX(8), UIUtils.dip2PX(8), UIUtils.dip2PX(8));
//            tv_auto.setBackground(getResources().getDrawable(R.drawable.bg_corner_jd_red));
//            tv_auto.setTextColor(getResources().getColor(R.color.jd_red));
//            tv_auto.setEnabled(false);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            layoutParams.setMargins(UIUtils.dip2PX(5), UIUtils.dip2PX(5), UIUtils.dip2PX(5), 0);
//            layoutParams.gravity = Gravity.CENTER_VERTICAL;
//            tv_auto.setLayoutParams(layoutParams);
////                    tv_auto.setLetterSpacing(5);
//            ll_skulist.addView(tv_auto);
//            return;
//        }
        ll_skulist.removeAllViews();
        final List<AutoLinefeedLayout> autoLinefeedLayouts = new ArrayList<>();
        final List<List<TextView>> tvListList = new ArrayList<>();
        List<LinearLayout> linearLayouts = new ArrayList<>();
        for (int i = 0; i < goods_spec_lists.size(); i++) {
            final AutoLinefeedLayout autoLinefeedLayout = new AutoLinefeedLayout(getActivity());
            final LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayouts.add(linearLayout);
            final TextView textView = new TextView(getActivity());
            textView.setText(goods_spec_lists.get(i).getSpec_name());
            textView.setTextColor(getResources().getColor(R.color.tv_333333));
            LinearLayout.LayoutParams tvlayoutps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvlayoutps.setMargins(UIUtils.dip2PX(10), UIUtils.dip2PX(15), 0, UIUtils.dip2PX(15));
            textView.setLayoutParams(tvlayoutps);

//                final GridView gridView=new GridView(getActivity());

//                gridView.setNumColumns(2);
            linearLayouts.get(i).removeAllViews();
            linearLayouts.get(i).addView(textView);
            final int finalI = i;
            final int currentPosition = 0;
            final List<TextView> tvList = new ArrayList<>();
            for (int g = 0; g < goods_spec_lists.get(finalI).getSpec_list().size(); g++) {
                final LinearLayout linearLayoutForTv = new LinearLayout(getActivity());
                final TextView tv_auto = new TextView(getActivity());
                tv_auto.setText(goods_spec_lists.get(finalI).getSpec_list().get(g).getItem());
//                        tv_auto.setBackground(getDrawable(R.drawable.bg_corner_8c8e91));
//                        tv_auto.setTextColor(getResources().getColor(R.color.tv_232326));
                tv_auto.setGravity(Gravity.CENTER);
                tv_auto.setSingleLine(true);
                tv_auto.setEnabled(true);
                tv_auto.setPadding(UIUtils.dip2PX(8), UIUtils.dip2PX(8), UIUtils.dip2PX(8), UIUtils.dip2PX(8));
                final int finalY = g;
                tv_auto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvListList.get(finalI).get(finalY).setEnabled(false);
                        for (int x = 0; x < tvListList.get(finalI).size(); x++) {
                            if (x != finalY) {
                                tvListList.get(finalI).get(x).setEnabled(true);
                            }
                            if (!tvListList.get(finalI).get(x).isEnabled()) {
//                            currentPosition=y;
//                            break;
                                tvListList.get(finalI).get(x).setBackground(getResources().getDrawable(R.drawable.bg_attr_selected));
                                tvListList.get(finalI).get(x).setTextColor(getResources().getColor(R.color.theme_red));
                            } else {
                                tvListList.get(finalI).get(x).setBackground(getResources().getDrawable(R.drawable.bg_attr_default));
                                tvListList.get(finalI).get(x).setTextColor(getResources().getColor(R.color.tv_333333));
                            }
                        }
                        List<Spec_list>spec_lists =goods_spec_lists.get(finalI).getSpec_list();
                        goods_spec_lists.get(finalI).currentP=finalY;
                        String url="";
                        url=spec_lists.get(finalY).getSrc();
                        if(url!=null&&!TextUtils.isEmpty(url)){
//                            ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST+url,iv_img);
                            ImageLoader.getInstance().loadImage(NetWorkConst.API_HOST + url, new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String s, View view) {
//                                    progressDialog = ProgressDialog.show(getActivity(),"","加载中");
                                    myProgressDialog = new MyProgressDialog(getActivity());
                                    myProgressDialog.show();

                                }

                                @Override
                                public void onLoadingFailed(String s, View view, FailReason failReason) {
//                                    progressDialog.dismiss();
                                    myProgressDialog.dismiss();
                                }

                                @Override
                                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                                    progressDialog.dismiss();
                                    myProgressDialog.dismiss();
                                    iv_img.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onLoadingCancelled(String s, View view) {
                                }
                            });
                        }
                        String str="";
                        for(int j=0;j<goods_spec_lists.size();j++) {
                            List<Spec_list> temp=goods_spec_lists.get(j).getSpec_list();
                            for (int i = 0; i < temp.size(); i++) {
                                if(goods_spec_lists.get(j).currentP==i){
                                    str += temp.get(i).getItem();
                                }
                            }
                        }
                        tv_attr_tips.setText(str);
                        mParamentTv.setText(str);
                        String[] strKeys=new String[goods_spec_lists.size()];
                        for(int i=0;i<goods_spec_lists.size();i++){
                            List<Spec_list> temp=goods_spec_lists.get(i).getSpec_list();
                            for(int j=0;j<temp.size();j++){
                                if(j==goods_spec_lists.get(i).currentP){
                                    strKeys[i]=temp.get(j).getItem_id()+"";
                                }
                            }
                        }

                        boolean isNotContain=true;
                        for(int j=0;j<spec_goods_prices.size();j++){
                            for(int i=0;i<strKeys.length;i++) {
                                if (spec_goods_prices.get(j).getKey().contains(strKeys[i])) {
                                    isNotContain=false;
                                }else {
                                    isNotContain=true;
                                    break;
                                }
                            }
                            if(!isNotContain){
                                currentAttrPosi=j;
                                break;
                            }
                        }
                        String price=spec_goods_prices.get(currentAttrPosi).getPrice();
                        if(IssApplication.isShowDiscount){
                            price=spec_goods_prices.get(currentAttrPosi).getCost_price();
                        }

                        proPriceTv.setText("¥"+price);
                        tv_price.setText("¥"+price);
                        warn_number = spec_goods_prices.get(currentAttrPosi).getWarn_number();
                        String unit=spec_goods_prices.get(currentAttrPosi).getUnit();
                        if (TextUtils.isEmpty(unit)||unit.equals("null")) {
                            unit="个";
                        }
                        tv_num.setText(warn_number+"");
                        tv_warn_number.setText("("+ warn_number +unit+"起订)");
                        mProperty=spec_goods_prices.get(currentAttrPosi).getItem_id()+"";
                        mPropertyName = spec_goods_prices.get(currentAttrPosi).getKey_name();
                        getActivityInfo();

                        tv_store_count.setText("工厂库存（"+spec_goods_prices.get(currentAttrPosi).getStore_count()+"）");
                        tv_store_count_dialog.setText("库存："+spec_goods_prices.get(currentAttrPosi).getStore_count());
//                        String src=goods_spec_lists.get(finalI).getSpec_list().get(finalY).getSrc();


//                        adapter.replaceAll(spec_lists);
                        }
//                        ApiClient.productDetail(pid, uid[0], new GklCallBack() {
//                            @Override
//                            public void response(Object response, int id) {
//                                if (id == 0) {
//                                    BaseBean baseBean = (BaseBean) response;
//                                    if (baseBean.getCode().equals("100000")) {
////                                                                           Toast.makeText(getActivity(), baseBean.getMessage(), Toast.LENGTH_SHORT).show();
//                                        btn_dialog_add_to_sc.setText("该商品已下架");
//                                        btn_dialog_add_to_sc.setBackgroundColor(getResources().getColor(R.color.btn_febe14));
//                                        isUnderC = true;
//                                    }
//                                } else {
//                                    isUnderC = false;
//                                    btn_dialog_add_to_sc.setText("加入购物车");
//                                    btn_dialog_add_to_sc.setBackgroundColor(getResources().getColor(R.color.jd_red));
//
//                                    goodsDetail = (GoodsDetail) response;
//                                    imgList = goodsDetail.getProductImageList();
//                                    productInfo = goodsDetail.getProductInfo();
//                                    refreshUI();
//                                    tv_dialog_price.setText("¥" + goodsDetail.getProductInfo().getShopPrice());
//                                    tv_dialog_no.setText("商品编号：" + goodsDetail.getProductInfo().getPid());
//                                    AppContext.getImageLoader().displayImage(urlBean.getStore() + imgList.get(0).getStoreId() + "/product/show/thumb350_350/" + imgList.get(0).getShowImg(), iv_dialog_goods);
//                                }
//                            }
//                        });
//                    }
                });

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(UIUtils.dip2PX(5), UIUtils.dip2PX(5), 0, 0);
                tv_auto.setLayoutParams(layoutParams);
//                if (propertyArray[finalI].equals(goods_spec_lists.get(finalI).getSpec_list().get(finalG).getItem())) {
                if(finalY==0){
//                            currentPosition=y;
//                            break;
                    tv_auto.setBackground(getResources().getDrawable(R.drawable.bg_attr_selected));
                    tv_auto.setTextColor(getResources().getColor(R.color.theme_red));
                    tv_auto.setEnabled(false);
                } else {
                    tv_auto.setBackground(getResources().getDrawable(R.drawable.bg_attr_default));
                    tv_auto.setTextColor(getResources().getColor(R.color.tv_333333));
                    tv_auto.setEnabled(true);
                }
                linearLayoutForTv.addView(tv_auto);
                linearLayoutForTv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                autoLinefeedLayout.addView(linearLayoutForTv);
                tvList.add(tv_auto);
            }
            autoLinefeedLayouts.add(autoLinefeedLayout);
            linearLayouts.get(finalI).addView(autoLinefeedLayouts.get(finalI));
            int total_width = 0;
            int tv_heigt = 0;
            tvListList.add(tvList);
            for (int b = 0; b < tvListList.get(finalI).size(); b++) {
                TextView view = tvListList.get(finalI).get(b);
                total_width += view.getPaint().measureText(view.getText().toString());
                view.measure(0, 0);
                tv_heigt = view.getMeasuredHeight();
                total_width += UIUtils.dip2PX(5);
            }
//                    System.out.println("total_width:"+total_width);
//                    System.out.println("widthpx:"+getResources().getDisplayMetrics().widthPixels);
//                    int row=total_width/getResources().getDisplayMetrics().widthPixels+1;
//                    int column=gridView.getAdapter().getCount()/row;
//                    gridView.setNumColumns(column);
//                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, row*tv_heigt+(row-1)*UIUtils.dip2PX(5));
//                    params.setMargins(UIUtils.dip2PX(10),0,UIUtils.dip2PX(15),0);
//                    gridView.setLayoutParams(params);
//                    gridView.setVerticalSpacing(UIUtils.dip2PX(5));

//                    linearLayout.addView(gridView);
//                    LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    layoutParams.setMargins(0,5,0,0);
//                    ll_skulist.addView(linearLayout);
            LinearLayout.LayoutParams llps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll_skulist.addView(linearLayouts.get(finalI), llps);
//                    gridViewList.add(gridView);

        }
//        ListView lv_attr=dialog.findViewById(R.id.lv_attr);
        if(isDiy){
            ll_buy.setVisibility(View.GONE);
            btn_diy.setVisibility(View.VISIBLE);
        }else {
            ll_buy.setVisibility(View.VISIBLE);
            btn_diy.setVisibility(View.GONE);
        }
        btn_diy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), DiyActivity.class);
                List<Spec_list> spec_lists=goods_spec_lists.get(urlPos).getSpec_list();
                ProDetailActivity.goods.getGoods().c_property=spec_lists.get(goods_spec_lists.get(urlPos).currentP).getItem_id()+"";
                ProDetailActivity.goods.getGoods().c_url=spec_lists.get(goods_spec_lists.get(urlPos).currentP).getSrc();
                mIntent.putExtra(Constance.product, new Gson().toJson(ProDetailActivity.goods.getGoods(),Goods.class));
                mIntent.putExtra(Constance.property, mProperty);
                IssApplication.mSelectProducts.add(ProDetailActivity.goods.getGoods());
                startActivity(mIntent);
                dialog.dismiss();
            }
        });
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
        ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+ProDetailActivity.goods.getGoods().getGoods_id(),iv_img);
        final InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tv_num.getWindowToken(),0);
        tv_name.setText(mProductionObject.getGoods_name());
        urlPos = -1;
        for(int i=0;i<goods_spec_lists.size();i++){
            Goods_spec_list temp=goods_spec_lists.get(i);
            if(temp!=null){
                List<Spec_list> spec_lists=temp.getSpec_list();
                for(int j=0;j<spec_lists.size();j++){
                    String url=spec_lists.get(j).getSrc();
                    if(url!=null&&!TextUtils.isEmpty(url)&&!url.equals("null")){
                        urlPos =i;
                        break;
                    }
                }
                if(urlPos!=-1){
                    break;
                }
            }
        }
        String[] strKeys=new String[goods_spec_lists.size()];
        for(int i=0;i<goods_spec_lists.size();i++){
            List<Spec_list> temp=goods_spec_lists.get(i).getSpec_list();
            for(int j=0;j<temp.size();j++){
                if(j==goods_spec_lists.get(i).currentP){
                    strKeys[i]=temp.get(j).getItem_id()+"";
                }
            }
        }

        boolean isNotContain=true;
        for(int j=0;j<spec_goods_prices.size();j++){
            for(int i=0;i<strKeys.length;i++) {
                if (spec_goods_prices.get(j).getKey().contains(strKeys[i])) {
                    isNotContain=false;
                }else {
                    isNotContain=true;
                    break;
                }
            }
            if(!isNotContain){
                currentAttrPosi=j;
                break;
            }
        }
        String currentPrice=ProDetailActivity.goods.getGoods().getShop_price();
        if(IssApplication.isShowDiscount){
            currentPrice=ProDetailActivity.goods.getGoods().getCost_price();
        }
        for(int i=0;i<spec_goods_prices.size();i++){
            String price=spec_goods_prices.get(i).getPrice();
            if(IssApplication.isShowDiscount){
                price=spec_goods_prices.get(i).getCost_price();
            }
            if(price.equals(currentPrice)){
                currentAttrPosi=i;
                break;
            }
        }
        for(int i=0;i<goods_spec_lists.size();i++){
            for(int j=0;j<goods_spec_lists.get(i).getSpec_list().size();j++){
                if(spec_goods_prices.get(currentAttrPosi).getKey().contains(goods_spec_lists.get(i).getSpec_list().get(j).getItem_id()+"")){
                    tvListList.get(i).get(j).performClick();
                }
            }
        }


        tv_price.setText("¥"+currentPrice);
        tv_store_count_dialog.setText("库存："+spec_goods_prices.get(currentAttrPosi).getStore_count());
        String unit=spec_goods_prices.get(currentAttrPosi).getUnit();
        warn_number = spec_goods_prices.get(currentAttrPosi).getWarn_number();
        if (TextUtils.isEmpty(unit)||unit.equals("null")) {
            unit="个";
        }
        tv_warn_number.setText("("+warn_number+unit+"起订)");
        tv_num.setText(warn_number+"");
        iv_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        QuickAdapter<Goods_spec_list> goods_spec_listQuickAdapter=new QuickAdapter<Goods_spec_list>(getActivity(),R.layout.item_attr_lv) {
            @Override
            protected void convert(final BaseAdapterHelper helper1, final Goods_spec_list itemList) {
                final GridView gv_attr=helper1.getView(R.id.gv_attr);
                helper1.setText(R.id.tv_attr_name,itemList.getSpec_name());
                String item=itemList.getSpec_list().get(0).getItem();
                    TextView tv_temp = new TextView(getActivity());
                    tv_temp.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
                    tv_temp.setText(item);
                    tv_temp.setLines(1);
                    tv_temp.setGravity(Gravity.CENTER);
                    tv_temp.setPadding(UIUtils.dip2PX(10), UIUtils.dip2PX(10), UIUtils.dip2PX(10), UIUtils.dip2PX(10));

                    int width = UIUtils.getTvWidth(getActivity(), tv_temp, item) + UIUtils.dip2PX(10);
//                    LogUtils.logE("width",width+"");
                    int screenWidth = UIUtils.getScreenWidth(getActivity());
                    numColumn = (screenWidth - UIUtils.dip2PX(10)) / width;
//                if(item.length()<4){
//                    numColumn=6;
//                }else if(item.length()>=4&&item.length()<6){
//                    numColumn=4;
//                }else if(item.length()>=6&&item.length()<8){
//                    numColumn=3;
//                } else{
//                    numColumn=2;
//                }
                    gv_attr.setNumColumns(numColumn);
                final QuickAdapter<Spec_list> adapter =new QuickAdapter<Spec_list>(getActivity(),R.layout.item_attr) {
                    @Override
                    protected void convert(BaseAdapterHelper helper, Spec_list item) {
                        TextView tv_attr=helper.getView(R.id.tv_attr_name);
                        if(helper.getPosition()==itemList.currentP){
                            helper.setBackgroundRes(R.id.tv_attr_name,R.drawable.bg_attr_selected);
//                    helper.setTextColor(R.id.tv_attr_name,R.color.theme_red);
                            ((TextView)helper.getView(R.id.tv_attr_name)).setTextColor(getActivity().getResources().getColor(R.color.theme_red));
                        }else {
                            helper.setBackgroundRes(R.id.tv_attr_name,R.drawable.bg_attr_default);
                            ((TextView)helper.getView(R.id.tv_attr_name)).setTextColor(getActivity().getResources().getColor(R.color.tv_333333));
                        }
                        int numColumn=gv_attr.getNumColumns();
                        int width=(UIUtils.getScreenWidth(getActivity())-UIUtils.dip2PX(20+10*(numColumn-1)))/numColumn;
//                        LogUtils.logE("itemWidth",""+width);
                        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) tv_attr.getLayoutParams();
                        if(numColumn<=4){
                            params.width=width;
                            (helper.getView(R.id.tv_attr_name)).setLayoutParams(params);
                        }else {
                            (helper.getView(R.id.tv_attr_name)).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        }
                        helper.setText(R.id.tv_attr_name,""+item.getItem());
                    }
                };
                gv_attr.setAdapter(adapter);
                final List<Spec_list> spec_lists=itemList.getSpec_list();
                adapter.replaceAll(spec_lists);
                UIUtils.initGridViewHeight(gv_attr, numColumn);
//                final int finalUrlPos = urlPos;
                gv_attr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        itemList.currentP=position;
                        String url="";
                        url=spec_lists.get(position).getSrc();
                        if(url!=null&&!TextUtils.isEmpty(url)){
                            ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST+url,iv_img);
                        }
                        String str="";
                        for(int j=0;j<goods_spec_lists.size();j++) {
                            List<Spec_list> temp=goods_spec_lists.get(j).getSpec_list();
                            for (int i = 0; i < temp.size(); i++) {
                                if(goods_spec_lists.get(j).currentP==i){
                                str += temp.get(i).getItem();
                                }
                            }
                        }
                        tv_attr_tips.setText(str);
                        mParamentTv.setText(str);
                        String[] strKeys=new String[goods_spec_lists.size()];
                            for(int i=0;i<goods_spec_lists.size();i++){
                                List<Spec_list> temp=goods_spec_lists.get(i).getSpec_list();
                                for(int j=0;j<temp.size();j++){
                                    if(j==goods_spec_lists.get(i).currentP){
                                        strKeys[i]=temp.get(j).getItem_id()+"";
                                    }
                                }
                            }

                                boolean isNotContain=true;
                               for(int j=0;j<spec_goods_prices.size();j++){
                                   for(int i=0;i<strKeys.length;i++) {
                                       if (spec_goods_prices.get(j).getKey().contains(strKeys[i])) {
                                           isNotContain=false;
                                       }else {
                                           isNotContain=true;
                                           break;
                                       }
                                   }
                                   if(!isNotContain){
                                       currentAttrPosi=j;
                                       break;
                                   }
                               }
                               String price=spec_goods_prices.get(currentAttrPosi).getPrice();
                               if(IssApplication.isShowDiscount){
                                price=spec_goods_prices.get(currentAttrPosi).getCost_price();
                               }
                        proPriceTv.setText("¥"+price);
                        tv_price.setText("¥"+price);
                        warn_number = spec_goods_prices.get(currentAttrPosi).getWarn_number();
                        String unit=spec_goods_prices.get(currentAttrPosi).getUnit();
                        if (TextUtils.isEmpty(unit)||unit.equals("null")) {
                            unit="个";
                        }
                        tv_num.setText(warn_number+"");
                        tv_warn_number.setText("("+ warn_number +unit+"起订)");
                        mProperty=spec_goods_prices.get(currentAttrPosi).getItem_id()+"";
                        mPropertyName = spec_goods_prices.get(currentAttrPosi).getKey_name();
                        getActivityInfo();

                        tv_store_count.setText("工厂库存（"+spec_goods_prices.get(currentAttrPosi).getStore_count()+"）");
                        tv_store_count_dialog.setText("库存："+spec_goods_prices.get(currentAttrPosi).getStore_count());

                        adapter.replaceAll(spec_lists);
//                        if(spec_goods_prices.get(currentAttrPosi).getProm_type()==3){
//                            setSaleInfo();
//                        }else {
//                            time_ll.setVisibility(View.GONE);
//                        }
                    }
                });
                gv_attr.performItemClick(null,itemList.currentP,0);

            }
        };
//        lv_attr.setAdapter(goods_spec_listQuickAdapter);
        goods_spec_listQuickAdapter.replaceAll(goods_spec_lists);
//        UIUtils.initListViewHeight(lv_attr);
//        gv_attr.performItemClick(null,currentAttrPosi,0);
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=tv_num.getText().toString();
                if(TextUtils.isEmpty(str)){
                    str=warn_number+"";
                    tv_num.setText(str);
                }
                int num= Integer.parseInt(tv_num.getText().toString());
                num+=warn_number;
                tv_num.setText(""+num);
            }
        });
        tv_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=tv_num.getText().toString();
                if(TextUtils.isEmpty(str)){
                    str=warn_number+"";
                    tv_num.setText(str);
                }
                int num= Integer.parseInt(tv_num.getText().toString());
                if(num>warn_number){
                    num-=warn_number;
                }
                tv_num.setText(""+num);
            }
        });
        btn_add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCount = Integer.parseInt(tv_num.getText().toString());
                if(currentCount%warn_number!=0){
                    MyToast.show(getActivity(),"数量必须为"+warn_number+"的整数倍");
                 return;
                }
                misson(TYPE_ADD_TO_CART, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                    String result=response.body().string();
                        final bocang.json.JSONObject jsonObject=new bocang.json.JSONObject(result);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyToast.show(getActivity(),jsonObject.getString(Constance.msg));
                                dialog.dismiss();
                                EventBus.getDefault().post(Constance.refreshCart);
                            }
                        });
//                    LogUtils.logE("result",result);
                    }
                });
            }
        });
        btn_buy_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCount = Integer.parseInt(tv_num.getText().toString());
                if(currentCount%warn_number!=0){
                    MyToast.show(getActivity(),"数量必须为"+warn_number+"的整数倍");
                    return;
                }
                ((ProDetailActivity)getActivity()).mCount= Integer.parseInt(tv_num.getText().toString());
                ((ProDetailActivity)getActivity()).mProperty=spec_goods_prices.get(currentAttrPosi).getItem_id()+"";
                ((ProDetailActivity)getActivity()).buyNow();
            }
        });

    }

    @Override
    public void onEnd(CountdownView cv) {

    }
    /**
     * 收藏图标状态
     */
    private void selectCollect() {
        if (mProductionObject.getIs_collect() != 0) {
            collect_iv.setImageResource(R.mipmap.page_collection_selected);
            tv_collect.setText("已收藏");
        } else {
            collect_iv.setImageResource(R.mipmap.page_collection_default);
            tv_collect.setText("收藏");
        }
    }

    public interface ScrollListener {
        void onScrollToBottom(int currPosition);

    }
    class NetworkImageHolderView implements CBPageAdapter.Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            // 你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, String data) {
            imageView.setImageResource(R.drawable.bg_default);
            ImageLoader.getInstance().displayImage(data, imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(view.getContext(), "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //在主线程执行
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(Integer action) {
        if (action == Constance.PROPERTY) {
            String property = ((ProDetailActivity) getActivity()).mPropertyValue;
            int price=((ProDetailActivity) getActivity()).mPrice;
            if (AppUtils.isEmpty(property))
                return;
            mParamentTv.setText("已选 " + (property));
//            unPriceTv.setText("￥"+(Double.parseDouble(mController.mOldPrice)+price));
//            proPriceTv.setText("￥"+(Double.parseDouble(mController.mCurrentPrice)+price));
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
