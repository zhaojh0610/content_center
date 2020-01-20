package com.zjh.contentcenter.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @EnumName AuditStatusEnum
 * @Author zhaojh
 * @Date 2020/1/11 9:36
 * @Version 1.0
 * @Description
 **/
@Getter
@AllArgsConstructor
public enum AuditStatusEnum {
    /**
     * 待审核
     */
    NOT_YET,
    /**
     * 审核通过
     */
    PASS,
    /**
     * 审核不通过
     */
    REJECT
}
