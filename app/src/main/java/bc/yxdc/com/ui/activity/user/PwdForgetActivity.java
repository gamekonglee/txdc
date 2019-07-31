package bc.yxdc.com.ui.activity.user;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.view.TimerButton;
import bc.yxdc.com.utils.CommonUtil;
import bc.yxdc.com.utils.HyUtil;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/10/17.
 */

public class PwdForgetActivity extends BaseActivity {
    @BindView(R.id.et_mobile)
    EditText et_mobile;
    @BindView(R.id.et_verify)
    EditText et_verify;
    @BindView(R.id.tv_foget_pwd)
    TimerButton tv_foget_pwd;
    @BindView(R.id.btn_next)
    Button btn_next;
    @BindView(R.id.iv_clear)
    ImageView iv_clear;

    private String phone;

    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_pwd_forget);
        ButterKnife.bind(this);
        tv_foget_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestYZM();
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_mobile.getText())||TextUtils.isEmpty(et_verify.getText())){
                    MyToast.show(PwdForgetActivity.this,"请输入手机号和验证码");
                    return;
                }
                OkHttpUtils.forgetPwd(et_mobile.getText() + "", "", new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                                    String result=response.body().string();
                                    final JSONObject res=new JSONObject(result);
                                    LogUtils.logE("forPwd",res.toString());
                                    if(res.getInt(Constance.status)==1){
                                        final String mobile=res.getJSONObject(Constance.result).getString(Constance.mobile);
                                        OkHttpUtils.forgetPwd2(mobile, et_verify.getText().toString(), new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {

                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                final JSONObject jsonObject=new JSONObject(response.body().string());
                                                LogUtils.logE("forPwd2",jsonObject.toString());
                                                if(jsonObject.getInt(Constance.status)==1){
                                                    Intent intent=new Intent(PwdForgetActivity.this,ResetPwdActivity.class);
                                                    intent.putExtra(Constance.mobile,mobile);
                                                    startActivity(intent);
                                                    finish();
                                                }else {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            MyToast.show(PwdForgetActivity.this,jsonObject.getString(Constance.msg));
                                                        }
                                                    });
                                                }

                                            }
                                        });
                                    }else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MyToast.show(PwdForgetActivity.this,res.getString(Constance.msg));
                                            }
                                        });
                                    }

                    }
                });
            }
        });
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_mobile.setText("");
            }
        });


    }

    @Override
    public void getData(int type, Callback callback) {

    }

    public void requestYZM() {
        phone = et_mobile.getText().toString();
        if (HyUtil.isEmpty(phone)) {
            MyToast.show(this, "请输入手机号码");
            return;
        }
        if (!CommonUtil.isMobileNO(phone)) {
            MyToast.show(this, "请输入正确的手机号码");
            return;
        }
        tv_foget_pwd.start(60);
        OkHttpUtils.sendSms(phone, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
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
    @Override
    public void onBackPressed() {
        UIUtils.showSingleWordDialog(this, "要取消重置密码吗？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PwdForgetActivity.super.goBack(v);
            }
        });
    }

    @Override
    public void goBack(View v) {
        UIUtils.showSingleWordDialog(this, "要取消重置密码吗？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PwdForgetActivity.super.goBack(v);
            }
        });

    }
}

