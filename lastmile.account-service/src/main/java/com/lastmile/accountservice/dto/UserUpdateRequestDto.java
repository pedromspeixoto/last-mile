package com.lastmile.accountservice.dto;

import com.lastmile.accountservice.enums.Authorities;

public class UserUpdateRequestDto  {

    private String username;
    
    private String password;

    private boolean activated;

    private String activationKey;

    private String resetPasswordKey;

    private Authorities role;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public boolean getActivated() {
        return this.activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getActivationKey() {
        return this.activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getResetPasswordKey() {
        return this.resetPasswordKey;
    }

    public void setResetPasswordKey(String resetPasswordKey) {
        this.resetPasswordKey = resetPasswordKey;
    }

    public Authorities getRole() {
        return this.role;
    }

    public void setRole(Authorities role) {
        this.role = role;
    }

}