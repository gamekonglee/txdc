package bc.yxdc.com.ui.activity.user;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.AddressBean;
import bc.yxdc.com.bean.AddressList;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.net.OkHttpUtils;
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
 * Created by gamekonglee on 2018/10/9.
 */

public class AddressListActivity extends BaseActivity {
    private static final int TYPE_DELETE = 3;
    private static final int TYPE_LIST = 0;
    private static final int TYPE_UPDATE = 1;
    @BindView(R.id.lv_address)
    ListView lv_address;
    @BindView(R.id.btn_add_address)
    TextView btn_add_address;
    @BindView(R.id.layout_empty)
    View layout_empty;
    private List<AddressBean> addressBeans;
    private QuickAdapter<AddressBean> adapter;
    private int cp;
    private int mCurrentId;
    private boolean isSELECTADDRESS;


    @Override
    protected void initData() {
        if(getIntent().hasExtra(Constance.isSELECTADDRESS)){
            isSELECTADDRESS = getIntent().getBooleanExtra(Constance.isSELECTADDRESS,false);
        }
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_address_home);
        ButterKnife.bind(this);
        adapter = new QuickAdapter<AddressBean>(this, R.layout.item_address) {
            @Override
            protected void convert(final BaseAdapterHelper helper, final AddressBean item) {
                helper.setText(R.id.tv_consginer,item.getConsignee());
                helper.setText(R.id.tv_mobile,item.getMobile());
                helper.setText(R.id.tv_address,item.getProvince_name()+item.getCity_name()+item.getDistrict_name()+item.getTwon_name()+item.getAddress());
                helper.setOnClickListener(R.id.tv_edit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(AddressListActivity.this,AddressAddActivity.class);
                        intent.putExtra(Constance.is_edit,true);
                        intent.putExtra(Constance.address,new Gson().toJson(item,AddressBean.class));
                        startActivity(intent);
                    }
                });
                helper.setOnClickListener(R.id.tv_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSingleWordDialog(AddressListActivity.this, "是否确认删除该地址？", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mCurrentId = item.getAddress_id();
                                misson(TYPE_DELETE, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                    final JSONObject res=new JSONObject(response.body().string());
//                                    if(res.getInt(Constance.status)==1){
//
//                                    }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MyToast.show(AddressListActivity.this,""+res.getString(Constance.msg));
                                            }
                                        });

