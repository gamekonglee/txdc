package bc.yxdc.com.ui.activity.buy;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.ui.fragment.CartFragment;
import okhttp3.Callback;

/**
 * Created by gamekonglee on 2018/9/14.
 */

public class  ShoppingCartActivity extends BaseActivity{

    private LinearLayout ll_flame;
    private View back_ll;

    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.layout_shoppping_cart);
        setStatuTextColor(this, Color.WHITE);
        setFullScreenColor(Color.TRANSPARENT,this);
        ll_flame = findViewById(R.id.ll_flame);
        back_ll = findViewById(R.id.back_ll);
        CartFragment cartFragment=new CartFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean(Constance.product,true);
        cartFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.ll_flame,cartFragment ).commitAllowingStateLoss();
    }

    @Override
    public void getData(int type, Callback callback) {

    }
}
