package bc.yxdc.com.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseFragment;
import bc.yxdc.com.bean.CouponMineBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.goods.SelectGoodsActivity;
import bc.yxdc.com.ui.activity.user.LoginActivity;
import bc.yxdc.com.utils.DateUtils;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.view.TextViewPlus;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/10/29.
 */

public class CouponMineFragment extends BaseFragment implements View.OnClickListener {

    private static final int TYPE_GET_COUPON = 2;
    private static final int TYPE_LIST = 0;
    private TextViewPlus tv_never_use;
    private TextViewPlus tv_has_use;
    private TextViewPlus tv_has_pass;
    private ListView lv_mine;
    private int p;
    private int type;
    private int store_id;
    private QuickAdapter<CouponMineBean> adapter;
    private List<CouponMineBean> couponMineBeans;
    private int currentP;
    private int current;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_coupon_min,null);
    }

    @Override
    public void initUI() {
        tv_never_use = getView().findViewById(R.id.tv_never_use);
        tv_has_use = getView().findViewById(R.id.tv_has_use);
        tv_has_pass = getView().findViewById(R.id.tv_has_pass);
        lv_mine = getView().findViewById(R.id.lv_mine_coupons);
        tv_never_use.setOnClickListener(this);
        tv_has_use.setOnClickListener(this);
        tv_has_pass.setOnClickListener(this);
        p = 1;
        type = 0;
        store_id = 0;
        couponMineBeans = new ArrayList<>();
        adapter = new QuickAdapter<CouponMineBean>(getActivity(),R.layout.item_counpon_mine) {
            @Override
            protected void convert(final BaseAdapterHelper helper, CouponMineBean item) {
                helper.setText(R.id.tv_money,item.getMoney()+"");
                helper.setText(R.id.tv_limit,"满"+item.getCondition()+"可用");
                helper.setText(R.id.tv_name,item.getName());
                helper.setText(R.id.tv_time, DateUtils.getStrTime02(item.getUse_start_time()+"")+"-"+DateUtils.getStrTime02(item.getUse_end_time()+""));
                helper.setBackgroundRes(R.id.ll_bg,item.getStatus()==0?R.mipmap.content_wsy_left:R.mipmap.content_ygq_left);
                helper.setVisible(R.id.tv_use_now,item.getStatus()==0?true:false);
                helper.setOnClickListener(R.id.tv_use_now, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        current = helper.getPosition();
                        if(current==0){
                            startActivity(new Intent(getActivity(), SelectGoodsActivity.class));
                            getActivity().finish();
                        }
//                             misson(TYPE_GET_COUPON, new Callback() {
//                                 @Override
//                                 public void onFailure(Call call, IOException e) {
//
//                                 }
//
//                                 @Override
//                                 public void onResponse(Call call, Response response) throws IOException {
//
//                                 }
//                             });
                    }
                });

            }
        };
        lv_mine.setAdapter(adapter);
        load();
    }

    private void load() {
        misson(TYPE_LIST, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    LogUtils.logE("mine.",res.toString());
                    JSONArray array=res.getJSONArray(Constance.result);
                    if(array!=null&&array.length()>0){
                        for(int i=0;i<array.length();i++){
                            couponMineBeans.add(new Gson().fromJson(array.getJSONObject(i).toString(),CouponMineBean.class));
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.replaceAll(couponMineBeans);
                        }
                    });

                }

            }
        });
    }

    @Override
    public void getData(int types, Callback callback) {
        String token= MyShare.get(getActivity()).getString(Constance.token);
        String user_id=MyShare.get(getActivity()).getString(Constance.user_id);
        if(TextUtils.isEmpty(token)||TextUtils.isEmpty(user_id)){
            MyToast.show(getActivity(),"请先登录");
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }
        if(types==TYPE_LIST){
        OkHttpUtils.getCouponMineList(user_id,token,p,type,store_id,callback);
        }else if(types==TYPE_GET_COUPON){
//            OkHttpUtils.getCoupon(couponMineBeans.get(current).getId(),callback);
        }

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_never_use:
                currentP = 0;
                break;
            case R.id.tv_has_use:
                currentP=1;
                break;
            case R.id.tv_has_pass:
                currentP=2;
                break;

        }
        refreshUI();
    }

    private void refreshUI() {
        tv_never_use.setTextColor(getResources().getColor(R.color.tv_333333));
        tv_has_pass.setTextColor(getResources().getColor(R.color.tv_333333));
        tv_has_use.setTextColor(getResources().getColor(R.color.tv_333333));
        Drawable drawable=getResources().getDrawable(R.drawable.bg_line);
        drawable.setBounds(0,0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
        tv_never_use.setCompoundDrawables(null,null,null,null);
        tv_has_use.setCompoundDrawables(null,null,null,null);
        tv_has_pass.setCompoundDrawables(null,null,null,null);
        switch (currentP){
            case 0:
                tv_never_use.setTextColor(getResources().getColor(R.color.theme_red));
                tv_never_use.setCompoundDrawables(null,null,null,drawable);
                break;
            case 1:
                tv_has_use.setTextColor(getResources().getColor(R.color.theme_red));
                tv_has_use.setCompoundDrawables(null,null,null,drawable);
                break;
            case 2:
                tv_has_pass.setTextColor(getResources().getColor(R.color.theme_red));
                tv_has_pass.setCompoundDrawables(null,null,null,drawable);
                break;
        }
        type=currentP;
        couponMineBeans=new ArrayList<>();
        load();



    }
}
