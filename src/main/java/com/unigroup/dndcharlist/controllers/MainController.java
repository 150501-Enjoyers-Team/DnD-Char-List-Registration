package com.unigroup.dndcharlist.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.unigroup.dndcharlist.dtos.UserDataResponse;
import com.unigroup.dndcharlist.entities.User;
import com.unigroup.dndcharlist.services.UserService;
import com.unigroup.dndcharlist.utils.GoogleKeyKeeper;
import com.unigroup.dndcharlist.utils.KeyLocator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.unigroup.dndcharlist.mapper.UserDataMapper.mapToUserDataResponse;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class MainController {
    private final UserService userService;

    @GetMapping("/unsecured")
    public String unsecuredData(String token) {

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
