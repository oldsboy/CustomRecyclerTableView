package com.oldsboy.views.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * @ProjectName: MyCustomRecyclerTableView
 * @Package: com.oldsboy.views.utils
 * @ClassName: FileUtil
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/16 16:20
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/16 16:20
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class FileUtil {
    /**
     * 判断文件存不存在
     */
    public static boolean isFileExists(String path) {
        if (StringUtil.isEmpty(path)) {
            return false;
        }
        File f = new File(path);
        return f.exists();
    }

    /**
     * 关闭所有可关闭的流
     *
     * @param closeable 可关闭的流
     */
    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
        }
    }
}
