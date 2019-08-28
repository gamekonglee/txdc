package bc.yxdc.com.ui.activity.user;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Region;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.AddressBean;
import bc.yxdc.com.bean.ContactInfo;
import bc.yxdc.com.bean.RegionBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.MyShare;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.utils.AppUtils;
import bc.yxdc.com.utils.ContactUtils;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import cn.qqtheme.framework.util.ConvertUtils;
import cn.qqtheme.framework.util.LogUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/9/24.
 */

public class AddressAddActivity extends BaseActivity {
    private static final int TYPE_ADD_ADDRESS = 0;
    private static final int REQUEST_LOCATION = 1000;
    private static final int TYPE_UPDATE_ADDRESS = 1;
    @BindView(R.id.et_consginer)
    EditText et_consginer;
    @BindView(R.id.et_mobile)
    EditText et_mobile;
    @BindView(R.id.et_address)
    EditText et_address;
    @BindView(R.id.rl_region)
    RelativeLayout rl_region;
    @BindView(R.id.btn_save)
    Button btn_save;
    @BindView(R.id.cb_set_default_address)
    CheckBox cb_set_default;
    @BindView(R.id.tv_region)
    TextView tv_region;
    private String mRegion;
    private String consgin;
    private String mobile;
    private String address;
    private int is_default;
    private String user_id;
    private String mProvince="0";
    private String mCity="0";
    private String mCountry="0";
    private String token;
    private boolean is_edit;
    private AddressBean mAddressBean;
    private List<ContactInfo> contacts;
    private ProgressDialog pd;
    private ArrayList<Province> data;
    private boolean isClickAddress;
    private List<RegionBean> provinceList;
    private Dialog dialog;
    private TextView tv_area;
    private ListView lv_area;
    private QuickAdapter<RegionBean> cityAdapter;
    private QuickAdapter<RegionBean> zoneAdapter;
    private QuickAdapter<RegionBean> areaAdapter;
    private List<RegionBean> cityList;
    private List<RegionBean> zoneList;
    private List<RegionBean> areaList;
    private String mArea="";

