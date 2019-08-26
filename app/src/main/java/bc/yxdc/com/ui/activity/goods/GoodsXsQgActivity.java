package bc.yxdc.com.ui.activity.goods;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import bc.yxdc.com.bean.SaleListBean;
import bc.yxdc.com.bean.SaleTimeBean;
import bc.yxdc.com.bean.Time;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
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
 * Created by gamekonglee on 2018/10/26.
 */

public class GoodsXsQgActivity extends BaseActivity {
    private static final int TYPE_SALE_TIME = 1;
    private static final int TYPE_SALE_LIST = 2;
    @BindView(R.id.ll_xsqg)
    LinearLayout ll_xsqg;
    @BindView(R.id.tv_cd_hour) TextView tv_cd_hour;@BindView(R.id.tv_cd_min) TextView tv_cd_min;@BindView(R.id.tv_cd_seconds) TextView tv_cd_secodes;
    @BindView(R.id.lv_xsqg)
    ListView lv_xsqg;
    @BindView(R.id.layout_empty)
    View layout_empty;
    private int currentPostion;
    private String sT;
    private String eT;
    private QuickAdapter<SaleListBean> adapter;
    private List<SaleListBean> saleBeans;
    private List<Time> times;
    private CountDownTimer countDownTimer;

    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_xsqg);
        fullScreen(this);
        ButterKnife.bind(this);
        adapter = new QuickAdapter<SaleListBean>(this, R.layout.item_promotion_list) {
            @Override
            protected void convert(BaseAdapterHelper helper, SaleListBean item) {
                helper.setText(R.id.tv_name,item.getGoods_name());
                helper.setText(R.id.tv_price,"¥"+item.getShop_price());

                helper.setText(R.id.tv_store_count,"库存"+item.getStore_count()+"件");
                helper.setText(R.id.tv_sold,"已售"+(item.getPercent()*item.getStore_count())+"件");
                ImageView iv=helper.getView(R.id.iv_img);
                ProgressBar progressBar=helper.getView(R.id.progressBar);
                progressBar.setProgress(item.getPercent());
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getGoods_id(),iv, IssApplication.getImageLoaderOption());
            }
        };
        lv_xsqg.setAdapter(adapter);

        misson(TYPE_SALE_TIME, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    LogUtils.logE("saleTime",res.toString());
                    SaleTimeBean saleTimeBean=new Gson().fromJson(res.getJSONObject(Constance.result).toString(),SaleTimeBean.class);
                    times = saleTimeBean.getTime();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(times !=null&& times.size()>0){
                                layout_empty.setVisibility(View.GONE);
                                currentPostion = 0;
                                ll_xsqg.removeAllViews();
                                for(int i = 0; i< times.size(); i++){
                                    View view=View.inflate(GoodsXsQgActivity.this,R.layout.layout_xianshi,null);
                                    TextView tv_time=view.findViewById(R.id.tv_time);
                                    TextView tv_status=view.findViewById(R.id.tv_status);
                                    tv_time.setText(""+ times.get(i).getFont());
                                    long currenTime=System.currentTimeMillis()/1000L;
                                    LogUtils.logE("currT",""+currenTime);
                                    if(times.get(i).getStart_time()>currenTime){
                                        tv_status.setText("即将开始");
                                    }else if(times.get(i).getEnd_time()<currenTime){
                                        tv_status.setText("已结束");
                                    }else {
                                        tv_status.setText("抢购中");
                                    }
                                    if(currentPostion!=i){
                                        tv_time.setTextColor(getResources().getColor(R.color.tv_ec9795));
                                        tv_status.setTextColor(getResources().getColor(R.color.tv_ec9795));
                                    }else {
                                        tv_time.setTextColor(Color.WHITE);
                                        tv_status.setTextColor(Color.WHITE);
                                    }
                                    final int finalI = i;
                                    view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            currentPostion= finalI;
                                            for(int j = 0; j< times.size(); j++){
                                                if(currentPostion!=j){
                                                ((TextView)ll_xsqg.getChildAt(j).findViewById(R.id.tv_time)).setTextColor(getResources().getColor(R.color.tv_ec9795));
                                                ((TextView)ll_xsqg.getChildAt(j).findViewById(R.id.tv_status)).setTextColor(getResources().getColor(R.color.tv_ec9795));
                                                }else {
                                                    ((TextView)ll_xsqg.getChildAt(j).findViewById(R.id.tv_time)).setTextColor(Color.WHITE);
                                                    ((TextView)ll_xsqg.getChildAt(j).findViewById(R.id.tv_status)).setTextColor(Color.WHITE);
                                                }
                                            }
                                            getSaleList(times.get(currentPostion).getStart_time(), times.get(currentPostion).getEnd_time());
                                        }
                                    });

                                ll_xsqg.addView(view,new LinearLayout.LayoutParams(UIUtils.dip2PX(75),UIUtils.dip2PX(45)));
                                }

                                getSaleList(times.get(currentPostion).getStart_time(), times.get(currentPostion).getEnd_time());
                            }else {
                                layout_empty.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }

            }
        });
        lv_xsqg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(saleBeans!=null&&position<saleBeans.size()){
                    MobclickAgent.onEvent(GoodsXsQgActivity.this,"product",""+saleBeans.get(position).getGoods_name());
                    Intent intent=new Intent(GoodsXsQgActivity.this, ProDetailActivity.class);
                    intent.putExtra(Constance.product,saleBeans.get(position).getGoods_id());
                    startActivity(intent);
                }
            }
        });
    }

    private void getSaleList(long start_time, long end_time) {
        sT = ""+start_time;
        eT = ""+end_time;
        misson(TYPE_SALE_LIST, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res=new JSONObject(response.body().string());
                if(res.getInt(Constance.status)==1){
                    LogUtils.logE("sale_list",res.toString());
                    JSONArray array=res.getJSONArray(Constance.result);
                    saleBeans = new ArrayList<>();
                    for (int i=0;i<array.length();i++){
                        saleBeans.add(new Gson().fromJson(array.getJSONObject(i).toString(),SaleListBean.class));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(saleBeans==null||saleBeans.size()==0){
                                layout_empty.setVisibility(View.VISIBLE);
                                lv_xsqg.setVisibility(View.GONE);
                            }else {
                                layout_empty.setVisibility(View.GONE);
                                lv_xsqg.setVisibility(View.VISIBLE);
                            }
                            adapter.replaceAll(saleBeans);
                        }
                    });

                }

            }
        });
        long current=System.currentTimeMillis()/1000L;
        if(times.get(currentPostion).getEnd_time()<current){
            if(countDownTimer!=null){
                countDownTimer.onTick(0);
            }
            return;
        }
        Date date= null;
        Date date2=null;
        date = new Date(end_time*1000L);
        date2 =new Date();
        long timeDis=date.getTime()-date2.getTime();
        if(countDownTimer!=null)
        {
          countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(timeDis,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    millisUntilFinished=millisUntilFinished/1000L;
                    int day= (int) (millisUntilFinished/60/60/24);
                    int hour= (int) ((millisUntilFinished-day*24*60*60)/60/60);
                    int min=(int)((millisUntilFinished-day*24*60*60-hour*60*60))/60;
                    int secodes=(int)((millisUntilFinished-day*24*60*60-hour*60*60-min*60));
                    tv_cd_hour.setText((hour<10?("0"+hour):(hour+"")));
                    tv_cd_min.setText((min<10?("0"+min):(min+"")));
                    tv_cd_secodes.setText((secodes<10?("0"+secodes):(secodes+"")));

                }

                @Override
                public void onFinish() {

                }
            };
        countDownTimer.start();
    }

    @Override
    public void getData(int type, Callback callback) {
        if(type==TYPE_SALE_TIME){
            OkHttpUtils.getSaleTime(callback);
        }else if(type==TYPE_SALE_LIST){
            OkHttpUtils.getSaleTimeList(sT,eT,callback);
        }

    }
}
