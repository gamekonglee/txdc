package bc.yxdc.com.ui.activity.user;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.IOException;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.User;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.MyShare;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.net.OkHttpUtils;
import bc.yxdc.com.ui.activity.home.MainActivity;
import bc.yxdc.com.utils.LogUtils;
import bc.yxdc.com.utils.MyToast;
import bocang.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gamekonglee on 2018/10/18.
 */

public class ChangePwdActivity extends BaseActivity{
    @BindView(R.id.et_old_pwd)
    EditText et_old;
    @BindView(R.id.et_new_pwd)
    EditText et_new_pwd;
    @BindView(R.id.et_new_pwd2)
    EditText et_new_pwd2;
    @BindView(R.id.btn_save)
    Button btn_save;
    private User user;

    @Override
    protected void initData() {
        user = new Gson().fromJson(IssApplication.mUserBean, User.class);
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_change_pwd);
        ButterKnife.bind(this);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpwd=et_old.getText().toString();
                String newpwd=et_new_pwd.getText().toString();
                String newpwd2=et_new_pwd2.getText().toString();
                if(TextUtils.isEmpty(oldpwd)||TextUtils.isEmpty(newpwd)||TextUtils.isEmpty(newpwd2)){
                    MyToast.show(ChangePwdActivity.this,"请完整填写密码");
                    return;
                }
                if(!newpwd.equals(newpwd2)){
                    MyToast.show(ChangePwdActivity.this,"两次输入的密码不一致");
                    return;
                }
                String token=MyShare.get(ChangePwdActivity.this).getString(Constance.token);

                OkHttpUtils.changePwd(user.getUser_id()+"",token,oldpwd.trim(),newpwd.trim(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final JSONObject res=new JSONObject(response.body().string());
                        LogUtils.logE("changePwd",res.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyToast.show(ChangePwdActivity.this,res.getString(Constance.msg));
                                if(res.getInt(Constance.status)==1){
                                    MyShare.get(ChangePwdActivity.this).putString(Constance.token,"");
                                    MyShare.get(ChangePwdActivity.this).putString(Constance.user_id,"");
                                    MainActivity.currentTab=0;
                                    startActivity(new Intent(ChangePwdActivity.this,MainActivity.class));
                                    finish();
                                }

                            }
                        });

                    }
                });

            }
        });
    }

    @Override
    public void getData(int type, Callback callback) {

    }
}
