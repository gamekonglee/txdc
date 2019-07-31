package bc.yxdc.com.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.txdc.shop.R;
import bc.yxdc.com.bean.GoodsCategoryBean;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.constant.NetWorkConst;
import bc.yxdc.com.utils.ImageLoadProxy;
import bocang.json.JSONArray;
import bocang.json.JSONObject;

/**
 * @author Jun
 * @time 2017/1/9  10:49
 * @desc ${TODD}
 */
public class ItemClassifyAdapter extends BaseAdapter {
    private List<GoodsCategoryBean> mClassifyGoodses;
    private Context mContext;

    public ItemClassifyAdapter(Context context) {
        this.mContext = context;
    }

    public void setDatas(List<GoodsCategoryBean> classifyGoodses) {
        mClassifyGoodses = classifyGoodses;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null == mClassifyGoodses)
            return 0;
        return mClassifyGoodses.size();
    }

    @Override
    public GoodsCategoryBean getItem(int position) {
        if (null == mClassifyGoodses)
            return null;
        return mClassifyGoodses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_gridview_fm_classify_new, null);

            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.classify_iv);
            holder.textView = (TextView) convertView.findViewById(R.id.classify_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String name = mClassifyGoodses.get(position).getName();
        String thumbs =mClassifyGoodses.get(position).getImage();
        holder.textView.setText(name);
        ImageLoadProxy.displayImage(NetWorkConst.API_HOST+thumbs, holder.imageView);
        /*if (name.length() > 10) {
            holder.textView.setTextSize(13);
            holder.textView.setText(name);
            ImageLoadProxy.displayImage(thumbs, holder.imageView);
        } else {
            holder.textView.setTextSize(15);
            holder.textView.setText(name);
            ImageLoadProxy.displayImage(thumbs, holder.imageView);
        }*/

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
