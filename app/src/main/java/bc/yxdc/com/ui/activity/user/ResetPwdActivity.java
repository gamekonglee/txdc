package bc.yxdc.com.ui.activity.user;

import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.MyShare;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.home.MainActivity;
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

public class ResetPwdActivity extends BaseActivity {
    @BindView(R.id.et_new_pwd)
    EditText et_new_pwd;
    @BindView(R.id.btn_reset)
    Button btn_reset;
    @BindView(R.id.iv_pwd_reset)
    ImageView iv_pwd_reset;

    private String mobile;
    private boolean canSee;


    @Override
    protected void initData() {
        mobile = getIntent().getStringExtra(Constance.mobile);

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_reset_pwd);
        ButterKnife.bind(this);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd=et_new_pwd.getText().toString();
                if(TextUtils.isEmpty(pwd)){
                    MyToast.show(ResetPwdActivity.this,"请填写新密码");
                    return;
                }
                OkHttpUtils.forgetPwd3(mobile, pwd, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final JSONObject res=new JSONObject(response.body().string());
                        if(res.getInt(Constance.status)==1){
                            MyShare.get(ResetPwdActivity.this).putString(Constance.token,"");
                            MyShare.get(ResetPwdActivity.this).putString(Constance.user_id,"");
                            MainActivity.currentTab=0;
                            finish();

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MyToast.show(ResetPwdActivity.this,res.getString(Constance.msg));
                                }
                            });

                        }

                    }
                });
            }
        });
        iv_pwd_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canSee){
                    canSee=false;
                    et_new_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    iv_pwd_reset.setImageDrawable(getResources().getDrawable(R.mipmap.icon_password_default));
                }else {
                    canSee=true;
                    et_new_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    iv_pwd_reset.setImageDrawable(getResources().getDrawable(R.mipmap.icon_password_seleted));

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        UIUtils.showSingleWordDialog(this, "要取消重置密码吗？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPwdActivity.super.goBack(v);
            }
        });
    }

    @Override
    public void goBack(View v) {
        UIUtils.showSingleWordDialog(this, "要取消重置密码吗？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPwdActivity.super.goBack(v);
            }
        });

    }

    @Override
    public void getData(int type, Callback callback) {

    }
}
