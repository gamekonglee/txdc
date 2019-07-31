package bc.yxdc.com.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseFragment;
import bc.yxdc.com.ui.activity.goods.SelectGoodsActivity;
import bc.yxdc.com.utils.IntentUtil;
import okhttp3.Callback;


/**
 * @author Jun
 * @time 2017/1/5  12:00
 * @desc 分类页面
 */
public class ClassifyFragment extends BaseFragment {
    private ViewPager mPager;//页卡内容
    private List<Fragment> listViews; // Tab页面列表
    private TextView t1, t2;// 页卡头标
    private ClassifyGoodsFragment mClassifyGoodsFragment;
    private FilterGoodsFragment mFilterGoodsFragment;
    private RelativeLayout rl_search;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        ((MainActivity)getActivity()).setColor(getActivity(), Color.WHITE);
        return inflater.inflate(R.layout.fm_classify, null);
    }



    class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter
    {

        public MyFrageStatePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            return listViews.get(position);
        }

        @Override
        public int getCount() {
            return listViews.size();
        }

    }

    @Override
    public void initUI() {
        t1 = (TextView) getActivity().findViewById(R.id.text2);
        t2 = (TextView) getActivity().findViewById(R.id.type_02_tv);
        t1.setOnClickListener(new MyOnClickListener(0));
        t2.setOnClickListener(new MyOnClickListener(1));
        mPager = (ViewPager) getActivity().findViewById(R.id.vPager);
        rl_search = getView().findViewById(R.id.rl_search);
        rl_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtil.startActivity(getActivity(), SelectGoodsActivity.class, false);

            }
        });
        listViews = new ArrayList<Fragment>();
        mClassifyGoodsFragment=new ClassifyGoodsFragment();
        mFilterGoodsFragment=new FilterGoodsFragment();
        listViews.add(mClassifyGoodsFragment);
        listViews.add(mFilterGoodsFragment);
        try {
            mPager.setAdapter(new MyFrageStatePagerAdapter(getActivity().getSupportFragmentManager()));
            mPager.setCurrentItem(0);
            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    getCurrentTv(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }catch ( Exception e){

        }


    }

    @Override
    public void getData(int type, Callback callback) {

    }

    @Override
    protected void initData() {

    }

    /**
     * 头标点击监听
     */
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            getCurrentTv(index);
            mPager.setCurrentItem(index);
        }
    }

    private void getCurrentTv(int type){
        regiestTv();
        switch (type){
            case 0:
                t1.setBackgroundResource(R.drawable.classify_shape_pressed);
                t1.setTextColor(getResources().getColor(R.color.white));
                break;
            case 1:
                t2.setBackgroundResource(R.drawable.classify_shape_pressed_right);
                t2.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    private void regiestTv(){
        t1.setBackgroundResource(R.drawable.classify_shape_active);
        t2.setBackgroundResource(R.drawable.classify_shape_active_right);
        t1.setTextColor(getResources().getColor(R.color.theme_red));
        t2.setTextColor(getResources().getColor(R.color.theme_red));
    }


}
