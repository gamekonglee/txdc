package bc.yxdc.com.ui.activity.order;

/**
 * Created by gamekonglee on 2018/11/14.
 */

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.ExpressBean;
import bc.yxdc.com.bean.OrderDetailBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/11/13.
 */

public class MyExpressActivity extends BaseActivity {
    private static final int TYPE_EXPRESS = 0;
    private static final int TYPE_ORDER = 1;
    @BindView(R.id.tv_status)TextView tv_status;
    @BindView(R.id.tv_express_name)TextView tv_express_name;
    @BindView(R.id.tv_express_num)TextView tv_express_num;
    @BindView(R.id.lv_express)ListView lv_express;
    @BindView(R.id.iv_img)ImageView iv_img;
    private ExpressBean expressBean;
    private QuickAdapter<ExpressBean.Data> adapter;
    private String shipping_code;
    private String invoice_no;
    private String order_id;
    private String url;
    private int state;

    @Override
    protected void initData() {
        shipping_code = getIntent().getStringExtra(Constance.shipping_code);
        invoice_no = getIntent().getStringExtra(Constance.invoice_no);
        order_id = getIntent().getStringExtra(Constance.order_id);
        url = getIntent().getStringExtra(Constance.url);
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_express);
        ButterKnife.bind(this);
        adapter = new QuickAdapter<ExpressBean.Data>(this, R.layout.item_express) {
            @Override
            protected void convert(BaseAdapterHelper helper, ExpressBean.Data item) {
                helper.setText(R.id.tv_context,item.getContext());
                helper.setText(R.id.tv_date,item.getFtime());
                helper.setTextColor(R.id.tv_context,getResources().getColor(R.color.tv_333333));
                helper.setTextColor(R.id.tv_date,getResources().getColor(R.color.tv_999999));
                helper.setVisible(R.id.view2,true);
                helper.setVisible(R.id.view1,true);
                if(helper.getPosition()==0){
                    helper.getView(R.id.view1).setVisibility(View.INVISIBLE);
                    helper.setTextColor(R.id.tv_context,getResources().getColor(R.color.theme_red));
                    helper.setTextColor(R.id.tv_date,getResources().getColor(R.color.theme_red));
                    helper.setImageResource(R.id.iv_mid,R.mipmap.dot_03);
                }else if(helper.getPosition()==expressBean.getData().size()-1){
                    helper.getView(R.id.view2).setVisibility(View.INVISIBLE);
                    helper.setImageResource(R.id.iv_mid,R.mipmap.dot_02);
                }else {
                    helper.setImageResource(R.id.iv_mid,R.drawable.shape_dot_dddddd);
                }
            }
        };
        lv_express.setAdapter(adapter);
        if(!TextUtils.isEmpty(url)){
            ImageLoader.getInstance().displayImage(url,iv_img, IssApplication.getImageLoaderOption());
        }
        if(TextUtils.isEmpty(shipping_code)||TextUtils.isEmpty(invoice_no)){
            getOrderInfo();
        }else {
            getExpress();
        }
    }

    private void getOrderInfo() {
        misson(TYPE_ORDER, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                LogUtils.logE("order_detail",res.toString());
                if(res.getInt(Constance.status)==1) {
                    final OrderDetailBean orderDetailBean = new Gson().fromJson(res.getJSONObject(Constance.result).toString(), OrderDetailBean.class);
                    shipping_code=orderDetailBean.getShipping_code();
                    invoice_no=orderDetailBean.getInvoice_no();
                    getExpress();
                }

            }
        });
    }

    private void getExpress() {
        misson(TYPE_EXPRESS, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final JSONObject jsonObject=new JSONObject(response.body().string());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        String res="{\"message\":\"ok\",\"nu\":\"3383909746611\",\"ischeck\":\"1\",\"condition\":\"F00\",\"com\":\"shentong\",\"status\":\"200\",\"state\":\"3\",\"data\":[{\"time\":\"2018-11-13 13:12:27\",\"ftime\":\"2018-11-13 13:12:27\",\"context\":\"本人签收-已签收\",\"location\":\"\"},{\"time\":\"2018-11-13 12:31:56\",\"ftime\":\"2018-11-13 12:31:56\",\"context\":\"已到达-广东佛山石湾分部\",\"location\":\"\"},{\"time\":\"2018-11-13 08:15:36\",\"ftime\":\"2018-11-13 08:15:36\",\"context\":\"广东佛山石湾分部-陈阳(13016629397,0757-82260956)-派件中\",\"location\":\"\"},{\"time\":\"2018-11-11 15:42:06\",\"ftime\":\"2018-11-11 15:42:06\",\"context\":\"浙江义乌公司-已装袋发往-广东佛山中转部\",\"location\":\"\"},{\"time\":\"2018-11-11 13:32:08\",\"ftime\":\"2018-11-11 13:32:08\",\"context\":\"浙江义乌公司-已发往-广东佛山中转部\",\"location\":\"\"},{\"time\":\"2018-11-11 13:32:08\",\"ftime\":\"2018-11-11 13:32:08\",\"context\":\"浙江义乌公司-已进行装袋扫描\",\"location\":\"\"},{\"time\":\"2018-11-11 11:25:39\",\"ftime\":\"2018-11-11 11:25:39\",\"context\":\"浙江义乌公司-菜鸟面单2(0579-85456666)-已收件\",\"location\":\"\"}]}\n";
                        expressBean = new Gson().fromJson(jsonObject.toString(),ExpressBean.class);
                        if(expressBean !=null){
                            String stateStr="";
                            try {
                                state = Integer.parseInt(expressBean.getState());

                            }catch (Exception e){
                                state=0;
                            }
                            switch (state){
                                case 0:
                                    stateStr="在途中";
                                    break;
                                case 1:
                                    stateStr="已揽件";
                                    break;
                                case 2:
                                    stateStr="疑难";
                                    break;
                                case 3:
                                    stateStr="已签收";
                                    break;
                                case 4:
                                    stateStr="退签";
                                    break;
                                case 5:
                                    stateStr="同城派送中";
                                    break;
                                case 6:
                                    stateStr="退回";
                                    break;
                                case 7:
                                    stateStr="转单";
                                    break;
                            }
                            tv_status.setText(stateStr);
                            tv_express_name.setText(expressBean.getCom());
                            tv_express_num.setText(""+ expressBean.getNu());
                            List<ExpressBean.Data> data= expressBean.getData();
                            if(data!=null&&data.size()>0){

                                adapter.replaceAll(data);

                            }
                        }
                    }
                });

            }
        });
    }

    @Override
    public void getData(int type, Callback callback) {
        String token= MyShare.get(this).getString(Constance.token);
        String user_id=MyShare.get(this).getString(Constance.user_id);
        LogUtils.logE("shipc,invoice_no",shipping_code+","+invoice_no);
        if(TYPE_EXPRESS==type){
            OkHttpUtils.getExpress(shipping_code,invoice_no,token,callback);
        }else if(TYPE_ORDER==type){
            OkHttpUtils.getOrderDetail(user_id,token,order_id,callback);
        }

    }
}
