package com.oldsboy.views.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oldsboy.views.R;
import com.oldsboy.views.TableView;

import java.util.ArrayList;
import java.util.List;

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
public class Dialog_BigTable<T> extends BaseDialog {
    private String table_name;
    private List<String[]> tableHeadList;
    private TableView.onToolBarClick onToolBarClick;
    private TableView.dataSetting<T> dataSetting;
    private TableView.OnBtnClickListener onBtnClickListener;
    private Context context;

    public Dialog_BigTable(@NonNull Context context, String table_name, List<String[]> tableHeadList, TableView.onToolBarClick onToolBarClick, TableView.dataSetting<T> dataSetting, TableView.OnBtnClickListener onBtnClickListener) {
        super(context);
        this.context = context;
        this.table_name = table_name;
        this.tableHeadList = new ArrayList<>(tableHeadList);
        this.onToolBarClick = onToolBarClick;
        this.dataSetting = dataSetting;
        this.onBtnClickListener = onBtnClickListener;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_big_table);

        bindViews();

        initTable();
    }

    private void initTable() {
        try {
            TableView tableView = new TableView<>(context, table_name, tableHeadList, onToolBarClick, dataSetting, onBtnClickListener);
            tableView.setTableTitleCanEnter(false);
            tableView.setNeedOrder(true);
            tableView.setCanScrollVertical(true);
            tableView.showTable((FrameLayout) findViewById(R.id.table1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindViews() {
        ImageView imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        imgBack.setFocusable(true);
        imgBack.setFocusableInTouchMode(true);
        imgBack.requestFocus();
    }
}
