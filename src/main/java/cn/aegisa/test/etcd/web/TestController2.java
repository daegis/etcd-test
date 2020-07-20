package cn.aegisa.test.etcd.web;

import cn.aegisa.test.etcd.config.EtcdNode;
import cn.aegisa.test.etcd.config.EtcdValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author xianyingda@gmail.com
 * @serial
 * @since 2020-07-20 11:32
 */
@Controller
@Slf4j
public class TestController2 {

    @EtcdValue(value = "age", defalut = "188")
    private EtcdNode<Integer> age;

    @EtcdValue("name")
    private EtcdNode<String> nameaaa;

    @EtcdValue("single")
    private EtcdNode<Boolean> single;

    @RequestMapping("/test2")
    @ResponseBody
    public Map<String, Object> testStudent() {
        Map<String, Object> map = new HashMap<>();
        map.put("age", age.val());
        map.put("name", nameaaa.val());
        map.put("single", single.val());
        return map;
    }
}
