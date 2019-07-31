package bc.yxdc.com.ui.activity.buy;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.AddressList;
import bc.yxdc.com.bean.CartList;
import bc.yxdc.com.bean.CouponList;
import bc.yxdc.com.bean.CouponMineBean;
import bc.yxdc.com.bean.Logistics;
import bc.yxdc.com.bean.OrderConfirmBean;
import bc.yxdc.com.bean.OrderConfirmBuyNowBean;
import bc.yxdc.com.bean.UserAddress;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.MyShare;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.listener.ILogisticsChooseListener;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.user.AddressAddActivity;
import bc.yxdc.com.ui.activity.user.AddressListActivity;
import bc.yxdc.com.ui.activity.user.LoginActivity;
import bc.yxdc.com.ui.view.popwindow.SelectLogisticsPopWindow;
import bc.yxdc.com.utils.AppUtils;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyToast;
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
 * Created by gamekonglee on 2018/9/15.
 */

public class ConfirmOrderActivity extends BaseActivity{
    private static final int TYPE_GET_CONFIRM = 0;
    private static final int TYPE_SUBMIT = 1;
    private static final int REQUEST_HAS_ADDRESS = 300;
    private static final int REQUEST_NO_ADDRESS = 200;
    private static final int TYPE_BUY_NOW = 2;
    @BindView(R.id.tv_add_address)
    TextView tv_add_address;
    @BindView(R.id.lv_goods)
    ListView lv_goods;
    @BindView(R.id.rg_wrap)
    RadioGroup rg_wrap;
    @BindView(R.id.rg_express)
    RadioGroup rg_express;
    @BindView(R.id.rg_pay_type)
    RadioGroup rg_type;
    @BindView(R.id.tv_total_money)
    TextView tv_total_m;
    @BindView(R.id.btn_submit_order)
    Button btn_submit;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.rl_address)
    RelativeLayout rl_address;
    @BindView(R.id.tv_consginer)
    TextView tv_consginer;
    @BindView(R.id.tv_mobile)
    TextView tv_mobile;
    @BindView(R.id.rl_address_select)
    RelativeLayout rl_address_select;
    @BindView(R.id.view_address)
    View view_address;
    @BindView(R.id.tv_coupon_name)
    TextView tv_coupon_name;
    @BindView(R.id.rl_coupon)
    RelativeLayout rl_coupon;
    @BindView(R.id.tv_total_price)
    TextView tv_total_price;
    @BindView(R.id.et_mark)
    EditText et_mark;
    @BindView(R.id.rl_logistics)
    View rl_logistics;
    @BindView(R.id.fl_main)
    View main_ll;
    @BindView(R.id.logistic_title_tv)
    TextView logistic_title_tv;
    @BindView(R.id.loginstic_tv)
    TextView loginstic_tv;
    @BindView(R.id.loginstic_phone_tv)
    TextView loginstic_phone_tv;
    @BindView(R.id.loginstic_address_tv)
    TextView loginstic_address_tv;

    private int wrap_type;
    private int express_type;
    private int pay_type=0;
    private List<CartList> goodsList;
    private QuickAdapter<CartList> adapter;
    private String user_id;
    private String token;
    private String mAddressId;
    private OrderConfirmBean orderConfirmBean;
    private boolean hasAddress;
    private boolean isbuy;
    private OrderConfirmBuyNowBean orderConfirmBuyNowBean;
    private String item_id;
    private QuickAdapter<CouponMineBean> couponAdapter;
    private List<CouponMineBean> couponLists;
    private String goods_id;
    private String count;
    private double couponMoney=0.0;
    private double totalPrice;
    private boolean hasSelectCp;
    DecimalFormat df=new DecimalFormat("#####.00");
    private SelectLogisticsPopWindow mPopWindow;

    @Override
    protected void initData() {
        JSONArray goods= (JSONArray) getIntent().getSerializableExtra(Constance.goods);
        goodsList = new ArrayList<>();
//        for(int i=0;i<goods.length();i++){
//            goodsList.add(new Gson().fromJson(goods.getJSONObject(i).toString(),CartList.class));
//        }
        if(getIntent().hasExtra(Constance.isbuy)){
            isbuy = getIntent().getBooleanExtra(Constance.isbuy,false);
//            String result=getIntent().getStringExtra(Constance.result);
//            LogUtils.logE("orderConfirm",result);
//            orderConfirmBuyNowBean = new Gson().fromJson(result,OrderConfirmBuyNowBean.class);
            item_id = getIntent().getStringExtra(Constance.item_id);
            goods_id = getIntent().getStringExtra(Constance.goods_id);
            count = getIntent().getStringExtra(Constance.count);

        }
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_confirm_order);
//        setStatuTextColor(this, Color.WHITE);
//        setFullScreenColor(Color.TRANSPARENT,this);
        ButterKnife.bind(this);
        wrap_type = 0;
        express_type = 1;
        pay_type = 0;
        tv_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rg_wrap.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_has_logo:
                        wrap_type=0;
                        break;
                    case R.id.rb_no_logo:
                        wrap_type=1;
                        break;
                }
            }
        });
        rg_express.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_express:
                        express_type=0;
                        rl_logistics.setVisibility(View.GONE);
                        break;
                    case R.id.rb_logistics:
                        express_type=1;
                        rl_logistics.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });
        rg_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_alipay:
                        pay_type=0;
                        break;
                    case R.id.rb_wx_pay:
                        pay_type=1;
                        break;
                    case R.id.rb_bank:
                        pay_type=2;
                        break;
                }
            }
        });
        adapter = new QuickAdapter<CartList>(this, R.layout.item_order_goods) {
            @Override
            protected void convert(BaseAdapterHelper helper, CartList item) {
                helper.setText(R.id.tv_name,item.getGoods_name()+"");
                if(item.getSpec_key_name()!=null){
                    helper.setText(R.id.tv_attr_num,"规格："+item.getSpec_key_name()+" 数量："+item.getGoods_num());
                }else {
                    helper.setText(R.id.tv_attr_num," 数量："+item.getGoods_num());
                }
                String sku=item.getGoods().getSku();
                if(TextUtils.isEmpty(sku)||sku==null||sku.equals("null")){
                    sku="现货";
                }
                helper.setText(R.id.tv_jiaohuoqi,sku+"");
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getGoods_id(), (ImageView) helper.getView(R.id.iv_img));
                helper.setText(R.id.tv_price,"¥"+item.getGoods_price());
            }
        };
        lv_goods.setAdapter(adapter);
        user_id = MyShare.get(this).getString(Constance.user_id);
        token = MyShare.get(this).getString(Constance.token);
        if(isbuy){
                getBuyNow();
        }else {
                loadConfirmInfo();
        }

