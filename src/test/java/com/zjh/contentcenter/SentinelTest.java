package com.zjh.contentcenter;

import org.springframework.web.client.RestTemplate;

/**
 * @ClassName SentinelTest
 * @Author zhaojh
 * @Date 2020/1/3 10:26
 * @Version 1.0
 * @Description //TODO
 **/
public class SentinelTest {
    public static void main1(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 1000; i++) {
            restTemplate.getForObject("http://localhost:8010/actuator/sentinel", String.class);
            Thread.sleep(500);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 100; i++) {
            String object = restTemplate.getForObject("http://localhost:8010/test-a", String.class);
            System.out.println("--------------"+object+"-------------");
        }
    }
}
