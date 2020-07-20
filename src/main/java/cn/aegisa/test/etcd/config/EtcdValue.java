package cn.aegisa.test.etcd.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xianyingda@gmail.com
 * @serial
 * @since 2020-07-20 11:33
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EtcdValue {
    public String value();

    public String defalut() default "";
}
