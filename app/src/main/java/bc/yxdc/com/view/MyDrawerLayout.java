package bc.yxdc.com.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import android.util.AttributeSet;

/**
 * Created by gamekonglee on 2019/5/5.
 */

public class MyDrawerLayout extends DrawerLayout {
    public MyDrawerLayout(@NonNull Context context) {
        super(context);
    }

    public MyDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
