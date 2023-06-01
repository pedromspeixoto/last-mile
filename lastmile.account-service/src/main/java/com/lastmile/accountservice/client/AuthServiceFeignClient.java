package com.lastmile.accountservice.client;

import com.lastmile.accountservice.dto.AuthUserRegistrationDto;
import com.lastmile.accountservice.dto.UserUpdateRequestDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service")
public interface AuthServiceFeignClient {

    @PostMapping(value = "/uaa/user")
    void createUser(@RequestBody AuthUserRegistrationDto user);

    @DeleteMapping(value = "/uaa/user")
    void deleteUser(@RequestBody String username);

    @PutMapping(value = "/uaa/user/{username}")
    void updateUser(@PathVariable("username") String username,
                    @RequestBody UserUpdateRequestDto user);

}
