package bc.yxdc.com.ui.activity.goods;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.PromoteBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.view.EndOfListView;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONArray;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/10/22.
 */

public class GoodsXsTmActivity extends BaseActivity implements EndOfListView.OnEndOfListListener {
    @BindView(R.id.lv_xstm)
    EndOfListView lv_xstm;
    @BindView(R.id.tv_cd_day)
    TextView tv_cd_day;
    @BindView(R.id.tv_cd_hour)
    TextView tv_cd_hour;
    @BindView(R.id.tv_cd_min)
    TextView tv_cd_min;
    @BindView(R.id.tv_cd_seconds)
    TextView tv_cd_secodes;
    @BindView(R.id.tv_today_temai)
    TextView tv_today_temai;
    @BindView(R.id.tv_next_temai)
    TextView tv_next_temai;
    @BindView(R.id.layout_empty)View layout_empty;
    int page=0;
    int per=10;
    private List<PromoteBean> promoteBeans;
    private QuickAdapter<PromoteBean> adapter;
    private CountDownTimer countDownTimer;
    private boolean isEnd;


    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_goods_xstm);
        ButterKnife.bind(this);
        lv_xstm.setOnEndOfListListener(this);
        promoteBeans = new ArrayList<>();
        adapter = new QuickAdapter<PromoteBean>(this, R.layout.item_promotion_list) {
            @Override
            protected void convert(BaseAdapterHelper helper, PromoteBean item) {
                helper.setText(R.id.tv_name,item.getGoods_name());
                if(IssApplication.isShowDiscount){
                    helper.setText(R.id.tv_price,"¥"+item.getCost_price());
                }else {
                helper.setText(R.id.tv_price,"¥"+item.getShop_price());
                }
                helper.setText(R.id.tv_store_count,"");
                helper.setText(R.id.tv_sold,"已售"+item.getSales_sum()+"件");
                ImageView iv=helper.getView(R.id.iv_img);
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getGoods_id(),iv, IssApplication.getImageLoaderOption());
            }
        };
        lv_xstm.setAdapter(adapter);
        lv_xstm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MobclickAgent.onEvent(GoodsXsTmActivity.this,"product",""+promoteBeans.get(position).getGoods_name());
                Intent intent=new Intent(GoodsXsTmActivity.this,ProDetailActivity.class);
                intent.putExtra(Constance.product,promoteBeans.get(position).getGoods_id());
                intent.putExtra(Constance.qianggou,true);
                startActivity(intent);
            }
        });
//        getPromotion();
    }

    private void getPromotion() {
        misson(0, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    LogUtils.logE("xstm",res.toString());
                    final JSONArray array=res.getJSONArray(Constance.result);
                    for(int i=0;i<array.length();i++){
                        promoteBeans.add(new Gson().fromJson(array.getJSONObject(i).toString(),PromoteBean.class));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(promoteBeans==null||promoteBeans.size()==0){
                                layout_empty.setVisibility(View.VISIBLE);
                            }else {
                                layout_empty.setVisibility(View.GONE);
                            }
                            adapter.replaceAll(promoteBeans);
                            UIUtils.initListViewHeight(lv_xstm);
                            if(array!=null&&array.length()>0){
                                PromoteBean temp=promoteBeans.get(0);
                                Long timeStamp=temp.getEnd_time();
                                Date date= null;
                                Date date2=null;
                                date = new Date(timeStamp*1000L);
                                date2 =new Date();
                                long timeDis=date.getTime()-date2.getTime();
                                countDownTimer = new CountDownTimer(timeDis,1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        millisUntilFinished=millisUntilFinished/1000L;
                                        int day= (int) (millisUntilFinished/60/60/24);
                                        int hour= (int) ((millisUntilFinished-day*24*60*60)/60/60);
                                        int min=(int)((millisUntilFinished-day*24*60*60-hour*60*60))/60;
                                        int secodes=(int)((millisUntilFinished-day*24*60*60-hour*60*60-min*60));
                                        tv_cd_day.setText((day<10?(""+day):(day+"")));
                                        tv_cd_hour.setText((hour<10?("0"+hour):(hour+"")));
                                        tv_cd_min.setText((min<10?("0"+min):(min+"")));
                                        tv_cd_secodes.setText((secodes<10?("0"+secodes):(secodes+"")));

                                    }

                                    @Override
                                    public void onFinish() {

                                    }
                                };
                                countDownTimer.start();
                            }else {
                                isEnd = true;
                            }
                        }
                    });
                }

            }
        });
    }

    @Override
    public void getData(int type, Callback callback) {
        OkHttpUtils.getPromoteList(page,per,callback);

    }

    @Override
    public void onEndOfList(Object lastItem) {
        if(page==1&&promoteBeans.size()==0||isEnd){
            return;
        }
        page++;
        getPromotion();
    }
}
