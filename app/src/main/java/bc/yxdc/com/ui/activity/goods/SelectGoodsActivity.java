package bc.yxdc.com.ui.activity.goods;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.FilterAttr;
import bc.yxdc.com.bean.FilterPrice;
import bc.yxdc.com.bean.Goods;
import bc.yxdc.com.bean.GoodsBean;
import bc.yxdc.com.bean.GoodsListResult;
import bc.yxdc.com.bean.Goods_spec_list;
import bc.yxdc.com.bean.Spec_goods_price;
import bc.yxdc.com.bean.Spec_list;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.view.EndOfGridView;
import bc.yxdc.com.ui.view.EndOfListView;
import bc.yxdc.com.ui.view.PMSwipeRefreshLayout;
import bc.yxdc.com.utils.AppUtils;
import bc.yxdc.com.utils.ConvertUtil;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/9/11.
 */

public class  SelectGoodsActivity extends BaseActivity implements EndOfListView.OnEndOfListListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private static final int TYPE_SEARCH = 2;
    private static final int TYPE_GOODS_DETAIL = 3;
    private static final int TYPE_FILTER = 4;
    private static final int TYPE_GOODS_LIST = 1;
    private TextView topRightBtn;
    private LinearLayout stylell;
    public String mCategoriesId="";
    public boolean isSelectGoods = false;
    public String mFilterAttr = "";
    public TextView select_num_tv;
    private RelativeLayout select_rl;
    public int mSort=-1;
    public boolean mIsYiJI=false;
    public String keyword="";
    public TextView et_search;
