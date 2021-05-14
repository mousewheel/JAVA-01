package io.kimmking.rpcfx.annotation;

/**
 * @Author zhurui
 * @Date 2021/3/20 5:39 下午
 * @Version 1.0
 */
public @interface Service {

    String hostUrl() default "";

}
