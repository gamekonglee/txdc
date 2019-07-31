package bc.yxdc.com.ui.activity.goods;

import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import astuetz.MyPagerSlidingTabStrip;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.GoodsBean;
import bc.yxdc.com.bean.GoodsCategoryBean;
import bc.yxdc.com.bean.GoodsListResult;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.view.EndOfGridView;
import bc.yxdc.com.ui.view.EndOfListView;
import bc.yxdc.com.ui.view.PMSwipeRefreshLayout;
import bc.yxdc.com.utils.LogUtils;
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
 * Created by gamekonglee on 2018/11/1.
 */

public class CategoryGoodsListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, EndOfListView.OnEndOfListListener {
    @BindView(R.id.gv_category)
    EndOfGridView gv_category;
    @BindView(R.id.tab_strip)
    MyPagerSlidingTabStrip pagerSlidingTabStrip;
    @BindView(R.id.tv_category_title)
    TextView tv_category_title;
    @BindView(R.id.pulltorefresh)
    PMSwipeRefreshLayout pulltorefresh;
//    public String[]titles={"吊灯","吊灯2","吊灯3","吊灯4","吊灯5","吊灯6","吊灯7","吊灯8","吊灯9","吊灯10","吊灯","吊灯","吊灯","吊灯","吊灯"};
    private QuickAdapter<GoodsBean> adapter;
    private List<GoodsBean> goodsBeans;
    private int page;
    private String mCategoriesId;
    private String mSortKey;
    private String mSortValue;
    private String brand;
    private String name;
    private List<GoodsCategoryBean> goodsCategoryBeans;
    private boolean isEnd;
    @BindView(R.id.iv_mode_2)ImageView iv_mode_2;
    private int current;

