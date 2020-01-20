package com.zjh.contentcenter.fegnClient.fallbackfactory;

import com.zjh.contentcenter.domain.dto.user.UserDTO;
import com.zjh.contentcenter.fegnClient.UserCenterFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName UserCenterFeignClientFallbackFactory
 * @Author zhaojh
 * @Date 2020/1/8 11:34
 * @Version 1.0
 * @Description //TODO
 **/
@Component
@Slf4j
public class UserCenterFeignClientFallbackFactory implements FallbackFactory<UserCenterFeignClient> {
    @Override
    public UserCenterFeignClient create(Throwable throwable) {
        return new UserCenterFeignClient() {
            @Override
            public UserDTO findById(Integer id) {
                log.warn("远程调用被限流/降级了",throwable);
                UserDTO userDTO = new UserDTO();
                userDTO.setWxNickname("一个默认用户");
                return userDTO;
            }
        };

    }
}
