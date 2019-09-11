package bc.yxdc.com.ui.activity.goods;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.yalantis.ucrop.util.BitmapLoadUtils;

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
import bc.yxdc.com.ui.view.EndOfGridView;
import bc.yxdc.com.utils.ImageUtil;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.bitmap.Base64BitmapUtils;
import bocang.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/11/21.
 */

public class GoodsSearchResultActivity extends BaseActivity {

    private static final int TYPE_SEARCH_IMG = 0;
    private QuickAdapter<GoodsBean> adapter;
    private String path;
    private String base64;
    private List<GoodsBean> goodsBeans;

    @Override
    protected void initData() {
        path = getIntent().getStringExtra(Constance.path);
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_goods_search_result);
        EndOfGridView gv_goods=findViewById(R.id.gv_goods);
        adapter = new QuickAdapter<GoodsBean>(this, R.layout.item_goods) {
            @Override
            protected void convert(BaseAdapterHelper helper, GoodsBean item) {
                helper.setText(R.id.name_tv,item.getGoods_name());
                helper.setText(R.id.name_tv,item.getGoods_name());
                View view1=helper.getView(R.id.ll_1);
                View view2=helper.getView(R.id.ll_2);
                if(IssApplication.isShowDiscount){
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_shop_price,"市场价：￥"+item.getShop_price());
                    helper.setText(R.id.tv_cost_price,"代理价：￥"+item.getCost_price());
//                helper.setText(R.id.price_tv,"¥"+item.getCost_price()+"");
                }else {
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.GONE);
                    helper.setText(R.id.price_tv,"¥"+item.getShop_price()+"");
                }
                helper.setText(R.id.tv_sold_2, "已售"+item.getSales_sum()+"件");
                helper.setText(R.id.tv_sold, "已售"+item.getSales_sum()+"件");
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getGoods_id(),((ImageView)helper.getView(R.id.imageView)), IssApplication.getImageLoaderOption());
            }
        };
        gv_goods.setAdapter(adapter);
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(jsonObject==null||jsonObject.getJSONArray(Constance.result)==null||jsonObject.getJSONArray(Constance.result).length()==0)
                                        {
                                            MyToast.show(GoodsSearchResultActivity.this,"没有搜索到相关结果");
                                            return;
                                        } else {
                                            goodsBeans = new ArrayList<>();
                                            goodsBeans=new Gson().fromJson(jsonObject.getJSONArray(Constance.result).toString(),new TypeToken<List<GoodsBean>>(){}.getType());
                                            adapter.replaceAll(goodsBeans);
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
                MobclickAgent.onEvent(GoodsSearchResultActivity.this,"product",""+goodsBeans.get(position).getGoods_name());
                Intent intent=new Intent(GoodsSearchResultActivity.this,ProDetailActivity.class);
                intent.putExtra(Constance.product,goodsBeans.get(position).getGoods_id());
                startActivity(intent);
            }
        });

    }

    @Override
    public void getData(int type, Callback callback) {
        if(type==TYPE_SEARCH_IMG){
            LogUtils.logE("base64",base64);
            OkHttpUtils.searchSimliarImg(base64,callback);
        }
    }
}
