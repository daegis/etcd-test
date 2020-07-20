package cn.aegisa.test.etcd.config;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xianyingda@gmail.com
 * @serial
 * @since 2020-07-20 11:21
 */
@Configuration
@Slf4j
public class EtcdConfig {

    @Bean
    public Client etcdClient() {
        return Client.builder().endpoints("http://dev.aegisa.cn:2379").build();
    }

    @Bean
    public KV etcdKvClient() {
        return etcdClient().getKVClient();
    }
}
