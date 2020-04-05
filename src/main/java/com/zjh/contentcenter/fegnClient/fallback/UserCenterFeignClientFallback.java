package com.zjh.contentcenter.fegnClient.fallback;

import com.zjh.contentcenter.domain.dto.user.UserAndBonusDTO;
import com.zjh.contentcenter.domain.dto.user.UserDTO;
import com.zjh.contentcenter.fegnClient.UserCenterFeignClient;
import org.springframework.stereotype.Component;

/**
 * @ClassName UserCenterFeignClientFallbackFactory
 * @Author zhaojh
 * @Date 2020/1/8 10:14
 * @Version 1.0
 * @Description //TODO
 **/
@Component
public class UserCenterFeignClientFallback implements UserCenterFeignClient {
    @Override
    public UserDTO findById(Integer id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setWxNickname("一个默认用户");
        return userDTO;
    }

    @Override
    public UserDTO addBonus(UserAndBonusDTO userAndBonusDTO) {
        return null;
    }
}



