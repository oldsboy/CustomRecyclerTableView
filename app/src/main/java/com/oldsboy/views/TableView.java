package com.oldsboy.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.oldsboy.views.R;
import com.oldsboy.views.dialog.Dialog_BigTable;
import com.oldsboy.views.scroll.NoScrollLinearLayoutManager;
import com.oldsboy.views.utils.ScreenUtil;

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
    private TableView tableView;

    /**
     * view
     * **/
    private TextView tv_table_name;
    private RelativeLayout btn_add, btn_edit, btn_delete, btn_save;
    private LinearLayout container_tablehead;
    private LinearLayout container_table_toolbar;
    private RecyclerView recycler_tablebody;
    private ImageView img_big;
    private ImageView img_refresh;
    private ImageView img_bottom;
    private FrameLayout container_title;

    /**
     * data/setting
     * **/
    private String table_name;
    private List<String[]> new_TableHeadList;
    private List<String[]> old_TableHeadList;
    private List<List<String[]>> tableList;
    private List<String> cellWidthPer;
    private String picture_base_path;
    private boolean is_editing = false;
    private boolean needOrder = true;
    private boolean canScroll = true;

    private NoScrollLinearLayoutManager linearLayoutManager;

    /**
     * interface
     * **/
    private TableView.onToolBarClick onToolBarClick;
    private TableView.dataSetting<T> dataSetting;
    private OnBtnClickListener onBtnClickListener;

    private TableRecyclerAdapter tableRecyclerAdapter;

    public void setOnToolBarClick(TableView.onToolBarClick onToolBarClick) {
        this.onToolBarClick = onToolBarClick;
    }

    public void setDataSetting(TableView.dataSetting<T> dataSetting) {
        this.dataSetting = dataSetting;
    }

    public void setPicture_base_path(String picture_base_path) {
        this.picture_base_path = picture_base_path;
    }

    public void setTableToolbarVisiablilty(int visibility) {
        this.container_table_toolbar.setVisibility(visibility);
    }

    public void setTitleVisiablilty(int visibility) {
        this.container_title.setVisibility(visibility);
    }

    public void setCanScrollVertical(boolean canScrollVertical){
        this.canScroll = canScrollVertical;
    }

    public void setTableHeadList(List<String[]> TableHeadList) {
        this.new_TableHeadList = TableHeadList;
        this.old_TableHeadList = new ArrayList<>(new_TableHeadList);
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    private TableRecyclerAdapter.OnMyItemClickListener onMyItemClickListener;
    public void setOnItemClicklistener(TableRecyclerAdapter.OnMyItemClickListener onItemClickListener){
        if (this.tableRecyclerAdapter != null) {
            this.tableRecyclerAdapter.setMyItemClickListener(onItemClickListener);
        }
        this.onMyItemClickListener = onItemClickListener;
    }

    private TableRecyclerAdapter.OnMyItemLongClickListener onMyItemLongClickListener;
    public void setOnMyItemLongClickListener(TableRecyclerAdapter.OnMyItemLongClickListener onMyItemLongClickListener){
        if (this.tableRecyclerAdapter != null) {
            this.tableRecyclerAdapter.setOnMyItemLongClickListener(onMyItemLongClickListener);
        }
        this.onMyItemLongClickListener = onMyItemLongClickListener;
    }

    public void setNeedOrder(boolean needOrder) {
        this.needOrder = needOrder;
    }

    public List<List<String[]>> getData(){
        return this.tableList;
    }

    public void setTableTitleCanEnter(boolean canEnter){
        if (canEnter){
            tv_table_name.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Dialog_BigTable dialog_bigTable = new Dialog_BigTable<>(context, table_name, old_TableHeadList, onToolBarClick, dataSetting, onBtnClickListener);
                    dialog_bigTable.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            reloadData();
                        }
                    });
                    dialog_bigTable.show();
                    return false;
                }
            });
            img_big.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog_BigTable dialog_bigTable = new Dialog_BigTable<>(context, table_name, old_TableHeadList, onToolBarClick, dataSetting, onBtnClickListener);
                    dialog_bigTable.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            reloadData();
                        }
                    });
                    dialog_bigTable.show();
                }
            });
        }else {
            img_big.setVisibility(GONE);
            tv_table_name.setOnLongClickListener(null);
            img_big.setOnClickListener(null);
        }
    }

    public @interface ItemEditType{
        String cannotEdit = "100";
        String textInput = "101";
        String textSpinner = "102";
        String imageSelect = "103";
    }

    public @interface HeadIndex{
        int value = 0;
        int width = 1;
        int itemType = 2;
    }

    /**
     *
     * @param context
     * @param table_name                设置表名
     * @param TableHeadList             表头配置（String[]{字段名， 宽度， 是否需要下拉框配置（0，1）}
     * @param onToolBarClick            工具栏配置
     * @param dataSetting               数据源配置
     */
    public TableView(Context context, String table_name, List<String[]> TableHeadList, onToolBarClick onToolBarClick, dataSetting<T> dataSetting, OnBtnClickListener onBtnClickListener) {
        super(context);
        this.context = context;
        this.table_name = table_name;
        this.new_TableHeadList = TableHeadList;
        this.old_TableHeadList = new ArrayList<>(new_TableHeadList);
        this.onToolBarClick = onToolBarClick;
        this.dataSetting = dataSetting;
        this.picture_base_path = context.getCacheDir().getAbsolutePath();
        this.onBtnClickListener = onBtnClickListener;
        this.tableList = new ArrayList<>();

        ((LayoutInflater)this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_table_recycler_view, this);
        bindViews();
    }

    public void showTable(ViewGroup root){
        try {
            if (dataSetting == null) throw new Exception("需要设置数据绑定!setDataSetting");
            createTableHead();
            initTable();
            tableView = this;
        }catch (Exception e){
            e.printStackTrace();
        }

        if (root != null) {
            root.removeAllViews();
            root.addView(this);
        }
    }

    private void refreshTableHeight() {
        int head_height;
        if (canScroll){
            head_height = -1;
        }else {
            head_height = ScreenUtil.px2dp(context, 30)
                    + ScreenUtil.px2dp(context, 30)
                    + ScreenUtil.px2dp(context, 25)
                    + tableRecyclerAdapter.getItemCount() * ScreenUtil.px2dp(context, TableRecyclerAdapter.LINE_HEIGHT)
                    + ScreenUtil.px2dp(context, 2);
        }

        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        }
        layoutParams.height = head_height;
        this.setLayoutParams(layoutParams);
    }

    private void createTableHead() {
        if (needOrder) {
            this.new_TableHeadList.add(0, new String[]{" ", "60", ItemEditType.cannotEdit});
        }
        cellWidthPer = new ArrayList<>();
        for(int i = 0; i < new_TableHeadList.size(); i++){              //  创建表头
            String[] cell = new_TableHeadList.get(i);

            try {
                String value = cell[HeadIndex.value];
                int width = Integer.valueOf(cell[HeadIndex.width]);
                String itemType = String.valueOf(cell[HeadIndex.itemType]);

                cellWidthPer.add(String.valueOf(width));        //  收集每列的列宽

                TableRecyclerAdapter.addTextViewToLayout(context, width, value, container_tablehead);
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                Log.e("customTable", "设置的表头参数不正确！[列名, 列宽, 列设置类型(ItemEditType)]");
            }
        }
    }

    private void initTable() {
        if (this.linearLayoutManager == null) {
            linearLayoutManager = new NoScrollLinearLayoutManager(context);
            linearLayoutManager.setScrollEnabled(canScroll);
        }
        recycler_tablebody.setLayoutManager(linearLayoutManager);

        initTableNameAndToolBar();      //  生成工具栏

        reloadData();

        img_refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadData();
            }
        });
        img_bottom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recycler_tablebody != null && tableList != null && tableList.size() != 0) {
                    try {
                        recycler_tablebody.scrollToPosition(tableList.size()-1);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void reloadData(){
        tableList.clear();
        tableList.addAll(getTableList(dataSetting.getDataList()));
        initRecyclerView();
    }

    public void reloadData(List<T> data){
        tableList.clear();
        tableList.addAll(getTableList(dataSetting.getDataList()));
        initRecyclerView();
    }

    private void initRecyclerView() {
        tableRecyclerAdapter = new TableRecyclerAdapter(context, new_TableHeadList, tableList, recycler_tablebody, picture_base_path, needOrder);
        if (onBtnClickListener != null) {
            tableRecyclerAdapter.setOnSpinnerClickListener(onBtnClickListener.onSpinnerClickListener());
            tableRecyclerAdapter.setOnImageViewClickListener(onBtnClickListener.onImageViewClickListener());
        }
        if (onMyItemClickListener != null){
            tableRecyclerAdapter.setMyItemClickListener(onMyItemClickListener);
        }
        if (onMyItemLongClickListener != null){
            tableRecyclerAdapter.setOnMyItemLongClickListener(onMyItemLongClickListener);
        }
        recycler_tablebody.setAdapter(tableRecyclerAdapter);

        refreshTableHeight();

        is_editing = false;
    }

    private void initTableNameAndToolBar() {
        tv_table_name.setText(isEmpty(table_name)?"默认表名":table_name);

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.btn_add) {
                    if (is_editing){
                        Toast.makeText(context, "请完成此条item编辑以后再添加！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    is_editing = true;

                    List<String[]> list = new ArrayList<>();
                    for (int lie = 0; lie < new_TableHeadList.size(); lie++) {
                        String[] gezi = new_TableHeadList.get(lie);
                        if (lie == 0){
                            list.add(new String[]{TableRecyclerAdapter.sufferString + tableList.size(), gezi[HeadIndex.width], gezi[HeadIndex.itemType]});
                        }else {
                            list.add(new String[]{TableRecyclerAdapter.sufferString, gezi[HeadIndex.width], gezi[HeadIndex.itemType]});
                        }
                    }
                    tableList.add(list);
                    tableRecyclerAdapter.notifyItemInserted(tableList.size());

                    refreshTableHeight();
                    recycler_tablebody.scrollToPosition(tableList.size()-1);
//                            onToolBarClick.clickAdd();
                } else if (id == R.id.btn_edit) {
                    if (is_editing){
                        Toast.makeText(context, "请完成此条item编辑以后再编辑！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    is_editing = true;

                    if (tableRecyclerAdapter.getLast_click_item() != -1) {
                        //  获取那条item数据重新设置，刷新表格
                        if (tableRecyclerAdapter.getItemViewType(tableRecyclerAdapter.getLast_click_item()) == TableRecyclerAdapter.SHOW_TYPE) {
                            List<String[]> line = (List<String[]>) tableRecyclerAdapter.getItem(tableRecyclerAdapter.getLast_click_item());
                            for (int i = 0; i < line.size(); i++) {
                                line.get(i)[0] = TableRecyclerAdapter.sufferString + line.get(i)[HeadIndex.value];
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
                    if (is_editing){
                        Toast.makeText(context, "请保存以后再删除！", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (tableRecyclerAdapter.getLast_click_item() != -1) {
//                                获取那条item数据重新设置，刷新表格
                        int idIndex = 0;
                        if (needOrder){
                            idIndex = 1;
                        }
                        String serverId = tableRecyclerAdapter.getItem(tableRecyclerAdapter.getLast_click_item()).get(idIndex)[HeadIndex.value].replaceFirst(TableRecyclerAdapter.sufferString, "");
                        if (onToolBarClick != null && onToolBarClick.clickDelete(serverId)) {
//                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                            tableRecyclerAdapter.removeItem(tableRecyclerAdapter.getLast_click_item());
                            tableRecyclerAdapter.setLast_click_item(-1);
                            refreshTableHeight();
                        } else {
                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "删除失败！serverId是：" + serverId);
                        }
                    } else {
                        Toast.makeText(context, "请选择一条item进行删除!", Toast.LENGTH_SHORT).show();
                    }
                } else if (id == R.id.btn_save) {           //  adapter内部维护一个changeBean类用以返回改变数据
                    if (onToolBarClick == null) return;

                    if (tableRecyclerAdapter.getLast_click_item() != -1)    tableRecyclerAdapter.notifyItemChanged(tableRecyclerAdapter.getLast_click_item());
                    tableRecyclerAdapter.setLast_click_item(-1);
                    List<ChangeBean> changeData = tableRecyclerAdapter.getChangeData();
                    List<ChangeBean> resultData = new ArrayList<>();

                    for (ChangeBean changeDatum : changeData) {
                        int position = changeDatum.getPosition();
                        if (position != -1) {
                            List<String[]> line = new ArrayList<>();
                            List<String> resultLine = new ArrayList<>();
                            for (int lie = 0; lie < changeDatum.getLine().size(); lie++) {
                                String value = changeDatum.getLine().get(lie);
                                String[] gezi = new String[]{value, new_TableHeadList.get(lie)[HeadIndex.width], new_TableHeadList.get(lie)[HeadIndex.itemType]};
                                line.add(gezi);
                                if (needOrder) {
                                    if (lie == 0) {
                                        continue;
                                    }
                                }
                                resultLine.add(value);
                            }
                            tableList.set(position, line);
                            ChangeBean resultBean = new ChangeBean();
                            resultBean.setPosition(position);
                            resultBean.setLine(resultLine);
                            resultData.add(resultBean);
                            tableRecyclerAdapter.notifyItemChanged(position);
                        }
                    }

                    try{
                        onToolBarClick.clickSave(tableRecyclerAdapter, resultData);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    is_editing = false;
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
            for (int column = 0; column < new_TableHeadList.size(); column++) {
                String value = "";
                if (needOrder) {
                    if (column != 0){
                        value = dataPer[column-1];
                    }
                }else {
                    value = dataPer[column];
                }
                raw.add(new String[]{value, new_TableHeadList.get(column)[HeadIndex.width], new_TableHeadList.get(column)[HeadIndex.itemType]});
            }
            table_list.add(raw);
        }
        return table_list;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void bindViews() {
        tv_table_name = findViewById(R.id.tv_table_name);
        btn_add = findViewById(R.id.btn_add);
        btn_edit = findViewById(R.id.btn_edit);
        btn_delete = findViewById(R.id.btn_delete);
        container_tablehead = findViewById(R.id.container_tablehead);
        recycler_tablebody = findViewById(R.id.container_tablebody);
        btn_save = findViewById(R.id.btn_save);
        img_big = findViewById(R.id.img_big);
        img_bottom = findViewById(R.id.img_bottom);
        img_refresh = findViewById(R.id.img_refresh);
        container_table_toolbar = findViewById(R.id.container_table_toolbar);
        container_title = findViewById(R.id.container_title);

        setTableTitleCanEnter(true);
    }

    public interface OnBtnClickListener {
        TableRecyclerAdapter.OnImageViewClickListener onImageViewClickListener();
        TableRecyclerAdapter.OnSpinnerClickListener onSpinnerClickListener();
    }

    public interface onToolBarClick{
//        void clickAdd();
//        void clickEdit();
        boolean clickDelete(String serverId);

        /**
         * 放在了adapter中响应，作用是当editText失去焦点时直接保存
         * @param tableRecyclerAdapter
         * @param save 参数的第一个list是修改的行，第二个list是对应行的修改的每个列，String是修改后的值
         */
        void clickSave(TableRecyclerAdapter tableRecyclerAdapter, List<ChangeBean> save);
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
