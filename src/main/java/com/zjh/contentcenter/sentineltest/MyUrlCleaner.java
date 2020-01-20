package com.zjh.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @ClassName MyUrlCleaner
 * @Author zhaojh
 * @Date 2020/1/10 10:43
 * @Version 1.0
 * @Description //TODO
 **/
@Component
public class MyUrlCleaner implements UrlCleaner {
    @Override
    public String clean(String originUrl) {
        //让/shares/1 与 /shares/2
        String[] split = originUrl.split("/");
        return Arrays.stream(split).map(string -> {
            if (NumberUtils.isNumber(string)) {
                return "{number}";
            }
            return string;
        }).reduce((a, b) -> a + "/" + b).orElse("");
    }
}
