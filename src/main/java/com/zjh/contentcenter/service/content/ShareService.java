package com.zjh.contentcenter.service.content;

import com.zjh.contentcenter.dao.content.ShareMapper;
import com.zjh.contentcenter.domain.dto.content.ShareDTO;
import com.zjh.contentcenter.domain.dto.user.UserDTO;
import com.zjh.contentcenter.domain.entity.content.Share;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @ClassName ShareService
 * @Author zhaojh
 * @Date 2019/12/23 12:29
 * @Version 1.0
 * @Description //TODO
 **/
@Service
public class ShareService {

    private static final Logger logger = LoggerFactory.getLogger(ShareService.class);

    @Resource
    private ShareMapper shareMapper;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Resource
    private final RestTemplate restTemplate;

    public ShareService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ShareDTO findById(Integer id){
        //获取分享详情
        Share share = this.shareMapper.selectByPrimaryKey(id);
        //获取发布人ID
        Integer userId = share.getUserId();
        List<ServiceInstance> instances = discoveryClient.getInstances("user-center");
        List<String> urls = instances.stream().map(instance -> instance.getUri().toString() + "/users/{id}")
                .collect(Collectors.toList());
        int i = ThreadLocalRandom.current().nextInt(urls.size());

        logger.info("请求地址：{}", urls.get(i));
        //怎么调用微服务的users/{id}
        UserDTO userDTO = this.restTemplate.getForObject(urls.get(i), UserDTO.class,userId);
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share,shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());
        return shareDTO;
    }

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        Integer userId = 1;
        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://localhost:8080/users/{id}", String.class,userId);
        System.out.println(forEntity);
    }
}
