package bc.yxdc.com.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.Logistics;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.db.LogisticDao;
import bc.yxdc.com.utils.AppUtils;
import bc.yxdc.com.utils.CommonUtil;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import okhttp3.Callback;

public  class UserLogAddActivity extends BaseActivity {
    private EditText logistics_name_tv, logistics_phone_tv, logistics_address_tv;
    private String mId;

    @Override
    public void initUI() {
        setContentView(R.layout.activity_user_logistics_add);
        setColor(this, Color.WHITE);
        btn_save = getViewAndClick(R.id.btn_save);
        logistics_name_tv = (EditText) findViewById(R.id.logistics_name_tv);
        logistics_phone_tv = (EditText) findViewById(R.id.logistics_phone_tv);
        logistics_address_tv = (EditText) findViewById(R.id.logistics_address_tv);


        if(AppUtils.isEmpty(mLogistics))return;
        logistics_name_tv.setText(mLogistics.getName());
        logistics_address_tv.setText(mLogistics.getAddress());
        logistics_phone_tv.setText(mLogistics.getTel());
        mId = mLogistics.getId()+"";
        
    }

    @Override
    public void getData(int type, Callback callback) {

    }
    private Button btn_save;
    private Intent mIntent;
    public Logistics mLogistics;




    @Override
    protected void initData() {
        mIntent=getIntent();
        mLogistics= (Logistics) mIntent.getSerializableExtra(Constance.logistics);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_save:
                sendAddLogistic();
                break;
        }
    }


    public void goBack(View v){
        UIUtils.showSingleWordDialog(this, "你的物流地址还未保存，是否放弃保存?", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    /**
     * 保存物流
     */
    public void sendAddLogistic() {
        String name=logistics_name_tv.getText().toString();
        String phone=logistics_phone_tv.getText().toString();
        String address=logistics_address_tv.getText().toString();
        if (AppUtils.isEmpty(name)) {
            MyToast.show(this, "物流公司名称不能为空!");
            return;
        }
//        if (AppUtils.isEmpty(phone)) {
//            MyToast.show(this, "电话不能为空!");
//            return;
//        }
//
//        // 做个正则验证手机号
//        if (!CommonUtil.isMobileNO(phone)) {
//            MyToast.show(this, UIUtils.getString(R.string.mobile_assert));
//            return;
//        }
//        if (AppUtils.isEmpty(address)) {
//            MyToast.show(this, "物流地址不能为空!");
//            return;
//        }

        LogisticDao dao=new LogisticDao(this);
        Logistics logistics=new Logistics();
//        logistics.setpId(System.currentTimeMillis() + "");
        logistics.setName(name);
        logistics.setTel(phone);
        logistics.setAddress(address);
        long isSave = dao.replaceOne(logistics);
        if(isSave==-1){
            MyToast.show(this,"保存失败");
        }else{
            MyToast.show(this,"保存成功");
            if(!AppUtils.isEmpty(mId)){
                dao.deleteOne(Integer.parseInt(mId));
            }
            mIntent=new Intent();
            setResult(Constance.FROMLOG, mIntent);//告诉原来的Activity 将数据传递给它
            finish();//一定要调用该方法 关闭新的AC 此时 老是AC才能获取到Itent里面的值
        }


    }

}