//        tv_add_address.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        rl_address_select.setOnClickListener(this);
    }

    private void getBuyNow() {
        misson(TYPE_BUY_NOW, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final JSONObject jsonObject=new JSONObject(response.body().string());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(jsonObject.getInt(Constance.status)==1){
                                orderConfirmBuyNowBean=new Gson().fromJson(jsonObject.getJSONObject(Constance.result).toString(),OrderConfirmBuyNowBean.class);
                                if(isbuy){
                                    if(orderConfirmBuyNowBean==null||orderConfirmBuyNowBean.getUserCartCouponList()==null||orderConfirmBuyNowBean.getUserCartCouponList().size()==0){
                                        tv_coupon_name.setText("暂无可用优惠券");
//                                        rl_coupon.setVisibility(View.GONE);
                                    }else {
                                        rl_coupon.setVisibility(View.VISIBLE);
                                    }
                                }

                                goodsList=orderConfirmBuyNowBean.getCartList();
                                adapter.replaceAll(goodsList);
                                UIUtils.initListViewHeight(lv_goods);
                                UserAddress addressLists = orderConfirmBuyNowBean.getUserAddress();

                                if (addressLists == null) {
                                    hasAddress = false;
                                    tv_add_address.setVisibility(View.VISIBLE);
                                    rl_address.setVisibility(View.GONE);
                                    rl_address_select.setBackgroundColor(getResources().getColor(R.color.theme_red));
                                    view_address.setVisibility(View.GONE);
                                    showAddressAddDialog();
                                } else {
                                    rl_address_select.setBackgroundColor(getResources().getColor(R.color.white));
                                    view_address.setVisibility(View.VISIBLE);
                                    hasAddress=true;
                                    tv_add_address.setVisibility(View.GONE);
                                    rl_address.setVisibility(View.VISIBLE);
                                    tv_address.setText(addressLists.getTotal_address());
                                    tv_consginer.setText(addressLists.getConsignee());
                                    tv_mobile.setText(addressLists.getMobile() + "");
                                    mAddressId = addressLists.getAddress_id()+"";
                                }
                                if (orderConfirmBuyNowBean.getCartPriceInfo()!= null) {
                                    totalPrice = Double.parseDouble(orderConfirmBuyNowBean.getCartPriceInfo().getTotal_fee());
                                    tv_total_m.setText("总计：¥" + df.format(totalPrice));
                                    tv_total_price.setText("¥" + df.format(totalPrice));

                                }
                            }else {
                                MyToast.show(ConfirmOrderActivity.this,jsonObject.getString(Constance.msg));
                                showAddressAddDialog();
                            }

                        }
                    });
            }
        });
    }

    private void loadConfirmInfo() {
        misson(TYPE_GET_CONFIRM, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strResult = response.body().string();
                LogUtils.logE("order_confirm", strResult);
                final JSONObject resultObject = new JSONObject(strResult);
                if (resultObject.getInt(Constance.status) != 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.show(ConfirmOrderActivity.this,resultObject.getString(Constance.msg));
                            if(resultObject.getString(Constance.msg).contains("收货")){
//                                 startActivity(new Intent(ConfirmOrderActivity.this,AddressAddActivity.class));
                                showAddressAddDialog();
                            }else {
                                startActivity(new Intent(ConfirmOrderActivity.this,LoginActivity.class));
                                finish();
                            }

                        }
                    });
                } else {
                    orderConfirmBean = new Gson().fromJson(resultObject.getJSONObject(Constance.result).toString(), OrderConfirmBean.class);

                    goodsList = orderConfirmBean.getCartList();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if(!isbuy){
                                if(orderConfirmBean==null||orderConfirmBean.getCouponList()==null||orderConfirmBean.getCouponList().size()==0){
                                    tv_coupon_name.setText("暂无可用优惠券");
//                                    rl_coupon.setVisibility(View.GONE);
                                }else {
                                    rl_coupon.setVisibility(View.VISIBLE);
                                }
                            }
                            if (goodsList != null && goodsList.size() > 0) {
                                adapter.replaceAll(goodsList);
                                UIUtils.initListViewHeight(lv_goods);
                            }
                            AddressList addressLists = orderConfirmBean.getAddressList();
                            if (addressLists == null) {
                                hasAddress = false;
                                tv_add_address.setVisibility(View.VISIBLE);
                                rl_address.setVisibility(View.GONE);
                                rl_address_select.setBackgroundColor(getResources().getColor(R.color.theme_red));
                                view_address.setVisibility(View.GONE);
//                                    showAddressAddDialog();
                            } else {
                                rl_address_select.setBackgroundColor(getResources().getColor(R.color.white));
                                view_address.setVisibility(View.VISIBLE);
                                hasAddress=true;
                                tv_add_address.setVisibility(View.GONE);
                                rl_address.setVisibility(View.VISIBLE);
                                tv_address.setText(addressLists.getTotal_address());
                                tv_consginer.setText(addressLists.getConsignee());
                                tv_mobile.setText(addressLists.getMobile() + "");
                                mAddressId = addressLists.getAddress_id();
                            }
                            if (orderConfirmBean.getTotalPrice() != null) {
                                totalPrice=orderConfirmBean.getTotalPrice().getZj();
                                tv_total_m.setText("总计：¥" + df.format(totalPrice));
                                tv_total_price.setText("¥" + df.format(totalPrice));
                            }

                        }
                    });

                }
            }
        });
    }

    private void showAddressAddDialog() {
        final Dialog dialog=new Dialog(this,R.style.customDialog);
        dialog.setContentView(R.layout.dialog_address_add);
        TextView tv_cancel=dialog.findViewById(R.id.tv_cancel);
        TextView tv_ensure=dialog.findViewById(R.id.tv_ensure);
        dialog.setCancelable(false);
        tv_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivityForResult(new Intent(ConfirmOrderActivity.this, AddressAddActivity.class),REQUEST_NO_ADDRESS);
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }
    /**
     * 选择物流公司
     */
    public void selectLogistic() {
        mPopWindow = new SelectLogisticsPopWindow(this, this);
        mPopWindow.onShow(main_ll);
        mPopWindow.setListener(new ILogisticsChooseListener() {
            @Override
            public void onLogisticsChanged(Logistics object) {
                if (AppUtils.isEmpty(object))
                    return;
                logistic_title_tv.setVisibility(View.GONE);
                loginstic_tv.setVisibility(View.VISIBLE);
                loginstic_phone_tv.setVisibility(View.VISIBLE);
                loginstic_address_tv.setVisibility(View.VISIBLE);
                Logistics mlogistics = new Logistics();
                mlogistics.setAddress(object.getAddress());
                mlogistics.setTel(object.getTel());
                mlogistics.setName(object.getName());
                loginstic_tv.setText("物流公司："+mlogistics.getName());
                loginstic_phone_tv.setText(""+mlogistics.getTel());
                loginstic_address_tv.setText("提货地址: " + mlogistics.getAddress());
                shipping_code="物流公司："+mlogistics.getName()+"物流电话："+mlogistics.getTel()+"提货地址: " + mlogistics.getAddress();
            }
        });
    }

    @OnClick({R.id.rl_coupon,R.id.rl_logistics})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_address_select:
                if(hasAddress){
                    Intent intent=new Intent(ConfirmOrderActivity.this, AddressListActivity.class);
                    intent.putExtra(Constance.isSELECTADDRESS,true);
                    startActivityForResult(intent,REQUEST_HAS_ADDRESS);
                }else {
                    startActivityForResult(new Intent(ConfirmOrderActivity.this, AddressAddActivity.class),REQUEST_NO_ADDRESS);
                }
                break;
            case R.id.btn_submit_order:
                submitOrder();
                break;
            case R.id.rl_coupon:
                if(isbuy){
                    if(orderConfirmBuyNowBean==null||orderConfirmBuyNowBean.getUserCartCouponList()==null||orderConfirmBuyNowBean.getUserCartCouponList().size()==0){
                        return;
                    }
                }else {
                if(orderConfirmBean==null||orderConfirmBean.getCouponList()==null||orderConfirmBean.getCouponList().size()==0){
                    return;
                }
                }
                final Dialog dialog=UIUtils.showBottomInDialog(this,R.layout.dialog_coupon);
                TextView tv_close=dialog.findViewById(R.id.tv_close);
                final ListView lv_coupon=dialog.findViewById(R.id.lv_coupons);
                if(couponAdapter==null)couponAdapter = new QuickAdapter<CouponMineBean>(ConfirmOrderActivity.this, R.layout.item_order_coupons) {
                    @Override
                    protected void convert(final BaseAdapterHelper helper, CouponMineBean item) {
                        helper.setText(R.id.tv_name,item.getName()+"");
                        int p=getCurrentP();
                        if(p==helper.getPosition()){
                            helper.setBackgroundRes(R.id.iv_check,R.mipmap.page_icon_round_seleted);
                        }else {
                            helper.setBackgroundRes(R.id.iv_check,R.mipmap.page_icon_round_default);
                        }
                    }
                };
                lv_coupon.setAdapter(couponAdapter);
                if(!hasSelectCp){
                    couponAdapter.setCurrentP(-1);
                }
                lv_coupon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        hasSelectCp=true;
                        if(couponAdapter.getCurrentP()==position){
                            couponAdapter.setCurrentP(-1);
                            tv_coupon_name.setText("");
                            tv_total_m.setText("总计：¥"+(df.format(totalPrice)));
                            couponMoney=0;
                        }else {
                            couponAdapter.setCurrentP(position);
                            coupon_id=couponLists.get(position).getId()+"";
                            couponMoney = Double.parseDouble(couponLists.get(position).getMoney());
                            tv_coupon_name.setText(couponLists.get(position).getName()+"(减"+couponMoney+"元)");
                            tv_total_m.setText("总计：¥"+df.format(totalPrice-couponMoney));
                        }
                        couponAdapter.notifyDataSetChanged();
                        dialog.dismiss();

                    }
                });
                if(isbuy){
                 couponLists=orderConfirmBuyNowBean.getUserCartCouponList();
                }else {
                couponLists = orderConfirmBean.getCouponList();
                }
