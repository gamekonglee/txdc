package bc.yxdc.com.ui.activity.user;

import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.DbGoodsBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.db.DaoMaster;
import bc.yxdc.com.db.DaoSession;
import bc.yxdc.com.db.DbGoodsBeanDao;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.goods.ProDetailActivity;
import bc.yxdc.com.ui.view.EndOfListView;
import bc.yxdc.com.utils.DateUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bocang.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/11/5.
 */

public class ZujiActivity extends BaseActivity {

    private static final int TYPE_ADD_TO_CART = 1;
    private EndOfListView lv_zuji;
    private QuickAdapter<DbGoodsBean> adapter;
    private List<DbGoodsBean> dbGoodsBeans;
    private int currentPos;
    private boolean isEdit;
    private boolean[] selected;
    private RelativeLayout rl_delete;
    private CheckBox cb_all;
    private TextView tv_delete;
    private TextView tv_edit;
    private View layout;

    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_zuji);
        lv_zuji = findViewById(R.id.lv_zuji);
        tv_edit = findViewById(R.id.tv_edit);
        rl_delete = findViewById(R.id.rl_delete);
        cb_all = findViewById(R.id.cb_all);
        tv_delete = findViewById(R.id.tv_delete);
        layout = findViewById(R.id.layout_empty);

        isEdit = false;
        selected=new boolean[0];
        tv_edit.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
        cb_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for(int i=0;i<selected.length;i++){
                    selected[i]=isChecked;
                }
                adapter.notifyDataSetChanged();
            }
        });
        adapter = new QuickAdapter<DbGoodsBean>(this, R.layout.item_zuji_goods) {
            @Override
            protected void convert(final BaseAdapterHelper helper, DbGoodsBean item) {
                TextView tv_date=helper.getView(R.id.tv_date);
                CheckBox checkbox=helper.getView(R.id.checkbox);
                if(isEdit){
                    checkbox.setVisibility(View.VISIBLE);
                }else {
                    checkbox.setVisibility(View.GONE);
                }
                Date date=new Date(Long.parseLong(item.getCreate_time()));
                tv_date.setText(new SimpleDateFormat("MM月dd号").format(date));
                if(helper.getPosition()==0){
                    tv_date.setVisibility(View.VISIBLE);
                }else {
                    Date offSetDate=new Date(Long.parseLong(dbGoodsBeans.get(helper.getPosition()-1).getCreate_time()));
                    if(date.getDate()!=offSetDate.getDate()){
                        tv_date.setVisibility(View.VISIBLE);
                    }else {
                        tv_date.setVisibility(View.GONE);
                    }
                }
                checkbox.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                helper.setText(R.id.tv_name,item.getName());
                helper.setText(R.id.tv_price,"¥"+item.getCurrent_price());
                ImageView iv_img=helper.getView(R.id.iv_img);
                ImageLoader.getInstance().displayImage(NetWorkConst.IMAGE_URL+item.getId(),iv_img, IssApplication.getImageLoaderOption());
                if(isEdit&&selected!=null&&selected.length>helper.getPosition()){
                    if(selected[helper.getPosition()]){
                        checkbox.setChecked(true);
                    }else {
                        checkbox.setChecked(false);
                    }
                }
                helper.setOnClickListener(R.id.iv_add_cart, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentPos = helper.getPosition();
                        misson(TYPE_ADD_TO_CART, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final JSONObject jsonObject=new JSONObject(response.body().string());
//                                if(jsonObject.getInt(Constance.status)==1){
//
//                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyToast.show(ZujiActivity.this,jsonObject.getString(Constance.msg));
                                    }
                                });

                            }
                        });

                    }
                });

            }
        };
        lv_zuji.setAdapter(adapter);
        load();
        lv_zuji.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(isEdit){
                selected[position]=!selected[position];
                adapter.notifyDataSetChanged();
            }else {
                Intent intent=new Intent(ZujiActivity.this, ProDetailActivity.class);
                intent.putExtra(Constance.product,dbGoodsBeans.get(position).getId());
                startActivity(intent);
            }
            }
        });
    }

    private void load() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "my-db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DbGoodsBeanDao dbGoodsBeanDao=daoSession.getDbGoodsBeanDao();
        dbGoodsBeans = dbGoodsBeanDao.queryBuilder().orderDesc(DbGoodsBeanDao.Properties.Create_time).list();
        adapter.replaceAll(dbGoodsBeans);
        if(dbGoodsBeans==null||dbGoodsBeans.size()==0){
            layout.setVisibility(View.VISIBLE);
        }else {
            layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_delete:
                boolean atLeaseOne=false;
                if(selected!=null){
                for(int i=0;i<selected.length;i++){
                    if(selected[i]){
                        atLeaseOne=true;
                        break;
                    }
                    }
                }
                if(!atLeaseOne){
                    MyToast.show(this,"请至少选中一个产品");
                    return;
                }
                DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "my-db", null);
                DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
                DaoSession daoSession = daoMaster.newSession();
                DbGoodsBeanDao dbGoodsBeanDao=daoSession.getDbGoodsBeanDao();
                for(int i=0;i<selected.length;i++){
                    if(selected[i]){
                        dbGoodsBeanDao.deleteByKey(dbGoodsBeans.get(i).getG_id());
                    }
                }
                MyToast.show(this,"删除成功");
                load();
                tv_edit.performClick();
                break;
            case R.id.tv_edit:
                if(isEdit){
                    isEdit=false;
                    tv_edit.setText("编辑");
                    rl_delete.setVisibility(View.GONE);
                }else {
                    isEdit=true;
                    tv_edit.setText("完成");
                    if(dbGoodsBeans!=null&&dbGoodsBeans.size()>0){
                    selected = new boolean[dbGoodsBeans.size()];
                    }else {
                        selected=null;
                    }
                    rl_delete.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                break;

        }
    }

    @Override
    public void getData(int type, Callback callback) {
        if(type==TYPE_ADD_TO_CART){
            String user_id= MyShare.get(this).getString(Constance.user_id);
            String token=MyShare.get(this).getString(Constance.token);
            if(TextUtils.isEmpty(user_id)||TextUtils.isEmpty(token)){
                MyToast.show(this,"请先登录");
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
                OkHttpUtils.addToShopCart(dbGoodsBeans.get(currentPos).getId()+"",dbGoodsBeans.get(currentPos).getAttr(),1,user_id,token,callback);
        }
    }
}
