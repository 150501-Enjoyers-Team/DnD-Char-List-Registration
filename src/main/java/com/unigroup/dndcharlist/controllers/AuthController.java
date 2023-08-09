package com.unigroup.dndcharlist.controllers;

import com.unigroup.dndcharlist.dtos.JwtRequest;
import com.unigroup.dndcharlist.dtos.RegistrationUserDto;
import com.unigroup.dndcharlist.services.AuthService;
import com.unigroup.dndcharlist.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@CrossOrigin(origins = "*")
@RestController
public class AuthController {
    private final AuthService authService;
    @Autowired
    private JwtTokenUtils utils;
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

    @GetMapping("/check")
    public ResponseEntity<?> check(String token) throws GeneralSecurityException, IOException {
        return ResponseEntity.ok(utils.getUsername(token));
    }
}
