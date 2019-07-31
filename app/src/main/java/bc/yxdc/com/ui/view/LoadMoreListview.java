package bc.yxdc.com.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import app.txdc.shop.R;
import bc.yxdc.com.utils.LogUtils;

/**
 * Created by gamekonglee on 2018/12/20.
 */

public class LoadMoreListview extends ListView implements AbsListView.OnScrollListener {
    private View v;
    private int headHeight;// 头部的高度
    private int downY;// 按下时候Y坐标

    private final int PULL_REF = 0;// 下拉
    private final int REL_REF = 1;// 松开刷新
    private final int REFING = 2;// 刷新中
    private int currentState = PULL_REF;

    private TextView tv;
//    private TextView tvtiem;
//    private ImageView img;
    private ProgressBar pb;

    private int footHeight;
    private View foot;

    private boolean footstate=false;//当前是否正在处于加载更多
    // 旋转动画
    private RotateAnimation upAnimation, downAnimation;


    public LoadMoreListview(Context context) {
        super(context);
    }

    public LoadMoreListview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadMoreListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**
     * 加载顶部布局文件
     *
     * @param context
     */
    private void initView(Context context) {
        setOnScrollListener(this);
        // LayoutInflater in =LayoutInflater.from(context);
        // v = in.inflate(R.layout.head,null);
        v = View.inflate(context, R.layout.head, null);
        this.addHeaderView(v);
        v.measure(0, 0);// 通知系统测量宽高
        headHeight = v.getMeasuredHeight();// 得到测量后的高度
        v.setPadding(0, -headHeight, 0, 0);// 进行隐藏head,就是把paddingtop设置成负高度

        tv = (TextView) v.findViewById(R.id.head_tv);
//        tvtiem = (TextView) v.findViewById(R.id.head_tvtime);
//        img = (ImageView) v.findViewById(R.id.head_img);
        pb = (ProgressBar) v.findViewById(R.id.foot_pb);
        initFoot();
        initHeadRotateAnimation();


    }
    //加载底部布局
    private void initFoot() {
        foot = View.inflate(getContext(), R.layout.head, null);
        addFooterView(foot);
        foot.measure(0, 0);
        footHeight = foot.getMeasuredHeight();
        foot.setPadding(0, 0, 0,-footHeight);

    }

    private void initHeadRotateAnimation() {
        upAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        upAnimation.setDuration(300);
        upAnimation.setFillAfter(true);
        downAnimation = new RotateAnimation(-180, -360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        downAnimation.setDuration(300);
        downAnimation.setFillAfter(true);

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果是正在刷新状态滑动没有
                if (currentState == REFING) {
                    break;
                }

                int deltaY = (int) (ev.getY() - downY);// 得到移动的距离
                int currentHeight = -headHeight + deltaY;
                // 判断当前的距离是不是大于headHeight 并且显示的是第一个位置
                if (currentHeight > -headHeight && getFirstVisiblePosition() == 0) {
                    v.setPadding(0, currentHeight, 0, 0);// 展现头部
                    if (currentHeight >= 0 && currentState == PULL_REF) {
                        currentState = REL_REF;
                        refHeadView();
                    } else if (currentHeight < 0 && currentState == REL_REF) {
                        currentState = PULL_REF;
                        refHeadView();
                    }
                    return true;// 拦截事件不让listview处理
                }
                break;
            case MotionEvent.ACTION_UP:
                // 判断当前是不是要刷新状态
                if (currentState == PULL_REF) {
                    v.setPadding(0, -headHeight, 0, 0);
                } else if (currentState == REL_REF) {
                    currentState = REFING;
                    v.setPadding(0, 0, 0, 0);
                    refHeadView();
                    if (listener != null) {
                        listener.setPullRfe();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void refHeadView() {
        switch (currentState) {
            case PULL_REF:
                tv.setText("下拉刷新");
//                img.startAnimation(downAnimation);
                break;
            case REL_REF:
                tv.setText("松开刷新");
//                img.startAnimation(upAnimation);
                break;
            case REFING:
                tv.setText("正在刷新……");
//                img.setVisibility(View.GONE);
//                img.clearAnimation();
                pb.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }

    }

    // 刷新完成要把控件和状态重置
    public void completeRef() {
        v.setPadding(0, -headHeight, 0, 0);// 展现头部
        currentState = PULL_REF;
        tv.setText("下拉刷新");
//        img.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
//        tvtiem.setText("最后刷新:" + date);

    }

    private OnRefLisner listener;


    public void setOnRefLisner(OnRefLisner listener) {
        this.listener = listener;
    }

    // 回调接口
    public interface OnRefLisner {
        void setPullRfe();
        void setDownRfe();
    }

    @Override
    public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScrollStateChanged(AbsListView arg0, int state) {
        LogUtils.logE("onscroll",getLastVisiblePosition()+","+getCount());
        if(state==OnScrollListener.SCROLL_STATE_IDLE
                && getLastVisiblePosition()==(getCount()-1)&&footstate==false){
            foot.setPadding(0, 0, 0, 0);
            setSelection(getCount());
            footstate = true;
            if(listener!=null){
                listener.setDownRfe();
            }
        }

    }
    public void completeDown(){
        footstate=false;
        foot.setPadding(0, 0, 0,-footHeight);
    }

}
