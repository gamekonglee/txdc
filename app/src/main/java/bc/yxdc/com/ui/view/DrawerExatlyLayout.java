package bc.yxdc.com.ui.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import android.util.AttributeSet;

/**
 * Created by gamekonglee on 2018/11/23.
 */

public class DrawerExatlyLayout extends DrawerLayout {
    public DrawerExatlyLayout(@NonNull Context context) {
        super(context);
    }

    public DrawerExatlyLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawerExatlyLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        widthSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthSpec), MeasureSpec.EXACTLY);
        heightSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightSpec), MeasureSpec.EXACTLY);
        super.onMeasure(widthSpec, heightSpec);
    }

}
