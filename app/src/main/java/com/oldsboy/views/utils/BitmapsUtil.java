package com.oldsboy.views.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @ProjectName: MyCustomRecyclerTableView
 * @Package: com.oldsboy.views.utils
 * @ClassName: BitmapsUtil
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/16 16:17
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/16 16:17
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class BitmapsUtil {
    public static Bitmap decodeFilePath(String filePath) {
        if (StringUtil.isEmpty(filePath)) {
            return null;
        }
        return decodeFile(new File(filePath));
    }

    /**
     * 解码图像用来减少内存消耗
     */
    private static Bitmap decodeFile(File f) {
        if (f == null) {
            return null;
        }
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inPreferredConfig = Bitmap.Config.RGB_565;
        o.inDither = false;
        o.inPurgeable = true;
        o.inInputShareable = true;
        o.inTempStorage = new byte[4 * 1024];
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(f);
            return BitmapFactory.decodeFileDescriptor(fs.getFD(), null, o);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.close(fs);
        }
        return null;
    }
}
