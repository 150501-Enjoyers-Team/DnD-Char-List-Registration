package com.unigroup.dndcharlist.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationUserDto {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
}
