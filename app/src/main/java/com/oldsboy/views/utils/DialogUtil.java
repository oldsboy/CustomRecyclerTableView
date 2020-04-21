package com.oldsboy.views.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.oldsboy.views.R;

/**
 * @ProjectName: MyCustomRecyclerTableView
 * @Package: com.oldsboy.views.utils
 * @ClassName: DialogUtil
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/17 17:52
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/17 17:52
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class DialogUtil {
    public static void showBottomDialog(final Activity activity, final int request_code, final int request_code_pic){
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(activity, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(activity, R.layout.dialog_custom_layout,null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        if (window != null) {
            //设置弹出位置
            window.setGravity(Gravity.BOTTOM);
            //设置弹出动画
            window.setWindowAnimations(R.style.main_menu_animStyle);
            //设置对话框大小
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();

        dialog.findViewById(R.id.tv_take_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(activity.getPackageManager()) != null){            //  不指定文件存储地点
                    activity.startActivityForResult(intent, request_code_pic);
                }else {
                    Toast.makeText(activity, "打开相机失败", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.tv_take_alb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activity.startActivityForResult(intent, request_code);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
