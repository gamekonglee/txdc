package bc.yxdc.com.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alipay.sdk.app.PayTask;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.bean.GoodsBean;
import bc.yxdc.com.bean.Order_Result;
import bc.yxdc.com.bean.Order_button;
import bc.yxdc.com.bean.Order_goods;
import bc.yxdc.com.bean.PayResult;
import bc.yxdc.com.bean.PrepayIdInfo;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.order.OrderDetailActivity;
import bc.yxdc.com.ui.activity.goods.SelectGoodsActivity;
import bc.yxdc.com.ui.activity.order.CommentActivity;
import bc.yxdc.com.ui.activity.order.MyExpressActivity;
import bc.yxdc.com.ui.activity.user.LoginActivity;
import bc.yxdc.com.ui.view.EndOfListView;
import bc.yxdc.com.ui.view.PMSwipeRefreshLayout;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/10/10.
 */

public class OrderFragment extends OrderBaseFragment implements SwipeRefreshLayout.OnRefreshListener, EndOfListView.OnEndOfListListener, OnItemClickListener, AdapterView.OnItemClickListener {
    private static final int TYPE_ORDER_LIST = 1;
    private static final int TYPE_RECEIVER_ENSURE = 3;
    private static final int TYPE_DELETE = 4;
    private static final int TYPE_ALI_PAY = 5;
    private static final int SDK_PAY_FLAG = 4223;
    private static final int TYPE_WX_PAY = 6;
    public List<String> list = new ArrayList<String>();
    public int flag;
    public Boolean isSearch=false;
    private PMSwipeRefreshLayout mPullToRefreshLayout;
    private QuickAdapter mProAdapter;
    private EndOfListView order_sv;
    public int page = 1;

