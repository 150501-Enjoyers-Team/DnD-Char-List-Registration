package com.unigroup.dndcharlist.utils;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {
    @Value("984hg493gh0439rthr0429uruj2309yh937gc763fe87t3f89723gf")
    private String secret;

    @Value("30m")
    private Duration jwtLifetime;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", rolesList);
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifetime.toMillis());
        String issuer = "150501";
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .setIssuer(issuer)
                .compact();
    }

    public String getGoogleUsername(String token) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .build();
        GoogleIdToken idToken = verifier.verify(token);
        if (idToken != null) {
            Payload payload = idToken.getPayload();
            return payload.getEmail();
        }
        return null;
    }

    public List<String> getRoles(String token) {
        return getAllClaimsFromOurJwt(token).get("roles", List.class);
    }

    public String getIssuer(String token) {
        return getAllClaimsFromToken(token).get("iss").toString();
    }

    public String getUsername(String token) throws GeneralSecurityException, IOException {
        String username = null;
        String issuer = getIssuer(token);
        if (issuer.equals("150501")) {
            username = getAllClaimsFromOurJwt(token).getSubject();
        } else if (issuer.equals("https://accounts.google.com")) {
            username = getGoogleUsername(token);
        }
        return username;
    }

    public Claims getAllClaimsFromOurJwt(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public Map<String, Object> convertJsonStringToMap(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        Map<String, Object> map = new HashMap<>();
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            map.put(key, value);
        }
        return map;
    }

    private Map<String, Object> getAllClaimsFromToken(String token) {
        String[] parts = token.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String decoded = new String(decoder.decode(parts[1]));
        Map<String, Object> map = convertJsonStringToMap(decoded);
        return map;
    }
}