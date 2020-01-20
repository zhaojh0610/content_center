package com.zjh.contentcenter.domain.dto.content;

import com.zjh.contentcenter.domain.enums.AuditStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName ShareAuditDTO
 * @Author zhaojh
 * @Date 2020/1/11 9:40
 * @Version 1.0
 * @Description //TODO
 **/
@Data
@AllArgsConstructor
public class ShareAuditDTO {
    private AuditStatusEnum auditStatusEnum;
    private String reason;
}