    private View mNullView;
    private View mNullNet;
    private Button mRefeshBtn;
    private TextView mNullNetTv;
    private TextView mNullViewTv;
    private int per_pag = 12;
    //    private ProgressBar pd;
    public int mPosition;
    private ImageView iv;
    private Button go_btn;
    private Boolean mIsUpdate = false;
    //    private int mProductAgio=80;
    private int mProductPosition = 0;
    private JSONArray mProductArray;
    private float mDiscount;
    private float mProductDiscount=0;//返回产品打折范围
    private int payType;
    private PrepayIdInfo bean;
    private String mTotal;
    private GridView priductGridView;
    private QuickAdapter likeGoods;
    private List<GoodsBean> goodsBeans;
    private LinearLayout ll_like;
    private boolean hasHeader;
    private LinearLayout ll_no_order;
    private boolean isNull;
    private InputMethodManager imm;
    private AlertView mAlertViewExt;
    private EditText etName;
    private InputMethodManager imm02;
    private AlertView mAlertViewExt02;
    private EditText etName02;
    private List<Order_Result> order_results;
    private boolean isBottom;
    private TextView tv_turn_to;
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
                        MyToast.show(getActivity(),"支付失败");
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null){
            list = bundle.getStringArrayList("content");
            flag = bundle.getInt("flag");
        }
        EventBus.getDefault().register(this);
    }




    @Override
    public void onStart() {
        super.onStart();
        page = 1;
        //        pd.setVisibility(View.VISIBLE);
        order_results = new ArrayList<>();
        setShowDialog(true);
        showLoading();
        sendOrderList(page);
    }

    private void sendOrderList(final int page) {
        String userid= MyShare.get(getActivity()).getString(Constance.user_id);
        String token=MyShare.get(getActivity()).getString(Constance.token);
        if(TextUtils.isEmpty(userid)||TextUtils.isEmpty(token)){
            MyToast.show(getActivity(),"登录状态失效");
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
            return;
        }
        LogUtils.logE("flag",flag+"");
        String flagstr="";
        switch (flag){
            case 1:
                flagstr="WAITPAY";
                break;
            case 2:
                flagstr="WAITSEND";
                break;
            case 3:
                flagstr="WAITRECEIVE";
                break;
            case 4:
                flagstr="COMMENTED";
                break;
            case 5:
                flagstr="WAITCCOMMENT";
                break;
        }
        OkHttpUtils.getOrderList(userid,token, flagstr+"",page+"",new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mPullToRefreshLayout!=null
                                )mPullToRefreshLayout.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                bocang.json.JSONObject res=new bocang.json.JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){

                    bocang.json.JSONArray jsonArray=res.getJSONArray(Constance.result);
                    List<Order_Result> temp=new Gson().fromJson(jsonArray.toString(),new TypeToken<List<Order_Result>>(){}.getType());
                    if(temp!=null&&temp.size()>0){
                        isBottom=false;
                        if(page==1){
                            order_results=temp;
                        }else {
                            order_results.addAll(temp);
                        }

                    }else {
                        isBottom = true;
                    }
//                    for(int i=0;i<jsonArray.length();i++){
//                        order_results.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(),Order_Result.class));
//                    }
                    if(getActivity()==null||getActivity().isFinishing()){
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mPullToRefreshLayout!=null)mPullToRefreshLayout.setRefreshing(false);

                            if(ll_no_order!=null&&mPullToRefreshLayout!=null){
                                if(order_results.size()==0){
                                    ll_no_order.setVisibility(View.VISIBLE);
                                    mPullToRefreshLayout.setVisibility(View.GONE);
                                }else {
                                    ll_no_order.setVisibility(View.GONE);
                                    mPullToRefreshLayout.setVisibility(View.VISIBLE);
                                }
                            }
                            if(mProAdapter!=null){
                            mProAdapter.replaceAll(order_results);
//                                UIUtils.initListViewHeight(order_sv);
                            }

                        }
                    });
                }
                LogUtils.logE("res",res.toString());

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fm_order, null);
    }

    @Override
    public void initUI() {
        super.initUI();
        isPrepared = true;
        lazyLoad();
    }

    Boolean isPrepared=false;

    @Override
    protected void initData() {

    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initView();
        page=1;
        order_results = new ArrayList<>();
        sendOrderList(page);
//        onRefresh();
    }

    private void initView() {
        if(getView()==null)return;
        mPullToRefreshLayout = ((PMSwipeRefreshLayout) getView().findViewById(R.id.refresh_view));
        mPullToRefreshLayout.setOnRefreshListener(this);
        order_sv = (EndOfListView) getView().findViewById(R.id.order_sv);
        ll_no_order=getView().findViewById(R.id.ll_no_order);
        tv_turn_to = getView().findViewById(R.id.tv_turn_to);
        tv_turn_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SelectGoodsActivity.class));
            }
        });
