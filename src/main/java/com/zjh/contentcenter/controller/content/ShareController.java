package com.zjh.contentcenter.controller.content;

import com.zjh.contentcenter.domain.dto.content.ShareDTO;
import com.zjh.contentcenter.service.content.ShareService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName ShareController
 * @Author zhaojh
 * @Date 2019/12/23 15:17
 * @Version 1.0
 * @Description //TODO
 **/
@RestController
@RequestMapping("/shares")
public class ShareController {

    @Resource
    private ShareService shareService;

    @GetMapping("/{id}")
    public ShareDTO findById(@PathVariable Integer id){
        ShareDTO shardto = this.shareService.findById(id);
        return shardto;
    }

}
