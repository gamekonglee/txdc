package bc.yxdc.com.ui.activity.goods;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.GoodsBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/10/22.
 */

public class GoodsWeiHuoActivity extends BaseActivity {
    @BindView(R.id.gv_weihuo_goods)
    GridView gv_weihuo;
    @BindView(R.id.layout_empty)View layout_empty;
    private QuickAdapter<GoodsBean> adapter;
    private List<GoodsBean> goodsBeans;

    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_weihuo);
        ButterKnife.bind(this);
        adapter = new QuickAdapter<GoodsBean>(this, R.layout.item_goods) {
            @Override
            protected void convert(BaseAdapterHelper helper, GoodsBean item) {
                helper.setText(R.id.name_tv,item.getGoods_name());
                if(IssApplication.isShowDiscount){
                    helper.setText(R.id.price_tv,"￥" +item.getCost_price());
                }else {
                    helper.setText(R.id.price_tv,"￥" +item.getShop_price());
                }
                helper.setText(R.id.tv_sold, "已售"+item.getSales_sum()+"件");
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getGoods_id(),((ImageView)helper.getView(R.id.imageView)), IssApplication.getImageLoaderOption());

            }
        };
        gv_weihuo.setAdapter(adapter);
        gv_weihuo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MobclickAgent.onEvent(GoodsWeiHuoActivity.this,"product",""+goodsBeans.get(position).getGoods_name());
                Intent intent=new Intent(GoodsWeiHuoActivity.this,ProDetailActivity.class);
                intent.putExtra(Constance.product,goodsBeans.get(position).getGoods_id());
                startActivity(intent);
            }
        });
        misson(0, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    JSONArray jsonArray=res.getJSONArray(Constance.result);
                    goodsBeans = new ArrayList<>();
                    for(int i=0;i<jsonArray.length();i++){
                        goodsBeans.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(),GoodsBean.class));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(goodsBeans==null||goodsBeans.size()==0){
                                layout_empty.setVisibility(View.VISIBLE);
                            }else {
                                layout_empty.setVisibility(View.GONE);
                            }
                            adapter.replaceAll(goodsBeans);
                            UIUtils.initGridViewHeight(gv_weihuo);
                        }
                    });

                }
            }
        });

    }

    @Override
    public void getData(int type, Callback callback) {
        OkHttpUtils.getGoodsWeiHuo(callback);

    }
}
