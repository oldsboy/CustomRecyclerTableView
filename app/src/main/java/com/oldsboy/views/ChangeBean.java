package com.oldsboy.views;

import java.util.List;

/**
 * @ProjectName: MyCustomRecyclerTableView
 * @Package: com.oldsboy.views
 * @ClassName: ChangeBean
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/16 17:38
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/16 17:38
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ChangeBean {
    private int position;
    private List<String> line;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<String> getLine() {
        return line;
    }

    public void setLine(List<String> line) {
        this.line = line;
    }
}
