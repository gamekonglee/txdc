package bc.yxdc.com.ui.activity.order;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.OrderDetailBean;
import bc.yxdc.com.bean.Order_Result;
import bc.yxdc.com.bean.Order_button;
import bc.yxdc.com.bean.Order_goods;
import bc.yxdc.com.bean.PayResult;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.MyShare;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.user.LoginActivity;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/10/10.
 */

public class OrderDetailActivity extends BaseActivity{
    private static final int TYPE_ORDER_DETAIL = 0;
    private static final int TYPE_ORDER_LIST = 1;
    private static final int TYPE_ALI_PAY = 2;
    private static final int TYPE_WX_PAY = 3;
    private static final int SDK_PAY_FLAG = 5;
    private static final int TYPE_ORDER_CANCEL = 4;
    private static final int TYPE_RECEIVER_ENSURE = 6;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.tv_pay_time)
    TextView tv_pay_time;
    @BindView(R.id.tv_pay_money)
    TextView tv_pay_money;
    @BindView(R.id.tv_consginer)
    TextView tv_consginer;
    @BindView(R.id.tv_mobile)
    TextView tv_mobile;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.lv_goods)
    ListView lv_goods;
    @BindView(R.id.tv_osn)
    TextView tv_osn;
    @BindView(R.id.tv_order_time)
    TextView tv_order_time;
    @BindView(R.id.tv_pay_type)
    TextView tv_pay_type;
    @BindView(R.id.tv_express_type)
    TextView tv_express_type;
    @BindView(R.id.tv_package_type)
    TextView tv_package_type;
    @BindView(R.id.tv_goods_money)
    TextView tv_goods_money;
    @BindView(R.id.tv_express_fee)
    TextView tv_express_fee;
    @BindView(R.id.tv_total_money)
    TextView tv_total_money;
    @BindView(R.id.btn_pay)
    TextView btn_pay;
    @BindView(R.id.btn_cancel)
    TextView btn_cancel;
    @BindView(R.id.btn_connect_store)
    TextView btn_connect;
    @BindView(R.id.btn_shipping)
    TextView btn_shipping;
    @BindView(R.id.btn_receive)
    TextView btn_receive;
    @BindView(R.id.btn_comment)
    TextView btn_comment;
    @BindView(R.id.btn_return)
    TextView btn_returm;
    @BindView(R.id.tv_copy)
    TextView tv_copy;
    @BindView(R.id.tv_coupon)
    TextView tv_coupon;


    private String order_id;
    private QuickAdapter<Order_goods> adapter;
    private int payType;
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            hideLoading();
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须上传到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    Log.d("TAG", "resultStatus=" + resultStatus);
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if ("9000".equals(resultStatus)) {
                        onResume();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            MyToast.show(OrderDetailActivity.this,"支付失败");
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
    private String order_sn;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void resultPay(PayResult result){
        hideLoading();
        int state=2;
//        Intent intent = new Intent(this, OrderDetailActivity.class);

        if(result.result.equals("0")){
            MyToast.show(this, "支付成功");
            onResume();
        }else if(result.result.equals("-2")){
            state=0;
            MyToast.show(this, "支付失败");
        }else {
            state=0;
        }
    }
    @Override
    protected void initData() {
        order_id = getIntent().getStringExtra(Constance.order_id);

        LogUtils.logE("order_id",order_id+"");
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        adapter = new QuickAdapter<Order_goods>(this, R.layout.item_order_goods) {
            @Override
            protected void convert(BaseAdapterHelper helper, Order_goods item) {
                helper.setText(R.id.tv_name,item.getGoods_name()+"");
                if(item.getSpec_key_name()!=null){
                    helper.setText(R.id.tv_attr_num,"规格："+item.getSpec_key_name()+" 数量："+item.getGoods_num());
                }else {
                    helper.setText(R.id.tv_attr_num," 数量："+item.getGoods_num());
                }
                helper.setText(R.id.tv_jiaohuoqi,item.getSku()+"");
                helper.setText(R.id.tv_price,"¥"+item.getGoods_price());
                ImageView iv_img=helper.getView(R.id.iv_img);
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getGoods_id(),iv_img, IssApplication.getImageLoaderOption());
            }
        };
        lv_goods.setAdapter(adapter);
        tv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(tv_osn.getText().toString());
                MyToast.show(OrderDetailActivity.this,"订单号已复制");
            }
        });



    }
    public void onRefresh(){

    }
    @Override
    protected void onResume() {
        super.onResume();
        loadDetail();
    }

    private void loadDetail() {
        misson(TYPE_ORDER_LIST, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                bocang.json.JSONObject res=new bocang.json.JSONObject(response.body().string());
                LogUtils.logE("orderRs",res.getString(Constance.result));
                try {
                    bocang.json.JSONArray jsonArray = res.getJSONArray(Constance.result);
                    final Order_Result orderDetailBean = new Gson().fromJson(jsonArray.getJSONObject(0).toString(), Order_Result.class);
                    if(TextUtils.isEmpty(order_id)){
                        order_id=orderDetailBean.getOrder_id()+"";
                    }
                    if(TextUtils.isEmpty(order_sn)){
                        order_sn = orderDetailBean.getOrder_sn();
                    }
                    LogUtils.logE("orderid",order_id);
                    misson(TYPE_ORDER_DETAIL, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            JSONObject res=new JSONObject(response.body().string());
                            LogUtils.logE("order_detail",res.toString());
                            if(res.getInt(Constance.status)==1){
                                final OrderDetailBean orderDetailBean=new Gson().fromJson(res.getJSONObject(Constance.result).toString(),OrderDetailBean.class);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_pay_money.setVisibility(View.GONE);
                                        tv_pay_time.setVisibility(View.GONE);
                                        if(orderDetailBean.getPay_status()==0){
                                            tv_status.setText("待支付");
                                        }else {
                                            switch (orderDetailBean.getOrder_status()){
                                                case 0:
                                                    tv_status.setText("待确认");
                                                    break;
                                                case 1:
                                                    tv_status.setText("已确认");
                                                    if(orderDetailBean.getShipping_status()==0){
                                                        tv_status.setText("未发货");
                                                    }else if(orderDetailBean.getShipping_status()==1){
                                                        tv_status.setText("已发货");
                                                    }
                                                    break;
                                                case 2:
                                                    tv_status.setText("已收货");
                                                    break;
                                                case 3:
                                                    tv_status.setText("已取消");
                                                    break;
                                                case 4:
                                                    tv_status.setText("已完成");
                                                    break;
                                                case 5:
                                                    tv_status.setText("已作废");
                                                    break;
                                            }

                                        }
                                        tv_consginer.setText(orderDetailBean.getConsignee());
                                        tv_mobile.setText(orderDetailBean.getMobile()+"");
                                        tv_address.setText(orderDetailBean.getProvince()+orderDetailBean.getCity()+orderDetailBean.getDistrict()+orderDetailBean.getAddress());
                                        adapter.replaceAll(orderDetailBean.getOrder_goods());
                                        UIUtils.initListViewHeight(lv_goods);
                                        tv_osn.setText(""+orderDetailBean.getOrder_sn());
                                        tv_order_time.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(orderDetailBean.getAdd_time()*1000L)));

                                        tv_pay_type.setText("线上支付");
                                        tv_express_type.setText("物流配送");
                                        tv_package_type.setText("加logo包装");
                                        tv_goods_money.setText("¥"+orderDetailBean.getGoods_price());
                                        tv_express_fee.setText("+"+orderDetailBean.getShipping_price());
                                        tv_total_money.setText("¥"+orderDetailBean.getOrder_amount());
                                        tv_coupon.setText("-"+orderDetailBean.getCoupon_price());
                                        Order_button order_button=orderDetailBean.getOrder_button();
                                        btn_pay.setVisibility(order_button.getPay_btn()==1?View.VISIBLE:View.GONE);
                                        btn_cancel.setVisibility(order_button.getCancel_btn()==1?View.VISIBLE:View.GONE);
                                        btn_comment.setVisibility(order_button.getComment_btn()==1?View.VISIBLE:View.GONE);
                                        btn_receive.setVisibility(order_button.getReceive_btn()==1?View.VISIBLE:View.GONE);
                                        btn_shipping.setVisibility(order_button.getShipping_btn()==1?View.VISIBLE:View.GONE);
//                                        btn_shipping.setVisibility(View.VISIBLE);
                                        btn_returm.setVisibility(order_button.getReturn_btn()==1?View.VISIBLE:View.GONE);

                                        btn_pay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showPaySelectDialog(orderDetailBean.getOrder_sn()+"");
                                            }
                                        });
                                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                UIUtils.showSingleWordDialog(OrderDetailActivity.this, "确定要取消订单吗？", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        cancelOrder(orderDetailBean.getOrder_id());
                                                    }
                                                });
                                            }
                                        });
                                        btn_shipping.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent=new Intent(OrderDetailActivity.this, MyExpressActivity.class);
                                                intent.putExtra(Constance.shipping_code,orderDetailBean.getShipping_code()+"");
                                                intent.putExtra(Constance.invoice_no,orderDetailBean.getInvoice_no()+"");
                                                intent.putExtra(Constance.order_id,orderDetailBean.getOrder_id()+"");
                                                intent.putExtra(Constance.url,NetWorkConst.IMAGE_URL+orderDetailBean.getOrder_goods().get(0).getGoods_id());
                                                startActivity(intent);
                                            }
                                        });
                                        btn_comment.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent=new Intent(OrderDetailActivity.this, CommentActivity.class);
                                                intent.putExtra(Constance.img,orderDetailBean.getOrder_goods().get(0).getOriginal_img());
                                                intent.putExtra(Constance.order_id,orderDetailBean.getOrder_id()+"");
                                                intent.putExtra(Constance.goods_id,orderDetailBean.getOrder_goods().get(0).getGoods_id());
                                                intent.putExtra(Constance.rec_id,orderDetailBean.getOrder_goods().get(0).getRec_id()+"");
                                                startActivity(intent);
                                            }
                                        });
                                        btn_receive.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                UIUtils.showSingleWordDialog(OrderDetailActivity.this, "要确定收货吗？", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
//                                                        mPosition=helper.getPosition();
                                                        misson(TYPE_RECEIVER_ENSURE, new Callback() {
                                                            @Override
                                                            public void onFailure(Call call, IOException e) {

                                                            }

                                                            @Override
                                                            public void onResponse(Call call, final Response response) throws IOException {
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            JSONObject res=new JSONObject(response.body().string());
                                                                            MyToast.show(OrderDetailActivity.this,res.getString(Constance.msg));
                                                                            if(res.getInt(Constance.status)==1){
                                                                                onRefresh();
                                                                            }
                                                                        } catch (IOException e) {
                                                                            e.printStackTrace();
                                                                        }

                                                                    }
                                                                });

                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });



                                    }
                                });

                            }
                        }
                    });
                }catch (Exception e){
                    startActivity(new Intent(OrderDetailActivity.this,LoginActivity.class));
                }
            }
        });
    }

    private void cancelOrder(int order_id) {
        misson(TYPE_ORDER_CANCEL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    MyToast.show(OrderDetailActivity.this,res.getString(Constance.msg));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onResume();
                        }
                    });
                }

            }
        });
    }

    private void showPaySelectDialog(final String orderId) {
        order_sn=orderId;
        final Dialog dialog=UIUtils.showBottomInDialog(this, R.layout.dialog_pay_select,UIUtils.dip2PX(200));
        LinearLayout ll_alipay=dialog.findViewById(R.id.ll_alipay);
        LinearLayout ll_wx=dialog.findViewById(R.id.ll_wxpay);
        final ImageView iv_alipay=dialog.findViewById(R.id.iv_alipay);
        final ImageView iv_wx=dialog.findViewById(R.id.iv_wxpay);
        Button btn_submit=dialog.findViewById(R.id.btn_submit);
        payType = 0;
        ll_alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(payType ==0){
                    return;
                }
                payType =0;
                iv_alipay.setImageResource(R.mipmap.page_icon_round_seleted);
                iv_wx.setImageResource(R.mipmap.page_icon_round_default);
            }
        });
        ll_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(payType ==1){
                    return;
                }
                payType =1;
                iv_alipay.setImageResource(R.mipmap.page_icon_round_default);
                iv_wx.setImageResource(R.mipmap.page_icon_round_seleted);
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShowDialog(true);
                setShowDialog("正在付款中!");
                showLoading();
                dialog.dismiss();
                if(payType ==0){
                    sendAliPayment(orderId);
                }else {
                    sendWxPayment(orderId);
                }
            }
        });
    }

    private void sendWxPayment(String orderId) {
        misson(TYPE_WX_PAY, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String reult=response.body().string();
                LogUtils.logE("wx_result",reult);
                JSONObject wxpayObject=new JSONObject(reult).getJSONObject(Constance.result);
                if(wxpayObject==null){
                    return;
                }
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
                IWXAPI iwxapi= WXAPIFactory.createWXAPI(OrderDetailActivity.this,appid);
                iwxapi.sendReq(request);
            }
        });
    }

    private void sendAliPayment(String orderId) {
        misson(TYPE_ALI_PAY, new Callback() {
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

            }
        });
    }
    private void alipay(final String string) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(OrderDetailActivity.this);
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
        String user_id= MyShare.get(this).getString(Constance.user_id);
        String token=MyShare.get(this).getString(Constance.token);
        if(TextUtils.isEmpty(user_id)||TextUtils.isEmpty(token)){
            MyToast.show(this,"登录状态失效");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        switch (type){
            case TYPE_ORDER_DETAIL:
                OkHttpUtils.getOrderDetail(user_id,token,order_id,callback);
                break;
            case TYPE_ORDER_LIST:
                OkHttpUtils.getOrderList(user_id,token,"0","1",callback);
                break;
            case TYPE_ALI_PAY:
                OkHttpUtils.getAliPay(user_id,token,order_sn,callback);
                break;
            case TYPE_WX_PAY:
                OkHttpUtils.getWxPay(user_id,token,order_sn,callback);
                break;
            case TYPE_ORDER_CANCEL:
                OkHttpUtils.cancelOrder(user_id,token,order_id,callback);
                break;
            case TYPE_RECEIVER_ENSURE:
                OkHttpUtils.orderConfirm(user_id,token, Integer.parseInt(order_id),callback);
                break;
        }

    }
}
