package com.oldsboy.views.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * @ProjectName: MyCustomRecyclerTableView
 * @Package: com.oldsboy.views.utils
 * @ClassName: ScreenUtil
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/20 11:38
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/20 11:38
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ScreenUtil {
    private static final String TAG = "ScreenUtil";

    public static int px2dp(Context context, int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px,context.getResources().getDisplayMetrics());
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp,context.getResources().getDisplayMetrics());
    }

    public static void configDialog(Dialog dialog) {
        if (dialog == null) {
            Log.e(TAG, "提供的dialog为null!");
            return;
        }

        if (dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            View decorView = window.getDecorView();
            decorView.setPadding(0, 0, 0, 0);
//            Point point = getDisplayMetrics(dialog.getContext());
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(attributes);
            decorView.setBackgroundResource(android.R.color.white);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.setDimAmount(0.6f);
            window.requestFeature(Window.FEATURE_NO_TITLE);
//            window.setGravity(Gravity.CENTER);

            dialog.setCanceledOnTouchOutside(false);
            dialog.setTitle(null);
        }
    }


    public static Point getDisplayMetrics(Context context) {
        int screenWidth = 0 , screenHeight = 0;
        if (context != null) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
        }
        return new Point(screenWidth ,screenHeight);
    }
}
