package com.zjh.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName MyRequestOriginParser
 * @Author zhaojh
 * @Date 2020/1/10 10:21
 * @Version 1.0
 * @Description //TODO
 **/
//@Component
public class MyRequestOriginParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest request) {
        //从请求参数中获取名为 origin 的参数并返回
        //如果获取不到 origin 参数，那么久抛异常
        String origin = request.getParameter("origin");
        if (StringUtils.isBlank(origin)) {
            throw new IllegalArgumentException("origin must be specified");
        }
        return origin;
    }
}
