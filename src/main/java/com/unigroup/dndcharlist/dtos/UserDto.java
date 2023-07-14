package com.unigroup.dndcharlist.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    private UUID userId;
    private String username;
    private String email;
}
