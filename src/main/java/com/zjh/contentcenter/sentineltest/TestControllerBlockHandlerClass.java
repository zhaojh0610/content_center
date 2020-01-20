package com.zjh.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName TestControllerBlockHandlerClass
 * @Author zhaojh
 * @Date 2020/1/6 17:02
 * @Version 1.0
 * @Description //TODO
 **/
@Slf4j
public class TestControllerBlockHandlerClass {
    /**
     * 处理限流或者降级
     * @param a
     * @param e
     * @return
     */
    public static String block(String a, BlockException e) {
        log.warn("限流，或者降级了", e);
        return "限流，或者降级了,block";
    }

}
