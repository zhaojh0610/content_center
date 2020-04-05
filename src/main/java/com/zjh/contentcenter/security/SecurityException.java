package com.zjh.contentcenter.security;

/**
 * @Author zhaojh
 * @CreateTime 2020/3/13 17:00
 * @Version 1.0
 * @Desc
 **/
public class SecurityException extends RuntimeException {
    public SecurityException(String s) {
        super(s);
    }

    public SecurityException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
