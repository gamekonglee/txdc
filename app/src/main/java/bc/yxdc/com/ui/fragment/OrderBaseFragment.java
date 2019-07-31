package bc.yxdc.com.ui.fragment;

import bc.yxdc.com.base.BaseFragment;
import okhttp3.Callback;

/**
 * Created by gamekonglee on 2018/10/10.
 */

public abstract class OrderBaseFragment extends BaseFragment{

    /** Fragment当前状态是否可见 */
    protected boolean isVisible;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }


    /**
     * 可见
     */
    protected void onVisible() {
        lazyLoad();
    }


    /**
     * 不可见
     */
    protected void onInvisible() {


    }


    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected abstract void lazyLoad();

    @Override
    public void initUI() {

    }

    @Override
    public void getData(int type, Callback callback) {

    }

    @Override
    protected void initData() {

    }
}