    @Override
    protected void initData() {
        mCategoriesId=getIntent().getStringExtra(Constance.categories);
        name = getIntent().getStringExtra(Constance.name);
        JSONArray categoryList= (JSONArray) getIntent().getSerializableExtra(Constance.categoryList);
        LogUtils.logE("cateL",categoryList.toString());
        goodsCategoryBeans = new ArrayList<>();
        for(int i=0;i<categoryList.length();i++){
            goodsCategoryBeans.add(new Gson().fromJson(categoryList.getString(i),GoodsCategoryBean.class));
            if(goodsCategoryBeans.get(i).getId().equals(mCategoriesId)){
                current = i;
            }
            }

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_category_goods);
        ButterKnife.bind(this);
        ViewPager viewPager=findViewById(R.id.vPager);
        UIUtils.initPullToRefresh(pulltorefresh);
        pulltorefresh.setOnRefreshListener(this);
        gv_category.setOnEndOfListListener(this);
        pagerSlidingTabStrip.selectColor=(getResources().getColor(R.color.green));
        pagerSlidingTabStrip.defaultColor=getResources().getColor(R.color.txt_black);
        pagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        pagerSlidingTabStrip.setIndicatorColor(getResources().getColor(R.color.green));
        pagerSlidingTabStrip.setUnderlineColor(getResources().getColor(R.color.goods_details_sku));
        pagerSlidingTabStrip.setUnderlineHeight(1);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return goodsCategoryBeans.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view==object;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return goodsCategoryBeans.get(position).getName();
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                TextView textView=new TextView(CategoryGoodsListActivity.this);
                container.addView(textView);
                return textView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        });
        pagerSlidingTabStrip.setViewPager(viewPager);
        initAdapter();
        viewPager.setCurrentItem(current);

        gv_category.setAdapter(adapter);
        gv_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MobclickAgent.onEvent(CategoryGoodsListActivity.this,"product",""+goodsBeans.get(position).getGoods_name());
                Intent intent=new Intent(CategoryGoodsListActivity.this,ProDetailActivity.class);
                intent.putExtra(Constance.product,goodsBeans.get(position).getGoods_id());
                startActivity(intent);
            }
        });
        tv_category_title.setText(name);
        goodsBeans = new ArrayList<>();
        page = 1;
        mSortKey = "";
        mSortValue = "";
        brand = "";
        load();
        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCategoriesId=goodsCategoryBeans.get(position).getId();
                isEnd=false;
                page=1;
                goodsBeans=new ArrayList<>();
                load();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initAdapter() {
        if(gv_category.getNumColumns()!=1){
            adapter = new QuickAdapter<GoodsBean>(this, R.layout.item_gridview_fm_product) {
                @Override
                protected void convert(BaseAdapterHelper helper, GoodsBean item) {
                    helper.setText(R.id.name_tv,item.getGoods_name());
                    ImageView imageView=helper.getView(R.id.imageView);
                    ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getGoods_id(),imageView, IssApplication.getImageLoaderOption());
                    helper.setText(R.id.price_tv,"￥" +item.getShop_price());
                    helper.setText(R.id.tv_sold,"已售"+item.getSales_sum()+"件");


                }
            };
        }else {
            adapter = new QuickAdapter<GoodsBean>(this, R.layout.item_gridview_fm_product_single) {
                @Override
                protected void convert(BaseAdapterHelper helper, GoodsBean item) {
                    helper.setText(R.id.name_tv,item.getGoods_name());
                    ImageView imageView=helper.getView(R.id.imageView);
                    ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getGoods_id(),imageView, IssApplication.getImageLoaderOption());
                    helper.setText(R.id.price_tv,"￥" +item.getShop_price());
                    helper.setText(R.id.tv_sold,"已售"+item.getSales_sum()+"件");
                }
            };
        }
    }

    @OnClick(R.id.rl_change_mode)
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_change_mode:
                int numC=gv_category.getNumColumns();
                if(numC==2){
                    gv_category.setNumColumns(1);
                    iv_mode_2.setBackgroundResource(R.mipmap.nav_icon_longitudinal);
                }else {
                    gv_category.setNumColumns(2);
                    iv_mode_2.setBackgroundResource(R.mipmap.nav_icon_transverse);
                }
                initAdapter();
                gv_category.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void load() {
        misson(0, new Callback() {
            private List<GoodsBean> temp;
            private GoodsListResult goodsListResult;

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(pulltorefresh!=null){
                            pulltorefresh.setRefreshing(false);
                        }
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(pulltorefresh!=null){
                            pulltorefresh.setRefreshing(false);
                        }
                    }
                });
                temp=new ArrayList<>();
                String result=response.body().string();
                JSONObject jsonObject=new JSONObject(result);
                LogUtils.logE("goods_list",jsonObject.getJSONObject(Constance.result).toString());
                if(jsonObject!=null&&jsonObject.getJSONObject(Constance.result)!=null){
//                    JSONObject resultObj=jsonObject.getJSONObject(Constance.result);

                    GoodsListResult goodsListResult= new Gson().fromJson(jsonObject.getJSONObject(Constance.result).toString(),GoodsListResult.class);
                    temp = goodsListResult.getGoods_list();
                    if(temp ==null|| temp.size()==0){
                        isEnd=true;
                        if(page==1&&(temp==null||temp.size()==0)){
                            goodsBeans=new ArrayList<>();
                            CategoryGoodsListActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.replaceAll(goodsBeans);
                                }
                            });
                        }
                        return;
                    }
                    if(page==1){
                        goodsBeans = temp;
                    }else {
                        goodsBeans.addAll(temp);
                    }
                }
                CategoryGoodsListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.replaceAll(goodsBeans);
                    }
                });
            }
        });
    }

    @Override
    public void getData(int type, Callback callback) {
        OkHttpUtils.getGoodsList("",mCategoriesId, mSortKey, mSortValue, brand,page,callback);
    }

    @Override
    public void onRefresh() {
        isEnd=false;
        page=1;
        load();
    }

    @Override
    public void onEndOfList(Object lastItem) {
        if(page==1&&(goodsBeans==null||goodsBeans.size()==0)||isEnd){
            return;
        }
        page++;
        load();
    }
}
