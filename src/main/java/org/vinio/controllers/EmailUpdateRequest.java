package org.vinio.controllers;

import lombok.Data;

@Data
class EmailUpdateRequest {
    private String oldEmail;
    private String newEmail;
}