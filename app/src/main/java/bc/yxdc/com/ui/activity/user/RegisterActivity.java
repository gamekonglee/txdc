package bc.yxdc.com.ui.activity.user;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.IOException;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.home.MainActivity;
import bc.yxdc.com.ui.view.TimerButton;
import bc.yxdc.com.utils.AppUtils;
import bc.yxdc.com.utils.CommonUtil;
import bc.yxdc.com.utils.HyUtil;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONObject;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/9/17.
 */

public class  RegisterActivity extends BaseActivity {
    private EditText edtPhone, edtCode, edPwd, edtAffirmPwd, nikname_et;
    private String mPhone;
    private Button sure_bt;
    public TimerButton find_pwd_btnGetCode;
    private String phone;
    private String code;
    private EditText et_nickname;
    private RelativeLayout rl_phone;
    private RelativeLayout rl_code;
    private boolean isSetting;
    private String third_oauth;


    @Override
    protected void initData() {
        phone = getIntent().getStringExtra(Constance.mobile);
        code = getIntent().getStringExtra(Constance.code);
        third_oauth = getIntent().getStringExtra(Constance.third_oauth);
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_register);
        setStatuTextColor(this, Color.WHITE);
        setFullScreenColor(Color.TRANSPARENT,this);
        sure_bt=getViewAndClick(R.id.sure_bt);
        find_pwd_btnGetCode=getViewAndClick(R.id.find_pwd_btnGetCode);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtCode = (EditText) findViewById(R.id.edtCode);
        edPwd = (EditText) findViewById(R.id.edPwd);
        edtAffirmPwd = (EditText) findViewById(R.id.edtAffirmPwd);
        et_nickname = findViewById(R.id.et_nickname);
        rl_phone = findViewById(R.id.rl_phone);
        rl_code = findViewById(R.id.rl_code);

        if(!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(code)){
            rl_code.setVisibility(View.GONE);
            rl_phone.setVisibility(View.GONE);
            edtPhone.setText(""+phone);
            edtCode.setText(""+code);
            isSetting = true;
        }
//        SMSSDK.initSDK(this, "1eba557757363", "29cd2e2ce4e9087bd43129580161b82c");
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        String deviceId = UIUtils.getLocalMac(RegisterActivity.this);
                        String code = edtCode.getText().toString() + "11";
                        String pwd = edPwd.getText().toString();
                        String nickName = nikname_et.getText().toString();
//                        mNetWork.sendRegiest(deviceId, mPhone, pwd, code, yaoqing, nickName, RegiestController.this);
                        //提交验证码成功
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                    try {
                        Throwable throwable = (Throwable) data;
                        throwable.printStackTrace();
                        JSONObject object = new JSONObject(throwable.getMessage());
                        final String des = object.getString("detail");//错误描述
                        int status = object.getInt("status");//错误代码
                        if (status > 0 && !TextUtils.isEmpty(des)) {
                            Log.e("asd", "des: " + des);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MyToast.show(RegisterActivity.this, des);
                                }
                            });

                            return;
                        }
                    } catch (Exception e) {
                        //do something
                    }
                }
            }
        };