//        order_sv.setDivider(null);//去除listview的下划线
        mProAdapter = new QuickAdapter<Order_Result>(getActivity(),R.layout.item_order) {
            @Override
            protected void convert(final BaseAdapterHelper helper, final Order_Result item) {
                helper.setText(R.id.tv_date,new SimpleDateFormat("yyyy-MM-dd").format(new Date(item.getAdd_time()*1000L)));
                helper.setText(R.id.tv_status,item.getOrder_status_detail());
                ListView lv_goods=helper.getView(R.id.lv_goods);
                List<Order_goods> order_goods=item.getOrder_goods();
                HorizontalScrollView hsc=helper.getView(R.id.hscv);
                if(order_goods!=null&&order_goods.size()>0){
                    QuickAdapter<Order_goods> quickAdapter=new QuickAdapter<Order_goods>(getActivity(),R.layout.item_order_goods_2) {
                        @Override
                        protected void convert(BaseAdapterHelper helper, Order_goods item) {
                            helper.setText(R.id.tv_jiaohuoqi,"现货");
                            helper.setText(R.id.tv_name,item.getGoods_name());
                            helper.setText(R.id.tv_attr_name,""+item.getSpec_key_name());
                            helper.setText(R.id.tv_num,"x"+item.getGoods_num());
                            helper.setText(R.id.tv_price,"¥"+item.getGoods_price());
                            ImageLoader.getInstance().displayImage( NetWorkConst.IMAGE_URL+item.getGoods_id(),((ImageView)helper.getView(R.id.iv_img)),IssApplication.getImageLoaderOption());
                        }
                    };
                    lv_goods.setAdapter(quickAdapter);
                    quickAdapter.replaceAll(order_goods);
                    UIUtils.initListViewHeight(lv_goods);
//                    hsc.setVisibility(View.VISIBLE);
//                    helper.setVisible(R.id.ll_order_goods_single,false);
//                    LinearLayout ll_order_img=helper.getView(R.id.ll_order_img);
//                    ll_order_img.removeAllViews();
//                    for(int i=0;i<order_goods.size();i++){
//                        ImageView imageView=new ImageView(getActivity());
//                        ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getOrder_goods().get(i).getGoods_id(),imageView, IssApplication.getImageLoaderOption());
//                        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(UIUtils.dip2PX(75),UIUtils.dip2PX(75));
//                        layoutParams.setMargins(UIUtils.dip2PX(10),0,0,0);
//                        layoutParams.gravity= Gravity.CENTER_VERTICAL;
//                        imageView.setLayoutParams(layoutParams);
//                        ll_order_img.addView(imageView);
//                    }
//                }else {
//                    hsc.setVisibility(View.GONE);
//                    helper.setVisible(R.id.ll_order_goods_single,true);
//
////                    LogUtils.logE("original", NetWorkConst.IMAGE_URL+item.getOrder_goods().get(0).getGoods_id());
//
//                    ImageLoader.getInstance().displayImage( NetWorkConst.IMAGE_URL+item.getOrder_goods().get(0).getGoods_id(),((ImageView)helper.getView(R.id.iv_img)),IssApplication.getImageLoaderOption());
                }else {
                    LogUtils.logE("ordergods","empty");
                }

                setStatus(helper,item.getOrder_button());
                helper.setOnClickListener(R.id.btn_buy_again, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //再次购买

                    }
                });
                helper.setOnClickListener(R.id.btn_pay, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                         Intent intent=new Intent(getActivity(), PayHomeActivity.class);
//                         intent.putExtra(Constance.order,item.getOrder_sn());
//                         intent.putExtra(Constance.money,item.getTotal_amount());
//                         startActivity(intent);
                         mPosition=helper.getPosition();
                         showPaySelectDialog();
                    }
                });
                helper.setOnClickListener(R.id.btn_ensure_receive, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    UIUtils.showSingleWordDialog(getActivity(), "要确定收货吗？", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPosition=helper.getPosition();
                            misson(TYPE_RECEIVER_ENSURE, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, final Response response) throws IOException {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                JSONObject res=new JSONObject(response.body().string());
                                                 MyToast.show(getActivity(),res.getString(Constance.msg));
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
                helper.setOnClickListener(R.id.ll_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    UIUtils.showSingleWordDialog(getActivity(), "确定要删除该订单吗？", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPosition=helper.getPosition();
                            misson(TYPE_DELETE, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {

                                }
                            });
                        }
                    });
                    }
                });

                helper.setText(R.id.tv_order_num,"共"+item.getOrder_goods().size()+"件商品");
                helper.setText(R.id.tv_order_price,"¥"+item.getOrder_amount());
                helper.setVisible(R.id.ll_delete,item.getDeleted()==0?false:true);
                helper.setOnClickListener(R.id.btn_express_receive, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(), MyExpressActivity.class);
                        intent.putExtra(Constance.shipping_code,item.getShipping_code()+"");
                        intent.putExtra(Constance.invoice_no,item.getInvoice_no()+"");
                        intent.putExtra(Constance.order_id,item.getOrder_id()+"");
                        intent.putExtra(Constance.url,NetWorkConst.IMAGE_URL+item.getOrder_goods().get(0).getGoods_id());
                        startActivity(intent);
                    }
                });
                helper.setOnClickListener(R.id.btn_comment, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(),CommentActivity.class);
                        intent.putExtra(Constance.order_id,item.getOrder_id()+"");
                        intent.putExtra(Constance.order_sn,item.getOrder_sn()+"");
                        intent.putExtra(Constance.img,item.getOrder_goods().get(0).getOriginal_img());
                        intent.putExtra(Constance.goods_id,item.getOrder_goods().get(0).getGoods_id()+"");
                        intent.putExtra(Constance.rec_id,item.getOrder_goods().get(0).getRec_id());
                        startActivity(intent);
                    }
                });


            }
        };
        order_sv.setAdapter(mProAdapter);
        order_sv.setOnEndOfListListener(this);
        mNullView = View.inflate(getActivity(),R.layout.empty_page_no_order,null);
        mNullNet = getView().findViewById(R.id.null_net);
        order_sv.setOnCanRefreshListener(new EndOfListView.OnCanRefreshListener() {
            @Override
            public void canRefresh(boolean refesh) {
                if(!refesh){
                    mPullToRefreshLayout.setEnabled(false);
                }else {
                    mPullToRefreshLayout.setEnabled(true);
                }
            }
        });
        order_sv.setOnItemClickListener(this);
