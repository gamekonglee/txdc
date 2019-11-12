package bc.yxdc.com.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gklee.regionselector.OnRegionDataSetListener;
import com.gklee.regionselector.RegionBean;
import com.gklee.regionselector.RegionLevel;
import com.gklee.regionselector.RegionSelectDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.home.MainActivity;
import bc.yxdc.com.utils.DateUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.ScannerUtils;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class BussinessApplyActivity extends BaseActivity {

    private EditText et_name;
    private EditText et_phone;
    private TextView tv_region;
    private EditText et_address;
    private EditText et_remark;
    private Button btn_apply;
    private List<RegionBean> provinceList;
    private String provinceId;
    private String cityId;
    private String zoneId;
    private String provinceName;
    private String cityName;
    private String zoneName;


    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_bussiness_apply);
        setColor(this, Color.WHITE);
        View rl_back=findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BussinessApplyActivity.this, MainActivity.class));
                finish();
            }
        });
        et_name = findViewById(R.id.et_name);
        et_phone = findViewById(R.id.et_phone);
        tv_region = findViewById(R.id.tv_region);
        et_address = findViewById(R.id.et_address);
        et_remark = findViewById(R.id.et_remark);
        btn_apply = findViewById(R.id.btn_apply);
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=et_name.getText().toString();
                String phone=et_phone.getText().toString();
                String region=tv_region.getText().toString();
                String address=et_address.getText().toString();
                String remark=et_remark.getText().toString();
                if(TextUtils.isEmpty(name)||TextUtils.isEmpty(phone)||TextUtils.isEmpty(region)){
                    MyToast.show(BussinessApplyActivity.this,"请填写完整必要的信息");
                    return;
                }
                OkHttpUtils.sendDealer(name,phone,provinceId,cityId,zoneId,address,remark, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                        MyToast.show(BussinessApplyActivity.this,"网络异常，请稍后再试");
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result=response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                        MyToast.show(BussinessApplyActivity.this,"提交成功！");
                                MyShare.get(BussinessApplyActivity.this).putBoolean(Constance.is_submit_bussiness_apply,true);

                            }
                        });
                        startActivity(new Intent(BussinessApplyActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }
        });
        tv_region.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_phone.getWindowToken(),0);
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
                                if(provinceList !=null&& provinceList.size()>0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            selectAddress();
                                        }
                                    });
                                }
                            }
                        }
                    }
                });

            }
        });
    }

    private void selectAddress() {
        ScannerUtils
        RegionSelectDialog dialog=new RegionSelectDialog(this, RegionLevel.LEVEL_THREE);
        dialog.setProvinceData(provinceList);
        dialog.setOnRegionDataSetListenr(new OnRegionDataSetListener() {

            @Override
            public List<RegionBean> setProvinceList() {
                return provinceList;
            }

            @Override
            public List<RegionBean> setOnProvinceSelected(RegionBean regionBean) {
                provinceId = regionBean.getId();
                provinceName = regionBean.getName();
                Response response=OkHttpUtils.getRegionSync(regionBean.getId());
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(jsonObject!=null){
                    JSONArray jsonArray=jsonObject.getJSONArray(Constance.result);
                    if(jsonArray!=null&&jsonArray.length()>0){
                        List<RegionBean> cityList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<RegionBean>>(){}.getType());
                        if(cityList  !=null&& cityList .size()>0){
                            return cityList;
                        }
                    }
                }

                return null;
            }

            @Override
            public List<RegionBean> setOnCitySelected(RegionBean regionBean) {
                cityId = regionBean.getId();
                cityName = regionBean.getName();
                Response response=OkHttpUtils.getRegionSync(regionBean.getId());
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(jsonObject!=null){
                    JSONArray jsonArray=jsonObject.getJSONArray(Constance.result);
                    if(jsonArray!=null&&jsonArray.length()>0){
                        List<RegionBean> zoneList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<RegionBean>>(){}.getType());
                        if(zoneList  !=null&& zoneList .size()>0){
                            return zoneList;
                        }
                    }
                }
                return null;
            }

            @Override
            public List<RegionBean> setOnZoneSelected(RegionBean regionBean) {
                zoneId = regionBean.getId();
                zoneName = regionBean.getName();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_region.setText(provinceName+" "+cityName+" "+zoneName);
                    }
                });

//                Response response=OkHttpUtils.getRegionSync(regionBean.getId());
//                JSONObject jsonObject= null;
//                try {
//                    jsonObject = new JSONObject(response.body().string());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if(jsonObject!=null){
//                    JSONArray jsonArray=jsonObject.getJSONArray(Constance.result);
//                    if(jsonArray!=null&&jsonArray.length()>0){
//                        List<RegionBean> zoneList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<RegionBean>>(){}.getType());
//                        if(zoneList  !=null&& zoneList .size()>0){
//                            return zoneList;
//                        }
//                    }
//                }
                return null;
            }

            @Override
            public void setOnAreaSelected(RegionBean regionBean) {

            }
        });
        dialog.showDialog();
    }

    @Override
    public void getData(int type, Callback callback) {

    }
}
