package bc.yxdc.com.ui.activity.user;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.global.IssApplication;
import bc.yxdc.com.ui.activity.home.MainActivity;
import bc.yxdc.com.utils.MyShare;
import bc.yxdc.com.utils.MyToast;
import bc.yxdc.com.utils.UIUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Callback;

/**
 * Created by gamekonglee on 2018/10/13.
 */

public class SettingActivity extends BaseActivity{
    @BindView(R.id.ll_clear)
    LinearLayout ll_clear;
    @BindView(R.id.ll_comment)
    LinearLayout ll_comment;
    @BindView(R.id.ll_permission)
    LinearLayout ll_permission;
    @BindView(R.id.btn_logout)
    Button btn_logout;
    @BindView(R.id.tv_version)
    TextView tv_version;

    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        ll_clear.setOnClickListener(this);
        ll_comment.setOnClickListener(this);
        ll_permission.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        tv_version.setText("当前版本号："+UIUtils.getVerName(this));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.ll_clear:
                MyToast.show(this,"清理成功");
                break;
            case R.id.ll_comment:
                MyToast.show(this,"该功能尚未开放");
                break;
            case R.id.ll_permission:
                MyToast.show(this,"该功能尚未开放");
                break;
            case R.id.btn_logout:
                UIUtils.showSingleWordDialog(this, "确定要退出吗？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyShare.get(SettingActivity.this).putString(Constance.token,"");
                        MyShare.get(SettingActivity.this).putString(Constance.user_id,"");
                        IssApplication.mUserBean="";
                        MainActivity.currentTab=0;
                        finish();
                    }
                });
                break;

        }
    }

    @Override
    public void getData(int type, Callback callback) {

    }
}