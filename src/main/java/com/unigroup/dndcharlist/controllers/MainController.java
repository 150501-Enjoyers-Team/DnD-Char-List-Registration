package com.unigroup.dndcharlist.controllers;

import com.unigroup.dndcharlist.dtos.UserDataResponse;
import com.unigroup.dndcharlist.dtos.UserDto;
import com.unigroup.dndcharlist.entities.User;
import com.unigroup.dndcharlist.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;

import static com.unigroup.dndcharlist.mapper.UserDataMapper.mapToUserDataResponse;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class MainController {
    private final UserService userService;

    @GetMapping("/unsecured")
    public String unsecuredData() {
        return "Unsecured data";
    }

    @GetMapping("/secured")
    public String securedData() {
        return "Secured data";
    }

    @GetMapping("/admin")
    public String adminData() {
        return "Admin data";
    }

    @GetMapping("/info")
    public ResponseEntity<UserDataResponse> userData(Principal principal) {
        Optional<User> optionalUser = userService.findByUsername(principal.getName());
        if (optionalUser.isEmpty())
        {
            UserDataResponse user = new UserDataResponse();
            user.setUsername(principal.getName());
            user.setEmail(principal.getName());
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.ok(mapToUserDataResponse(optionalUser.get()));
    }
}