//        order_sv.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });
        mRefeshBtn = (Button) mNullNet.findViewById(R.id.refesh_btn);
        mNullNetTv = (TextView) mNullNet.findViewById(R.id.tv);
        mNullViewTv = (TextView) mNullView.findViewById(R.id.tv);
        go_btn = (Button) mNullView.findViewById(R.id.go_btn);
        go_btn.setText("去逛逛");
        iv = (ImageView) mNullView.findViewById(R.id.iv);
//        pd = (ProgressBar) getView().findViewById(R.id.pd);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //拓展窗口
        mAlertViewExt = new AlertView("提示", "请输入价格折扣(不能低于"+mDiscount*10+"折)！", "取消", null, new String[]{"完成"}, getActivity(), AlertView.Style.Alert, this);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alertext_form, null);
        etName = (EditText) extView.findViewById(R.id.etName);
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                //输入框出来则往上移动
                boolean isOpen = imm.isActive();
                mAlertViewExt.setMarginBottom(isOpen && focus ? 120 : 0);
                System.out.println(isOpen);
            }
        });
        mAlertViewExt.addExtView(extView);
        imm02 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //拓展窗口
        mAlertViewExt02 = new AlertView("提示", "请输入价格折扣！(不得低于"+mProductDiscount*10+"折)", "取消", null, new String[]{"完成"}, getActivity(), AlertView.Style.Alert, this);
        ViewGroup extView02 = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.alertext_form, null);
        etName02 = (EditText) extView02.findViewById(R.id.etName);
        etName02.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                //输入框出来则往上移动
                boolean isOpen = imm02.isActive();
                mAlertViewExt02.setMarginBottom(isOpen && focus ? 120 : 0);
                System.out.println(isOpen);
            }
        });
        mAlertViewExt02.addExtView(extView02);
