package com.oldsboy.views.run.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: MyCustomRecyclerTableView
 * @Package: com.oldsboy.views.run.bean
 * @ClassName: Student
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/30 11:26
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/30 11:26
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class Student {
    private int id;
    private String name;
    private int age;

    public static List<Student> getList() {
        ArrayList<Student> students = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Student student = new Student();
            student.setId(i);
            student.setName("摸鱼大师" + i + "号");
            student.setAge(i*7);
            students.add(student);
        }
        return students;
    }

    public static Student findFormList(List<Student> data, String serverId) {
        for (Student datum : data) {
            if (datum.getId() == Integer.valueOf(serverId)){
                return datum;
            }
        }
        return null;
    }

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
}
