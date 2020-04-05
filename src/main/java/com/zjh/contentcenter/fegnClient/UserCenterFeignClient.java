package com.zjh.contentcenter.fegnClient;

import com.zjh.contentcenter.domain.dto.user.UserAndBonusDTO;
import com.zjh.contentcenter.domain.dto.user.UserDTO;
import com.zjh.contentcenter.fegnClient.fallbackfactory.UserCenterFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @InterfaceName UserCenterFeignClient
 * @Author zhaojh
 * @Date 2020/1/1 13:42
 * @Version 1.0
 * @Description
 **/
//@FeignClient(name = "user-center",configuration = UserCenterFeignConfiguration.class)
@FeignClient(name = "user-center",
//        fallback = UserCenterFeignClientFallback.class,
        fallbackFactory = UserCenterFeignClientFallbackFactory.class
)
public interface UserCenterFeignClient {

    @GetMapping("/users/{id}")
    UserDTO findById(@PathVariable Integer id);

    @PutMapping("/users/add-bonus/{id}")
    UserDTO addBonus(@RequestBody UserAndBonusDTO userAndBonusDTO);
}
