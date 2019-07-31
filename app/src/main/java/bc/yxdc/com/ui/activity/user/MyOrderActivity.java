package bc.yxdc.com.ui.activity.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import astuetz.MyPagerSlidingTabStrip;
import bc.yxdc.com.adapter.BaseAdapterHelper;
import bc.yxdc.com.adapter.FragmentVPAdapter;
import bc.yxdc.com.adapter.QuickAdapter;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.GoodsBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.ui.activity.goods.SearchActivity;
import bc.yxdc.com.ui.fragment.OrderFragment;
import bc.yxdc.com.utils.UIUtils;
import okhttp3.Callback;


/**
 * @author: Jun
 * @date : 2017/2/6 11:08
 * @description :我的订单
 */
public class MyOrderActivity extends BaseActivity implements OnItemClickListener {
    public int mOrderType = 0;

    public static String PARTNER;
    public static String SELLER;
    public static String RSA_PRIVATE;
    public static RelativeLayout search_rl;
    private AlertView mAlertViewExt;//窗口拓展例子
    private EditText etName;//拓展View内容
    private InputMethodManager imm;

    private MyPagerSlidingTabStrip mtabs;
    private ViewPager main_viewpager;
    private String[] titleArrs;
    private MyPageChangeListener mListener;
    private OrderFragment mOrderFragment;
    public ArrayList<OrderFragment> listFragment;

    private List<OrderFragment> fragmentList = new ArrayList<OrderFragment>(); //碎片链表
    private List<String> contentList = new ArrayList<String>(); //内容链表


    private void closeKeyboard() {
        //关闭软键盘
        imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
        //恢复位置
        mAlertViewExt.setMarginBottom(0);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mOrderType = intent.getIntExtra(Constance.order_type, 0);
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_myorder);
        //沉浸式状态栏
//        setColor(this, Color.WHITE);
        search_rl = getViewAndClick(R.id.search_rl);
        search_rl.setOnClickListener(this);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //拓展窗口
        mAlertViewExt = new AlertView("提示", "请输入要查询的订单号！", "取消", null, new String[]{"完成"}, this, AlertView.Style.Alert, this);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.alertext_form,null);
        etName = (EditText) extView.findViewById(R.id.etName);
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                //输入框出来则往上移动
                boolean isOpen = imm.isActive();
                mAlertViewExt.setMarginBottom(isOpen && focus ? 120 : 0);
                System.out.println(isOpen);
            }
        });
        mAlertViewExt.addExtView(extView);

        titleArrs = UIUtils.getStringArr(R.array.order_titles);
        main_viewpager = (ViewPager)findViewById(R.id.main_viewpager);
        mtabs = (MyPagerSlidingTabStrip) findViewById(R.id.tabs);
        listFragment = new ArrayList<>();
//        contentList.add("-1");
        contentList.add("0");
        contentList.add("1");
        contentList.add("2");
        contentList.add("3");
        contentList.add("4");

        //有多少个标题就有多少个碎片，动态添加
        for(int i=0;i<titleArrs.length;i++){
            OrderFragment testFm = new OrderFragment().newInstance(contentList, i);
            fragmentList.add(testFm);
        }
        main_viewpager.setAdapter(new FragmentVPAdapter(getSupportFragmentManager(), (ArrayList<OrderFragment>) fragmentList, titleArrs));
        mtabs.setViewPager(main_viewpager);
        mListener=new MyPageChangeListener();
        mtabs.setOnPageChangeListener(mListener);
        main_viewpager.setCurrentItem(mOrderType);

    }

    @Override
    public void getData(int type, Callback callback) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.search_rl:
//                Intent intent=new Intent(this, SearchActivity.class);
//                this.startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(Object o, int position) {
//        closeKeyboard();
//        //判断是否是拓展窗口View，而且点击的是非取消按钮
//        if(o == mAlertViewExt && position != AlertView.CANCELPOSITION){
//            String name = etName.getText().toString();
//            if(name.isEmpty()){
//                MyToast.show(this,"请输入需要查询的订单号!");
//            }
//            else{
//                mController.SearchOrder(name);
//            }
//
//            return;
//        }
    }

    class MyPageChangeListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //当选择后才进行获取数据，而不是预加载
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}
