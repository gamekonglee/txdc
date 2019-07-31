package bc.yxdc.com.inter;

import bc.yxdc.com.view.ObservableScrollView;

/**
 * Created by gamekonglee on 2018/10/27.
 */

public interface IScrollViewListener {
    void onScrollChanged(ObservableScrollView observableScrollView, int x, int y, int oldx, int oldy);
}
