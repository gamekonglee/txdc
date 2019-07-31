package bc.yxdc.com.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.txdc.shop.R;

/**
 * Created by gamekonglee on 2018/11/8.
 */

public class SchemeTypeGvAdapter extends BaseAdapter {
    private List<String> mProgrammes;
    private Activity mContext;
    private Intent mIntent;
    private List<Boolean> mIsClick;

    public SchemeTypeGvAdapter(Activity context,List<String> programmes) {
        mContext=context;
        mProgrammes=programmes;
        mIsClick = new ArrayList<>();
        for (int i = 0; i < mProgrammes.size(); i++) {
            if (i == 0) {
                mIsClick.add(true);
            } else {
                mIsClick.add(false);
            }
        }

    }

    public void setIsClick(int poistion,Boolean isclick){
        for(int i=0;i<mProgrammes.size();i++){
            mIsClick.set(i,false);
        }
        mIsClick.set(poistion,isclick);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null == mProgrammes)
            return 0;
        return mProgrammes.size();
    }

    @Override
    public String getItem(int position) {
        if (null == mProgrammes)
            return null;
        return mProgrammes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_scheme_type02, null);

            holder = new ViewHolder();
            holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String name=mProgrammes.get(position);
        holder.name_tv.setText(name);
        holder.name_tv.setBackgroundResource(mIsClick.get(position)==false? R.drawable.bg_corner_white: R.drawable.bg_corner_red_empty);
        holder.name_tv.setTextColor(mContext.getResources().getColor(mIsClick.get(position)==false?R.color.tv_333333:R.color.theme_red));
        return convertView;
    }

    class ViewHolder {
        TextView name_tv;
    }




}

