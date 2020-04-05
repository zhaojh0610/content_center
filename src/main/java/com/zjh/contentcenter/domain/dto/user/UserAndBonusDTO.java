package com.zjh.contentcenter.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author zhaojh
 * @CreateTime 2020/4/5 14:02
 * @Version 1.0
 * @Desc
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAndBonusDTO {

    private Integer userId;
    /**
     * 积分
     */
    private Integer bonus;
}
