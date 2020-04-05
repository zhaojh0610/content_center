package com.zjh.contentcenter.controller.content;

import com.github.pagehelper.PageInfo;
import com.zjh.contentcenter.auth.CheckLogin;
import com.zjh.contentcenter.domain.dto.content.ShareDTO;
import com.zjh.contentcenter.domain.entity.content.Share;
import com.zjh.contentcenter.service.content.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName ShareController
 * @Author zhaojh
 * @Date 2019/12/23 15:17
 * @Version 1.0
 * @Description //TODO
 **/
@RestController
@RequestMapping("/shares")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareController {
    private final ShareService shareService;

    @GetMapping("/{id}")
    @CheckLogin
    public ShareDTO findById(@PathVariable Integer id) {
        return this.shareService.findById(id);
    }

    @GetMapping("/q")
    public PageInfo<Share> q(@RequestParam(required = false) String title,
                             @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                             @RequestParam(required = false,defaultValue = "10") Integer pageSize) {
        return this.shareService.q(title, pageNo, pageSize);
    }

    @GetMapping("/exchange/{id}")
    @CheckLogin
    public Share exchangeById(@PathVariable Integer id, HttpServletRequest request) {
        return this.shareService.exchangeById(id,request);
    }





}
