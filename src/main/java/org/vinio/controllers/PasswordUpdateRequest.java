package org.vinio.controllers;

import lombok.Data;

@Data
class PasswordUpdateRequest {
    private String oldPassword;
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}