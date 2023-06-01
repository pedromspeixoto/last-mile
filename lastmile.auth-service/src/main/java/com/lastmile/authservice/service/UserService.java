package com.lastmile.authservice.service;

import com.lastmile.authservice.domain.User;
import com.lastmile.authservice.dto.UserUpdateRequestDto;

public interface UserService {

    User get(String username);

    User create(User user);

    User update(String username, UserUpdateRequestDto userUpdateRequestDto);

    void delete(String username);
}
