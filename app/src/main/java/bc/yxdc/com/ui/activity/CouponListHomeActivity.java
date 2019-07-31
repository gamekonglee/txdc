package bc.yxdc.com.ui.activity;

import android.graphics.drawable.Drawable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.ui.fragment.coupon.CouponGetListFragment;
import bc.yxdc.com.ui.fragment.CouponMineFragment;
import bc.yxdc.com.view.TextViewPlus;
import okhttp3.Callback;

/**
 * Created by gamekonglee on 2018/10/29.
 */

public class CouponListHomeActivity extends BaseActivity {

    private TextViewPlus tv_couponList;
    private TextViewPlus tv_coupinMine;
    private int current;
    private Fragment couponListFrag;
    private CouponMineFragment mineFragment;

    @Override
    protected void initData() {

    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_coupon_home);
        FrameLayout fl_content=findViewById(R.id.fl_content);
        tv_couponList = findViewById(R.id.tv_coupon_list);
        tv_coupinMine = findViewById(R.id.tv_coupon_mine);
        couponListFrag = new CouponGetListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_content, couponListFrag).commit();
        current = 0;
        tv_couponList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current==0){
                    return;
                }
                current=0;
                Drawable drawable=getResources().getDrawable(R.mipmap.icon_yhq_default);
                drawable.setBounds(0,0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
                tv_coupinMine.setCompoundDrawables(null,drawable,null,null);
                tv_coupinMine.setTextColor(getResources().getColor(R.color.tv_333333));

                Drawable drawable1=getResources().getDrawable(R.mipmap.iocn_lq_selected);
                drawable1.setBounds(0,0,drawable1.getMinimumWidth(),drawable1.getMinimumHeight());
                tv_couponList.setCompoundDrawables(null,drawable1,null,null);
                tv_couponList.setTextColor(getResources().getColor(R.color.theme_red));
                if(couponListFrag==null)couponListFrag=new CouponGetListFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_content,couponListFrag).commit();
            }
        });
        tv_coupinMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current==1){
                    return;
                }
                current=1;

                Drawable drawable=getResources().getDrawable(R.mipmap.icon_yhq_selected);
                drawable.setBounds(0,0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
                tv_coupinMine.setCompoundDrawables(null,drawable,null,null);
                tv_coupinMine.setTextColor(getResources().getColor(R.color.theme_red));

                Drawable drawable1=getResources().getDrawable(R.mipmap.iocn_lq_default);
                drawable1.setBounds(0,0,drawable1.getMinimumWidth(),drawable1.getMinimumHeight());
                tv_couponList.setCompoundDrawables(null,drawable1,null,null);
                tv_couponList.setTextColor(getResources().getColor(R.color.tv_333333));

                if(mineFragment==null)mineFragment = new CouponMineFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_content,mineFragment).commit();
            }
        });

    }

    @Override
    public void getData(int type, Callback callback) {

    }
}
