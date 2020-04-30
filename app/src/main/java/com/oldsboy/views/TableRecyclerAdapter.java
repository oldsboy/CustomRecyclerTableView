package com.oldsboy.views;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oldsboy.views.R;
import com.oldsboy.views.dialog.Dialog_ShowPicture;
import com.oldsboy.views.utils.BitmapsUtil;
import com.oldsboy.views.utils.FileUtil;
import com.oldsboy.views.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.oldsboy.views.utils.ScreenUtil.px2dp;
import static com.oldsboy.views.utils.StringUtil.isEmpty;


/**
 * 通用Adapter,条纹属性、动态字段数、动态字段宽度
 */
public class TableRecyclerAdapter extends RecyclerView.Adapter<TableRecyclerAdapter.ListViewHolder> {
    public static final String TAG = "customTableAdapter";
    public static final int SHOW_TYPE = 0;
    public static final int EDIT_TYPE = 1;

    //  可以考虑以对象List的方式来加载
    //  根据对象内字段的数量来设定列表的字段的数量

    private Context context;

    private List<List<String[]>> tableList;
    private List<String[]> tableHeadList;
    private String base_picture_path;
    private int last_click_item = -1;
    private boolean needOrder;

    private RecyclerView tablebody;

    private ChangeBean changeBean;

    private TableRecyclerAdapter.OnMyItemClickListener myItemClickListener;
    private OnMyItemLongClickListener onMyItemLongClickListener;

    public static String sufferString = "%待填入%";            //  占位前缀，分辨item类型的重要设置

    private static final int TEXT_SIZE = 4;
    public static final int LINE_HEIGHT = 35;
    private static final int SPINNER_SIZE = 30;

    public TableRecyclerAdapter(Context context, List<String[]> tableHeadList, List<List<String[]>> tableList, RecyclerView tablebody, String picture_base_path, boolean needOrder) {
        this.tableList = tableList;
        this.context = context;
        this.tablebody = tablebody;
        this.base_picture_path = picture_base_path;
        this.tableHeadList = tableHeadList;             //拿来专门做视图
        this.needOrder = needOrder;
        changeBean = new ChangeBean();
        changeBean.setPosition(-1);
        ArrayList<String> strings = new ArrayList<>();
        for (String[] temp : tableHeadList) {
            strings.add("");
        }
        changeBean.setLine(strings);
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListViewHolder holder = null;
        LinearLayout.LayoutParams rootPara = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px2dp(context, LINE_HEIGHT));
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setLayoutParams(rootPara);

