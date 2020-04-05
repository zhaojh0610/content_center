package com.zjh.contentcenter;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.zjh.contentcenter.dao.content.ShareMapper;
import com.zjh.contentcenter.domain.dto.user.UserDTO;
import com.zjh.contentcenter.domain.entity.content.Share;
import com.zjh.contentcenter.fegnClient.TestBaiduFeignClient;
import com.zjh.contentcenter.fegnClient.TestUserCenterFeignClient;
import com.zjh.contentcenter.sentineltest.TestControllerBlockHandlerClass;
import com.zjh.contentcenter.service.content.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhaojh
 * @version 1.0
 * @date 2019/10/14 8:44
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RefreshScope
public class TestController {

    @Resource
    private ShareMapper shareMapper;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/test")
    public List<Share> testInset() {
        Share share = new Share();
        share.setCreateTime(new Date());
        share.setUpdateTime(new Date());
        share.setTitle("xxx");
        share.setCover("aaa");
        share.setAuthor("zjh");
        share.setBuyCount(1);
        //插入数据
        this.shareMapper.insertSelective(share);
        //查询所有数据
        List<Share> list = shareMapper.selectAll();
        return list;
    }

    @GetMapping("/test2")
    public List<ServiceInstance> getInstances() {
        List<ServiceInstance> instances = discoveryClient.getInstances("user-center");
        return instances;
    }

    @Autowired
    private TestUserCenterFeignClient testUserCenterFeignClient;

    @GetMapping("/test-get")
    public UserDTO query(UserDTO userDTO) {
        return testUserCenterFeignClient.Query(userDTO);
    }

    @Autowired
    private TestBaiduFeignClient testBaiduFeignClient;

    @GetMapping("/test-baidu")
    public String baiduIndex() {
        return this.testBaiduFeignClient.index();
    }

    @Autowired
    private TestService testService;

    @GetMapping("/test-a")
    public String testA() {
        testService.common();
        return "test-a";
    }

    @GetMapping("/test-b")
    public String testB() {
        testService.common();
        return "test-b";
    }

    @GetMapping("/test-hot")
    @SentinelResource("hot")
    public String testHot(@RequestParam(required = false) String a, @RequestParam(required = false) String b) {
        return a + " " + b;
    }

    @GetMapping("/test-add-flow-rule")
    public String testHot() {
        this.initFlowQpsRule();
        return "success";
    }

    private void initFlowQpsRule() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule("/shares/1");
        rule.setCount(20);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setLimitApp("default");
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

    @GetMapping("/test-sentinel-api")
    public String testSentinelAPI(@RequestParam(required = false) String a) {
        //定义一个sentinel保护的资源，名称是test-sentinel-api
        String resourceName = "test-sentinel-api";
        ContextUtil.enter(resourceName, "test-wfw");
        Entry entry = null;
        try {
            entry = SphU.entry(resourceName);
            //被保护的业务逻辑
            if (StringUtils.isBlank(a)) {
                throw new IllegalArgumentException("a不能为空");
            }
            return a;
        } catch (BlockException e) {
            log.warn("限流，或者被降级了", e);
            return "限流，或者被降级了";
        } catch (IllegalArgumentException e2) {
            //统计IllegalArgumentException发生的次数、发生占比
            Tracer.trace(e2);
            return "参数非法！";
        } finally {
            if (entry != null) {
                //退出entry
                entry.exit();
            }
            ContextUtil.exit();
        }
    }

    @GetMapping("/test-sentinel-resource")
    @SentinelResource(value = "test-sentinel-api",
            blockHandler = "block",
            blockHandlerClass = TestControllerBlockHandlerClass.class,
            fallback = "fallback")
    public String testSentinelResource(@RequestParam(required = false) String a) {
        //被保护的业务逻辑
        if (StringUtils.isBlank(a)) {
            throw new IllegalArgumentException("a cannot be blank.");
        }
        return a;
    }

    /**
     * 1.5 处理降级
     * -sentinel 1.6 可以处理throwable
     *
     * @param a
     * @return
     */
    public String fallback(String a) {
        log.warn("限流，或者降级了");
        return "限流，或者降级了，fallback";
    }

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/test-rest-template-sentinel/{userId}")
    public UserDTO test(@PathVariable Integer userId) {
        return restTemplate
                .getForObject(
                        "http://user-center/users/{userId}",
                        UserDTO.class, userId);
    }

    @Autowired
    private Source source;

    @GetMapping("/test-stream")
    public String testStream() {
        this.source.output().send(MessageBuilder.withPayload("消息体").build());
        return "success";
    }

    @GetMapping("/tokenRelay/{userId}")
    public ResponseEntity<UserDTO> tokenRelay(@PathVariable Integer userId) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = request.getHeader("X-Token");
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Token", token);
        return this.restTemplate
                .exchange("http://user-center/users/{userId}",
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        UserDTO.class,
                        userId);
    }

    @Value("${your.configuration}")
    private String yourConfiguration;

    @GetMapping("/test-config")
    public String getYourConfiguration() {
        return this.yourConfiguration;
    }

}