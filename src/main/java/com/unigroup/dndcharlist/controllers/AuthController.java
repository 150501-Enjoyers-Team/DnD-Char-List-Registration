package com.unigroup.dndcharlist.controllers;

import com.unigroup.dndcharlist.dtos.JwtRequest;
import com.unigroup.dndcharlist.dtos.JwtResponse;
import com.unigroup.dndcharlist.dtos.RegistrationUserDto;
import com.unigroup.dndcharlist.exceptions.AppError;
import com.unigroup.dndcharlist.services.AuthService;
import com.unigroup.dndcharlist.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;

@CrossOrigin(origins = "*")
@RestController
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        return authService.createAuthToken(authRequest);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        return authService.createNewUser(registrationUserDto);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(String refreshToken) throws GeneralSecurityException {
        JwtResponse response = authService.refreshAccessToken(refreshToken);
        if(response == null)
            return new ResponseEntity<>(new AppError(HttpStatus.NO_CONTENT.value(),
                    "can't get new access token"), HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/username")
    public ResponseEntity<?> getUsernameFromToken(String token) throws GeneralSecurityException {
        String username;
        username = jwtTokenUtils.getUsername(token);
        if(username == null)
            return new ResponseEntity<>(new AppError(HttpStatus.NO_CONTENT.value(),
                    "can't extract username"), HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(username);
    }
}
