package com.zjh.contentcenter.fegnClient;

import com.zjh.contentcenter.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName TestUserCenterFeignClient
 * @Author zhaojh
 * @Date 2020/1/2 15:01
 * @Version 1.0
 * @Description //TODO
 **/
@FeignClient(name = "user-center")
public interface TestUserCenterFeignClient {
    @GetMapping("/q")
    UserDTO Query(@SpringQueryMap UserDTO userDTO);
}
