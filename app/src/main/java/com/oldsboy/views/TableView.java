package com.oldsboy.views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.oldsboy.views.utils.StringUtil.isEmpty;

/**
 * @ProjectName: FP-app
 * @Package: com.medicine.fxpg.view
 * @ClassName: CustomTableView
 * @Description: 表格控件，该控件只能代码生成，不能再xml内配置；配合CustomTableAdapter使用
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/2 15:33
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/2 15:33
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class TableView<T> extends LinearLayout {
    public static final String TAG = "customTableView";
    private Context context;

    /**
     * view
     * **/
    private TextView tv_table_name;
    private RelativeLayout btn_add, btn_edit, btn_delete, btn_save;
    private LinearLayout container_tablehead;
    private RecyclerView recycler_tablebody;

    /**
     * data/setting
     * **/
    private String table_name;
    private List<String[]> tableHeadList;
    private boolean hideId = true;          //  是否隐藏第一列的id
    private List<List<String[]>> table_list;
    private List<String> cellWidthPer;
    private String picture_base_path;

    private LinearLayoutManager linearLayoutManager;

    /**
     * interface
     * **/
    private TableView.onToolBarClick onToolBarClick;
    private TableView.dataSetting<T> dataSetting;

    public TableRecyclerAdapter tableRecyclerAdapter;

    public void setScrollListView(boolean canScroll){
        if (canScroll){
            linearLayoutManager = new LinearLayoutManager(context){
                @Override
                public boolean canScrollVertically() {
                    return true;
                }
            };
        }else {
            linearLayoutManager = new LinearLayoutManager(context){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
        }
        recycler_tablebody.setLayoutManager(linearLayoutManager);
    }

    public void setHideId(boolean hideId) {
        this.hideId = hideId;
    }

    public void setOnToolBarClick(TableView.onToolBarClick onToolBarClick) {
        this.onToolBarClick = onToolBarClick;
    }

    public void setDataSetting(TableView.dataSetting<T> dataSetting) {
        this.dataSetting = dataSetting;
    }

    public void setPicture_base_path(String picture_base_path) {
        this.picture_base_path = picture_base_path;
    }

    public @interface ItemEditType{
        String textInput = "0";
        String textSpinner = "1";
        String imageSelect = "2";
    }

    /**
     *
     * @param context
     * @param table_name                设置表名
     * @param tableHeadList             表头配置（String[]{字段名， 宽度， 是否需要下拉框配置（0，1）}
     * @param onToolBarClick            工具栏配置
     * @param dataSetting               数据源配置
     */
    public TableView(Context context, String table_name, List<String[]> tableHeadList, onToolBarClick onToolBarClick, dataSetting<T> dataSetting, OnBtnClickListener onBtnClickListener) {
        super(context);
        this.context = context;
        this.table_name = table_name;
        this.tableHeadList = tableHeadList;
        this.onToolBarClick = onToolBarClick;
        this.dataSetting = dataSetting;
        this.picture_base_path = context.getCacheDir().getAbsolutePath();
        this.onBtnClickListener = onBtnClickListener;

        ((LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_table_recycler_view, this);
        bindViews();
    }

    public void showTable(FrameLayout root){
        try {
            if (dataSetting == null) throw new Exception("需要设置数据绑定!setDataSetting");
            if (onToolBarClick == null) throw new Exception("需要设置toolbar的按键事件!setOnToolBarClick");
            createTableHead();
            initTable();
        }catch (Exception e){
            e.printStackTrace();
        }

        root.addView(this);
    }

    private void createTableHead() {
        cellWidthPer = new ArrayList<>();
        for(int i = 0; i < tableHeadList.size(); i++){              //  创建表头
            String[] cell = tableHeadList.get(i);

            try {
                String value = cell[0];
                int width = Integer.valueOf(cell[1]);
                String itemType = String.valueOf(cell[2]);

                cellWidthPer.add(String.valueOf(width));        //  收集每列的列宽

                if(i == 0){     //  第一列的id都隐藏
                    TableRecyclerAdapter.addTextViewToLayout(context, width, value, container_tablehead, hideId);
                }else {
                    TableRecyclerAdapter.addTextViewToLayout(context, width, value, container_tablehead, false);
                }
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                Log.e("customTable", "设置的表头参数不正确！[列名, 列宽, 列设置类型(ItemEditType)]");
            }
        }
    }

    private void initTable() {
        initTableNameAndToolBar();      //  生成工具栏

        initRecyclerView();
    }

    private void initRecyclerView() {
        table_list = getTableList(dataSetting.getDataList());

        if (this.linearLayoutManager == null) {
            linearLayoutManager = new LinearLayoutManager(context) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
        }
        tableRecyclerAdapter = new TableRecyclerAdapter(context, tableHeadList, table_list, hideId, recycler_tablebody, picture_base_path);
        if (onBtnClickListener != null) {
            tableRecyclerAdapter.setOnSpinnerClickListener(onBtnClickListener.onSpinnerClickListener());
            tableRecyclerAdapter.setOnImageViewClickListener(onBtnClickListener.onImageViewClickListener());
        }
        recycler_tablebody.setLayoutManager(linearLayoutManager);
        recycler_tablebody.setAdapter(tableRecyclerAdapter);
    }

    private void initTableNameAndToolBar() {
        tv_table_name.setText(isEmpty(table_name)?"默认表名":table_name);

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.btn_add) {
                    List<String[]> list = new ArrayList<>();
                    for (int i = 0; i < tableHeadList.size(); i++) {
                        String[] gezi = tableHeadList.get(i);
                        list.add(new String[]{TableRecyclerAdapter.sufferString, gezi[1], gezi[2]});
                    }
                    table_list.add(list);
                    tableRecyclerAdapter.notifyItemInserted(table_list.size());

//                            onToolBarClick.clickAdd();
                } else if (id == R.id.btn_edit) {
                    if (tableRecyclerAdapter.getLast_click_item() != -1) {
                        //  获取那条item数据重新设置，刷新表格
                        if (tableRecyclerAdapter.getItemViewType(tableRecyclerAdapter.getLast_click_item()) == TableRecyclerAdapter.SHOW_TYPE) {
                            List<String[]> line = (List<String[]>) tableRecyclerAdapter.getItem(tableRecyclerAdapter.getLast_click_item());
                            for (int i = 0; i < line.size(); i++) {
                                line.get(i)[0] = TableRecyclerAdapter.sufferString + line.get(i)[0];
                            }
                            tableRecyclerAdapter.notifyItemChanged(tableRecyclerAdapter.getLast_click_item(), line);
                        } else {
                            Log.e(TAG, "不可重复编辑单条item");
                        }
//                                onToolBarClick.clickEdit();
                    } else {
                        Toast.makeText(context, "请选择一条item进行编辑!", Toast.LENGTH_SHORT).show();
                    }
                } else if (id == R.id.btn_delete) {
                    if (tableRecyclerAdapter.getLast_click_item() != -1) {
//                                获取那条item数据重新设置，刷新表格
                        String serverId = tableRecyclerAdapter.getItem(tableRecyclerAdapter.getLast_click_item()).get(0)[0].replaceFirst(TableRecyclerAdapter.sufferString, "");
                        tableRecyclerAdapter.removeItem(tableRecyclerAdapter.getLast_click_item());
                        if (onToolBarClick.clickDelete(serverId)) {
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                            tableRecyclerAdapter.setLast_click_item(-1);
                        } else {
                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "删除失败！serverId是：" + serverId);
                        }
                    } else {
                        Toast.makeText(context, "请选择一条item进行删除!", Toast.LENGTH_SHORT).show();
                    }
                } else if (id == R.id.btn_save) {
                    tableRecyclerAdapter.setLast_click_item(-1);
                    List<ChangeBean> changeData = tableRecyclerAdapter.getChangeData();
                    onToolBarClick.clickSave(changeData);

                    for (ChangeBean changeDatum : changeData) {
                        int position = changeDatum.getPosition();
                        table_list.clear();
                        List<T> newData = dataSetting.getDataList();
                        table_list.addAll(getTableList(newData));
                        tableRecyclerAdapter.notifyItemChanged(position);
                    }
//                        table_list.clear();
//                        table_list.addAll(getTableList(dataSetting.getDataList()));
//                        tableRecyclerAdapter.notifyDataSetChanged();
//                        initRecyclerView();
                }
            }
        };

        btn_add.setOnClickListener(onClickListener);
        btn_edit.setOnClickListener(onClickListener);
        btn_delete.setOnClickListener(onClickListener);
        btn_save.setOnClickListener(onClickListener);
    }

    private List<List<String[]>> getTableList(List<T> datas) {
        if (datas == null) datas = new ArrayList<>();
        List<List<String[]>> table_list = new ArrayList<>();

        for (int i = 0; i < datas.size(); i++){
            List<String[]> raw = new ArrayList<>();
            String[] dataPer = dataSetting.parse(datas.get(i));        //  一列的数据
            for (int ii = 0; ii < dataPer.length; ii++) {
                raw.add(new String[]{dataPer[ii], cellWidthPer.get(ii), tableHeadList.get(ii)[2]});
            }
            table_list.add(raw);
        }
        return table_list;
    }

    private void bindViews() {
        tv_table_name = findViewById(R.id.tv_table_name);
        btn_add = findViewById(R.id.btn_add);
        btn_edit = findViewById(R.id.btn_edit);
        btn_delete = findViewById(R.id.btn_delete);
        container_tablehead = findViewById(R.id.container_tablehead);
        recycler_tablebody = findViewById(R.id.container_tablebody);
        btn_save = findViewById(R.id.btn_save);
    }

    private OnBtnClickListener onBtnClickListener;

    public interface OnBtnClickListener {
        TableRecyclerAdapter.OnImageViewClickListener onImageViewClickListener();
        TableRecyclerAdapter.OnSpinnerClickListener onSpinnerClickListener();
    }

    public interface onToolBarClick{
//        void clickAdd();
//        void clickEdit();
        boolean clickDelete(String serverId);

        /**
         * @param save 参数的第一个list是修改的行，第二个list是对应行的修改的每个列，String是修改后的值
         */
        void clickSave(List<ChangeBean> save);
    }

    public interface dataSetting<T> {
        /**
         * 获取每行值的数组化
         * @param task
         * @return
         */
        String[] parse(T task);
        List<T> getDataList();
    }
}
