package com.zjh.contentcenter.controller.content;

import com.zjh.contentcenter.domain.dto.content.ShareAuditDTO;
import com.zjh.contentcenter.domain.entity.content.Share;
import com.zjh.contentcenter.service.content.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ShareAdminController
 * @Author zhaojh
 * @Date 2020/1/10 11:36
 * @Version 1.0
 * @Description //TODO
 **/
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/admin/shares")
public class ShareAdminController {
    private final ShareService shareService;

    @PutMapping("/audit/{id}")
    public Share auditById(@PathVariable Integer id, @RequestBody ShareAuditDTO shareAuditDTO) {
        Share share = this.shareService.auditById(id, shareAuditDTO);
        return share;
    }
}