    @Override
    protected void initData() {
        data = new ArrayList<>();
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_address_add);
        ButterKnife.bind(this);
        if(getIntent().hasExtra(Constance.is_edit)){
            is_edit = getIntent().getBooleanExtra(Constance.is_edit,false);
            mAddressBean = new Gson().fromJson(getIntent().getStringExtra(Constance.address),AddressBean.class);
        }
        if(is_edit){
            mProvince=mAddressBean.getProvince()+"";
            mCity=mAddressBean.getCity()+"";
            mCountry=mAddressBean.getDistrict()+"";
            mArea=mAddressBean.getTwon()+"";
            mRegion=mCountry;
            et_consginer.setText(mAddressBean.getConsignee());
            et_mobile.setText(""+mAddressBean.getMobile());
            tv_region.setText(mAddressBean.getProvince_name()+" "+mAddressBean.getCity_name()+" "+mAddressBean.getDistrict_name()+" "+mAddressBean.getTwon_name());
            et_address.setText(mAddressBean.getAddress());
            if(mAddressBean.getIs_default()==1){
                cb_set_default.setChecked(true);
            }else {
                cb_set_default.setChecked(false);
            }
//            mRegion=mAddressBean.get
        }
        rl_region.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpUtils.getRegion("0", new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                    JSONObject jsonObject=new JSONObject(response.body().string());
                    if(jsonObject!=null){
                        JSONArray jsonArray=jsonObject.getJSONArray(Constance.result);
                        if(jsonArray!=null&&jsonArray.length()>0){
                            provinceList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<RegionBean>>(){}.getType());
                            if(provinceList!=null&&provinceList.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                selectAddressB();
                                    }
                                });
                            }
                        }
                    }
                    }
                });
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consgin = et_consginer.getText().toString();
                mobile = et_mobile.getText().toString();
                address = et_address.getText().toString();
                is_default = cb_set_default.isChecked()?1:0;
                user_id = MyShare.get(AddressAddActivity.this).getString(Constance.user_id);
                token = MyShare.get(AddressAddActivity.this).getString(Constance.token);
                if(TextUtils.isEmpty(consgin)){
                    MyToast.show(AddressAddActivity.this,"请填写联系人");
                    return;
                }
                if(TextUtils.isEmpty(mobile)){
                    MyToast.show(AddressAddActivity.this,"请填写联系方式");
                    return;
                }
                if(TextUtils.isEmpty(address)){
                    MyToast.show(AddressAddActivity.this,"请填写详细地址");
                    return;
                }
                if(TextUtils.isEmpty(mRegion)&&!is_edit){
                    MyToast.show(AddressAddActivity.this,"请选择地区");
                    return;
                }
                if(TextUtils.isEmpty(user_id)||TextUtils.isEmpty(token)){
                    MyToast.show(AddressAddActivity.this,"登录状态失效");
                    startActivity(new Intent(AddressAddActivity.this,LoginActivity.class));
                    finish();
                    return;
                }

                mobile=mobile.trim();
                if(is_edit){
                    try {
                        mAddressBean.setProvince(Integer.parseInt(mProvince));
                        mAddressBean.setCity(Integer.parseInt(mCity));
                        mAddressBean.setDistrict(Integer.parseInt(mCountry));
                        if(TextUtils.isEmpty(mArea)){
                            mArea="0";
                        }
                        mAddressBean.setTwon(Integer.parseInt(mArea));
                    }catch (Exception e){
                        mAddressBean.setProvince(0);
                        mAddressBean.setCity(0);
                        mAddressBean.setDistrict(0);
                        mAddressBean.setTwon(0);
                    }

                    misson(TYPE_UPDATE_ADDRESS, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final JSONObject resultJson=new JSONObject(response.body().string());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MyToast.show(AddressAddActivity.this,""+resultJson.getString(Constance.msg));
                                    if(resultJson.getInt(Constance.status)==1){
                                        Intent intent=new Intent();
                                        intent.putExtra(Constance.consignee,consgin);
                                        intent.putExtra(Constance.mobile,mobile);
                                        intent.putExtra(Constance.address,tv_region.getText().toString()+address);
                                        intent.putExtra(Constance.address_id,resultJson.getString(Constance.result));
                                        setResult(200,intent);
                                        AddressAddActivity.this.finish();
                                    }
                                }
                            });
                        }
                    });
                }else {
                    misson(TYPE_ADD_ADDRESS, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final JSONObject resultJson=new JSONObject(response.body().string());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MyToast.show(AddressAddActivity.this,""+resultJson.getString(Constance.msg));
                                    if(resultJson.getInt(Constance.status)==1){
                                        Intent intent=new Intent();
                                        intent.putExtra(Constance.consignee,consgin);
                                        intent.putExtra(Constance.mobile,mobile);
                                        intent.putExtra(Constance.address,tv_region.getText().toString()+address);
                                        intent.putExtra(Constance.address_id,resultJson.getString(Constance.result));
                                        setResult(200,intent);
                                        AddressAddActivity.this.finish();
                                    }
                                }
                            });


                        }
                    });
                }

            }
        });

