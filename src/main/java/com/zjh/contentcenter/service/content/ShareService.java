package com.zjh.contentcenter.service.content;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjh.contentcenter.dao.content.MidUserShareMapper;
import com.zjh.contentcenter.dao.content.RocketmqTransactionLogMapper;
import com.zjh.contentcenter.dao.content.ShareMapper;
import com.zjh.contentcenter.domain.dto.content.ShareAuditDTO;
import com.zjh.contentcenter.domain.dto.content.ShareDTO;
import com.zjh.contentcenter.domain.dto.messaging.UserAddBonusMsaDTO;
import com.zjh.contentcenter.domain.dto.user.UserAndBonusDTO;
import com.zjh.contentcenter.domain.dto.user.UserDTO;
import com.zjh.contentcenter.domain.entity.content.MidUserShare;
import com.zjh.contentcenter.domain.entity.content.RocketmqTransactionLog;
import com.zjh.contentcenter.domain.entity.content.Share;
import com.zjh.contentcenter.domain.enums.AuditStatusEnum;
import com.zjh.contentcenter.fegnClient.UserCenterFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final UserCenterFeignClient userCenterFeignClient;
    private final RocketmqTransactionLogMapper rocketmqTransactionLogMapper;
    private final Source source;
    private final MidUserShareMapper midUserShareMapper;

    public ShareDTO findById(Integer id) {
        //获取分享详情
        Share share = this.shareMapper.selectByPrimaryKey(id);
        //获取发布人ID
        Integer userId = share.getUserId();
        /*
        怎么调用微服务的users/{id}
        UserDTO userDTO = this.restTemplate.getForObject("http://user-center/users/{userId}", UserDTO.class, userId);
        使用feign实现负载均衡
        */
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
        if (!Objects.equals("NOT_YET", share.getAuditStatus())) {
            throw new IllegalArgumentException("参数非法！该分享已审核通过或审核不通过！");
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
        } else {
            this.auditByIdInDB(id, shareAuditDTO);
        }
        return share;
    }

    /**
     * 修改本地数据
     *
     * @param id
     * @param shareAuditDTO
     */
    @Transactional(rollbackFor = RuntimeException.class)
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
     *
     * @param id
     * @param shareAuditDTO
     * @param transactionId
     */
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdWithRocketMqLog(Integer id, ShareAuditDTO shareAuditDTO, String transactionId) {
        this.auditByIdInDB(id, shareAuditDTO);
        this.rocketmqTransactionLogMapper.insert(RocketmqTransactionLog
                .builder()
                .transactionId(transactionId)
                .log("审核分享...")
                .build());
    }

    public PageInfo<Share> q(String title, Integer pageNo, Integer pageSize, Integer userId) {
        //它会切入下面这条不分页的SQL，这里利用的是mybatis拦截器机制自动的给下面的SQL添加上分页语句
        PageHelper.startPage(pageNo, pageSize);
        //不分页的SQL
        List<Share> shares = this.shareMapper.selectByParam(title);
        List<Share> sharesDeal;
        if (userId == null) {
            sharesDeal = shares.stream()
                    .peek(share -> share.setDownloadUrl(null))
                    .collect(Collectors.toList());
        }
        //1.如果用户未登录，那么downloadUrl全部设为null
        //2.如果用户登录了，那么查询一下mid_user_share,如果没有数据，那么这条share的downloadUrl设为null
        else {
            sharesDeal = shares.stream().peek(share -> {
                MidUserShare midUserShare = this.midUserShareMapper.selectOne(MidUserShare.builder()
                        .shareId(share.getId())
                        .userId(userId)
                        .build()
                );
                if (midUserShare == null) {
                    share.setDownloadUrl(null);
                }
            }).collect(Collectors.toList());
        }
        return new PageInfo<Share>(sharesDeal);
    }

    public Share exchangeById(Integer id, HttpServletRequest request) {
        Object userId = request.getAttribute("id");
        Integer integerUserId = (Integer) userId;
        //1.根据ID查询share
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("该分享不存在");
        }
        MidUserShare midUserShare = this.midUserShareMapper.selectOne(
                MidUserShare.builder()
                        .userId(integerUserId)
                        .shareId(id)
                        .build());
        if (midUserShare != null) {
            return share;
        }
        //2.根据当前登录的用户ID，查询积分是否够
        UserDTO userDTO = this.userCenterFeignClient.findById(integerUserId);
        Integer price = share.getPrice();
        if (price > userDTO.getBonus()) {
            throw new IllegalArgumentException("用户积分不够用！");
        }
        //3.扣减积分，往mid_user_share表里插入一条数据
        this.userCenterFeignClient.addBonus(UserAndBonusDTO.builder()
                .userId(integerUserId)
                .bonus(-price)
                .build());
        this.midUserShareMapper.insert(
                MidUserShare.builder()
                        .userId(integerUserId)
                        .shareId(id)
                        .build());
        return share;
    }
}
