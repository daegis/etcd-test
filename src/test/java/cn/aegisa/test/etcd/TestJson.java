package cn.aegisa.test.etcd;

import cn.aegisa.test.etcd.vo.Student;
import com.alibaba.fastjson.JSON;

/**
 * @author xianyingda@gmail.com
 * @serial
 * @since 2020-07-20 14:11
 */
public class TestJson {
    public static void main(String[] args) {
        Student s = new Student();
        s.setName("李晓瑶");
        s.setAge(19);
        System.out.println("JSON.toJSONString(s) = " + JSON.toJSONString(s));
    }
}
