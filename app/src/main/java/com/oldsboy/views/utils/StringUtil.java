package com.oldsboy.views.utils;

/**
 * @ProjectName: MyCustomRecyclerTableView
 * @Package: com.oldsboy.views.utils
 * @ClassName: StringUtil
 * @Description: java类作用描述
 * @Author: 作者名 oldsboy
 * @CreateDate: 2020/4/16 16:17
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/16 16:17
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class StringUtil {
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() < 1 || str.equals("");
    }

    public static String printClassValue(Object obj){
        if (obj == null) return null;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        //获取这个对象的定义类
        Class cz = obj.getClass();
        //获取类的变量成员列表，注意，这个地方还有一个getDeclaredField方法，具体区别参见javadoc
        for (java.lang.reflect.Field f : cz.getDeclaredFields()){
            f.setAccessible(true);
            //获取变量的值，当然你也可以获取变量的名字
            Object value = null;
            try {
                value = f.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            stringBuilder.append(f.getName()).append("\t\t\t的值是：").append(value).append("\n");
        }
        return stringBuilder.toString();
    }
}
