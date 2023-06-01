package com.lastmile.authservice.controller;

import com.lastmile.authservice.domain.User;
import com.lastmile.authservice.dto.UserDto;
import com.lastmile.authservice.dto.UserRegistrationDto;
import com.lastmile.authservice.dto.UserUpdateRequestDto;
import com.lastmile.authservice.service.UserService;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/current")
    public Principal getUser(Principal principal) {
        return principal;
    }

    @GetMapping("/details")
    public UserDto getUserDetails(Principal principal) {
        User loggedUser = userService.get(principal.getName());
        return toDto(loggedUser);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserRegistrationDto userRegistration) {
        User savedUser = userService.create(toUser(userRegistration));
        return toDto(savedUser);
    }

    @PutMapping("/{username}")
    public UserDto updateUser(@PathVariable(value = "username") String username,
                              @Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        User updatedUser = userService.update(username, userUpdateRequestDto);
        return toDto(updatedUser);
    }

    @DeleteMapping
    public Boolean deleteUser(@Valid @RequestBody String username) {
        try {
            userService.delete(username);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUserIdentification(user.getUserIdentification());
        userDto.setUsername(user.getUsername());
        userDto.setRole(user.getAuthorities().get(0).toString());
        return userDto;
    }

    private User toUser(UserRegistrationDto userRegistration) {
        User user = new User();
        user.setUserIdentification(userRegistration.getUserIdentification());
        user.setUsername(userRegistration.getUsername());
        user.setPassword(userRegistration.getPassword());
        user.setAuthorities(new HashSet<>(Collections.singletonList(userRegistration.getRole())));
        return user;
    }

}