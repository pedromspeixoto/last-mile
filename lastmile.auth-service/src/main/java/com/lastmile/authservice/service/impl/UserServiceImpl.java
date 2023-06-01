package com.lastmile.authservice.service.impl;

import com.lastmile.authservice.domain.User;
import com.lastmile.authservice.dto.UserUpdateRequestDto;
import com.lastmile.authservice.repository.UserRepository;
import com.lastmile.authservice.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public User get(String username) {

        throwIfUsernameNotExists(username);

        User user = userRepository.findByUsername(username).get();

        return user;
    }

    @Override
    public User create(User user) {
        throwIfUsernameExists(user.getUsername());

        String hash = passwordEncoder.encode(user.getPassword());
        user.setPassword(hash);

        // TODO send sms or email with code for activation
        user.setActivated(Boolean.TRUE);

        // TODO other routines on account creation

        return userRepository.save(user);
    }

    @Override
    public User update(String username, UserUpdateRequestDto userUpdateRequestDto) {
        throwIfUsernameNotExists(username);

        User user = userRepository.findByUsername(username).get();

        if (userUpdateRequestDto.getActivated() && !user.isActivated()){
            user.setActivated(true);
        }

        if (userUpdateRequestDto.getActivationKey() != null && !userUpdateRequestDto.getActivationKey().isEmpty()){
            user.setActivationKey(userUpdateRequestDto.getActivationKey());
        }

        if (userUpdateRequestDto.getPassword() != null && !userUpdateRequestDto.getPassword().isEmpty()){
            String hash = passwordEncoder.encode(userUpdateRequestDto.getPassword());
            user.setPassword(hash);
        }

        if (userUpdateRequestDto.getResetPasswordKey() != null && !userUpdateRequestDto.getResetPasswordKey().isEmpty()){
            user.setResetPasswordKey(userUpdateRequestDto.getResetPasswordKey());
        }

        if (userUpdateRequestDto.getRole() != null && !userUpdateRequestDto.getRole().toString().isEmpty()){
            user.setAuthorities(new HashSet<>(Collections.singletonList(userUpdateRequestDto.getRole())));
        }

        logger.info(user.toString());
        return userRepository.save(user);
    }

    @Override
    public void delete(String username) {
        throwIfUsernameNotExists(username);
    
        userRepository.deleteByUsername(username);
    }

    private void throwIfUsernameExists(String username) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        existingUser.ifPresent((user) -> {
            throw new IllegalArgumentException("User not available");
        });
    }

    private void throwIfUsernameNotExists(String username) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (!existingUser.isPresent()) {
            throw new IllegalArgumentException("User does not exist");
        }
    }

}
