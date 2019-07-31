package bc.yxdc.com.ui.activity.buy;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.Order_Result;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.user.LoginActivity;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/9/28.
 */

public class PaySuccessActivity extends BaseActivity{
    @BindView(R.id.tv_osn)
    TextView tv_osn;
    @BindView(R.id.tv_money)
    TextView tv_money;
    @BindView(R.id.tv_mobile)
    TextView tv_mobile;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.btn_turn_to_home)
    Button btn_turn_to_home;
    private String osn;
    private String money;
    private String mobile;
    private String time;

    @Override
    protected void initData() {
        osn = getIntent().getStringExtra(Constance.order_id);
//        money = getIntent().getStringExtra(Constance.money);
//        mobile = getIntent().getStringExtra(Constance.mobile);
//        time = getIntent().getStringExtra(Constance.time);
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_pay_success);
        setStatuTextColor(this, Color.WHITE);
        setFullScreenColor(Color.TRANSPARENT,this);
        ButterKnife.bind(this);


        misson(0, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                JSONObject jsonObject=new JSONObject(response.body().string());

//                final OrderDetailBean orderDetailBean=new Gson().fromJson(jsonObject.getJSONObject(Constance.result).toString(),OrderDetailBean.class);
                bocang.json.JSONObject res=new bocang.json.JSONObject(response.body().string());
                LogUtils.logE("orderRs",res.getString(Constance.result));
                try {
                    bocang.json.JSONArray jsonArray = res.getJSONArray(Constance.result);
                    final Order_Result orderDetailBean = new Gson().fromJson(jsonArray.getJSONObject(0).toString(), Order_Result.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (orderDetailBean != null) {
                                tv_osn.setText("" + orderDetailBean.getOrder_sn());
                                tv_mobile.setText("" + orderDetailBean.getMobile());
                                tv_money.setText("¥" + orderDetailBean.getTotal_amount());
                                tv_time.setText("" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(orderDetailBean.getPay_time() * 1000L)));
                            }
                        }
                    });
                }catch (Exception e){
//                    startActivity(new Intent(PaySuccessActivity.this,LoginActivity.class));
                }
            }
        });
        btn_turn_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(200);
                finish();
            }
        });
    }

    @Override
    public void goBack(View v) {
       setResult(200);
       finish();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void getData(int type, Callback callback) {
        String token= MyShare.get(this).getString(Constance.token);
        String user_id=MyShare.get(this).getString(Constance.user_id);
        if(TextUtils.isEmpty(token)||TextUtils.isEmpty(user_id)){
            MyToast.show(this,"登录状态失效");
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        OkHttpUtils.getOrderList(user_id,token,0+"","1",callback);
    }
}
