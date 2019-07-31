package bc.yxdc.com.ui.activity.user;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import bc.yxdc.com.bean.MessageLogsticsBean;
import bc.yxdc.com.bean.OrderDetailBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.order.MyExpressActivity;
import bc.yxdc.com.ui.activity.order.OrderDetailActivity;
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

public class MessageLogiscsActivity extends BaseActivity {
    int p=1;
    String category="1";
    private String token;
    private QuickAdapter adapter;
    private List<MessageLogsticsBean> messageLogsticsBeans;
    private RelativeLayout rl_no_msg;
    private TextView tv_titile;

    @Override
    protected void initData() {
        category=getIntent().getStringExtra(Constance.category);
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_message_logiscs);
        final ListView lv_log=findViewById(R.id.lv_logistics);
        rl_no_msg = findViewById(R.id.rl_no_msg);
        tv_titile = findViewById(R.id.tv_title);
        if(category.equals("0")){
            tv_titile.setText("消息提醒");
        }else {
            tv_titile.setText("物流消息");
        }
        adapter = new QuickAdapter<MessageLogsticsBean>(this, R.layout.item_logistics_message) {
            @Override
            protected void convert(BaseAdapterHelper helper, MessageLogsticsBean item) {
                helper.setText(R.id.tv_message,item.getData().getTitle());
                helper.setText(R.id.tv_time, DateUtils.getStrTime02(item.getData().getPost_time()+""));
                helper.setText(R.id.tv_name,item.getData().getDiscription());
                ImageView iv_img=helper.getView(R.id.iv_img);
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getData().getGoods_id(),iv_img, IssApplication.getImageLoaderOption());
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
                messageLogsticsBeans=new Gson().fromJson(jsonObject.getJSONArray(Constance.result).toString(),new TypeToken<List<MessageLogsticsBean>>(){}.getType());
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
                if(category.equals("1")){
                    Intent intent=new Intent(MessageLogiscsActivity.this, OrderDetailActivity.class);
                    intent.putExtra(Constance.order_id,messageLogsticsBeans.get(position).getData().getOrder_id()+"");
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
