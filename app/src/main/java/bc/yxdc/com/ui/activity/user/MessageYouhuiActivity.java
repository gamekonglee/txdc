package bc.yxdc.com.ui.activity.user;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.MessageYouhuiBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.goods.GoodsXsQgActivity;
import bc.yxdc.com.ui.activity.goods.GoodsXsTmActivity;
import bc.yxdc.com.utils.DateUtils;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bocang.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/12/4.
 */

public class MessageYouhuiActivity extends BaseActivity {
    int p=1;
    String category="2";
    private String token;
    private QuickAdapter adapter;
    private List<MessageYouhuiBean> messageLogsticsBeans;
    private RelativeLayout rl_no_msg;

    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_message_youhui);
        final ListView lv_log=findViewById(R.id.lv_logistics);
        rl_no_msg = findViewById(R.id.rl_no_msg);
        adapter = new QuickAdapter<MessageYouhuiBean>(this, R.layout.item_youhui_message) {
            @Override
            protected void convert(BaseAdapterHelper helper, MessageYouhuiBean item) {
                helper.setText(R.id.tv_title,item.getData().getTitle());
                helper.setText(R.id.tv_time, DateUtils.getStrTime(item.getData().getPost_time()+""));
                helper.setText(R.id.tv_content,item.getData().getDiscription());
                ImageView iv_img=helper.getView(R.id.iv_img);
                ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST+item.getData().getCover(),iv_img, IssApplication.getImageLoaderOption());
            }
        };
        lv_log.setAdapter(adapter);
        messageLogsticsBeans = new ArrayList<>();
        misson(0, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res=response.body().string();
                LogUtils.logE("res",res);
                JSONObject jsonObject=new JSONObject(res);
                messageLogsticsBeans=new Gson().fromJson(jsonObject.getJSONArray(Constance.result).toString(),new TypeToken<List<MessageYouhuiBean>>(){}.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.replaceAll(messageLogsticsBeans);
                        if(messageLogsticsBeans==null||messageLogsticsBeans.size()==0){
                            lv_log.setVisibility(View.GONE);
                            rl_no_msg.setVisibility(View.VISIBLE);
                        }else {
                            lv_log.setVisibility(View.VISIBLE);
                            rl_no_msg.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        lv_log.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                OkHttpUtils.getGoodsActivity(messageLogsticsBeans.get(position).getData().getGoods_id(), "", new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        JSONObject jsonObject=new JSONObject(response.body().string());
//                        if(jsonObject.getInt(Constance.status)==1){
//                            JSONObject result=jsonObject.getJSONObject(Constance.result);
//                            if(result!=null){
//                                int type=result.getInt(Constance.prom_type);
//
//                                if(type==1){
//                                Intent intent=new Intent(MessageYouhuiActivity.this, GoodsXsQgActivity.class);
//                                startActivity(intent);
//                                }else if(type==3){
//                                    Intent intent=new Intent(MessageYouhuiActivity.this, GoodsXsTmActivity.class);
//                                    startActivity(intent);
//                                }
//
//                            }
//                        }
//
//                    }
//                });
                int type=messageLogsticsBeans.get(position).getData().getProm_type();

                if(type==1){
                    Intent intent=new Intent(MessageYouhuiActivity.this, GoodsXsQgActivity.class);
                    startActivity(intent);
                }else if(type==3){
                    Intent intent=new Intent(MessageYouhuiActivity.this, GoodsXsTmActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void getData(int type, Callback callback) {
        token = MyShare.get(this).getString(Constance.token);
        OkHttpUtils.getMessage(p,category,token,callback);
    }
}
