package com.zjh.contentcenter.service.content;

import com.alibaba.fastjson.JSON;
import com.zjh.contentcenter.dao.content.RocketmqTransactionLogMapper;
import com.zjh.contentcenter.dao.content.ShareMapper;
import com.zjh.contentcenter.domain.dto.content.ShareAuditDTO;
import com.zjh.contentcenter.domain.dto.content.ShareDTO;
import com.zjh.contentcenter.domain.dto.messaging.UserAddBonusMsaDTO;
import com.zjh.contentcenter.domain.dto.user.UserDTO;
import com.zjh.contentcenter.domain.entity.content.RocketmqTransactionLog;
import com.zjh.contentcenter.domain.entity.content.Share;
import com.zjh.contentcenter.domain.enums.AuditStatusEnum;
import com.zjh.contentcenter.fegnClient.UserCenterFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * @ClassName ShareService
 * @Author zhaojh
 * @Date 2019/12/23 12:29
 * @Version 1.0
 * @Description //TODO
 **/
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ShareService {
    private final ShareMapper shareMapper;
    private final RocketMQTemplate rocketMQTemplate;
    private final UserCenterFeignClient userCenterFeignClient;
    private final RocketmqTransactionLogMapper rocketmqTransactionLogMapper;
    private final Source source;

    public ShareDTO findById(Integer id) {
        //获取分享详情
        Share share = this.shareMapper.selectByPrimaryKey(id);
        //获取发布人ID
        Integer userId = share.getUserId();
        //怎么调用微服务的users/{id}
//        UserDTO userDTO = this.restTemplate.getForObject("http://user-center/users/{userId}", UserDTO.class, userId);
        //使用feign实现负载均衡
        UserDTO userDTO = this.userCenterFeignClient.findById(userId);
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());
        return shareDTO;
    }

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        Integer userId = 1;
        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://localhost:8080/users/{id}",
                String.class, userId);
        System.out.println(forEntity);
    }

    public Share auditById(Integer id, ShareAuditDTO shareAuditDTO) {
        //step1 查询share是否存在，不存在或者当前的audit_status != NOT_YET 那么久抛出异常
        Share share = shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("参数非法，该分享不存在！");
        }
        //step2 如果是PASS，那么发送消息给rocketmq，让用户中心去消费，并为发布人添加积分
        if (AuditStatusEnum.PASS.equals(shareAuditDTO.getAuditStatusEnum())) {
            String transactionId = UUID.randomUUID().toString();
            this.source.output().send(MessageBuilder.withPayload(UserAddBonusMsaDTO.builder()
                    .userId(share.getUserId())
                    .bonus(50)
                    .build())
                    .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                    .setHeader("share_id", id)
                    .setHeader("dto", JSON.toJSONString(shareAuditDTO))
                    .build());
        }else {
            this.auditByIdInDB(id, shareAuditDTO);
        }
        return share;
    }

    /**
     * 修改本地数据
     * @param id
     * @param shareAuditDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdInDB(Integer id, ShareAuditDTO shareAuditDTO) {
        Share share = Share.builder()
                .id(id)
                .auditStatus(shareAuditDTO.getAuditStatusEnum().toString())
                .reason(shareAuditDTO.getReason())
                .build();
        shareMapper.updateByPrimaryKeySelective(share);
    }

    /**
     * 修改数据并记录本地事务
     * @param id
     * @param shareAuditDTO
     * @param transactionId
     */
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdWithRocketMqLog(Integer id, ShareAuditDTO shareAuditDTO,String transactionId) {
        this.auditByIdInDB(id,shareAuditDTO);
        this.rocketmqTransactionLogMapper.insert(RocketmqTransactionLog
                .builder()
                .transactionId(transactionId)
                .log("审核分享...")
                .build());
    }
}
