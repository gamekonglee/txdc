package bc.yxdc.com.ui.activity.diy;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.widget.FrameLayout;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.ui.fragment.ProgrammerFragment;
import okhttp3.Callback;

/**
 * Created by gamekonglee on 2018/12/26.
 */

public class ProgrammerActivity extends BaseActivity {
    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
    setContentView(R.layout.activity_programme);
    fullScreen(this);
        Fragment fragment=new ProgrammerFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean(Constance.isActivity,true);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_content,fragment).commitAllowingStateLoss();
    }

    @Override
    public void getData(int type, Callback callback) {

    }
}
