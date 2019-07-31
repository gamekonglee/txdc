package bc.yxdc.com.ui.activity.buy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.io.IOException;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.PayResult;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.order.OrderDetailActivity;
import bc.yxdc.com.ui.activity.home.MainActivity;
import bc.yxdc.com.ui.activity.user.LoginActivity;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/9/27.
 */

public class PayHomeActivity extends BaseActivity {
    private static final int TYPE_ALI = 0;
    private static final int TYPE_WX = 1;
    private static final int TYPE_BANK = 2;
    private static final int SDK_PAY_FLAG = 6;
    @BindView(R.id.tv_money)
    TextView tv_money;
    @BindView(R.id.rg_pay_type)
    RadioGroup rg_pay_type;
    @BindView(R.id.btn_pay)
    Button btn_pay;
    @BindView(R.id.rb_alipay)RadioButton rb_ali;
    @BindView(R.id.rb_wx_pay)RadioButton rb_wx;

    private String order;
    private String money;
    private int type;
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==SDK_PAY_FLAG){
            PayResult payResult = new PayResult((String) msg.obj);

            String resultInfo = payResult.getResult();// 同步返回需要验证的信息
            String resultStatus = payResult.getResultStatus();
            Log.d("TAG", "resultStatus=" + resultStatus);
            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
//                PgyCrashManager.reportCaughtException(PayHomeActivity.this,new Exception("resultStatus："+resultStatus));
            if (resultStatus.equals("9000")) {
//                    page = 1;
//                    sendOrderList(page);
                final Intent intent = new Intent(PayHomeActivity.this, PaySuccessActivity.class);
                intent.putExtra(Constance.order_id,order);
                startActivityForResult(intent,200);
//                    intent.putExtra(Constance.order, goodses.getJSONObject(mPosition).toJSONString());
//                    intent.putExtra(Constance.state, 0);
            } else {
                final Intent intent = new Intent(PayHomeActivity.this, OrderDetailActivity.class);
//                intent.putExtra(Constance.order_id,order);
                MyToast.show(PayHomeActivity.this, "支付失败");
                startActivityForResult(intent,200);
                finish();
                // 判断resultStatus 为非"9000"则代表可能支付失败
                // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
//                if (TextUtils.equals(resultStatus, "8000")) {
//                    MyToast.show(PayHomeActivity.this, "支付结果确认中");
//
//                } else {
//                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
//                    MyToast.show(PayHomeActivity.this, "支付失败");
//                }
            }
                    }
            }
        };
    private int pay_type;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void resultPay(PayResult result){
        int state=2;
        Intent intent = new Intent(this, OrderDetailActivity.class);
//        intent.putExtra(Constance.order_id,order);
//        PgyCrashManager.reportCaughtException(PayHomeActivity.this,new Exception("resultStatusWx："+result.result));
        if(result.result.equals("0")){
            MyToast.show(this, "支付成功");
            intent = new Intent(PayHomeActivity.this, PaySuccessActivity.class);
            startActivityForResult(intent,200);
        }else if(result.result.equals("-2")){
            state=0;
            MyToast.show(this, "支付失败");
            intent.putExtra(Constance.state, state);
            startActivity(intent);
            finish();
        }else {
            state=0;
            intent.putExtra(Constance.state, state);
            startActivity(intent);
            finish();
        }


    }
    @Override
    protected void initData() {
        order = getIntent().getStringExtra(Constance.order);
        money = getIntent().getStringExtra(Constance.money);
        pay_type = getIntent().getIntExtra(Constance.type,0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_pay_home);
        setStatuTextColor(this, Color.WHITE);
//        setFullScreenColor(Color.TRANSPARENT,this);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        tv_money.setText("¥"+money);
        rg_pay_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_alipay:
                        type = TYPE_ALI;
                        break;
                    case R.id.rb_wx_pay:
                        type=TYPE_WX;
                        break;
                    case R.id.rb_bank:
                        type=TYPE_BANK;
                        break;
                }
            }
        });
        if(pay_type==0){
            rb_ali.setChecked(true);
        }else {
            rb_wx.setChecked(true);
        }
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type){
                    case TYPE_ALI:
                         misson(TYPE_ALI, new Callback() {
                             @Override
                             public void onFailure(Call call, IOException e) {

                             }

                             @Override
                             public void onResponse(Call call, Response response) throws IOException {
                                 String reult=response.body().string();
                                 try {
                                     org.json.JSONObject jsonObject=new org.json.JSONObject(reult);
                                     alipay(jsonObject.getString(Constance.result));
                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }
                                 LogUtils.logE("ali_result",reult);


                             }
                         });
                        break;
                    case TYPE_WX:
                        misson(TYPE_WX, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String reult=response.body().string();
                                LogUtils.logE("wx_result",reult);
                                JSONObject wxpayObject=new JSONObject(reult).getJSONObject(Constance.result);
                                String appid = wxpayObject.getString("appid");
                                String mch_id = wxpayObject.getString("partnerid");
                                String nonce_str = wxpayObject.getString("noncestr");
                                String packages = wxpayObject.getString("package");
                                String prepay_id = wxpayObject.getString("prepayid");
                                String sign = wxpayObject.getString("sign");
                                String timestamp = wxpayObject.getString("timestamp");
                                PayReq request = new PayReq();
                                request.appId=appid;
                                request.partnerId=mch_id;
                                request.prepayId=prepay_id;
                                request.packageValue=packages;
                                request.nonceStr=nonce_str;
                                request.timeStamp=timestamp;
                                request.sign=sign;
                                IWXAPI iwxapi= WXAPIFactory.createWXAPI(PayHomeActivity.this,appid);
                                iwxapi.sendReq(request);
                            }
                        });
                        break;
                    case TYPE_BANK:
                        break;

                }
            }
        });
//        misson(3, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
////                JSONObject jsonObject=new JSONObject(response.body().string());
//                LogUtils.logE("res",response.body().string());
//            }
//        });
    }

    private void alipay(final String string) {


        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(PayHomeActivity.this);
                String result = alipay.pay(string,true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @Override
    public void getData(int type, Callback callback) {
        String userid= MyShare.get(this).getString(Constance.user_id);
        String token=MyShare.get(this).getString(Constance.token);
        if(TextUtils.isEmpty(userid)||TextUtils.isEmpty(token)){
            MyToast.show(this,"登录状态已失效，请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        if(type==TYPE_WX){
            OkHttpUtils.getWxPay(userid,token,order,callback);
        }else if(type==TYPE_ALI){
            OkHttpUtils.getAliPay(userid,token,order,callback);
        }else {
            LogUtils.logE("order_",order);
            OkHttpUtils.getOrderDetail(userid,token,order,callback);
        }
    }

    @Override
    public void goBack(View v) {
        UIUtils.showSingleWordDialog(this, "确定要取消支付吗？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PayHomeActivity.this,OrderDetailActivity.class);
                int state=0;
                intent.putExtra(Constance.state, state);
                startActivity(intent);
                PayHomeActivity.super.goBack(v);
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200&&resultCode==200){
            MainActivity.currentTab=0;
            finish();
        }
    }
}
