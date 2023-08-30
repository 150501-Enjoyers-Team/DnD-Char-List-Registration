package com.unigroup.dndcharlist.mapper;

import com.unigroup.dndcharlist.dtos.UserDataResponse;
import com.unigroup.dndcharlist.entities.User;

public class UserDataMapper {

    public static UserDataResponse mapToUserDataResponse(User user) {
        UserDataResponse userDataResponse = UserDataResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
        return userDataResponse;
    }
}
