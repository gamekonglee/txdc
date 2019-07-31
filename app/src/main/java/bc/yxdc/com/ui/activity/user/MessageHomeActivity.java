package bc.yxdc.com.ui.activity.user;

import android.content.Intent;
import android.view.View;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.constant.Constance;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Callback;

/**
 * Created by gamekonglee on 2018/11/24.
 */

public class MessageHomeActivity extends BaseActivity {

    private Intent intent;

    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_message_home);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.rl_msg,R.id.rl_activity,R.id.rl_logistics})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_msg:
                intent = new Intent(this,MessageLogiscsActivity.class);
                intent.putExtra(Constance.category,"0");
                startActivity(intent);
                break;
            case R.id.rl_activity:
                startActivity(new Intent(this,MessageYouhuiActivity.class));
                break;
            case R.id.rl_logistics:
                intent =new Intent(this,MessageLogiscsActivity.class);
                intent.putExtra(Constance.category,"1");
                startActivity(intent);
                break;

        }
    }

    @Override
    public void getData(int type, Callback callback) {

    }
}