//                CouponMineBean couponMineBean=new CouponMineBean();
//                couponMineBean.setName("测试");
//                couponLists.add(couponMineBean);
//                couponLists.add(couponMineBean);
//                couponLists.add(couponMineBean);
                couponAdapter.replaceAll(couponLists);
                tv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        case R.id.rl_logistics:
            selectLogistic();
            break;
        }
    }

    private void submitOrder() {
        if(TextUtils.isEmpty(user_id)){
            MyToast.show(ConfirmOrderActivity.this,"登录状态失效");
            startActivity(new Intent(ConfirmOrderActivity.this, LoginActivity.class));
            finish();
            return;
        }
        if(express_type==0&&TextUtils.isEmpty(mAddressId)){
            MyToast.show(ConfirmOrderActivity.this,"请填写并选择收货地址");
            return;
        }
        if(express_type==1&&TextUtils.isEmpty(shipping_code)){
            MyToast.show(ConfirmOrderActivity.this,"请填写并选择物流地址");
            return;
        }
//        user_note=wrap_type==0?"加logo包装":"不加logo包装";
        user_note=et_mark.getText().toString();
        if(express_type==1){
            user_note=user_note+shipping_code;
        }
        misson(TYPE_SUBMIT, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                LogUtils.logE("submit",result);
                final JSONObject object=new JSONObject(result);
                if(object.getInt(Constance.status)==1){
                    Intent intent=new Intent(ConfirmOrderActivity.this,PayHomeActivity.class);
                    intent.putExtra(Constance.order,object.getString(Constance.result));
                    if(isbuy){
                        intent.putExtra(Constance.money,(Double.parseDouble(orderConfirmBuyNowBean.getCartPriceInfo().getTotal_fee())-couponMoney)+"");
                    }else {
                        intent.putExtra(Constance.money,(orderConfirmBean.getTotalPrice().getZj()-couponMoney)+"");
                    }
                    intent.putExtra(Constance.type,pay_type);
                    startActivity(intent);
                    finish();
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.show(ConfirmOrderActivity.this,object.getString(Constance.msg));
                        }
                    });
                }

