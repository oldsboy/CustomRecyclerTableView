package com.oldsboy.views;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
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

import com.oldsboy.views.utils.BitmapsUtil;
import com.oldsboy.views.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

import static com.oldsboy.views.utils.StringUtil.isEmpty;

/**
 * 通用Adapter,条纹属性、动态字段数、动态字段宽度
 */
public class CustomTableRecyclerAdapter extends RecyclerView.Adapter<CustomTableRecyclerAdapter.ListViewHolder> {
    public static final String TAG = "customTableAdapter";
    public static final int SHOW_TYPE = 0;
    public static final int EDIT_TYPE = 1;

    //  可以考虑以对象List的方式来加载
    //  根据对象内字段的数量来设定列表的字段的数量

    private Context context;

    private List<List<String[]>> tableList;
    private List<String[]> tableHeadList;
    private boolean hideId;                 //  是否隐藏id列
    private String base_picture_path;
    private int last_click_item = -1;

    private RecyclerView tablebody;

    public static String sufferString = "%待填入%";            //  占位前缀，分辨item类型的重要设置
    private int sufferStringLength = sufferString.length();

    private static final int TEXT_SIZE = 4;
    private static final int LINE_HEIGHT = 35;
    private static final int SPINNER_SIZE = 30;

    public CustomTableRecyclerAdapter(Context context, List<String[]> tableHeadList, List<List<String[]>> tableList, boolean hideId, RecyclerView tablebody, String picture_base_path) {
        this.tableList = tableList;
        this.context = context;
        this.hideId = hideId;
        this.tablebody = tablebody;
        this.base_picture_path = picture_base_path;
        this.tableHeadList = tableHeadList;             //拿来专门做视图
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
        if (viewType == EDIT_TYPE){
            for (int lie = 0; lie < cell_num; lie++) {
                String[] mg = tableHeadList.get(lie);
                int width = Integer.valueOf(mg[1]);                         //  列宽[1]
                int itemType = Integer.valueOf(mg[2]);                   //  下拉[2]

                View view;
                if (lie == 0) {     //  第一列的id都隐藏
                    view = addEditTextToLayout(context, width, null, root, hideId);
                } else {
                    if (itemType == 1){
                        view = addEditSpinnerToLayout(context, width, null, root, spinner_num);
                        spinner_num++;
                    }else if (itemType == 2){
                        view = addEditImageViewToLayout(context, width, null, root, image_num);
                        image_num++;
                    }else {
                        view = addEditTextToLayout(context, width, null, root, false);
                    }
                }
                holder.list.add(view);
            }
        }else {
            for (int lie = 0; lie < cell_num; lie++) {
                String[] mg = tableHeadList.get(lie);
                int width = Integer.valueOf(mg[1]);                         //  列宽[1]
                int itemType = Integer.valueOf(mg[2]);                   //  下拉[2]

                View view;
                if (lie == 0) {     //  第一列的id都隐藏
                    view = addTextViewToLayout(context, width, null, root, hideId);
                } else {
                    if (itemType == 2){
                        view = addImageViewToLayout(context, width, null, root);
                    }else {
                        view = addTextViewToLayout(context, width, null, root, false);
                    }
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
        void onItemClickListener(View v, int position, int itemViewType);
    }

    private CustomTableRecyclerAdapter.OnMyItemClickListener myItemClickListener;

    public void setMyItemClickListener(OnMyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, final int position) {
        int cell_num = tableList.get(position).size();      //  一行的格子数量

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp_position = last_click_item;
                last_click_item = position;
                if (temp_position != -1 && getItemViewType(temp_position) != EDIT_TYPE)    notifyItemChanged(temp_position);
                if (last_click_item != -1 && getItemViewType(last_click_item) != EDIT_TYPE)    notifyItemChanged(last_click_item);
                Log.d(TAG, "当前的current_position的位置是：" + last_click_item);

                if (myItemClickListener != null){
                    myItemClickListener.onItemClickListener(v, position, getItemViewType(temp_position));
                }
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

//        int func_num = 0;
        if (getItemViewType(position) == EDIT_TYPE){
            for (int lie = 0; lie < cell_num; lie++) {
                String[] mg = tableList.get(position).get(lie);
                String value = mg[0];
                if (!isEmpty(value) && value.startsWith(sufferString)) {     //  值[0]
                    value = value.substring(sufferStringLength);
                }
//                int width = Integer.valueOf(mg[1]);                         //  列宽[1]
                int itemType = Integer.valueOf(mg[2]);                   //  下拉[2]
                Log.d(TAG, "开始编辑-->第" + position + "行第"+ lie +"列的数据：值是：" + value + "，itemType类型是：" + itemType);

                setText((EditText) holder.list.get(lie).findViewById(R.id.custom_table_item_edit_text1), value);
                if (itemType == 1){
                    LinearLayout root = (LinearLayout)holder.list.get(lie);

//                        setBtnClick(Type.Type_Spinner, ((Button) holder.list.get(lie).findViewById(R.id.custom_table_item_btn1)), func_num, root, editText, null);
//                        func_num++;
                }else if (itemType == 2){
                    LinearLayout root = (LinearLayout)holder.list.get(lie);
                    ImageView imageView = holder.list.get(lie).findViewById(R.id.custom_table_item_image_view1);

//                        setBtnClick(Type.Type_Image, ((Button) holder.list.get(lie).findViewById(R.id.custom_table_item_btn1)), func_num, root, editText, imageView);
                    showPicture(imageView, value);
//                        func_num++;
                }
            }
        }else {
//            Log.d(TAG, "------------------------------------------------------------------------------------------------------------");
            for (int lie = 0; lie < cell_num; lie++) {
                String[] mg = tableList.get(position).get(lie);
                String value = mg[0];
                if (!isEmpty(value) && value.startsWith(sufferString)) {     //  值[0]
                    value = value.substring(sufferStringLength);
                }
//                int width = Integer.valueOf(mg[1]);                         //  列宽[1]
                int itemType = Integer.valueOf(mg[2]);                   //  下拉[2]
                Log.d(TAG, "展示-->第" + position + "行第"+ lie +"列的数据：值是：" + value + "，itemType类型是：" + itemType);

                setText((TextView)holder.list.get(lie).findViewById(R.id.custom_table_item_text_view1), value);
                if (itemType == 2){
                    showPicture(((ImageView)holder.list.get(lie).findViewById(R.id.custom_table_item_image_view1)), value);
                }
            }
//            Log.d(TAG, "------------------------------------------------------------------------------------------------------------");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (tableList != null){
            List<String []> line = tableList.get(position);
            for (int i = 0; i < line.size(); i++) {
                String [] gezi = line.get(i);
                String value = gezi[0];
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
        for (int position = 0; position < tableList.size(); position++) {
            if (getItemViewType(position) == EDIT_TYPE && tablebody.getLayoutManager()!= null){
                View view = tablebody.getLayoutManager().findViewByPosition(position);
                if (view != null) {
                    List<View> allView = getAllChildViews(view);
                    List<String> line = new ArrayList<>();
                    for (int i = 0; i < allView.size(); i++) {
                        if (allView.get(i) instanceof EditText) {
                            EditText editText = (EditText) allView.get(i);
                            line.add(editText.getText().toString());
                        }
                    }
                    ChangeBean changeBean = new ChangeBean();
                    changeBean.setPosition(position);
                    changeBean.setLine(line);
                    changeData.add(changeBean);
                }
            }
        }
        Log.d(TAG, "表格更新了" + changeData.size() + "行数据");
        return changeData;
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

    public static View addTextViewToLayout(Context context, int width, String value, LinearLayout root, boolean hide) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(px2dp(context, width+1), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearParam);
        TextView tv = null;
        if (!hide) {       //  不隐藏id
            linearLayout.addView(tv = getNormalTextView(context, width, value, View.VISIBLE));

            linearLayout.addView(getNormal1pxView(context, View.VISIBLE));
        }else {
            // 隐藏id
            linearLayout.addView(tv = getNormalTextView(context, width, value, View.GONE));

            linearLayout.addView(getNormal1pxView(context, View.GONE));

            linearLayout.setVisibility(View.GONE);
        }
        root.addView(linearLayout);
        return linearLayout;
    }

    public View addEditTextToLayout(Context context, int width, String value, LinearLayout root, boolean hide) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(px2dp(context, width+1), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearParam);
        EditText editText = null;
        if (!hide) {       //  不隐藏id
            linearLayout.addView(editText = getNormalEditTextView(context, width, value, View.VISIBLE));

            linearLayout.addView(getNormal1pxView(context, View.VISIBLE));
        }else {
            // 隐藏id
            linearLayout.addView(editText = getNormalEditTextView(context, width, value, View.GONE));

            linearLayout.addView(getNormal1pxView(context, View.GONE));

            linearLayout.setVisibility(View.GONE);
        }
        root.addView(linearLayout);
        return linearLayout;
    }

    private View addEditSpinnerToLayout(Context context, int width, String value, final LinearLayout root, int func_num) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(px2dp(context, width+1), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearParam);

        final EditText editText;
        linearLayout.addView(editText = getNormalEditTextView(context, width-SPINNER_SIZE, value, View.VISIBLE));

        Button button = getNormalButton(context, px2dp(context, SPINNER_SIZE));
        setBtnClick(Type.Type_Spinner, button, func_num, root, editText, null);
        linearLayout.addView(button);

        linearLayout.addView(getNormal1pxView(context, View.VISIBLE));

        root.addView(linearLayout);
        return linearLayout;
    }

    private View addImageViewToLayout(Context context, int width, String value, final LinearLayout root) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(px2dp(context, width+1), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearParam);

        final TextView textView;
        linearLayout.addView(textView = getNormalTextView(context, 0, value, View.VISIBLE));

        final ImageView imgView;
        linearLayout.addView(imgView = getNormalImageView(context, width, value));

        linearLayout.addView(getNormal1pxView(context, View.VISIBLE));

        root.addView(linearLayout);
        return linearLayout;
    }

    private View addEditImageViewToLayout(Context context, int width, String value, final LinearLayout root, int func_num) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParam = new LinearLayout.LayoutParams(px2dp(context, width+1), ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearParam);

        final EditText editText;
        linearLayout.addView(editText = getNormalEditTextView(context, 0, value, View.VISIBLE));

        final ImageView imgView;
        linearLayout.addView(imgView = getNormalImageView(context, width-SPINNER_SIZE, value));


        Button button = getNormalButton(context, px2dp(context, SPINNER_SIZE));
        setBtnClick(Type.Type_Image, button, func_num, root, editText, imgView);
        linearLayout.addView(button);

        linearLayout.addView(getNormal1pxView(context, View.VISIBLE));

        root.addView(linearLayout);
        return linearLayout;
    }

    public void setOnSpinnerClickListener(CustomTableRecyclerAdapter.OnSpinnerClickListener onSpinnerClickListener) {
        this.onSpinnerClickListener = onSpinnerClickListener;
    }

    public CustomTableRecyclerAdapter.OnSpinnerClickListener getOnSpinnerClickListener() {
        return onSpinnerClickListener;
    }

    private CustomTableRecyclerAdapter.OnSpinnerClickListener onSpinnerClickListener;
    
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

    private static EditText getNormalEditTextView(Context context, int width, String value, int visible){
        EditText editText = new EditText(context);
        LinearLayout.LayoutParams tvPara = new LinearLayout.LayoutParams(px2dp(context, width), ViewGroup.LayoutParams.MATCH_PARENT);
        tvPara.gravity = Gravity.CENTER_VERTICAL;
        editText.setLayoutParams(tvPara);
        editText.setText(value);            //  此处已经绑定值了
        editText.setBackgroundResource(0);
        editText.setGravity(Gravity.CENTER);
        editText.setVisibility(visible);
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

    private ImageView getNormalImageView(Context context, int width, String img_name){
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

    private void showPicture(ImageView imageView, String img_name){
        if (imageView != null) {
            if (this.base_picture_path != null && !this.base_picture_path.isEmpty()){
                String file_path = this.base_picture_path + "/" + img_name;
                if (img_name == null || img_name.length() == 0){
                    imageView.setImageBitmap(null);
                }else if (FileUtil.isFileExists(file_path)){
                    imageView.setImageBitmap(BitmapsUtil.decodeFilePath(this.base_picture_path + "/" + img_name));
                }else {
                    Log.e(TAG, "图片损坏！图片路径：" + file_path);
                    imageView.setImageResource(R.drawable.custom_picture_break);
                }
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

    private void setText(EditText editText, String string){
        if (editText != null) {
            if (!isEmpty(string)) {
                editText.setText(string);
            }else {
                editText.setText("");
            }
        }else Log.e(TAG, "设置的EditText是null！string-->" + string);
    }

    private static View getNormal1pxView(Context context, int visible){
        LinearLayout.LayoutParams vPara = new LinearLayout.LayoutParams(px2dp(context, 1), ViewGroup.LayoutParams.MATCH_PARENT);
        View view = new View(context);
        view.setBackgroundResource(R.color.table_line);
        view.setVisibility(visible);
        view.setLayoutParams(vPara);
        return view;
    }

    private static int px2dp(Context context, int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px,context.getResources().getDisplayMetrics());
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