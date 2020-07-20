package cn.aegisa.test.etcd.config;

/**
 * @author xianyingda@gmail.com
 * @serial
 * @since 2020-07-20 11:34
 */
public class EtcdNode<T> {
    private volatile T value;

    public T val() {
        return this.value;
    }

    void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.val().toString();
    }

}