//        getAddressData();


    }
    private TextView tv_province;
    private TextView tv_city;
    private TextView tv_zone_dialog;
    private ListView lv_city;
    private ListView lv_zone;
    private ImageView iv_back;
    private ListView lv_province;

    private void selectAddressB() {
        dialog = new Dialog(this,R.style.regionsDialog);
        dialog.setContentView(R.layout.dialog_address_b);
        dialog.setCanceledOnTouchOutside(true);
        tv_province = (TextView) dialog.findViewById(R.id.tv_province);
        iv_back = (ImageView) dialog.findViewById(R.id.iv_back);
        tv_city = (TextView) dialog.findViewById(R.id.tv_city);
        tv_zone_dialog = (TextView) dialog.findViewById(R.id.tv_zone_dialog);
        tv_area = dialog.findViewById(R.id.tv_area_dialog);

        lv_province = (ListView) dialog.findViewById(R.id.lv_address);
        lv_city = (ListView) dialog.findViewById(R.id.lv_city);
        lv_zone = (ListView) dialog.findViewById(R.id.lv_zone);
        lv_area = dialog.findViewById(R.id.lv_area);

        tv_province.setOnClickListener(this);
        tv_city.setOnClickListener(this);
        tv_zone_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lv_city.setVisibility(View.GONE);
                lv_zone.setVisibility(View.VISIBLE);
                lv_province.setVisibility(View.GONE);
                lv_area.setVisibility(View.GONE);
                tv_province.setTextColor(getResources().getColor(R.color.region_black));
                tv_city.setTextColor(getResources().getColor(R.color.region_black));
                tv_zone_dialog.setTextColor(getResources().getColor(R.color.theme_red));
                tv_area.setTextColor(getResources().getColor(R.color.region_black));
            }
        });
        iv_back.setOnClickListener(this);
        tv_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lv_city.setVisibility(View.GONE);
                lv_zone.setVisibility(View.GONE);
                lv_province.setVisibility(View.GONE);
                lv_area.setVisibility(View.VISIBLE);
                tv_province.setTextColor(getResources().getColor(R.color.region_black));
                tv_city.setTextColor(getResources().getColor(R.color.region_black));
                tv_area.setTextColor(getResources().getColor(R.color.theme_red));
                tv_zone_dialog.setTextColor(getResources().getColor(R.color.region_black));
            }
        });
        QuickAdapter<RegionBean> baseAdapter=new QuickAdapter<RegionBean>(AddressAddActivity.this,R.layout.item_address_dialog) {
            @Override
            protected void convert(BaseAdapterHelper helper, RegionBean item) {
            helper.setText(R.id.tv_item,item.getName());
            }
        };
        cityAdapter = new QuickAdapter<RegionBean>(AddressAddActivity.this,R.layout.item_address_dialog) {
            @Override
            protected void convert(BaseAdapterHelper helper, RegionBean item) {
                helper.setText(R.id.tv_item,item.getName());
            }
        };
        zoneAdapter = new QuickAdapter<RegionBean>(AddressAddActivity.this,R.layout.item_address_dialog) {
            @Override
            protected void convert(BaseAdapterHelper helper, RegionBean item) {
                helper.setText(R.id.tv_item,item.getName());
            }
        };
        areaAdapter = new QuickAdapter<RegionBean>(AddressAddActivity.this,R.layout.item_address_dialog) {
            @Override
            protected void convert(BaseAdapterHelper helper, RegionBean item) {
                helper.setText(R.id.tv_item,item.getName());
            }
        };
        lv_province.setAdapter(baseAdapter);
        lv_city.setAdapter(cityAdapter);
        lv_zone.setAdapter(zoneAdapter);
        lv_area.setAdapter(areaAdapter);

        baseAdapter.replaceAll(provinceList);

