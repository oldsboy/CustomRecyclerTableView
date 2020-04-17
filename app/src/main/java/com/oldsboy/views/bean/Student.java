package com.oldsboy.views.bean;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: MyCustomRecyclerTableView
 * @Package: com.oldsboy.views.bean
 * @ClassName: Student
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/16 16:25
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/16 16:25
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class Student {
    private int id;
    private String name;
    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static List<Student> getList(){
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Student student = new Student();
            student.setId(i);
            student.setName("摸鱼大师" + i + "号");
            student.setAge(i * 20 % 17);
            list.add(student);
        }
        return list;
    }

    public static int deleteById(String id) {
        return 1;
    }

    public static Student findFormList(List<Student> data, String id){
        for (Student datum : data) {
            if (String.valueOf(datum.getId()).equals(id)){
                return datum;
            }
        }
        return null;
    }
}
