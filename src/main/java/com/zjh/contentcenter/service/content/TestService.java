package com.zjh.contentcenter.service.content;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName TestService
 * @Author zhaojh
 * @Date 2020/1/3 11:10
 * @Version 1.0
 * @Description //TODO
 **/
@Slf4j
@Service
public class TestService {
    @SentinelResource("common")
    public String common() {
        log.info("common......");
        return "common";
    }
}



