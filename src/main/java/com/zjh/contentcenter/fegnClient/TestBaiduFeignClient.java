package com.zjh.contentcenter.fegnClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName TestBaiduFeignClient
 * @Author zhaojh
 * @Date 2020/1/2 15:37
 * @Version 1.0
 * @Description //TODO
 * Feign脱离Ribbon的使用
 **/
@FeignClient(name = "baidu", url = "http://wwww.baidu.com")
public interface TestBaiduFeignClient {

    @GetMapping("")
    String index();
}