//                        MyToast.show(ConfirmOrderActivity.this,object.getString(Constance.msg));


            }
        });
    }

    String  act="submit_order";
    String shipping_code="";
    String couponTypeSelect="";
    String coupon_id="";
    String couponCode="";
    String invoice_title="";
    String taxpayer="";
    String user_note="";

    @Override
    public void getData(int type, Callback callback) {
        if(type==TYPE_GET_CONFIRM){
        OkHttpUtils.getOrderConfirm(user_id,token,callback);
        }else if(type==TYPE_SUBMIT){
            if(isbuy){
                CartList cartList=orderConfirmBuyNowBean.getCartList().get(0);
                OkHttpUtils.submitOrderBuyNow(user_id,token,cartList.getGoods_id()+"",item_id+"",cartList.getGoods_num()+"",act,mAddressId,shipping_code,couponTypeSelect,coupon_id,couponCode,invoice_title,taxpayer,user_note,callback);
            }else {
            OkHttpUtils.submitOrder(user_id,token,act,mAddressId,shipping_code,couponTypeSelect,coupon_id,couponCode,invoice_title,taxpayer,user_note,callback);
            }
        }else if(type==TYPE_BUY_NOW){
            String token= bc.yxdc.com.utils.MyShare.get(this).getString(Constance.token);
            String user_id= bc.yxdc.com.utils.MyShare.get(this).getString(Constance.user_id);
            if(TextUtils.isEmpty(token)||TextUtils.isEmpty(user_id)){
                MyToast.show(this,"登录状态失效");
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            OkHttpUtils.buyGoodsNow(user_id,token,Integer.parseInt(goods_id),item_id,Integer.parseInt(count),callback);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_NO_ADDRESS){
            if(data!=null&&data.hasExtra(Constance.address_id)&&resultCode==200){
                    tv_address.setText(data.getStringExtra(Constance.address));
                    tv_consginer.setText(data.getStringExtra(Constance.consignee));
                    tv_mobile.setText(data.getStringExtra(Constance.mobile));
                    mAddressId = data.getStringExtra(Constance.address_id);
            }
            if (isbuy){
                getBuyNow();
            }else {
                loadConfirmInfo();
            }
        }else  if(requestCode==REQUEST_HAS_ADDRESS&&resultCode==200){
            if(data!=null&&data.hasExtra(Constance.address_id)){
                tv_address.setText(data.getStringExtra(Constance.address));
                tv_consginer.setText(data.getStringExtra(Constance.consignee));
                tv_mobile.setText(data.getStringExtra(Constance.mobile));
                mAddressId = data.getStringExtra(Constance.address_id);
            }else {
                showAddressAddDialog();
            }
        }else if (requestCode == Constance.FROMLOG && !AppUtils.isEmpty(data)) {
            Logistics mlogistics = (Logistics) data.getSerializableExtra(Constance.logistics);
            logistic_title_tv.setVisibility(View.GONE);
            loginstic_tv.setVisibility(View.VISIBLE);
            loginstic_phone_tv.setVisibility(View.VISIBLE);
            loginstic_address_tv.setVisibility(View.VISIBLE);
            loginstic_tv.setText(mlogistics.getName());
            loginstic_phone_tv.setText(mlogistics.getTel());
            loginstic_address_tv.setText("提货地址: " + mlogistics.getAddress());
        }
    }

}