//        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    @Override
    public void getData(int type, Callback callback) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.sure_bt:
                sendRegiest();
                break;
            case R.id.find_pwd_btnGetCode:
                requestYZM();
                break;
        }
    }



    public void goBack(View v){
        UIUtils.showSingleWordDialog(this, "你是否放弃当前注册?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void sendRegiest() {
        mPhone = edtPhone.getText().toString();
        String code = edtCode.getText().toString();
        final String pwd = edPwd.getText().toString();
//        String nikName = nikname_et.getText().toString();
        String affirmPwd = edtAffirmPwd.getText().toString();
        String nickname=et_nickname.getText().toString();
        String deviceId = UIUtils.getLocalMac(this);

        if (AppUtils.isEmpty(mPhone)) {
            MyToast.show(this,UIUtils.getString(R.string.isnull_phone));
            return;
        }
        if (AppUtils.isEmpty(code)) {
            MyToast.show(this,UIUtils.getString(R.string.isnull_verification_code));
            return;
        }
        if (AppUtils.isEmpty(pwd)) {
            MyToast.show(this,UIUtils.getString(R.string.isnull_pwd));
            return;
        }
        if (AppUtils.isEmpty(affirmPwd)) {
            MyToast.show(this,UIUtils.getString(R.string.isnull_affirm_pwd));
            return;
        }
//        if (AppUtils.isEmpty(nikName)) {
//            MyToast.show(this,"昵称不能为空!");
//            return;
//        }

        // 做个正则验证手机号
        if (!CommonUtil.isMobileNO(mPhone)) {
            MyToast.show(this,UIUtils.getString(R.string.mobile_assert));
            return;
        }

        if (!affirmPwd.equals(pwd)) {
            MyToast.show(this,UIUtils.getString(R.string.compare_pwd_affirm));
            return;
        }
//        setShowDialog(true);
//        setShowDialog("正在注册中..");
//        showLoading();
//        mNetWork.sendRegiest(deviceId, mPhone, pwd, code, yaoqing, nikName, RegiestController.this);
        if(isSetting){
            if(TextUtils.isEmpty(nickname)){
                MyToast.show(RegisterActivity.this,"请输入昵称");
                return;
            }
            String push_id=MyShare.get(this).getString(Constance.RegistrationID);
            OkHttpUtils.bindReg(mPhone,pwd,code,nickname, push_id,third_oauth,new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
//                    String reuslt=response.body().string();
//                    LogUtils.logE("r",reuslt);
                    JSONObject jsonObject=new JSONObject(response.body().string());
                    int status=jsonObject.getInt(Constance.status);
                    if(status==1){
                        String token = jsonObject.getString(Constance.token);
                        String id = jsonObject.getInt(Constance.user_id)+"";
                        MyShare.get(RegisterActivity.this).putString(Constance.TOKEN, token);//保存TOKEN
                        MyShare.get(RegisterActivity.this).putString(Constance.USERNAME, mPhone);//保存帐号
                        MyShare.get(RegisterActivity.this).putString(Constance.USERID, id);//保存帐号
//                    AppDialog.messageBox(UIUtils.getString(R.string.regiest_ok));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyToast.show(RegisterActivity.this,"绑定成功！");
                            }
                        });
////                    sendRegiestSuccess();
//                        Intent logoutIntent = new Intent(RegisterActivity.this, MainActivity.class);
////                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(logoutIntent);
//                        finish();

                        Intent logoutIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        logoutIntent.putExtra(Constance.user_name,""+mPhone);
                        logoutIntent.putExtra(Constance.pwd,""+pwd);
//                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logoutIntent);
                        finish();
                }
            }});
        }else {
            String regId=MyShare.get(this).getString(Constance.RegistrationID);
            OkHttpUtils.register(mPhone,pwd,affirmPwd,code,regId, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String reuslt=response.body().string();
                    LogUtils.logE("r",reuslt);
                    final JSONObject jsonObject=new JSONObject(reuslt);
                    int status=jsonObject.getInt(Constance.status);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.show(RegisterActivity.this,jsonObject.getString(Constance.msg));
                        }
                    });
                    if(status==1){
                        String token = jsonObject.getString(Constance.token);
                        String id = jsonObject.getInt(Constance.user_id)+"";
                        MyShare.get(RegisterActivity.this).putString(Constance.TOKEN, token);//保存TOKEN
                        MyShare.get(RegisterActivity.this).putString(Constance.USERNAME, mPhone);//保存帐号
                        MyShare.get(RegisterActivity.this).putString(Constance.USERID, id);//保存帐号
//                    AppDialog.messageBox(UIUtils.getString(R.string.regiest_ok));
//                    sendRegiestSuccess();
                        Intent logoutIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        logoutIntent.putExtra(Constance.user_name,""+mPhone);
                        logoutIntent.putExtra(Constance.pwd,""+pwd);
//                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logoutIntent);
                        finish();
                    }
                }
            });
        }

    }

    public void requestYZM() {
        mPhone = edtPhone.getText().toString();
        if (HyUtil.isEmpty(mPhone)) {
            MyToast.show(this, "请输入手机号码");
            return;
        }
        if (!CommonUtil.isMobileNO(mPhone)) {
            MyToast.show(this, "请输入正确的手机号码");
            return;
        }
        find_pwd_btnGetCode.start(60);
        OkHttpUtils.sendSms(mPhone, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("smsF",e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.e("sms",response.body().string());

            }
        });
        //
//        mNetWork.sendRequestYZM(mPhone, this);
        //        mNetWork.sendRequestYZM(mPhone,this);
    }
}
