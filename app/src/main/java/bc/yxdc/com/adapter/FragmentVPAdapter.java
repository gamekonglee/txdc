package bc.yxdc.com.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
//import android.support.v4.view.PagerAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import bc.yxdc.com.ui.fragment.OrderFragment;

/**
 * Created by gamekonglee on 2018/10/10.
 */

public class FragmentVPAdapter extends FragmentPagerAdapter {
    private ArrayList<OrderFragment> fragments;
    private FragmentManager fm;
    private String[] mTitleArrs;
    public FragmentVPAdapter(FragmentManager fm, ArrayList<OrderFragment> fragments, String[] titleArrs) {
        super(fm);
        this.fm = fm;
        this.fragments = fragments;
        this.mTitleArrs=titleArrs;
    }

    public void setFragments(ArrayList<OrderFragment> fragments) {
        if(this.fragments != null){
            FragmentTransaction ft = fm.beginTransaction();
            for(Fragment f:this.fragments){
                ft.remove(f);
            }
            ft.commit();
            ft=null;
            fm.executePendingTransactions();
        }
        this.fragments = fragments;
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position>mTitleArrs.length){
            return "暂未加载";
        }
        return mTitleArrs[position];
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragments.get(arg0);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}