//    public JSONArray goodses;
    private PMSwipeRefreshLayout mPullToRefreshLayout;
    private ProAdapter mProAdapter;
    private EndOfGridView order_sv;
    private int page = 1;
    private View mNullView;
    private View mNullNet;
    private Button mRefeshBtn;
    private TextView mNullNetTv;
    private TextView mNullViewTv;
    private String per_pag = "10";
    public int mScreenWidth;
    public String mSortKey="";
    public String mSortValue="";
    //综合，上新，价格
    private TextView popularityTv,newTv, priceTv, saleTv;
    private ImageView price_iv;
    private Intent mIntent;
    private ProgressBar pd;
    private List<GoodsBean> goodsBeanList;
    private String brand="";
    private boolean is_price_asc;
    private boolean is_news_asc;
    @BindView(R.id.rl_goods_title)
    RelativeLayout rl_goods_title;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.iv_visibility_mode)
    ImageView iv_mode;
    @BindView(R.id.toolbar)
    View toolbar;
    @BindView(R.id.drawerlayout)
    DrawerLayout drawerlayout;
    @BindView(R.id.iv_mode_2)ImageView iv_mode_2;
    @BindView(R.id.tv_filter_has_choosen)TextView tv_filter_has_choosen;
    @BindView(R.id.search_ll)LinearLayout search_ll;
    private boolean isNew;
    private String title;
    private boolean isXianHuo;
    private View fl_filter;
    private ListView lv_filter_type;
    private QuickAdapter<FilterAttr> filterAdapter;
    private List<FilterAttr> filterAttrs;
    private TextView tv_reset;
    private TextView tv_ensure;
    private int currentP;
    private List<Spec_goods_price> spec_goods_prices;
    private int currentAttrPosi;
    private List<Goods_spec_list> goods_spec_lists;
    private String filterUrl;
    private int currentFilterPos;
    private GoodsListResult goodsListResult;
    private List<String> filter_choosen;
    private boolean isBottom;

    @Override
    protected void initData() {
        isNew = getIntent().getBooleanExtra(Constance.isNew,false);
        isXianHuo = getIntent().getBooleanExtra(Constance.isXianHuo,false);
        title = getIntent().getStringExtra(Constance.text);
        isSelectGoods =getIntent().getBooleanExtra(Constance.ISSELECTGOODS, false);
        filter_choosen = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initUI() {
        setContentView(R.layout.activity_select_product);
//        setColor(this, Color.WHITE);
        ButterKnife.bind(this);
        topRightBtn = getViewAndClick(R.id.topRightBtn);
        popularityTv = getViewAndClick(R.id.popularityTv);
        newTv = getViewAndClick(R.id.newTv);
        saleTv = getViewAndClick(R.id.saleTv);
        stylell = getViewAndClick(R.id.stylell);
        select_num_tv = (TextView)findViewById(R.id.select_num_tv);
        select_rl = getViewAndClick(R.id.select_rl);

        if (isSelectGoods) {
            select_rl.setVisibility(View.VISIBLE);
        }
        select_num_tv.setText(IssApplication.mSelectProducts.size()+"");
        et_search = (TextView) findViewById(R.id.et_search);
        mPullToRefreshLayout = ((PMSwipeRefreshLayout) findViewById(R.id.refresh_view));
        mPullToRefreshLayout.setColorSchemeColors(Color.BLUE,Color.GREEN,Color.YELLOW,Color.RED);
        mPullToRefreshLayout.setRefreshing(false);
        mPullToRefreshLayout.setOnRefreshListener(this);
        order_sv = (EndOfGridView) findViewById(R.id.priductGridView);
        order_sv.setOnEndOfListListener(this);
        mProAdapter = new ProAdapter();
        order_sv.setAdapter(mProAdapter);
        order_sv.setOnItemClickListener(this);
        mNullView = findViewById(R.id.null_view);
        mNullNet = findViewById(R.id.null_net);
        mRefeshBtn = (Button) mNullNet.findViewById(R.id.refesh_btn);
        mNullNetTv = (TextView) mNullNet.findViewById(R.id.tv);
        mNullViewTv = (TextView) mNullView.findViewById(R.id.tv);
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        popularityTv = (TextView) findViewById(R.id.popularityTv);
        priceTv = (TextView) findViewById(R.id.priceTv);
        newTv = (TextView) findViewById(R.id.newTv);
        saleTv = (TextView) findViewById(R.id.saleTv);
        price_iv = (ImageView) findViewById(R.id.price_iv);
        pd = (ProgressBar) findViewById(R.id.pd);
//        drawerlayout.setScrimColor(Color.TRANSPARENT);
        fl_filter = findViewById(R.id.fl_filter);
        drawerlayout.closeDrawer(fl_filter);
        lv_filter_type = findViewById(R.id.lv_filter_type);
        tv_reset = findViewById(R.id.tv_reset);
        tv_ensure = findViewById(R.id.tv_ensure);
        tv_reset.setOnClickListener(this);
        tv_ensure.setOnClickListener(this);
        filterAdapter = new QuickAdapter<FilterAttr>(this, R.layout.item_filter) {
            @Override
            protected void convert(final BaseAdapterHelper helper, final FilterAttr itemAttr) {
                helper.setText(R.id.tv_name,itemAttr.getName());
                QuickAdapter<FilterPrice> filterPriceQuickAdapter=new QuickAdapter<FilterPrice>(SelectGoodsActivity.this,R.layout.item_filter_item) {
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
                        for(int i=0;i<filterAttrs.size();i++){
                            if(i!=helper.getPosition()){
                                filterAttrs.get(i).setCurrent(-1);
                            }
                        }
                        currentFilterPos = helper.getPosition();
                        filterAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        lv_filter_type.setAdapter(filterAdapter);

        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerlayout,R.string.open,R.string.close){
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
        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SelectGoodsActivity.this, SearchActivity.class);
                startActivityForResult(intent,120);
            }
        });

        page = 1;
        if(isNew){
            rl_goods_title.setVisibility(View.VISIBLE);
            tv_title.setText(title);
            toolbar.setVisibility(View.GONE);
            search_ll.setVisibility(View.GONE);
        }else {
            rl_goods_title.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
        }
        if(getIntent().hasExtra(Constance.categories)){
        mCategoriesId = getIntent().getStringExtra(Constance.categories);
        }
//        JSONArray goodses= new JSONArray();
        if (!AppUtils.isEmpty(mSort)) {
            if (mSort == 4) {
                selectSortType(R.id.saleTv);
            } else if (mSort == 5) {
                selectSortType(R.id.newTv);
            } else if (mSort == 2) {
                selectSortType(R.id.popularityTv);
            }
        }
        selectProduct(1);

        if(getIntent().hasExtra("news")){
            newTv.performClick();
        }
        if (isSelectGoods) {
            select_rl.setVisibility(View.VISIBLE);
        }
    }

    public void selectProduct(final int page) {
        mPullToRefreshLayout.setRefreshing(true);
        if(isNew&&!isXianHuo){
            OkHttpUtils.getHomeNewGoods(page,new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    dismissRefesh();
                    String result=response.body().string();
                    JSONObject jsonObject=new JSONObject(result);
                    LogUtils.logE("news_goods_list",jsonObject.toString());
                    if(jsonObject!=null&&jsonObject.getJSONArray(Constance.result)!=null){
                        JSONArray array=jsonObject.getJSONArray(Constance.result);
                        List<GoodsBean> temp=new ArrayList<>();
                        if(array==null||array.length()==0){
                            isBottom = true;
                            return;
                        }
                        for(int i=0;i<array.length();i++){
                            temp.add(new Gson().fromJson(array.getJSONObject(i).toString(),GoodsBean.class));
                        }

                        if(page==1){
                            goodsBeanList = temp;
                        }else {
                            goodsBeanList.addAll(temp);
                        }
                        SelectGoodsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProAdapter.notifyDataSetChanged();
                            }
                        });
                    }else {
                        isBottom = true;
                    }
                }
            });
        }else {
                loadList();
        }


    }

    private void loadList() {
        LogUtils.logE("filter","cate:"+mCategoriesId+",sortKey:"+mSortKey+",SortValue:"+mSortValue+",brand:"+brand);
        misson(TYPE_GOODS_LIST, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dismissRefesh();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                dismissRefesh();
                String result=response.body().string();
                JSONObject jsonObject=new JSONObject(result);
//                LogUtils.logE("goods_list",jsonObject.getJSONObject(Constance.result).toString());
                if(jsonObject!=null&&jsonObject.getJSONObject(Constance.result)!=null){
                    filterAttrs = new ArrayList<>();
                    JSONObject resultObj=jsonObject.getJSONObject(Constance.result);
//                        JSONArray specArray=resultObj.getJSONArray(Constance.filter_spec);
                    JSONArray attrArray=resultObj.getJSONArray(Constance.filter_attr);
                    JSONArray priceArray=resultObj.getJSONArray(Constance.filter_price);
//                        if(specArray!=null&&specArray.length()>0){
//                            for(int i=0;i<specArray.length();i++){
//                                filterAttrs.add(new Gson().fromJson(specArray.getJSONObject(i).toString(),FilterAttr.class));
//                            }
//                        }
                    if(attrArray!=null&&attrArray.length()>0){

                    for(int i=0;i<attrArray.length();i++){
                        filterAttrs.add(new Gson().fromJson(attrArray.getJSONObject(i).toString(),FilterAttr.class));
                        filterAttrs.get(i).setCurrent(-1);
                        }
                    }
                    FilterAttr filterAttr=new FilterAttr();
                    filterAttr.setName("价格");
                    List<FilterPrice> filterPrices=new ArrayList<>();
                    if(priceArray!=null&&priceArray.length()>0){

                    for(int i=0;i<priceArray.length();i++){
                        filterPrices.add(new Gson().fromJson(priceArray.getJSONObject(i).toString(),FilterPrice.class));
                        }
                    }
                    filterAttr.setItem(filterPrices);
                    filterAttr.setCurrent(-1);
                    filterAttrs.add(filterAttr);
                    goodsListResult = new Gson().fromJson(jsonObject.getJSONObject(Constance.result).toString(),GoodsListResult.class);
                    List<GoodsBean> temp= goodsListResult.getGoods_list();
//                    LogUtils.logE("page+goods",page+","+temp.size());
                    if(temp==null||temp.size()==0){
                        isBottom=true;
                        return;
                    }
                    if(page==1){
                        goodsBeanList = temp;
                    }else {
                        goodsBeanList.addAll(temp);
                    }
                    SelectGoodsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProAdapter.notifyDataSetChanged();
                            filterAdapter.replaceAll(filterAttrs);
                            UIUtils.initListViewHeight(lv_filter_type);
                        }
                    });
                }else {
                    isBottom=true;
                }

            }
        });

    }

    @Override
    public void getData(int type, Callback callback) {
        switch (type){
            case TYPE_SEARCH:
                OkHttpUtils.searchProcude(keyword,page,"12",callback);
                break;
            case TYPE_GOODS_DETAIL:
                String userid=MyShare.get(this).getString(Constance.user_id);
                String token=MyShare.get(this).getString(Constance.token);
                OkHttpUtils.getGoodsDetail(userid,token,goodsBeanList.get(currentP).getGoods_id(),callback);
                break;
            case TYPE_FILTER:
                OkHttpUtils.getFilterGoods(NetWorkConst.API_HOST+filterUrl,callback);
                break;
            case TYPE_GOODS_LIST:
                if(!TextUtils.isEmpty(filterUrl)){
                    filterUrl=NetWorkConst.API_HOST+filterUrl;
                }
                OkHttpUtils.getGoodsList(filterUrl,mCategoriesId,mSortKey,mSortValue,brand,page, callback);
                break;
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        mCategoriesId = intent.getStringExtra(Constance.categories);
        if(!intent.hasExtra("key"))keyword="";
        String et_content=intent.getStringExtra("name");
        if(!TextUtils.isEmpty(et_content)){
            et_search.setText(et_content);
        }
//        if (mIsYiJI) {
//            selectYijiProduct(1, "12", null, null, null);
//        } else {
//        }
//            selectProduct(1, "12", null, null, null);
        page=1;
        searchProduct(keyword,page,"12");
        super.onNewIntent(intent);
    }

    private void searchProduct(String keyword, int page, String s) {
        misson(TYPE_SEARCH, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                dismissRefesh();
                String result=response.body().string();
                LogUtils.logE("search",result);
                JSONObject jsonObject=new JSONObject(result);
                if(jsonObject!=null&&jsonObject.getJSONObject(Constance.result)!=null){
                    GoodsListResult goodsListResult=new Gson().fromJson(jsonObject.getJSONObject(Constance.result).toString(),GoodsListResult.class);
                    goodsBeanList = goodsListResult.getGoods_list();
                    SelectGoodsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }


    public void selectSortType(int type) {
        Drawable drawable= getResources().getDrawable(R.mipmap.down_default);
/// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, UIUtils.dip2PX(6),UIUtils.dip2PX(4));
        popularityTv.setCompoundDrawables(null,null,drawable,null);

        drawable= getResources().getDrawable(R.mipmap.btn_all);
/// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, UIUtils.dip2PX(6),UIUtils.dip2PX(10));
        newTv.setCompoundDrawables(null,null,drawable,null);

        popularityTv.setTextColor(getResources().getColor(R.color.fontColor60));
        priceTv.setTextColor(getResources().getColor(R.color.fontColor60));
        newTv.setTextColor(getResources().getColor(R.color.fontColor60));
        price_iv.setImageResource(R.mipmap.btn_all);

        saleTv.setTextColor(getResources().getColor(R.color.fontColor60));

        switch (type) {
            case R.id.popularityTv:
                popularityTv.setTextColor(getResources().getColor(R.color.theme_red));
                drawable= getResources().getDrawable(R.mipmap.down_selected);
                drawable.setBounds(0, 0, UIUtils.dip2PX(6),UIUtils.dip2PX(4));
                popularityTv.setCompoundDrawables(null,null,drawable,null);
                mSortKey = "";//人气
                mSortValue = "";//排序
                filterUrl="";
                break;
            case R.id.stylell:
                priceTv.setTextColor(getResources().getColor(R.color.theme_red));
                price_iv.setImageResource(R.mipmap.btn_up_selected);
                mSortKey = "shop_price";//价格
                mSortValue = "asc";//排序
                is_price_asc = true;
                filterUrl=goodsListResult.getOrderby_price();
                if(filterUrl.contains("desc")){
                    filterUrl.replace("desc","asc");
                }
                break;
            case 2:
                priceTv.setTextColor(getResources().getColor(R.color.theme_red));
                price_iv.setImageResource(R.mipmap.btn_down_selected);
                mSortKey = "shop_price";//价格
                mSortValue = "desc";//排序
                is_price_asc = false;
                filterUrl=goodsListResult.getOrderby_price();
                if(filterUrl.contains("asc")){
                    filterUrl.replace("asc","desc");
                }
                break;
            case R.id.newTv:
                newTv.setTextColor(getResources().getColor(R.color.theme_red));
                drawable= getResources().getDrawable(R.mipmap.btn_down_selected);
                drawable.setBounds(0, 0, UIUtils.dip2PX(6),UIUtils.dip2PX(10));
                newTv.setCompoundDrawables(null,null,drawable,null);
                mSortKey = "goods_id";//新品
                mSortValue = "desc";//排序
                is_news_asc = false;
                filterUrl=goodsListResult.getOrderby_is_new();
                if(filterUrl.contains("asc")){
                    filterUrl.replace("asc","desc");
                }
                break;
            case 3:
                newTv.setTextColor(getResources().getColor(R.color.theme_red));
                drawable= getResources().getDrawable(R.mipmap.btn_up_selected);
                drawable.setBounds(0, 0, UIUtils.dip2PX(6),UIUtils.dip2PX(10));
                newTv.setCompoundDrawables(null,null,drawable,null);
                mSortKey = "goods_id";//新品
                mSortValue = "asc";//排序
                is_news_asc=true;
                filterUrl=goodsListResult.getOrderby_is_new();
                if(filterUrl.contains("desc")){
                    filterUrl.replace("desc","asc");
                }
                break;
            case R.id.saleTv:
                saleTv.setTextColor(getResources().getColor(R.color.theme_red));
                mSortKey = "4";//销售
                mSortValue = "2";//排序
                break;
        }
        page = 1;
        keyword="";
        order_sv.smoothScrollToPositionFromTop(0,0);
//        if (mIsYiJI) {
//            selectYijiProduct(1, per_pag, null, null, null);
//        } else {
//        }
            selectProduct(1);

    }

    @OnClick({R.id.rl_change_mode,R.id.iv_visibility_mode,R.id.tv_filter_has_choosen})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.popularityTv:
                selectSortType(v.getId());
                break;
            case R.id.newTv:
                if(is_news_asc){
                selectSortType(v.getId());
                }else {
                    selectSortType(3);
                }
                break;
            case R.id.stylell:
                if(!is_price_asc)
                {
                    selectSortType(v.getId());
                }else {
                    selectSortType(2);
                }
                break;
            case R.id.topRightBtn:
//                DrawerLayout.LayoutParams params=new DrawerLayout.LayoutParams(UIUtils.getScreenWidth(this)-100, ViewGroup.LayoutParams.MATCH_PARENT);
//                params.gravity= Gravity.END;
//                view.setLayoutParams(params);
//                if(drawerlayout==null)return;
                if(drawerlayout.isDrawerOpen(fl_filter)){
                    drawerlayout.closeDrawer(fl_filter);
                }else {
                    drawerlayout.openDrawer(fl_filter);
                }
                break;
            case R.id.tv_reset:
                for (int i=0;i<filterAttrs.size();i++){
                    filterAttrs.get(i).setCurrent(0);
                }
                filterAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_ensure:
                drawerlayout.closeDrawers();
                page=1;
//                selectProduct(page);
                if(currentFilterPos!=-1&&currentFilterPos<filterAttrs.size()){
                int currentHref=filterAttrs.get(currentFilterPos).getCurrent();
                if(currentHref!=-1){
                filterUrl=filterAttrs.get(currentFilterPos).getItem().get(currentHref).getHref();
                filter_choosen.add(filterAttrs.get(currentFilterPos).getItem().get(currentHref).getName());
                String str="";
                for(int i=0;i<filter_choosen.size();i++){
                    str+=filter_choosen.get(i);
                    if(i!=filter_choosen.size()-1)str+=",";
                }
                tv_filter_has_choosen.setText("已筛选："+str);
                if(TextUtils.isEmpty(str)){
                    tv_filter_has_choosen.setVisibility(View.GONE);
                }else {
                    tv_filter_has_choosen.setVisibility(View.VISIBLE);
                }
                }
                }
//                selectFilterProduct(page);
                selectProduct(page);
                mPullToRefreshLayout.setRefreshing(true);
                break;
            case R.id.select_rl:
                mIntent = new Intent();
                setResult(Constance.FROMDIY, mIntent);//告诉原来的Activity 将数据传递给它
                finish();//一定要调用该方法 关闭新的AC 此时 老是AC才能获取到Itent里面的值
                break;
            case R.id.rl_change_mode:
            case R.id.iv_visibility_mode:
                int numC=order_sv.getNumColumns();
                if(numC==2){
                    order_sv.setNumColumns(1);
                    iv_mode.setBackgroundResource(R.mipmap.nav_icon_longitudinal);
                    iv_mode_2.setBackgroundResource(R.mipmap.nav_icon_longitudinal);
                }else {
                    order_sv.setNumColumns(2);
                    iv_mode.setBackgroundResource(R.mipmap.nav_icon_transverse);
                    iv_mode_2.setBackgroundResource(R.mipmap.nav_icon_transverse);
                }
                mProAdapter=new ProAdapter();
                order_sv.setAdapter(mProAdapter);
                mProAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_filter_has_choosen:
                filterUrl="";
                filter_choosen=new ArrayList<>();
                tv_filter_has_choosen.setVisibility(View.INVISIBLE);
                for(int i=0;i<filterAttrs.size();i++){
                    filterAttrs.get(i).setCurrent(-1);
                }
//                filterAdapter.notifyDataSetChanged();
                selectProduct(page);
                break;

        }
    }

    private void selectFilterProduct(int page) {
        misson(TYPE_FILTER, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res=response.body().string();
                LogUtils.logE("filter",res);
                JSONObject jsonObject=new JSONObject(res);



            }
        });
    }

    @Override
    public void onEndOfList(Object lastItem) {
        if(goodsBeanList==null||page==1&&goodsBeanList.size()==0||isBottom){
            return;
        }
//        if(goodsBeanList.size()%Integer.parseInt(per_pag)!=0){
//            LogUtils.logE("page!=0","page"+page);
//            return;
//        }
//        if(goodsBeanList.size()/Integer.parseInt(per_pag)<page){
//            page--;
//            LogUtils.logE("page--","page"+page);
//            return;
//        }
        page++;
        selectProduct(page);
    }

    @Override
    public void onRefresh() {
        page=1;
        selectProduct(page);
    }

    private void dismissRefesh() {
        if (null != mPullToRefreshLayout) {
            mPullToRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mPullToRefreshLayout.setRefreshing(false);
                }
            });
        }
    }
            @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (isSelectGoods ) {
            currentP = position;
            misson(TYPE_GOODS_DETAIL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final JSONObject jsonObject=new JSONObject(response.body().string());
                    bocang.json.JSONObject result=jsonObject.getJSONObject(Constance.result);
                    if(jsonObject.getInt(Constance.status)!=1){
                        UIUtils.showSingleWordDialog(SelectGoodsActivity.this, jsonObject.getString(Constance.msg), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        return;
                    }
//                    ProDetailActivity.goods=new Gson().fromJson(jsonObject.getJSONObject(Constance.result).toString(),ProductResult.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONArray spec_goods_array = jsonObject.getJSONObject(Constance.result).getJSONArray(Constance.spec_goods_price);
                            JSONArray goods_speclist=jsonObject.getJSONObject(Constance.result).getJSONArray(Constance.goods_spec_list);
                            if(spec_goods_array!=null&&spec_goods_array.length()>0){
                                spec_goods_prices = new Gson().fromJson(spec_goods_array.toString(),new TypeToken<List<Spec_goods_price>>(){}.getType());
                                goods_spec_lists = new Gson().fromJson(goods_speclist.toString(),new TypeToken<List<Goods_spec_list>>(){}.getType());
                                showParamSelectDialog();
                            }else {
                                for (int i = 0; i < IssApplication.mSelectProducts.size(); i++) {
                                    String selectName = IssApplication.mSelectProducts.get(i).getGoods_name();
                                    String name = goodsBeanList.get(position).getGoods_name();
                                    String c_property=goodsBeanList.get(position).c_property;
                                    String selectC_property=IssApplication.mSelectProducts.get(i).c_property;
                                    if (selectName.equals(name)&&c_property!=null&&selectC_property!=null&&c_property.equals(selectC_property)) {
                                        IssApplication.mSelectProducts.remove(i);
                                        mProAdapter.notifyDataSetChanged();
                                        select_num_tv.setText(IssApplication.mSelectProducts.size() + "");
                                        return;
                                    }
                                }
                                Goods goods=new Goods();
                                goods.setGoods_id(goodsBeanList.get(position).getGoods_id());
                                goods.setOriginal_img(goodsBeanList.get(position).getOriginal_img());
                                goods.setShop_price(goodsBeanList.get(position).getShop_price());
                                goods.setGoods_name(goodsBeanList.get(position).getGoods_name());
                                goods.c_url=goodsBeanList.get(position).c_url;
                                goods.c_property=goodsBeanList.get(position).c_property;
                                IssApplication.mSelectProducts.add(goods);
                                mProAdapter.notifyDataSetChanged();
                                select_num_tv.setText(IssApplication.mSelectProducts.size() + "");
                            }
                        }
                    });

                }
            });

        } else {
            MobclickAgent.onEvent(SelectGoodsActivity.this,"product",""+goodsBeanList.get(position).getGoods_name());

            mIntent = new Intent(this, ProDetailActivity.class);
            int productId =goodsBeanList.get(position).getGoods_id();
            mIntent.putExtra(Constance.product, productId);
            startActivity(mIntent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==120&&resultCode==120){
            keyword = data.getStringExtra("key");
            mCategoriesId="";
            et_search.setText(keyword);
            searchProduct(keyword,page,"12");
        }
    }

    public  void showParamSelectDialog() {
        if(spec_goods_prices==null||spec_goods_prices.size()==0){
            LogUtils.logE("no_goods","return");
            return;
        }
        final Dialog dialog=UIUtils.showBottomInDialog(SelectGoodsActivity.this,R.layout.dialog_attr_goods_select);
        LogUtils.logE("has_goods","show");
        final ImageView iv_img=dialog.findViewById(R.id.iv_img);
        TextView tv_name=dialog.findViewById(R.id.tv_name);
        final TextView tv_price=dialog.findViewById(R.id.tv_price);
        final TextView tv_store_count_dialog=dialog.findViewById(R.id.tv_store_count);
        ImageView iv_dismiss=dialog.findViewById(R.id.iv_dismiss);
//        final TextView tv_attr=dialog.findViewById(R.id.tv_attr_name);
//        GridView gv_attr=dialog.findViewById(R.id.gv_attr);
        final TextView tv_attr_tips=dialog.findViewById(R.id.tv_attr_tips);
        Button btn_diy=dialog.findViewById(R.id.btn_diy);
        ListView lv_attr=dialog.findViewById(R.id.lv_attr);
        btn_diy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                for (int i = 0; i < IssApplication.mSelectProducts.size(); i++) {
                    String selectName = IssApplication.mSelectProducts.get(i).getGoods_name();
                    String name = goodsBeanList.get(currentP).getGoods_name();
                    String c_property=goodsBeanList.get(currentP).c_property;
                    String selectC_property=IssApplication.mSelectProducts.get(i).c_property;
                    if (c_property!=null&&selectC_property!=null&&selectName.equals(name)&&c_property.equals(selectC_property)) {
                        IssApplication.mSelectProducts.remove(i);
                        mProAdapter.notifyDataSetChanged();
                        select_num_tv.setText(IssApplication.mSelectProducts.size() + "");
                        return;
                    }
                }
                Goods goods=new Goods();
                goods.setGoods_id(goodsBeanList.get(currentP).getGoods_id());
                goods.setOriginal_img(goodsBeanList.get(currentP).getOriginal_img());
                goods.setShop_price(goodsBeanList.get(currentP).getShop_price());
                goods.setGoods_name(goodsBeanList.get(currentP).getGoods_name());
                goods.c_url=goodsBeanList.get(currentP).c_url;
                goods.c_property=goodsBeanList.get(currentP).c_property;
                IssApplication.mSelectProducts.add(goods);
                mProAdapter.notifyDataSetChanged();
                select_num_tv.setText(IssApplication.mSelectProducts.size() + "");
            }
        });
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
        ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+ProDetailActivity.goods.getGoods().getGoods_id(),iv_img);

        tv_name.setText(goodsBeanList.get(currentP).getGoods_name());
        for(int i=0;i<spec_goods_prices.size();i++){
            if(i>0){
                if(Double.parseDouble(spec_goods_prices.get(i).getPrice())<Double.parseDouble(spec_goods_prices.get(i-1).getPrice())){
                    currentAttrPosi = i;
                }
            }
        }

        tv_price.setText("¥"+spec_goods_prices.get(currentAttrPosi).getPrice());
        tv_store_count_dialog.setText("库存："+spec_goods_prices.get(currentAttrPosi).getStore_count());
        iv_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        QuickAdapter<Goods_spec_list> goods_spec_listQuickAdapter=new QuickAdapter<Goods_spec_list>(SelectGoodsActivity.this,R.layout.item_attr_lv) {
            @Override
            protected void convert(BaseAdapterHelper helper, final Goods_spec_list itemList) {
                GridView gv_attr=helper.getView(R.id.gv_attr);
                helper.setText(R.id.tv_attr_name,itemList.getSpec_name());
                String item=itemList.getSpec_list().get(0).getItem();
                int numColumn;
                if(item.length()<4){
                    numColumn=6;
                }else if(item.length()>=4&&item.length()<6){
                    numColumn=4;
                }else if(item.length()>=6&&item.length()<8){
                    numColumn=3;
                } else{
                    numColumn=2;
                }
                gv_attr.setNumColumns(numColumn);
                final QuickAdapter<Spec_list> adapter =new QuickAdapter<Spec_list>(SelectGoodsActivity.this,R.layout.item_attr) {
                    @Override
                    protected void convert(BaseAdapterHelper helper, Spec_list item) {
                        if(helper.getPosition()==itemList.currentP){
                            helper.setBackgroundRes(R.id.tv_attr_name,R.drawable.bg_attr_selected);
//                    helper.setTextColor(R.id.tv_attr_name,R.color.theme_red);
                            ((TextView)helper.getView(R.id.tv_attr_name)).setTextColor(getResources().getColor(R.color.theme_red));
                        }else {
                            helper.setBackgroundRes(R.id.tv_attr_name,R.drawable.bg_attr_default);
                            ((TextView)helper.getView(R.id.tv_attr_name)).setTextColor(getResources().getColor(R.color.tv_333333));
                        }
                        helper.setText(R.id.tv_attr_name,""+item.getItem());
                    }
                };
                gv_attr.setAdapter(adapter);
                final List<Spec_list> spec_lists=itemList.getSpec_list();
                adapter.replaceAll(spec_lists);
                UIUtils.initGridViewHeight(gv_attr,numColumn);
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
                        tv_price.setText("¥"+spec_goods_prices.get(currentAttrPosi).getPrice());
                        tv_store_count_dialog.setText("库存："+spec_goods_prices.get(currentAttrPosi).getStore_count());
                        adapter.replaceAll(spec_lists);
                    }
                });
                gv_attr.performItemClick(null,itemList.currentP,0);

            }
        };
        lv_attr.setAdapter(goods_spec_listQuickAdapter);
        goods_spec_listQuickAdapter.replaceAll(goods_spec_lists);
        UIUtils.initListViewHeight(lv_attr);
