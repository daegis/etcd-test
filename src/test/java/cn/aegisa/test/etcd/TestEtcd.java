package cn.aegisa.test.etcd;

import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * @author xianyingda@gmail.com
 * @serial
 * @since 2020-07-20 11:04
 */
@Slf4j
public class TestEtcd extends BaseTester {

    @Test
    public void test01() throws Exception {
        // create client
        Client client = Client.builder().endpoints("http://dev.aegisa.cn:2379").build();
        KV kvClient = client.getKVClient();

        ByteSequence key = ByteSequence.from("test_key".getBytes());
        ByteSequence key2 = ByteSequence.from("name".getBytes());
        ByteSequence value = ByteSequence.from("test_value".getBytes());

        kvClient.put(key, value).get();

        CompletableFuture<GetResponse> getFuture = kvClient.get(key);

        client.getWatchClient().watch(key2, new Watch.Listener() {
            @Override
            public void onNext(WatchResponse watchResponse) {
                for (WatchEvent event : watchResponse.getEvents()) {
                    switch (event.getEventType()) {
                        case PUT: {
                            KeyValue keyValue = event.getKeyValue();
                            System.out.println("key = " + keyValue.getKey().toString(StandardCharsets.UTF_8));
                            System.out.println("value = " + keyValue.getValue().toString(StandardCharsets.UTF_8));
                            break;
                        }
                        case DELETE: {
                            KeyValue keyValue = event.getKeyValue();
                            System.out.println("key = " + keyValue.getKey().toString(StandardCharsets.UTF_8));
                            System.out.println("value = " + keyValue.getValue().toString(StandardCharsets.UTF_8));
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

        GetResponse response = getFuture.get();
        Thread.sleep(99999L);
    }

}
