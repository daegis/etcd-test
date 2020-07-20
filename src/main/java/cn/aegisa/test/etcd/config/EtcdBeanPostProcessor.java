package cn.aegisa.test.etcd.config;

import com.alibaba.fastjson.JSON;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xianyingda@gmail.com
 * @serial
 * @since 2020-07-20 11:26
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class EtcdBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private KV kv;

    @Autowired
    private Client etcdClient;

    private final Map<String, EtcdNode> map = new ConcurrentHashMap<>(256);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            EtcdValue annotation = field.getAnnotation(EtcdValue.class);
            if (annotation != null) {
                Type type = field.getGenericType();
                ParameterizedType pt = ((ParameterizedType) type);
                final Class valueClass = ((Class) pt.getActualTypeArguments()[0]);
                final String key0 = annotation.value();
                if (StringUtils.isEmpty(key0)) {
                    throw new RuntimeException("etcd key can not be null");
                }
                String value = annotation.defalut();
                ByteSequence key = ByteSequence.from(key0.getBytes());
                try {
                    CompletableFuture<GetResponse> future = kv.get(key);
                    GetResponse response = future.get();
                    List<KeyValue> kvs = response.getKvs();
                    KeyValue keyValue = kvs.listIterator().next();
                    value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                } catch (Exception e) {
                    log.warn("field 初始化失败");
                }
                final String finalValue = value;
                EtcdNode etcdNode = map.get(key0);
                if (etcdNode == null) {
                    etcdNode = new EtcdNode();
                    map.put(key0, etcdNode);
                    etcdClient.getWatchClient().watch(key, new Watch.Listener() {
                        @Override
                        public void onNext(WatchResponse watchResponse) {
                            for (WatchEvent event : watchResponse.getEvents()) {
                                switch (event.getEventType()) {
                                    case PUT: {
                                        KeyValue keyValue = event.getKeyValue();
                                        String newValue = keyValue.getValue().toString(StandardCharsets.UTF_8);
                                        EtcdNode node = map.get(key0);
                                        node.setValue(initValue(valueClass, newValue));
                                        log.info(key0 + "已更新");
                                    }
                                    break;
                                    case DELETE: {
                                        EtcdNode node = map.get(key0);
                                        node.setValue(initValue(valueClass, finalValue));
                                        break;
                                    }
                                    case UNRECOGNIZED:
                                    default: {
                                        log.info("未知问题");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }

                        @Override
                        public void onCompleted() {

                        }
                    });
                }
                etcdNode.setValue(initValue(valueClass, finalValue));
                try {
                    field.setAccessible(true);
                    field.set(bean, etcdNode);
                } catch (IllegalAccessException e) {
                    // impossible
                }
            }
        }

        return bean;
    }

    private Object initValue(Class c, String value) {
        if (c == String.class) {
            return value;
        }
        if (c == Integer.class) {
            return Integer.valueOf(value);
        }
        if (c == Boolean.class) {
            return Boolean.valueOf(value);
        }
        // json type
        if (value.equals("")) {
            value = "{}";
        }
        Object o = JSON.parseObject(value, c);
        return o;
    }
}
