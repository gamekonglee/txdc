package bc.yxdc.com.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseFragment;
import bc.yxdc.com.bean.ProductResult;
import bc.yxdc.com.bean.Recommend_goods;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.goods.ProDetailActivity;
import bc.yxdc.com.ui.activity.user.LoginActivity;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/9/14.
 */

public class ParameterFragment extends BaseFragment{

    private GridView gv_tujian;
    private QuickAdapter<Recommend_goods> adapter;
    private List<Recommend_goods> recommend_goods;

    @Override
    public void initUI() {

    }

    @Override
    public void getData(int type, Callback callback) {
        String token= MyShare.get(getActivity()).getString(Constance.token);
        String user_id=MyShare.get(getActivity()).getString(Constance.user_id);
        if(TextUtils.isEmpty(token)||TextUtils.isEmpty(user_id)){
            MyToast.show(getActivity(),"登录状态失效");
            startActivity(new Intent(getContext(), LoginActivity.class));
            return;
        }
        OkHttpUtils.getGoodsDetail(user_id,token,((ProDetailActivity)getActivity()).mProductId,callback);
    }

    @Override
    protected void initData() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_tuijian,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gv_tujian = view.findViewById(R.id.gv_tuijian);
        adapter = new QuickAdapter<Recommend_goods>(getActivity(), R.layout.item_goods) {
            @Override
            protected void convert(BaseAdapterHelper helper, Recommend_goods item) {
                helper.setText(R.id.name_tv,item.getGoods_name());
                helper.setText(R.id.name_tv,item.getGoods_name());
                View view1=helper.getView(R.id.ll_1);
                View view2=helper.getView(R.id.ll_2);
                if(IssApplication.isShowDiscount){
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_shop_price,"市场价：￥"+item.getShop_price());
                    helper.setText(R.id.tv_cost_price,"代理价：￥"+item.getCost_price());
//                helper.setText(R.id.price_tv,"¥"+item.getCost_price()+"");
                }else {
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.GONE);
                    helper.setText(R.id.price_tv,"¥"+item.getShop_price()+"");
                }
                helper.setText(R.id.tv_sold_2, "已售"+item.getSales_sum()+"件");
                helper.setText(R.id.tv_sold, "已售"+(TextUtils.isEmpty(item.getSales_sum())?"0":item.getSales_sum()+""+"件"));
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getGoods_id(),((ImageView)helper.getView(R.id.imageView)), IssApplication.getImageLoaderOption());

            }
        };
        gv_tujian.setAdapter(adapter);
        gv_tujian.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(),ProDetailActivity.class);
                intent.putExtra(Constance.product,recommend_goods.get(position).getGoods_id());
                startActivity(intent);
                getActivity().finish();
            }
        });
        misson(0, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res=response.body().string();
                final bocang.json.JSONObject jsonObject=new bocang.json.JSONObject(res);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProDetailActivity.goods=new Gson().fromJson(jsonObject.getJSONObject(Constance.result).toString(),ProductResult.class);
                        recommend_goods = ProDetailActivity.goods.getRecommend_goods();
                        adapter.replaceAll(recommend_goods);
                    }
                });


            }
        });
    }
}
