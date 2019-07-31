package bc.yxdc.com.ui.fragment.coupon;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseFragment;
import bc.yxdc.com.bean.CouponListBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.goods.SelectGoodsActivity;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/10/27.
 */

public class CouponGetListFragment extends BaseFragment {

    private static final int TYPE_LIST = 0;
    private static final int TYPE_GET = 1;
    private ListView lv_couponList;
    private int p;
    private List<CouponListBean> couponListBeans;
    private QuickAdapter<CouponListBean> adapter;
    private int current;

    @Override
    protected void initData() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_coupon_getlist,null);
    }

    @Override
    public void initUI() {
        lv_couponList = getView().findViewById(R.id.lv_coupons);
        couponListBeans = new ArrayList<>();
        adapter = new QuickAdapter<CouponListBean>(getActivity(), R.layout.item_counpon_list) {
            @Override
            protected void convert(final BaseAdapterHelper helper, CouponListBean item) {
                helper.setText(R.id.tv_name,item.getName());
                helper.setText(R.id.tv_coupon_name,item.getMoney()+"");
                helper.setText(R.id.tv_coupon_limit,"满"+(int)Double.parseDouble(item.getCondition())+"可用");
                ImageView iv_img=helper.getView(R.id.iv_img);
                ImageLoader.getInstance().displayImage(NetWorkConst.API_HOST+item.getImage(),iv_img, IssApplication.getImageLoaderOption());
                int isGet=item.getIsget();
                if(isGet==0){
                    helper.setVisible(R.id.iv_got,false);
                    helper.setVisible(R.id.tv_got,true);
                    helper.setVisible(R.id.progressBar,true);
                    ProgressBar progressBar=helper.getView(R.id.progressBar);
                    if(item.getCreatenum()!=0){
                    helper.setText(R.id.tv_got,"已领取"+item.getSend_num()/item.getCreatenum()+"%");
                    progressBar.setProgress(item.getSend_num()/item.getCreatenum());
                    }else {
                        helper.setText(R.id.tv_got,"已领取0%");
                    }

                    helper.setText(R.id.tv_get,"立即领取");
                    helper.setBackgroundRes(R.id.tv_get,R.mipmap.btn_qsy);
                    helper.setOnClickListener(R.id.tv_get, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            current = helper.getPosition();
                            misson(TYPE_GET, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    final JSONObject res=new JSONObject(response.body().string());
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MyToast.show(getActivity(),res.getString(Constance.msg));
                                            couponListBeans=new ArrayList<>();
                                            load();

                                        }
                                    });

                                }
                            });

                        }
                    });
                }else {
                    helper.setVisible(R.id.iv_got,true);
                    helper.setVisible(R.id.tv_got,false);
                    helper.setVisible(R.id.progressBar,false);
                    helper.setText(R.id.tv_get,"去使用");
                    helper.setBackgroundRes(R.id.tv_get,R.mipmap.btn_ljlq);
                    helper.setOnClickListener(R.id.tv_get, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        startActivity(new Intent(getActivity(), SelectGoodsActivity.class));
                        getActivity().finish();
                        }
                    });
                }

            }
        };
        lv_couponList.setAdapter(adapter);
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
                    LogUtils.logE("couponList",res.toString());
                    JSONArray array=res.getJSONArray(Constance.result);
                    for(int i=0;i<array.length();i++){
                        couponListBeans.add(new Gson().fromJson(array.getJSONObject(i).toString(),CouponListBean.class));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.replaceAll(couponListBeans);
                        }
                    });

                }

            }
        });
    }

    @Override
    public void getData(int type, Callback callback) {
        if(isToken()){
            return;
        }
            if(type==TYPE_LIST){
                p = 1;
                String token= MyShare.get(getActivity()).getString(Constance.token);
                String user_id=MyShare.get(getActivity()).getString(Constance.user_id);

                OkHttpUtils.getCouponList(token,user_id,p,callback);
            }else if(type==TYPE_GET){
                String token= MyShare.get(getActivity()).getString(Constance.token);
                String user_id=MyShare.get(getActivity()).getString(Constance.user_id);
                OkHttpUtils.getCoupon(token,user_id,couponListBeans.get(current).getId(),callback);
            }
    }
}
