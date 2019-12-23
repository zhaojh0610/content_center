package com.zjh.contentcenter;

import com.zjh.contentcenter.dao.content.ShareMapper;
import com.zjh.contentcenter.domain.entity.content.Share;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zhaojh
 * @version 1.0
 * @date 2019/10/14 8:44
 */
@RestController
public class TestController {

    @Resource
    private ShareMapper shareMapper;

    @GetMapping("/test")
    public List<Share> testInset(){
        Share share = new Share();
        share.setCreateTime(new Date());
        share.setUpdateTime(new Date());
        share.setTitle("xxx");
        share.setCover("aaa");
        share.setAuthor("zjh");
        share.setBuyCount(1);
        //插入数据
        this.shareMapper.insertSelective(share);
        ///查询所有数据
        List<Share> list = shareMapper.selectAll();
        return list;
    }


}