//        ApiClient.getRegionsList(new GklCallBack() {
//            @Override
//            public void response(Object response, int id) {
//                regionList = (List<RegionBean>) response;
//
//
////                        AddressDAO dao=new AddressDAO(ShipAddressAddActivity.this);
////                        dao.insert(regionList);
//            }
//        });
        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM );
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = getResources().getDisplayMetrics().heightPixels/2;
        win.setWindowAnimations(R.style.dialogButtomInStyle);
        win.setAttributes(lp);
        dialog.show();
        lv_province.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                cityList =new AddressDAO(this).queryRegions(String.valueOf(provinceList.get(position).getRegionId()));
                OkHttpUtils.getRegion(provinceList.get(position).getId(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        JSONObject jsonObject=new JSONObject(response.body().string());
                        if(jsonObject!=null){
                            JSONArray jsonArray=jsonObject.getJSONArray(Constance.result);
                            if(jsonArray!=null&&jsonArray.length()>0){
                                cityList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<RegionBean>>(){}.getType());
                                if(cityList !=null&& cityList.size()>0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mProvince=provinceList.get(position).getId();
                                            tv_province.setText(provinceList.get(position).getName());
                                            tv_province.setEnabled(true);
                                            tv_city.setVisibility(View.VISIBLE);
                                            tv_province.setTextColor(getResources().getColor(R.color.region_black));
                                            tv_city.setTextColor(getResources().getColor(R.color.theme_red));
                                            tv_zone_dialog.setTextColor(getResources().getColor(R.color.region_black));
                                            tv_area.setTextColor(getResources().getColor(R.color.region_black));
//                                            setCityAdapter();
                                            cityAdapter.replaceAll(cityList);
                                            lv_province.setVisibility(View.GONE);
                                            lv_zone.setVisibility(View.GONE);
                                            lv_area.setVisibility(View.GONE);
                                            lv_city.setVisibility(View.VISIBLE);
                                            lv_city.setAdapter(cityAdapter);
//                                            baseAdapter.notifyDataSetChanged();
                                        }
                                    });

                                }
                            }
                        }
                    }
                });

            }
        });
        lv_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OkHttpUtils.getRegion(cityList.get(position).getId(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        JSONObject jsonObject=new JSONObject(response.body().string());
                        if(jsonObject!=null){
                            JSONArray jsonArray=jsonObject.getJSONArray(Constance.result);
                            if(jsonArray!=null&&jsonArray.length()>0){
                                zoneList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<RegionBean>>(){}.getType());
                                if(zoneList !=null&& zoneList.size()>0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mCity=cityList.get(position).getId();
                                            zoneAdapter.replaceAll(zoneList);
//                                            setZoneAdapter();
                                            tv_city.setText(cityList.get(position).getName());
                                            tv_city.setTextColor(getResources().getColor(R.color.region_black));
                                            tv_province.setTextColor(getResources().getColor(R.color.region_black));
                                            tv_area.setTextColor(getResources().getColor(R.color.region_black));
                                            tv_zone_dialog.setTextColor(getResources().getColor(R.color.theme_red));
                                            tv_zone_dialog.setVisibility(View.VISIBLE);
                                            tv_city.setEnabled(true);
                                            lv_province.setVisibility(View.GONE);
                                            lv_zone.setVisibility(View.VISIBLE);
                                            lv_city.setVisibility(View.GONE);
                                            lv_area.setVisibility(View.GONE);
                                        }
                                    });

                                }
                            }
                        }
                    }
                });

            }
        });
        lv_zone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OkHttpUtils.getRegion(zoneList.get(position).getId(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        mCountry=zoneList.get(position).getId();

                        JSONObject jsonObject=new JSONObject(response.body().string());
                        if(jsonObject!=null){
                            JSONArray jsonArray=jsonObject.getJSONArray(Constance.result);
                            if(jsonArray!=null&&jsonArray.length()>0){
                                areaList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<RegionBean>>(){}.getType());
                                if(areaList !=null&& areaList.size()>0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
//                                            setZoneAdapter();
                                            tv_zone_dialog.setText(zoneList.get(position).getName());
                                            tv_zone_dialog.setTextColor(getResources().getColor(R.color.region_black));
                                            tv_province.setTextColor(getResources().getColor(R.color.region_black));
                                            tv_area.setTextColor(getResources().getColor(R.color.region_black));
                                            tv_area.setTextColor(getResources().getColor(R.color.theme_red));
                                            tv_area.setVisibility(View.VISIBLE);
                                            tv_zone_dialog.setEnabled(true);
                                            lv_province.setVisibility(View.GONE);
                                            lv_zone.setVisibility(View.GONE);
                                            lv_city.setVisibility(View.GONE);
                                            lv_area.setVisibility(View.VISIBLE);
                                            areaAdapter.replaceAll(areaList);
//                                            lv_area.setAdapter(areaAdapter);
                                        }
                                    });
                                }
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRegion=mCountry;
                                        dialog.dismiss();
                                        tv_region.setText(""+tv_province.getText().toString().trim()+" "+tv_city.getText().toString().trim()+" "+zoneList.get(position).getName());
                                    }
                                });
                            }
                        }
                    }
                });


            }
        });
        lv_area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mArea = areaList.get(position).getId();
                tv_area.setText(areaList.get(position).getName());
                dialog.dismiss();
                mRegion=mArea;
                tv_region.setText(""+tv_province.getText().toString().trim()+" "+tv_city.getText().toString().trim()+" "+tv_zone_dialog.getText().toString().trim()+" "+areaList.get(position).getName());
                String regionID = String.valueOf(areaList.get(position).getId());
                boolean hasZone=true;
