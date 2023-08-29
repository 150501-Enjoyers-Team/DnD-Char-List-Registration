package com.unigroup.dndcharlist.controllers;

import com.unigroup.dndcharlist.dtos.JwtRequest;
import com.unigroup.dndcharlist.dtos.RegistrationUserDto;
import com.unigroup.dndcharlist.services.AuthService;
import com.unigroup.dndcharlist.utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, GeneralSecurityException {
        authService.createRefreshToken(request, response);
    }

    @GetMapping("/get/username")
    public ResponseEntity<?> getUsernameFromToken(String token) throws GeneralSecurityException {
        String username = null;
        username = jwtTokenUtils.getUsername(token);
        if(username == null)
            return new ResponseEntity<>("can't extract username", HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(username);
    }
}
