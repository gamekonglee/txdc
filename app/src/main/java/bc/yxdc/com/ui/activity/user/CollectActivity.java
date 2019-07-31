package bc.yxdc.com.ui.activity.user;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.CollectBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.goods.ProDetailActivity;
import bc.yxdc.com.ui.view.EndOfListView;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/11/6.
 */

public class CollectActivity extends BaseActivity {
    private static final int TYPE_COLLECT = 0;
    private static final int TYPE_ADD_TO_CART = 1;
    private static final int TYPE_CANCEL = 2;
    @BindView(R.id.tv_edit)
    TextView tv_edit;
    @BindView(R.id.lv_collect)
    EndOfListView lv_collect;
    @BindView(R.id.tv_default)
    TextView tv_default;
    @BindView(R.id.tv_price)
    TextView tv_price;
    @BindView(R.id.tv_category)
    TextView tv_category;
    @BindView(R.id.tv_store)
    TextView tv_store;
    @BindView(R.id.layout_empty)
    View layout_empty;
    private List<CollectBean> collectBeans;
    private QuickAdapter<CollectBean> adapter;
    private int currentPos;

    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);
        collectBeans = new ArrayList<>();
        adapter = new QuickAdapter<CollectBean>(this, R.layout.item_zuji_goods) {
            @Override
            protected void convert(final BaseAdapterHelper helper, CollectBean item) {
                helper.setText(R.id.tv_name,item.getGoods_name());
                helper.setText(R.id.tv_price,"¥"+item.getShop_price());
                helper.setVisible(R.id.tv_cancel,true);
                helper.setOnClickListener(R.id.tv_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIUtils.showSingleWordDialog(CollectActivity.this, "确定要取消收藏吗？", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                currentPos=helper.getPosition();
                                misson(TYPE_CANCEL, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        final JSONObject jsonObject=new JSONObject(response.body().string());
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MyToast.show(CollectActivity.this,jsonObject.getString(Constance.msg));
                                                collectBeans=new ArrayList<>();
                                                load();
                                            }
                                        });

                                    }
                                });

                            }
                        });
                    }
                });
                ImageView iv_img=helper.getView(R.id.iv_img);
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getGoods_id(),iv_img, IssApplication.getImageLoaderOption());
                helper.setOnClickListener(R.id.iv_add_cart, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentPos = helper.getPosition();
                        misson(TYPE_ADD_TO_CART, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final JSONObject jsonObject=new JSONObject(response.body().string());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        MyToast.show(CollectActivity.this,jsonObject.getString(Constance.msg));
                                    }
                                });

                            }
                        });

                    }
                });
            }
        };
        lv_collect.setAdapter(adapter);
        lv_collect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(CollectActivity.this, ProDetailActivity.class);
                intent.putExtra(Constance.product,collectBeans.get(position).getGoods_id());
                startActivity(intent);
            }
        });
        load();
    }

    private void load() {
        misson(TYPE_COLLECT, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jsonObject=new JSONObject(response.body().string());
                LogUtils.logE("collect",jsonObject.toString());
                if(jsonObject.getInt(Constance.status)==1){
                    JSONArray array=jsonObject.getJSONArray(Constance.result);
                    for(int i=0;i<array.length();i++){
                        collectBeans.add(new Gson().fromJson(array.getJSONObject(i).toString(),CollectBean.class));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(collectBeans==null||collectBeans.size()==0){
                                layout_empty.setVisibility(View.VISIBLE);
                            }else {
                                layout_empty.setVisibility(View.GONE);
                            }
                            adapter.replaceAll(collectBeans);
                        }
                    });
                }

            }
        });
    }

    @Override
    public void getData(int type, Callback callback) {
        if(type==TYPE_ADD_TO_CART){
            String user_id= MyShare.get(this).getString(Constance.user_id);
            String token=MyShare.get(this).getString(Constance.token);
            if(TextUtils.isEmpty(user_id)||TextUtils.isEmpty(token)){
                MyToast.show(this,"请先登录");
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            OkHttpUtils.addToShopCart(collectBeans.get(currentPos).getGoods_id()+"","",1,user_id,token,callback);
        }else if(type==TYPE_COLLECT){
        String user_id= MyShare.get(this).getString(Constance.user_id);
        String tokend=MyShare.get(this).getString(Constance.token);
        if(TextUtils.isEmpty(user_id)||TextUtils.isEmpty(tokend)){
            MyToast.show(this,"登录状态失效");
            startActivity(new Intent(this,LoginActivity.class));
            finish();
            return;
        }
        OkHttpUtils.getCollect(user_id,tokend,callback);
    }else if(type==TYPE_CANCEL){
            String user_id= MyShare.get(this).getString(Constance.user_id);
            String token=MyShare.get(this).getString(Constance.token);
            if(TextUtils.isEmpty(user_id)||TextUtils.isEmpty(token)){
                MyToast.show(this,"请先登录");
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            OkHttpUtils.collectGoods(collectBeans.get(currentPos).getGoods_id(),user_id,token,callback);
        }
    }
}