        holder = new ListViewHolder(root);
        int cell_num = tableHeadList.size();      //  一行的格子数量
        int spinner_num = 0;
        int image_num = 0;
        for (int lie = 0; lie < cell_num; lie++) {
            String[] mg = tableHeadList.get(lie);
            int width = Integer.valueOf(mg[TableView.HeadIndex.width]);                         //  列宽[1]
            String itemType = mg[TableView.HeadIndex.itemType];                   //  下拉[2]

            if (viewType == EDIT_TYPE){
                View view;
                if (itemType.equals(TableView.ItemEditType.textSpinner)) {
                    view = addEditSpinnerToLayout(context, width, null, root, spinner_num);
                    spinner_num++;
                }else if (itemType.equals(TableView.ItemEditType.imageSelect)){
                    view = addEditImageViewToLayout(context, width, null, root, image_num);
                    image_num++;
                }else if (itemType.equals(TableView.ItemEditType.cannotEdit)){
                    view = addEditTextToLayout(context, width, null, root, false);
                }else {
                    view = addEditTextToLayout(context, width, null, root, true);
                }
                holder.list.add(view);
            }else {
                View view;
                if (itemType.equals(TableView.ItemEditType.imageSelect)) {
                    view = addImageViewToLayout(context, width, null, root);
                } else {
                    view = addTextViewToLayout(context, width, null, root);
                }
                holder.list.add(view);
            }
        }
        return holder;
    }

    public void removeItem(int current_position) {
        this.getData().remove(getItem(current_position));
        this.notifyItemRemoved(current_position);
        this.notifyItemRangeChanged(current_position, this.getData().size() - current_position);
        last_click_item = -1;
    }

    public interface OnMyItemClickListener{
        void onItemClickListener(View v, int position, List<String> data);
    }

    public interface OnMyItemLongClickListener {
        void onItemLongClickListener(View v, int position, List<String> data);
    }

    public void setMyItemClickListener(OnMyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    public void setOnMyItemLongClickListener(OnMyItemLongClickListener onMyItemLongClickListener){
        this.onMyItemLongClickListener = onMyItemLongClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp_position = last_click_item;
                last_click_item = position;                                             //  刷新前后两个item的背景！
                if (temp_position != -1 && getItemViewType(temp_position) != EDIT_TYPE)
                    notifyItemChanged(temp_position);
                if (last_click_item != -1 && getItemViewType(last_click_item) != EDIT_TYPE)
                    notifyItemChanged(last_click_item);
                if (myItemClickListener != null){
                    myItemClickListener.onItemClickListener(v, position, formatStringArrayToString(position));
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onMyItemLongClickListener != null){
                    onMyItemLongClickListener.onItemLongClickListener(v, position, formatStringArrayToString(position));
                }
                return false;
            }
        });

        if ((position + 1) % 2 != 0) {
            holder.itemView.setBackgroundResource(R.drawable.custom_table_list_2);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.custom_table_list_1);
        }
        if (last_click_item != -1){
            if (last_click_item == position){
                holder.itemView.setBackgroundResource(R.drawable.list_body_border);
            }
        }

        int cell_num = tableList.get(position).size();      //  一行的格子数量
        for (int lie = 0; lie < cell_num; lie++) {
            String[] mg = tableList.get(position).get(lie);
            String value = mg[TableView.HeadIndex.value];
            if (!isEmpty(value) && value.startsWith(sufferString)) {     //  值[0]
                value = value.substring(sufferString.length());
            }
            String itemType = mg[TableView.HeadIndex.itemType];                   //  下拉[2]

            if (getItemViewType(position) == EDIT_TYPE){
                changeBean = getLineChangeData(position);
                EditText editText = holder.list.get(lie).findViewById(R.id.custom_table_item_edit_text1);
                editText.addTextChangedListener(getTextWatcher(position));
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        changeBean = getLineChangeData(position);
                    }
                });
                editText.requestFocus();
                setText(editText, String.valueOf(value));
                if (needOrder){         //  重置值为序号
                    if (lie == 0) {
                        setText(editText, String.valueOf(position));
                    }
                }
                if (itemType.equals(TableView.ItemEditType.imageSelect)){
                    ImageView imageView = holder.list.get(lie).findViewById(R.id.custom_table_item_image_view1);
                    showPicture(imageView, value);
                }
            }else {
                TextView textView = holder.list.get(lie).findViewById(R.id.custom_table_item_text_view1);

                setText(textView, value);
                if (needOrder) {
                    if (lie == 0) {
                        setText(textView, String.valueOf(position));
                    }
                }
                if (itemType.equals(TableView.ItemEditType.imageSelect)) {
                    Log.d(TAG, "TableRecyclerAdapter:显示中，显示的图片名是："+ value);
                    ImageView imageView = holder.list.get(lie).findViewById(R.id.custom_table_item_image_view1);
                    showPicture(imageView, value);
                }
            }
        }
    }

    private List<String> formatStringArrayToString(int position) {
        List<String> strings = new ArrayList<>();
        if (position != -1) {
            for (int lie = 0; lie < tableList.get(position).size(); lie++) {
                if (needOrder) {
                    if (lie == 0) {
                        continue;
                    }
                }
                String[] s = tableList.get(position).get(lie);
                strings.add(s[0]);
            }
        }
        return strings;
    }

    private TextWatcher getTextWatcher(final int position) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changeBean = getLineChangeData(position);
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        if (tableList != null){
            List<String []> line = tableList.get(position);
            for (int i = 0; i < line.size(); i++) {
                String [] gezi = line.get(i);
                String value = gezi[TableView.HeadIndex.value];
                if (!value.startsWith(sufferString)){
                    return SHOW_TYPE;
                }
            }
        }
        return EDIT_TYPE;
    }

    @Override
    public int getItemCount() {
        if (tableList != null) {
            return tableList.size();
        } else return 0;
    }

    public List<String[]> getItem(int position) {
        return tableList.get(position);
    }

    public int getLast_click_item() {
        return last_click_item;
    }

    public void setLast_click_item(int last_click_item) {
        this.last_click_item = last_click_item;
    }

    /**
     * 凡是item类型是edit的就将一行的editText值拿到
     * @return
     */
    public List<ChangeBean> getChangeData() {
        List<ChangeBean> changeData = new ArrayList<>();
        if (changeBean != null && changeBean.getPosition() != -1) {
            changeData.add(changeBean);
            Log.d(TAG, "表格更新了" + changeData.size() + "行数据");

            StringBuilder builder = new StringBuilder();
            for (String s : changeBean.getLine()) {
                builder.append(s).append("， ");
            }
            Log.d(TAG, "数据的内容：position:" + changeBean.getPosition() + "，line:" + builder.toString());
            return changeData;
        }else Log.e(TAG, "获取的changeBean的位置-1，故没有返回");
        return changeData;
    }

    private ChangeBean getLineChangeData(int position){
        if (getItemViewType(position) == EDIT_TYPE && tablebody.getLayoutManager()!= null){
            View view = tablebody.getLayoutManager().findViewByPosition(position);
            if (view != null) {
                List<View> allView = getAllChildViews(view);
                List<String> line = new ArrayList<>();
                for (int i = 0; i < allView.size(); i++) {
                    if (allView.get(i) instanceof EditText) {
                        EditText editText = (EditText) allView.get(i);
                        String string = editText.getText().toString();
                        if (StringUtil.isEmpty(string)) string = "";
                        line.add(string);
                    }
                }
                ChangeBean changeBean = new ChangeBean();
                changeBean.setPosition(position);
                changeBean.setLine(line);
                return changeBean;
            }
        }
        return null;
    }

    public void setLineLieValue(int position, int lie, String value){
        if (needOrder) lie += 1;

        String[] getzi = new String[]{value, tableHeadList.get(lie)[TableView.HeadIndex.width], tableHeadList.get(lie)[TableView.HeadIndex.itemType]};
        tableList.get(position).set(lie, getzi);
        notifyItemChanged(position);
    }

    public List<List<String[]>> getData(){
        return tableList;
    }

    public void setData(List<List<String[]>> newData){
        this.tableList.clear();
        this.tableList.addAll(newData);
        this.notifyDataSetChanged();
    }

    class ListViewHolder extends RecyclerView.ViewHolder{
        List<View> list;

        ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.list = new ArrayList<>();
        }
    }

    public static View addTextViewToLayout(Context context, int width, String value, LinearLayout root) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(px2dp(context, width), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearParam);
        TextView tv = null;
        linearLayout.addView(tv = getNormalTextView(context, width-1, value, View.VISIBLE));

        linearLayout.addView(getNormal1pxView(context, View.VISIBLE));
        root.addView(linearLayout);
        return linearLayout;
    }

    public View addEditTextToLayout(Context context, int width, String value, LinearLayout root, boolean enable) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(px2dp(context, width), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearParam);
        EditText editText = null;
        linearLayout.addView(editText = getNormalEditTextView(context, width-1, value, View.VISIBLE, enable));

        linearLayout.addView(getNormal1pxView(context, View.VISIBLE));
        root.addView(linearLayout);
        return linearLayout;
    }

    private View addEditSpinnerToLayout(Context context, int width, String value, final LinearLayout root, int func_num) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(px2dp(context, width), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearParam);

        final EditText editText;
        linearLayout.addView(editText = getNormalEditTextView(context, width-1-SPINNER_SIZE, value, View.VISIBLE, true));

        Button button = getNormalButton(context, px2dp(context, SPINNER_SIZE));
        setBtnClick(Type.Type_Spinner, button, func_num, root, editText, null);
        linearLayout.addView(button);

        linearLayout.addView(getNormal1pxView(context, View.VISIBLE));

        root.addView(linearLayout);
        return linearLayout;
    }

    private View addImageViewToLayout(Context context, int width, String value, final LinearLayout root) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(px2dp(context, width), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearParam);

        final TextView textView;
        linearLayout.addView(textView = getNormalTextView(context, 0, value, View.VISIBLE));

        final ImageView imgView;
        linearLayout.addView(imgView = getNormalImageView(context, width-1, value));

        linearLayout.addView(getNormal1pxView(context, View.VISIBLE));

        root.addView(linearLayout);
        return linearLayout;
    }

    private View addEditImageViewToLayout(Context context, int width, String value, final LinearLayout root, int func_num) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(px2dp(context, width), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearParam);

        final EditText editText;
        linearLayout.addView(editText = getNormalEditTextView(context, 0, value, View.VISIBLE, true));

        final ImageView imgView;
        linearLayout.addView(imgView = getNormalImageView(context, width-1-SPINNER_SIZE, value));


        Button button = getNormalButton(context, px2dp(context, SPINNER_SIZE));
        setBtnClick(Type.Type_Image, button, func_num, root, editText, imgView);
        linearLayout.addView(button);

        linearLayout.addView(getNormal1pxView(context, View.VISIBLE));

        root.addView(linearLayout);
        return linearLayout;
    }

    public void setOnSpinnerClickListener(TableRecyclerAdapter.OnSpinnerClickListener onSpinnerClickListener) {
        this.onSpinnerClickListener = onSpinnerClickListener;
    }

    public TableRecyclerAdapter.OnSpinnerClickListener getOnSpinnerClickListener() {
        return onSpinnerClickListener;
    }

    private TableRecyclerAdapter.OnSpinnerClickListener onSpinnerClickListener;
    
    public interface OnSpinnerClickListener {
        void onSpinner0Click(View v, LinearLayout root, EditText editText);
        void onSpinner1Click(View v, LinearLayout root, EditText editText);
        void onSpinner2Click(View v, LinearLayout root, EditText editText);
        void onSpinner3Click(View v, LinearLayout root, EditText editText);
        void onSpinner4Click(View v, LinearLayout root, EditText editText);
        void onSpinner5Click(View v, LinearLayout root, EditText editText);
        void onSpinner6Click(View v, LinearLayout root, EditText editText);
    }

    public void setOnImageViewClickListener(OnImageViewClickListener onImageViewClickListener) {
        this.onImageViewClickListener = onImageViewClickListener;
    }

    private OnImageViewClickListener onImageViewClickListener;

    public interface OnImageViewClickListener {
        void onImageView0Click(View v, LinearLayout root, ImageView imgView, EditText editText);
        void onImageView1Click(View v, LinearLayout root, ImageView imgView, EditText editText);
    }

    public void updata(List<List<String[]>> tablelist){
        this.tableList = tablelist;
        this.notifyDataSetChanged();
    }

    private static TextView getNormalTextView(Context context, int width, String value, int visible){
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams tvPara = new LinearLayout.LayoutParams(px2dp(context, width), ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(tvPara);
        textView.setText(value);            //  此处已经绑定值了
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(px2dp(context, TEXT_SIZE));
        textView.setVisibility(visible);
        textView.setId(R.id.custom_table_item_text_view1);
        textView.setPadding(px2dp(context, 4), px2dp(context, 5), px2dp(context, 4), px2dp(context, 5));
        textView.setTextColor(Color.parseColor("#646464"));
        return textView;
    }

    private EditText getNormalEditTextView(Context context, int width, String value, int visible, boolean enable){
        EditText editText = new EditText(context);
        LinearLayout.LayoutParams tvPara = new LinearLayout.LayoutParams(px2dp(context, width), ViewGroup.LayoutParams.MATCH_PARENT);
        tvPara.gravity = Gravity.CENTER_VERTICAL;
        editText.setLayoutParams(tvPara);
        editText.setText(value);            //  此处已经绑定值了
        editText.setBackgroundResource(0);
        editText.setGravity(Gravity.CENTER);
        editText.setVisibility(visible);
        editText.setEnabled(enable);
        editText.setTextSize(px2dp(context, TEXT_SIZE));
        editText.setId(R.id.custom_table_item_edit_text1);
        editText.setTextColor(Color.parseColor("#646464"));
        return editText;
    }

    @interface Type{
        int Type_Spinner = 0;
        int Type_Image = 1;
    }
    private void setBtnClick(@Type int type, Button button, int func_num, final LinearLayout root, final EditText editText, final ImageView imageView){
        if (type == Type.Type_Spinner){
            switch (func_num){          //  预设7个下拉框事件
                case 0:
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onSpinnerClickListener != null){
                                onSpinnerClickListener.onSpinner0Click(v, root, editText);
                            }
                        }
                    });
                    break;
                case 1:
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onSpinnerClickListener != null){
                                onSpinnerClickListener.onSpinner1Click(v, root, editText);
                            }
                        }
                    });
                    break;
                case 2:
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onSpinnerClickListener != null){
                                onSpinnerClickListener.onSpinner2Click(v, root, editText);
                            }
                        }
                    });
                    break;
                case 3:
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onSpinnerClickListener != null){
                                onSpinnerClickListener.onSpinner3Click(v, root, editText);
                            }
                        }
                    });
                    break;
                case 4:
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onSpinnerClickListener != null){
                                onSpinnerClickListener.onSpinner4Click(v, root, editText);
                            }
                        }
                    });
                    break;
                case 5:
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onSpinnerClickListener != null){
                                onSpinnerClickListener.onSpinner5Click(v, root, editText);
                            }
                        }
                    });
                    break;
                case 6:
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onSpinnerClickListener != null){
                                onSpinnerClickListener.onSpinner6Click(v, root, editText);
                            }
                        }
                    });
                    break;
            }
        }else if (type == Type.Type_Image){
            switch (func_num){          //  预设2个下拉框事件
                case 0:
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            DialogUtil.showBottomDialog(context, 100, 102);
                            if (onImageViewClickListener != null){
                                onImageViewClickListener.onImageView0Click(v, root, imageView, editText);
                            }
                        }
                    });
                    break;
                case 1:
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onImageViewClickListener != null){
                                onImageViewClickListener.onImageView1Click(v, root, imageView, editText);
                            }
                        }
                    });
                    break;
            }
        }
    }

    private static Button getNormalButton(Context context, int width){
        Button button = new Button(context);
        LinearLayout.LayoutParams btnParam = new LinearLayout.LayoutParams(width, px2dp(context, SPINNER_SIZE));
        btnParam.gravity = Gravity.CENTER_VERTICAL;
        button.setLayoutParams(btnParam);
        button.setBackgroundResource(R.drawable.btn_edit_right);
        button.setId(R.id.custom_table_item_btn1);
        return button;
    }

    private ImageView getNormalImageView(final Context context, int width, final String img_name){
        ImageView imgView = new ImageView(context);
        LinearLayout.LayoutParams tvPara = new LinearLayout.LayoutParams(px2dp(context, width), px2dp(context, LINE_HEIGHT-2));
        tvPara.gravity = Gravity.CENTER_VERTICAL;
        imgView.setLayoutParams(tvPara);
        showPicture(imgView, img_name);
        imgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imgView.setVisibility(View.VISIBLE);
        imgView.setId(R.id.custom_table_item_image_view1);
        return imgView;
    }

    private void showPicture(ImageView imageView, final String img_name){
        if (imageView != null) {
            try {
                if (this.base_picture_path != null && !this.base_picture_path.isEmpty()){
                    final String file_path = this.base_picture_path + "/" + img_name;
                    if (StringUtil.isEmpty(img_name)){
                        imageView.setImageBitmap(null);
                    }else if (FileUtil.isFileExists(file_path) && new File(file_path).length() > 0 && FileUtil.isFile(file_path)){
                            imageView.setImageBitmap(BitmapsUtil.decodeFilePath(file_path));
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Dialog_ShowPicture dialog_showPicture = new Dialog_ShowPicture(context, file_path);
                                    dialog_showPicture.show();
                                }
                            });
                    }else {
                        Log.e(TAG, "图片损坏！图片路径：" + file_path);
                        imageView.setImageResource(R.drawable.custom_picture_break);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                imageView.setImageResource(R.drawable.custom_picture_break);
            }
        }else Log.e(TAG, "设置的imageView是null！imageName-->" + img_name);
    }

    private void setText(TextView textView, String string){
        if (textView != null) {
            if (!isEmpty(string)) {
                textView.setText(string);
            }else {
                textView.setText("");
            }
        }else Log.e(TAG, "设置的TextView是null！string-->" + string);
    }

    private static View getNormal1pxView(Context context, int visible){
        LinearLayout.LayoutParams vPara = new LinearLayout.LayoutParams(px2dp(context, 1), ViewGroup.LayoutParams.MATCH_PARENT);
        View view = new View(context);
        view.setBackgroundResource(R.color.table_line);
        view.setVisibility(visible);
        view.setLayoutParams(vPara);
        return view;
    }

    private List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                allchildren.add(viewchild);
                //再次 调用本身（递归）
                allchildren.addAll(getAllChildViews(viewchild));
            }
        }
        return allchildren;
    }
}