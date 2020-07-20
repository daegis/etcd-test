package cn.aegisa.test.etcd.web;

import cn.aegisa.test.etcd.config.EtcdNode;
import cn.aegisa.test.etcd.config.EtcdValue;
import cn.aegisa.test.etcd.vo.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xianyingda@gmail.com
 * @serial
 * @since 2020-07-20 11:32
 */
@Controller
@Slf4j
public class TestController {

    @EtcdValue(value = "name", defalut = "")
    private EtcdNode<String> name;

    @EtcdValue("student")
    private EtcdNode<Student> student;


    @RequestMapping("/test")
    @ResponseBody
    public Map<String, Object> testStudent() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name.val());
        map.put("student", student.val());
        return map;
    }
}
