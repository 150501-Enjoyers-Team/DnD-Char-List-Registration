package com.unigroup.dndcharlist.services;

import com.unigroup.dndcharlist.dtos.JwtRequest;
import com.unigroup.dndcharlist.dtos.JwtResponse;
import com.unigroup.dndcharlist.dtos.RegistrationUserDto;
import com.unigroup.dndcharlist.dtos.UserDto;
import com.unigroup.dndcharlist.entities.User;
import com.unigroup.dndcharlist.exceptions.AppError;
import com.unigroup.dndcharlist.utils.JwtTokenUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String accessToken = jwtTokenUtils.generateToken(userDetails);
        String refreshToken = jwtTokenUtils.generateRefreshToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
    }

    public JwtResponse refreshAccessToken(String refreshToken)
            throws GeneralSecurityException {
        String username;
        JwtResponse response = null;
        username = jwtTokenUtils.getUsername(refreshToken);
        if (username != null) {
            var user = userService.loadUserByUsername(username);
            if(jwtTokenUtils.isTokenValid(refreshToken, user)) {
                String accessToken = jwtTokenUtils.generateToken(user);
                response = JwtResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        return response;
    }

    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        if (!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
                    "Пароли не совпадают"), HttpStatus.BAD_REQUEST);
        }
        if (userService.findByUsername(registrationUserDto.getUsername()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
                    "Пользователь с указанным именем уже существует"), HttpStatus.BAD_REQUEST);
        }
        User user = userService.createNewUser(registrationUserDto);
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername(), user.getEmail()));
    }
}