                                    sendAddressList();
                                    }
                                });

                            }
                        });
                    }
                });
                TextView tv_default=helper.getView(R.id.tv_default);
                if(item.getIs_default()==0){
                    Drawable drawable=getResources().getDrawable(R.mipmap.page_icon_round_default);
                    drawable.setBounds(0,0, UIUtils.dip2PX(20),UIUtils.dip2PX(20));
                    tv_default.setCompoundDrawables(drawable,null,null,null);
                    tv_default.setText("设置为默认地址");
                }else {
                    Drawable drawable=getResources().getDrawable(R.mipmap.page_icon_round_seleted);
                    drawable.setBounds(0,0, UIUtils.dip2PX(20),UIUtils.dip2PX(20));
                    tv_default.setCompoundDrawables(drawable,null,null,null);
                    tv_default.setText("默认地址");
                }
                tv_default.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(item.getIs_default()==1){
                            return;
                        }
                        setDefaultAddress(helper.getPosition());
                    }
                });
            }
        };
        lv_address.setAdapter(adapter);
        btn_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddressListActivity.this,AddressAddActivity.class));
            }
        });
        lv_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isSELECTADDRESS){
                    Intent intent=new Intent();
                    intent.putExtra(Constance.consignee,addressBeans.get(position).getConsignee());
                    intent.putExtra(Constance.mobile,addressBeans.get(position).getMobile());
                    intent.putExtra(Constance.address,addressBeans.get(position).getProvince_name()+addressBeans.get(position).getCity_name()+addressBeans.get(position).getDistrict_name()+addressBeans.get(position).getAddress());
                    intent.putExtra(Constance.address_id,addressBeans.get(position).getAddress_id()+"");
                    setResult(200,intent);
                    finish();
                }else {
                    Intent intent=new Intent(AddressListActivity.this,AddressAddActivity.class);
                    intent.putExtra(Constance.is_edit,true);
                    intent.putExtra(Constance.address,new Gson().toJson(addressBeans.get(position),AddressBean.class));
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        sendAddressList();
    }

    @Override
    public void onBackPressed() {
            Intent intent=new Intent();
        if(isSELECTADDRESS&&addressBeans!=null&&addressBeans.size()>0){
            intent.putExtra(Constance.consignee,addressBeans.get(0).getConsignee());
            intent.putExtra(Constance.mobile,addressBeans.get(0).getMobile());
            intent.putExtra(Constance.address,addressBeans.get(0).getProvince_name()+addressBeans.get(0).getCity_name()+addressBeans.get(0).getDistrict_name()+addressBeans.get(0).getAddress());
            intent.putExtra(Constance.address_id,addressBeans.get(0).getAddress_id()+"");
        }
            setResult(200,intent);
            finish();
    }

    @Override
    public void goBack(View v) {
        onBackPressed();
    }

    private void sendAddressList() {
        misson(TYPE_LIST, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                LogUtils.logE("res",res.toString());
                if(res.getInt(Constance.status)==1){
                    final JSONArray jsonArray=res.getJSONArray(Constance.result);
                        addressBeans = new ArrayList<>();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(jsonArray==null||jsonArray.length()==0){
                                layout_empty.setVisibility(View.VISIBLE);
                                lv_address.setVisibility(View.GONE);
                            }else {
                                layout_empty.setVisibility(View.GONE);
                                lv_address.setVisibility(View.VISIBLE);
                                for(int i=0;i<jsonArray.length();i++){
                                    addressBeans.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(),AddressBean.class));
                                }
                            }
                            adapter.replaceAll(addressBeans);
                        }
                    });
                }
            }
        });
    }

    private void setDefaultAddress(int address_id) {
        cp = address_id;
        misson(TYPE_UPDATE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                LogUtils.logE("res",res.toString());
                sendAddressList();
            }
        });
    }

    @Override
    public void getData(int type, Callback callback) {
        String userid= MyShare.get(this).getString(Constance.user_id);
        String token=MyShare.get(this).getString(Constance.token);
        if(TextUtils.isEmpty(userid)||TextUtils.isEmpty(token)){
            MyToast.show(this,"登录状态失效");
            startActivity(new Intent(this,LoginActivity.class));
            finish();
            return;
        }
        if(type==TYPE_LIST){
            OkHttpUtils.getAddressList(userid,token,callback);
        }else if(type==TYPE_UPDATE){
            AddressBean addressBean=addressBeans.get(cp);
            OkHttpUtils.updateAddress(userid,token,addressBean.getAddress_id()+"",addressBean.getConsignee(),addressBean.getProvince()+"",addressBean.getCity()+"",addressBean.getDistrict()+"",addressBean.getTwon()+"",addressBean.getAddress(),addressBean.getMobile(),1,callback);
        }else if(type==TYPE_DELETE){
            OkHttpUtils.deleteAddress(userid,token,mCurrentId,callback);
        }
    }

    public void showSingleWordDialog(final Context activity, String tittle, final View.OnClickListener listener) {
        final Dialog dialog = new Dialog(activity, R.style.customDialog);
        dialog.setContentView(R.layout.dialog_layout);
        TextView tv_num= (TextView) dialog.findViewById(R.id.tv_num);
        tv_num.setText(tittle);

        TextView btn = (TextView) dialog.findViewById(R.id.tv_ensure);
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setTextColor(getResources().getColor(R.color.theme_red));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
                dialog.dismiss();
            }
        });
        TextView cancel= (TextView) dialog.findViewById(R.id.tv_cancel);
        cancel.setBackgroundColor(Color.TRANSPARENT);
        cancel.setTextColor(getResources().getColor(R.color.blue));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
           /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }
}
