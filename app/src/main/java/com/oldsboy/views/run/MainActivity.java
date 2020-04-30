package com.oldsboy.views.run;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.oldsboy.views.ChangeBean;
import com.oldsboy.views.R;
import com.oldsboy.views.TableRecyclerAdapter;
import com.oldsboy.views.TableView;
import com.oldsboy.views.run.bean.Student;
import com.oldsboy.views.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: MyCustomRecyclerTableView
 * @Package: com.oldsboy.views.run
 * @ClassName: MainActivity
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/30 11:24
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/30 11:24
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MainActivity extends Activity {
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_activity);
        context = this;
        initTable1();
    }

    private void initTable1() {
        final List<Student> data = Student.getList();

        List<String[]> headList = new ArrayList<>();                                  //  表头配置（String[]{字段名， 宽度， 是否需要下拉框配置（0，1）}
        headList.add(new String[]{"Id", "80", TableView.ItemEditType.textInput});
        headList.add(new String[]{"名字","120", TableView.ItemEditType.textInput});
        headList.add(new String[]{"年龄","80", TableView.ItemEditType.textInput});
        TableView.dataSetting<Student> dataSetting = new TableView.dataSetting<Student>() {
            @Override
            public String[] parse(Student task) {
                return new String[]{                                            //  配置列表显示数据②
                        String.valueOf(task.getId())
                        , String.valueOf(task.getName())
                        , String.valueOf(task.getAge())
                };
            }

            @Override
            public List<Student> getDataList() {
                return data;
            }
        };

        TableView.onToolBarClick onToolBarClick = new TableView.onToolBarClick() {                          //  开放删除、保存点击事件
            @Override
            public boolean clickDelete(String serverId) {
                Student formList = Student.findFormList(data, serverId);
                if (formList != null) {
                    return data.remove(formList);
                }else {
                    return false;
                }
            }

            @Override
            public void clickSave(TableRecyclerAdapter tableRecyclerAdapter, List<ChangeBean> save) {
                for (int i = 0; i < save.size(); i++) {
                    ChangeBean bean = save.get(i);
                    List<String> line = bean.getLine();

                    Student task = Student.findFormList(data, line.get(0));
                    boolean is_newTask = false;
                    try {
                        if (task == null) {
                            task = new Student();
                            task.setId(Integer.valueOf(line.get(0)));
                            is_newTask = true;
                        }
                        task.setName(line.get(1));
                        task.setAge(Integer.valueOf(line.get(2)));

                        if (is_newTask) {
                            Log.d("table", "插入的task属性为：");
                            data.add(task);
                        } else {
                            Log.d("table", "更新的task属性为：");
                            data.set(bean.getPosition(), task);
                        }
                    }catch (IndexOutOfBoundsException | NumberFormatException e){
                        e.printStackTrace();
                        Toast.makeText(context, "输入的数据类型不正确！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        TableView.OnBtnClickListener onBtnClickListener = new TableView.OnBtnClickListener() {
            @Override
            public TableRecyclerAdapter.OnImageViewClickListener onImageViewClickListener() {
                return new TableRecyclerAdapter.OnImageViewClickListener() {            //  预设了2个选择图片的点击
                    @Override
                    public void onImageView0Click(View v, LinearLayout root, ImageView imgView, EditText editText) {

                    }

                    @Override
                    public void onImageView1Click(View v, LinearLayout root, ImageView imgView, EditText editText) {

                    }
                };
            }

            @Override
            public TableRecyclerAdapter.OnSpinnerClickListener onSpinnerClickListener() {
                return new TableRecyclerAdapter.OnSpinnerClickListener() {              //  预设了7个点击下拉框的点击
                    @Override
                    public void onSpinner0Click(View v, LinearLayout root, EditText editText) {

                    }

                    @Override
                    public void onSpinner1Click(View v, LinearLayout root, EditText editText) {

                    }

                    @Override
                    public void onSpinner2Click(View v, LinearLayout root, EditText editText) {

                    }

                    @Override
                    public void onSpinner3Click(View v, LinearLayout root, EditText editText) {

                    }

                    @Override
                    public void onSpinner4Click(View v, LinearLayout root, EditText editText) {

                    }

                    @Override
                    public void onSpinner5Click(View v, LinearLayout root, EditText editText) {

                    }

                    @Override
                    public void onSpinner6Click(View v, LinearLayout root, EditText editText) {

                    }
                };
            }
        };

        TableView tableView = new TableView<>(context, "测试用表", headList, onToolBarClick, dataSetting, onBtnClickListener);
        //  true：   设置为recyclerView可以滑动模式
        //  false：  设置为recyclerView不可滑动模式
        tableView.setCanScrollVertical(true);
        //  设置长按标题/点击扩展进入全屏表格模式
        tableView.setTableTitleCanEnter(true);
        //  显示标题
        tableView.setTitleVisiablilty(View.VISIBLE);
        //  显示工具栏
        tableView.setTableToolbarVisiablilty(View.VISIBLE);
        //  显示序号
        tableView.setNeedOrder(true);
        //  单击事件
        tableView.setOnItemClicklistener(null);
        //长按事件
        tableView.setOnMyItemLongClickListener(null);
        //  以上配置需要配置再showTable之前
        tableView.showTable((FrameLayout) findViewById(R.id.table1));
    }
}
