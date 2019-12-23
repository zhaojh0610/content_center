package com.zjh.contentcenter.service.content;

import com.zjh.contentcenter.dao.content.ShareMapper;
import com.zjh.contentcenter.domain.dto.content.ShareDTO;
import com.zjh.contentcenter.domain.dto.user.UserDTO;
import com.zjh.contentcenter.domain.entity.content.Share;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @ClassName ShareService
 * @Author zhaojh
 * @Date 2019/12/23 12:29
 * @Version 1.0
 * @Description //TODO
 **/
@Service
public class ShareService {

    @Resource
    private ShareMapper shareMapper;

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
        //怎么调用微服务的users/{id}
        UserDTO userDTO = this.restTemplate.getForObject("http://localhost:8080/users/{id}", UserDTO.class,userId);
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