//                setBtnEnabled();
            }
        });

//        Dialog dialog= UIUtils.showBottomInDialog(this,R.layout.dialog_address,UIUtils.dip2PX(250));
//        fourPicker = (PickAddressView) dialog.findViewById(R.id.fourPicker);
//        addressBeanList = DataHelper.getAddress(this);
//        fourPicker.setData(addressBeanList);
//        fourPicker.setOnTopClicklistener(new PickAddressInterface() {
//            @Override
//            public void onOkClick(String name, List<com.realpower.addresspicker.bean.AddressBean.CityChildsBean.CountyChildsBean.StreetChildsBean> beans) {
//                LogUtils.debug("beans",beans.toString());
//            }
//
//            @Override
//            public void onCancelClick() {
//
//            }
//        });
    }


    @Override
    public void onDestroy() {
//        fourPicker.onDistory();
//        addressBeanList.clear();
//        addressBeanList = null;
//        fourPicker.removeAllViews();
//        fourPicker = null;
        super.onDestroy();

    }

    @OnClick(R.id.rl_consigner)
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_consigner:
                if(Build.VERSION.SDK_INT>=23){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},REQUEST_LOCATION);
                    }else {
                        getContact();
                    }
                }else {
                    getContact();
                }
                break;
            case R.id.iv_back:
                dialog.dismiss();
                break;
            case R.id.tv_province:
                lv_province.setVisibility(View.VISIBLE);
                lv_zone.setVisibility(View.GONE);
                lv_city.setVisibility(View.GONE);
                lv_area.setVisibility(View.GONE);
                tv_province.setTextColor(getResources().getColor(R.color.theme_red));
                tv_city.setTextColor(getResources().getColor(R.color.region_black));
                tv_zone_dialog.setTextColor(getResources().getColor(R.color.region_black));
                tv_area.setTextColor(getResources().getColor(R.color.region_black));
                break;
            case R.id.tv_city:
                lv_province.setVisibility(View.GONE);
                lv_zone.setVisibility(View.GONE);
                lv_city.setVisibility(View.VISIBLE);
                lv_area.setVisibility(View.GONE);
                tv_province.setTextColor(getResources().getColor(R.color.region_black));
                tv_city.setTextColor(getResources().getColor(R.color.theme_red));
                tv_zone_dialog.setTextColor(getResources().getColor(R.color.region_black));
                tv_area.setTextColor(getResources().getColor(R.color.region_black));
                break;
            case R.id.tv_zone_dialog:
