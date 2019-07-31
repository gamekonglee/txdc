package bc.yxdc.com.view;

import android.content.Context;
import android.util.AttributeSet;

import com.lib.common.hxp.view.PullableScrollView;

import bc.yxdc.com.inter.IScrollViewListener;

/**
 * @author: Jun
 * @date : 2017/5/24 9:54
 * @description :
 */
public class ObservableScrollView extends PullableScrollView {
    private IScrollViewListener scrollViewListener = null;
    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(IScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
//    @Override
//    protected void onMeasure(int widthSpec, int heightSpec) {
//        widthSpec = MeasureSpec.makeMeasureSpec(
//                MeasureSpec.getSize(widthSpec), MeasureSpec.EXACTLY);
//        heightSpec = MeasureSpec.makeMeasureSpec(
//                MeasureSpec.getSize(heightSpec), MeasureSpec.EXACTLY);
//        super.onMeasure(widthSpec, heightSpec);
//    }

}
