package bc.yxdc.com.ui.activity.user;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.User;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.home.MainActivity;
import bc.yxdc.com.ui.view.TimerButton;
import bc.yxdc.com.utils.CommonUtil;
import bc.yxdc.com.utils.HyUtil;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/10/25.
 */

public class WxBindActivity extends BaseActivity {
    @BindView(R.id.et_mobile)
    EditText et_mobile;
    @BindView(R.id.btn_next)
    Button btn_next;
    private String mobile;
    @BindView(R.id.et_verify)
    EditText et_verify;
    @BindView(R.id.tb_bind)
    TimerButton timerButton;
    private String verify;

    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_wx_bind);
        ButterKnife.bind(this);
        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mobile = et_mobile.getText().toString();
                verify = et_verify.getText().toString();
                if(TextUtils.isEmpty(mobile)){
                    MyToast.show(WxBindActivity.this,"请输入手机号");
                    return;
                }

                if(TextUtils.isEmpty(verify)){
                    MyToast.show(WxBindActivity.this,"请输入验证码");
                    return;
                }
                misson(0, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final JSONObject res=new JSONObject(response.body().string());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyToast.show(WxBindActivity.this,res.getString(Constance.msg));
                                JSONObject result=res.getJSONObject(Constance.result);
                                if(res.getInt(Constance.status)==1) {

                                    String phone=result.getString(Constance.mobile);
                                    String pwd=result.getString(Constance.password);
                                    String token = result.getString(Constance.token);
                                    String user_id=result.getString(Constance.user_id);
                                    MyShare.get(WxBindActivity.this).putString(Constance.username,phone);
                                    MyShare.get(WxBindActivity.this).putString(Constance.pwd,pwd);
                                    MyShare.get(WxBindActivity.this).putString(Constance.TOKEN, token);
                                    MyShare.get(WxBindActivity.this).putString(Constance.user_id,user_id);
                                    MainActivity.currentTab=0;
                                    Intent intent=new Intent(WxBindActivity.this,LoginActivity.class);
                                    intent.putExtra(Constance.user_name,phone);
                                    intent.putExtra(Constance.pwd,pwd);
                                    setResult(200,intent);
                                    EventBus.getDefault().postSticky(new User());
                                    finish();
                                }else if(res.getInt(Constance.status)==0){
                                    if(result==null){
                                        return;
                                    }
                                    Intent intent=new Intent(WxBindActivity.this,RegisterActivity.class);
                                    intent.putExtra(Constance.mobile,et_mobile.getText().toString());
                                    intent.putExtra(Constance.code,verify);
                                    intent.putExtra(Constance.third_oauth,result.getString(Constance.third_oauth));
                                    startActivity(intent);
                                    WxBindActivity.this.finish();

                                }
                            }
                        });


                    }
                });
            }
        });
        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestYZM();
            }
        });

    }

    @Override
    public void onBackPressed() {
        UIUtils.showSingleWordDialog(this, "确定要取消绑定吗？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(300);
                finish();
            }
        });
    }

    @Override
    public void goBack(View v) {
        UIUtils.showSingleWordDialog(this, "确定要取消绑定吗？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(300);
                finish();
            }
        });

    }

    @Override
    public void getData(int type, Callback callback) {
        OkHttpUtils.BindAccount(mobile,verify,callback);
    }
    public void requestYZM() {
        mobile= et_mobile.getText().toString();
        if (HyUtil.isEmpty(mobile)) {
            MyToast.show(this, "请输入手机号码");
            return;
        }
        if (!CommonUtil.isMobileNO(mobile)) {
            MyToast.show(this, "请输入正确的手机号码");
            return;
        }
        timerButton.start(60);
        OkHttpUtils.sendSms(mobile, new Callback() {
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
}