//                System.out.println("tvzone_dialog");
                lv_city.setVisibility(View.GONE);
                lv_zone.setVisibility(View.VISIBLE);
                lv_province.setVisibility(View.GONE);
                lv_area.setVisibility(View.GONE);
                tv_province.setTextColor(getResources().getColor(R.color.region_black));
                tv_city.setTextColor(getResources().getColor(R.color.region_black));
                tv_zone_dialog.setTextColor(getResources().getColor(R.color.theme_red));
                tv_area.setTextColor(getResources().getColor(R.color.region_black));
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if(requestCode==REQUEST_LOCATION){
                getContact();
        }

    }

    private void getContact() {
        contacts = ContactUtils.getContactsInfos(this);
        final Dialog dialog=new Dialog(this, R.style.customDialog);
        dialog.setContentView(R.layout.dialog_contact);
        ListView lv_contact=dialog.findViewById(R.id.lv_contact);
        QuickAdapter<ContactInfo> adapter=new QuickAdapter<ContactInfo>(this, R.layout.item_contact) {
            @Override
            protected void convert(BaseAdapterHelper helper, ContactInfo item) {
                helper.setText(R.id.tv_name,item.name);
                helper.setText(R.id.tv_tel,item.phone);
            }
        };
        lv_contact.setAdapter(adapter);
        adapter.replaceAll(contacts);
        adapter.notifyDataSetChanged();
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                et_consginer.setText(contacts.get(i).name);
                et_mobile.setText(""+ contacts.get(i).phone);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    public void getData(int type, Callback callback) {
        if(type==TYPE_ADD_ADDRESS){
            OkHttpUtils.addAddress(user_id,token,consgin.trim(),mProvince,mCity,mCountry,mArea,address.trim(),mobile.trim(),is_default,callback);
        }else if(type==TYPE_UPDATE_ADDRESS){
            OkHttpUtils.updateAddress(user_id,token,mAddressBean.getAddress_id()+"",consgin.trim(),mAddressBean.getProvince()+"",mAddressBean.getCity()+"",mAddressBean.getDistrict()+"",mAddressBean.getTwon()+"",address.trim(),mobile.trim(),is_default,callback);
        }
    }
    public void getAddressData(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String json = null;
                try {
                    json = ConvertUtils.toString(getAssets().open("city.json"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                data.addAll(JSON.parseArray(json, Province.class));
                if(isClickAddress){
                    handler.sendEmptyMessage(0);
                }
            }
        }.start();

    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pd.dismiss();
            selectAddress();
        }
    };
    /**
     * 选择城市
     */
    public void selectAddress() {
        try {
            isClickAddress = true;
            pd = ProgressDialog.show(this,"请稍等","加载中");
            if(data.size()==0){
//                getAddressData();
                return;
            }
            final AddressPicker picker = new AddressPicker(this, data);

            picker.setHideProvince(false);
            picker.setSelectedItem("广东", "佛山", "禅城");
            picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {

                @Override
                public void onAddressPicked(final Province province, final City city, final County county) {
                    mProvince = province.getAreaId();
                    mCity = city.getAreaId();
                    mCountry = county.getAreaId();

                    String address = province.getAreaName() + " " + city.getAreaName() + " " + county.getAreaName();

                    if (AppUtils.isEmpty(county.getCityId())) {
                        mRegion = city.getAreaId();
                    } else {
                        mRegion = county.getAreaId();
                    }
                    tv_region.setText(address);
                    OkHttpUtils.getRegion("0", new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                        JSONObject res1=new JSONObject(response.body().string());
                            JSONArray array1=res1.getJSONArray(Constance.result);
                            for(int i=0;i<array1.length();i++){
                                if(array1.getJSONObject(i).getString(Constance.name).contains(province.getAreaName())){
                                    mProvince=array1.getJSONObject(i).getInt(Constance.id)+"";
                                    OkHttpUtils.getRegion(array1.getJSONObject(i).getInt(Constance.id) + "", new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {

                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            JSONObject res2=new JSONObject(response.body().string());
                                            JSONArray array2=res2.getJSONArray(Constance.result);
                                            for(int i=0;i<array2.length();i++){
                                                if(array2.getJSONObject(i).getString(Constance.name).contains(city.getAreaName())){
                                                    mCity=array2.getJSONObject(i).getInt(Constance.id)+"";
                                                    OkHttpUtils.getRegion(array2.getJSONObject(i).getInt(Constance.id) + "", new Callback() {
                                                        @Override
                                                        public void onFailure(Call call, IOException e) {

                                                        }

                                                        @Override
                                                        public void onResponse(Call call, Response response) throws IOException {
                                                            JSONObject res3=new JSONObject(response.body().string());
                                                            JSONArray array3=res3.getJSONArray(Constance.result);
                                                            for(int i=0;i<array3.length();i++){
                                                                if(array3.getJSONObject(i).getString(Constance.name).contains(county.getAreaName())){
                                                                        mRegion=array3.getJSONObject(i).getInt(Constance.id)+"";
                                                                        mCountry=mRegion;
                                                                    bc.yxdc.com.utils.LogUtils.logE("mRegino",mRegion+"");
                                                                }
                                                            }
                                                        }
                                                    });
                                                    break;
                                                }
                                            }
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    });
                    picker.dismiss();
                }
            });
            pd.dismiss();
            picker.show();

        } catch (Exception e) {
            MyToast.show(this, LogUtils.toStackTraceString(e));
        }
    }
}