//        pd = (ProgressBar) getActivity().findViewById(R.id.pd);
//        pd.setVisibility(View.VISIBLE);
        mPullToRefreshLayout.setEnabled(true);
    }

    private void setStatus(BaseAdapterHelper helper, Order_button order_status) {

        helper.setVisible(R.id.btn_pay,order_status.getPay_btn()==1?true:false);
        helper.setVisible(R.id.btn_express_receive,order_status.getShipping_btn()==1?true:false);
//        helper.setVisible(R.id.btn_express_receive,true);
        helper.setVisible(R.id.btn_ensure_receive,order_status.getReceive_btn()==1?true:false);
        helper.setVisible(R.id.btn_comment,order_status.getComment_btn()==1?true:false);
//        helper.setVisible(R.id.btn_buy_again,order_status.getPay_btn()!=1?true:false);
        helper.setVisible(R.id.btn_buy_again,false);
//        switch (order_status){
//            case 0:
//                helper.setVisible(R.id.btn_pay,true);
//                break;
//            case 1:
//                break;
//            case 2:
//                helper.setVisible(R.id.btn_express_receive,true);
//                helper.setVisible(R.id.btn_ensure_receive,true);
//                break;
//            case 3:
//                helper.setVisible(R.id.btn_comment,true);
//                helper.setVisible(R.id.btn_buy_again,true);
//                break;
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isPrepared=false;
    }

    @Override
    public void getData(int type, Callback callback) {
        super.getData(type, callback);
        String token=MyShare.get(getActivity()).getString(Constance.token);
        String user_id=MyShare.get(getActivity()).getString(Constance.user_id);
        if(TextUtils.isEmpty(token)||TextUtils.isEmpty(user_id)){
            MyToast.show(getActivity(),"登录状态失效");
            startActivity(new Intent(getActivity(),LoginActivity.class));
            return;
        }
        switch (type){
            case TYPE_RECEIVER_ENSURE:
                OkHttpUtils.orderConfirm(user_id,token,order_results.get(mPosition).getOrder_id(),callback);
                break;
            case TYPE_DELETE:
//                OkHttpUtils.deleteOrder(user_id,token,order_results.get(mPosition).getOrder_id(),callback);
                break;
            case TYPE_ALI_PAY:
                OkHttpUtils.getAliPay(user_id,token,order_results.get(mPosition).getOrder_sn(),callback);
                break;
            case TYPE_WX_PAY:
                OkHttpUtils.getWxPay(user_id,token,order_results.get(mPosition).getOrder_sn(),callback);
                break;
        }
    }
    private void showPaySelectDialog() {
        final String orderId=order_results.get(mPosition).getOrder_id()+"";
        final Dialog dialog=UIUtils.showBottomInDialog(getActivity(), R.layout.dialog_pay_select,UIUtils.dip2PX(200));
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
                PayTask alipay = new PayTask(getActivity());
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
                IWXAPI iwxapi= WXAPIFactory.createWXAPI(getActivity(),appid);
                iwxapi.sendReq(request);
            }
        });
    }


    public static OrderFragment newInstance(List<String> contentList, int flag){
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("content", (ArrayList<String>) contentList);
        bundle.putInt("flag", flag);
        OrderFragment orderFm = new OrderFragment();
        orderFm.setArguments(bundle);
        return orderFm;

    }
    //    @Subscribe (threadMode = ThreadMode.MAIN)
//    public void resultPay(int resultCode){
//        page = 1;
//        sendOrderList(page);
//        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
//        intent.putExtra(Constance.order, goodses.getJSONObject(mPosition).toJSONString());
//        intent.putExtra(Constance.state, 1);
//        getActivity().startActivity(intent);
//    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void resultPay(PayResult result){
        int state=2;
        Intent intent = new Intent(getContext(), OrderDetailActivity.class);

        if(result.result.equals("0")){
            MyToast.show(getContext(), "支付成功");
            page = 1;
            flag=0;
            sendOrderList(page);
            sendPaySuccess();
        }else if(result.result.equals("-2")){
            state=0;
            MyToast.show(getContext(), "支付失败");
            intent.putExtra(Constance.order,new Gson().toJson(order_results.get(mPosition),Order_Result.class));
            intent.putExtra(Constance.state, state);
            startActivity(intent);
        }else {
            state=0;
            intent.putExtra(Constance.order, new Gson().toJson(order_results.get(mPosition),Order_Result.class));
            intent.putExtra(Constance.state, state);
            startActivity(intent);
        }


    }

    private void sendPaySuccess() {
            onRefresh();
    }

    @Override
    public void onRefresh() {
        order_sv.smoothScrollToPosition(0,0);
        page=1;
        order_results=new ArrayList<>();
        sendOrderList(page);
    }

    @Override
    public void onEndOfList(Object lastItem) {
//        if(page==1&&(order_results==null||order_results.size()==0)||order_results!=null&&order_results.size()%per_pag!=0){
        if(page==1&&(order_results==null||order_results.size()==0)||isBottom){
            return;
        }
        page++;
        Log.e("onEnd",""+page);
        sendOrderList(page);
    }

    @Override
    public void onItemClick(Object o, int position) {


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(getActivity(),OrderDetailActivity.class);
        intent.putExtra(Constance.order_id,order_results.get(position).getOrder_id()+"");
        intent.putExtra(Constance.order_sn,order_results.get(position).getOrder_sn()+"");
        startActivity(intent);
    }
}
