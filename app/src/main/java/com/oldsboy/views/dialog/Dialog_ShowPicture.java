package com.oldsboy.views.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oldsboy.views.R;
import com.oldsboy.views.utils.BitmapsUtil;
import com.oldsboy.views.utils.FileUtil;

/**
 * @ProjectName: MyCustomRecyclerTableView
 * @Package: com.oldsboy.views.activity
 * @ClassName: Dialog_ShowPicture
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/20 17:29
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/20 17:29
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class Dialog_ShowPicture extends BaseDialog {
    private String img_path;

    private ImageView imgContent;

    public Dialog_ShowPicture(@NonNull Context context, String img_path) {
        super(context);
        this.img_path = img_path;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_show_picture);
        
        bindViews();
        showPicture();
    }

    private void showPicture() {
        if (imgContent != null) {
            if (img_path == null || img_path.length() == 0) {
                imgContent.setImageBitmap(null);
            } else if (FileUtil.isFileExists(img_path)) {
                imgContent.setImageBitmap(BitmapsUtil.decodeFilePath(img_path));
            } else {
                Log.e("showPicture", "图片损坏！图片路径：" + img_path);
                imgContent.setImageResource(R.drawable.custom_picture_break);
            }
        }
    }

    private void bindViews() {
        ImageView imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgContent != null) {
                    imgContent.setImageBitmap(null);
                    imgContent.setImageDrawable(null);
                }
                dismiss();
            }
        });
        imgContent = (ImageView) findViewById(R.id.img_content);
    }
}
