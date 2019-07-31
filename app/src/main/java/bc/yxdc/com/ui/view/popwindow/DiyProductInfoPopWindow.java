package bc.yxdc.com.ui.view.popwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.bean.Goods;
import bc.yxdc.com.bean.Goods_attr_list;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.listener.IDiyProductInfoListener;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.adapter.ParamentAdapter02;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/12/21.
 */

public class DiyProductInfoPopWindow extends BasePopwindown implements View.OnClickListener {
    private Activity mActivity;
    private IDiyProductInfoListener mListener;
    private ImageView product_iv, close_iv;
    private RelativeLayout two_bar_codes_rl, parameter_rl, logo_rl, card_rl;
    public Goods productObject;
    private ListView attr_lv;
    private ParamentAdapter02 mParamentAdapter;
    private StringBuffer mParamMsg;

    public void setListener(IDiyProductInfoListener listener) {
        mListener = listener;
    }

    public DiyProductInfoPopWindow(Context context, Activity activity) {
        super(context);
        mActivity = activity;
    }

    @Override
    protected void initView(Context context) {
        View contentView = View.inflate(mContext, R.layout.pop_diy_product_info, null);
        initUI(contentView);
    }

    public void initViewData() {
        String path = productObject.c_url;
        if(path!=null&&!path.contains("http")){
            path= NetWorkConst.API_HOST+path;
        }
        ImageLoader.getInstance().displayImage(path, product_iv);
        getDetail();
    }


    private void initUI(View contentView) {

        product_iv = (ImageView) contentView.findViewById(R.id.product_iv);
        two_bar_codes_rl = (RelativeLayout) contentView.findViewById(R.id.two_bar_codes_rl);
        parameter_rl = (RelativeLayout) contentView.findViewById(R.id.parameter_rl);
        logo_rl = (RelativeLayout) contentView.findViewById(R.id.logo_rl);
        card_rl = (RelativeLayout) contentView.findViewById(R.id.card_rl);
        close_iv = (ImageView) contentView.findViewById(R.id.close_iv);
        attr_lv = (ListView) contentView.findViewById(R.id.attr_lv);
        product_iv.setOnClickListener(this);
        two_bar_codes_rl.setOnClickListener(this);
        parameter_rl.setOnClickListener(this);
        logo_rl.setOnClickListener(this);
        card_rl.setOnClickListener(this);
        close_iv.setOnClickListener(this);

        mPopupWindow = new PopupWindow(contentView, -1, -1);
        // 1.让mPopupWindow内部的控件获取焦点
        mPopupWindow.setFocusable(true);
        // 2.mPopupWindow内部获取焦点后 外部的所有控件就失去了焦点
        mPopupWindow.setOutsideTouchable(true);
        //只有加载背景图还有效果
        // 3.如果不马上显示PopupWindow 一般建议刷新界面
        mPopupWindow.update();
        // 设置弹出窗体显示时的动画，从底部向上弹出
        mPopupWindow.setAnimationStyle(R.style.AnimBottom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_iv:
                onDismiss();
                break;
            case R.id.bg_ll:
                onDismiss();
                break;
            case R.id.two_bar_codes_rl://二维码
                mListener.onDiyProductInfo(0,null);
                onDismiss();
                break;
            case R.id.parameter_rl://参数
                mListener.onDiyProductInfo(1,mParamMsg.toString());
                onDismiss();
                break;
            case R.id.logo_rl://LOGO
                mListener.onDiyProductInfo(2,null);
                onDismiss();
                break;
            case R.id.card_rl://产品卡
                mListener.onDiyProductInfo(3,null);
                onDismiss();
                break;
        }
    }

    /**
     * 产品详情
     */
    public void getDetail() {
        mParamMsg=new StringBuffer();
        OkHttpUtils.getGoodsContent(productObject.getGoods_id(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    List<Goods_attr_list> attachmentArray = new Gson().fromJson(res.getJSONObject(Constance.result).getJSONArray(Constance.goods_attr_list).toString(),new TypeToken<List<Goods_attr_list>>(){}.getType());
                    final List<String> attachs = new ArrayList<>();
                    String name=productObject.getGoods_name();
                    if(name.length()>=8){
                        name=name.substring(0,8);
                    }
                    attachs.add("名称: " + name);
                    attachs.add("价格: " +"￥"+ productObject.getShop_price());
                    mParamMsg.append("名称: " + name+ "\n");
                    mParamMsg.append("价格: ￥" + productObject.getShop_price()+ "\n");
                    for (int i = 0; i < attachmentArray.size(); i++) {
                        Goods_attr_list jsonObject = attachmentArray.get(i);
                        attachs.add(jsonObject.getAttr_name()+ ": " + jsonObject.getAttr_value());
                        if(i<attachmentArray.size()-1){
                            mParamMsg.append(jsonObject.getAttr_name() + ": " + jsonObject.getAttr_value()+"\n");
                        }else{
                            mParamMsg.append(jsonObject.getAttr_name() + ": " + jsonObject.getAttr_value());
                        }

                    }


                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mParamentAdapter = new ParamentAdapter02(attachs, mContext, 1);
                            attr_lv.setAdapter(mParamentAdapter);
                            attr_lv.setDivider(null);//去除listview的下划线
                        }
                    });

                }
            }
        });

    }



}

