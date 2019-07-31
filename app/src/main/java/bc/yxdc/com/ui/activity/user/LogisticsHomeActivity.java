package bc.yxdc.com.ui.activity.user;

import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.LogisticsBean;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Callback;

/**
 * Created by gamekonglee on 2018/9/26.
 */

public class LogisticsHomeActivity extends BaseActivity {
    @BindView(R.id.lv_logistics)
    ListView lv_logistics;
    @BindView(R.id.btn_add_logistics)
    Button btn_logistics;
    private QuickAdapter<LogisticsBean> adapter;
    private List<LogisticsBean> logisticsBeans;

    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_logistics_home);
        ButterKnife.bind(this);
        adapter = new QuickAdapter<LogisticsBean>(this,R.layout.item_logistics) {
            @Override
            protected void convert(BaseAdapterHelper helper, LogisticsBean item) {
                helper.setText(R.id.tv_log_name,item.getName());
                helper.setText(R.id.tv_log_mobile,item.getPhone());

            }
        };
        lv_logistics.setAdapter(adapter);
        logisticsBeans = new ArrayList<>();
    }

    @Override
    public void getData(int type, Callback callback) {

    }
}
