package com.zjh.contentcenter.fegnClient;

import com.zjh.contentcenter.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @InterfaceName UserCenterFeignClient
 * @Author zhaojh
 * @Date 2020/1/1 13:42
 * @Version 1.0
 * @Description
 **/
@FeignClient(name = "user-center")
public interface UserCenterFeignClient {

    @GetMapping("/users/{id}")
    UserDTO findById(@PathVariable Integer id);
}
