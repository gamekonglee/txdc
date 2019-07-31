package bc.yxdc.com.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lib.common.hxp.view.ListViewForScrollView;

import java.util.ArrayList;

import app.txdc.shop.R;
import bc.yxdc.com.base.BaseActivity;
import bc.yxdc.com.bean.Logistics;
import bc.yxdc.com.constant.Constance;
import bc.yxdc.com.db.LogisticDao;
import okhttp3.Callback;

public class UserLogActivity extends BaseActivity {
    private Button btn_save;
    public boolean isSelectLogistics=false;
    private Intent mIntent;
    private ProAdapter mProAdapter;
    private ListViewForScrollView order_sv;
    private int page = 1;
    private View mNullView;
    private View mNullNet;
    private Button mRefeshBtn;
    private TextView mNullNetTv;
    private TextView mNullViewTv;
    private ArrayList<Logistics> mLogisticList;
    private LogisticDao mLogisticDao;
    private ImageView iv;


    @Override
    protected void initData() {
        Intent intent = getIntent();
        isSelectLogistics=intent.getBooleanExtra(Constance.isSelectLogistice, false);
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_user_logistics);
        setColor(this, Color.WHITE);
        btn_save = getViewAndClick(R.id.btn_save);
        order_sv = (ListViewForScrollView) findViewById(R.id.order_sv);
        order_sv.setDivider(null);//去除listview的下划线
        mProAdapter = new ProAdapter();
        order_sv.setAdapter(mProAdapter);
        order_sv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //                if (isSelectLogistics == false) {
                //                    mIntent = new Intent(mView, UserLogAddActivity.class);
                //                    mIntent.putExtra(Constance.logistics, mLogisticList.get(position));
                //                    startActivityForResult(mIntent, Constance.FROMLOG);
                //                }else{
                //                    mIntent=new Intent();
                //                    mIntent.putExtra(Constance.logistics, mLogisticList.get(position));
                //                    setResult(Constance.FROMLOG, mIntent);//告诉原来的Activity 将数据传递给它
                //                    finish();//一定要调用该方法 关闭新的AC 此时 老是AC才能获取到Itent里面的值
                //                }
            }
        });

        mNullView = findViewById(R.id.null_view);
        mNullNet = findViewById(R.id.null_net);
        mRefeshBtn = (Button) mNullNet.findViewById(R.id.refesh_btn);
        mNullNetTv = (TextView) mNullNet.findViewById(R.id.tv);
        mNullViewTv = (TextView) mNullView.findViewById(R.id.tv);
        iv = (ImageView) mNullView.findViewById(R.id.iv);
        mLogisticDao = new LogisticDao(this);

    }

    @Override
    public void getData(int type, Callback callback) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_save:
                mIntent=new Intent(this,UserLogAddActivity.class);
                this.startActivityForResult(mIntent, Constance.FROMLOG);
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        initViewData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constance.FROMLOG) {
            initViewData();
        }
    }

    private void initViewData() {
        mLogisticList = (ArrayList<Logistics>) mLogisticDao.getAll();
        if(mLogisticList.size()==0){
            mNullView.setVisibility(View.VISIBLE);
            iv.setImageResource(R.mipmap.icon_no_address);
        }else{
            mNullView.setVisibility(View.GONE);
        }
        mProAdapter.notifyDataSetChanged();
    }

    private class ProAdapter extends BaseAdapter {
        public ProAdapter() {
        }

        @Override
        public int getCount() {
            if (null == mLogisticList)
                return 0;
            return mLogisticList.size();
        }

        @Override
        public Logistics getItem(int position) {
            if (null == mLogisticList)
                return null;
            return mLogisticList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(UserLogActivity.this, R.layout.item_user_logistic, null);

                holder = new ViewHolder();
                holder.consignee_tv = (TextView) convertView.findViewById(R.id.consignee_tv);
                holder.address_tv = (TextView) convertView.findViewById(R.id.address_tv);
                holder.phone_tv = (TextView) convertView.findViewById(R.id.phone_tv);
                holder.default_addr_tv = (TextView) convertView.findViewById(R.id.default_addr_tv);
                holder.delete_bt = (Button) convertView.findViewById(R.id.delete_bt);
                holder.edit_tv = (Button) convertView.findViewById(R.id.edit_tv);
                holder.ll = (LinearLayout) convertView.findViewById(R.id.ll);
                holder.edit_rl = (RelativeLayout) convertView.findViewById(R.id.edit_rl);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Logistics logistics = mLogisticList.get(position);
            holder.consignee_tv.setText(logistics.getName());
            holder.address_tv.setText(logistics.getAddress());
            holder.phone_tv.setText(logistics.getTel());
            holder.edit_rl.setVisibility(isSelectLogistics==true?View.GONE:View.VISIBLE);
            holder.delete_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(UserLogActivity.this).setTitle(null).setMessage("是否删除该物流地址")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mLogisticDao.deleteOne(mLogisticList.get(position).getId());
                                    initViewData();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            }).show();
                }
            });

            holder.edit_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIntent = new Intent(UserLogActivity.this, UserLogAddActivity.class);
                    mIntent.putExtra(Constance.logistics, mLogisticList.get(position));
                    startActivityForResult(mIntent, Constance.FROMLOG);
                }
            });
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSelectLogistics){
                        mIntent=new Intent();
                        mIntent.putExtra(Constance.logistics, mLogisticList.get(position));
                        setResult(Constance.FROMLOG, mIntent);//告诉原来的Activity 将数据传递给它
                        finish();//一定要调用该方法 关闭新的AC 此时 老是AC才能获取到Itent里面的值
                    }else{
                        mIntent = new Intent(UserLogActivity.this, UserLogAddActivity.class);
                        mIntent.putExtra(Constance.logistics, mLogisticList.get(position));
                        startActivityForResult(mIntent, Constance.FROMLOG);
                    }

                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView consignee_tv;
            TextView address_tv;
            TextView phone_tv;
            TextView default_addr_tv;
            Button delete_bt, edit_tv;
            LinearLayout ll;
            RelativeLayout edit_rl;
        }
    }

//    public void ActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == Constance.FROMLOG) {
//            initViewData();
//        }
//    }
}