//        final QuickAdapter adapter =new QuickAdapter<Spec_goods_price>(SelectGoodsActivity.this,R.layout.item_attr) {
//            @Override
//            protected void convert(BaseAdapterHelper helper, Spec_goods_price item) {
//                if(helper.getPosition()== currentAttrPosi){
//                    helper.setBackgroundRes(R.id.tv_attr_name,R.drawable.bg_attr_selected);
////                    helper.setTextColor(R.id.tv_attr_name,R.color.theme_red);
//                    ((TextView)helper.getView(R.id.tv_attr_name)).setTextColor(getResources().getColor(R.color.theme_red));
//                }else {
//                    helper.setBackgroundRes(R.id.tv_attr_name,R.drawable.bg_attr_default);
//                    ((TextView)helper.getView(R.id.tv_attr_name)).setTextColor(getResources().getColor(R.color.tv_333333));
//                }
//                helper.setText(R.id.tv_attr_name,item.getKey_name());
//            }
//        };
//        gv_attr.setAdapter(adapter);
//        adapter.replaceAll(spec_goods_prices);
//        gv_attr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                currentAttrPosi =position;
//                goodsBeanList.get(currentP).c_property=spec_goods_prices.get(currentAttrPosi).getItem_id()+"";
//                goodsBeanList.get(currentP).c_url=spec_goods_prices.get(currentAttrPosi).getSpec_img();
//                tv_attr_tips.setText(spec_goods_prices.get(currentAttrPosi).getKey_name());
//                tv_price.setText("¥"+spec_goods_prices.get(currentAttrPosi).getPrice());
//                tv_store_count_dialog.setText("库存："+spec_goods_prices.get(currentAttrPosi).getStore_count());
//                ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST+spec_goods_prices.get(currentAttrPosi).getSpec_img(),iv_img);
//                adapter.replaceAll(spec_goods_prices);
//            }
//        });
//        gv_attr.performItemClick(null,currentAttrPosi,0);

    }


    private class ProAdapter extends BaseAdapter {
        public ProAdapter() {
        }

        @Override
        public int getCount() {
            if (null == goodsBeanList)
                return 0;
            return goodsBeanList.size();
        }

        @Override
        public GoodsBean getItem(int position) {
            if (null == goodsBeanList)
                return null;
            return goodsBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                int columns=order_sv.getNumColumns();
                if(columns==1){
                    convertView = View.inflate(SelectGoodsActivity.this, R.layout.item_gridview_fm_product_single, null);
                }else {
                    convertView = View.inflate(SelectGoodsActivity.this, R.layout.item_gridview_fm_product, null);
                }
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.check_iv = (ImageView) convertView.findViewById(R.id.check_iv);
                holder.textView = (TextView) convertView.findViewById(R.id.name_tv);
                holder.groupbuy_tv = (TextView) convertView.findViewById(R.id.groupbuy_tv);
                holder.old_price_tv = (TextView) convertView.findViewById(R.id.old_price_tv);
                holder.price_tv = (TextView) convertView.findViewById(R.id.price_tv);
                holder.tv_sold=convertView.findViewById(R.id.tv_sold);
                if(columns!=1){
                    RelativeLayout.LayoutParams lLp = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
                float h = (mScreenWidth - ConvertUtil.dp2px(SelectGoodsActivity.this, 45.8f)) / 2;
                lLp.height = (int) h;
                holder.imageView.setLayoutParams(lLp);
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                String name = goodsBeanList.get(position).getGoods_name();
                holder.textView.setText(name);
                //                holder.imageView.setImageResource(R.drawable.bg_default);
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+goodsBeanList.get(position).getGoods_id()
                        , holder.imageView);

//                JSONObject groupBuyObject = goodses.getJSONObject(position).getJSONObject(Constance.group_buy);
//                int isFinished=-1;
//                if(!AppUtils.isEmpty(groupBuyObject))
//                {
//                    isFinished=groupBuyObject.getInt(Constance.is_finished);
//                }

//                holder.groupbuy_tv.setVisibility(isFinished==0? View.VISIBLE : View.GONE);
                double old_Price=0;
//                JSONArray propertieArray = goodses.getJSONObject(position).getJSONArray(Constance.properties);
//                if (!AppUtils.isEmpty(propertieArray)&&propertieArray.length()>0) {
//                    JSONArray attrsArray = propertieArray.getJSONObject(0).getJSONArray(Constance.attrs);
//                    int price = attrsArray.getJSONObject(0).getInt(Constance.attr_price);
//                    double currentPrice = price;
//                    old_Price=currentPrice;
//                    holder.price_tv.setText("￥" + currentPrice);
//                } else {
//
//                }
//                old_Price= Double.parseDouble(goodses.getJSONObject(position).getString(Constance.current_price));
                if(IssApplication.isShowDiscount){
                    holder.price_tv.setText("￥" + goodsBeanList.get(position).getCost_price());
                }else {
                    holder.price_tv.setText("￥" + goodsBeanList.get(position).getShop_price());
                }

                holder.tv_sold.setText("已售"+goodsBeanList.get(position).getSales_sum()+"件");
                old_Price=old_Price*1.6;
                DecimalFormat df=new DecimalFormat("###.00");
                holder.old_price_tv.setText("￥" + df.format(old_Price));
                holder.old_price_tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.check_iv.setVisibility(View.GONE);
                if (isSelectGoods) {
                    for (int i = 0; i < IssApplication.mSelectProducts.size(); i++) {
                        String goodName = IssApplication.mSelectProducts.get(i).getGoods_name();
                        if (name.equals(goodName)) {
                            holder.check_iv.setVisibility(View.VISIBLE);
                            break;
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            ImageView check_iv;
            TextView textView;
            TextView groupbuy_tv;
            TextView old_price_tv;
            TextView price_tv;
            TextView tv_sold;

        }
    }
    
}
