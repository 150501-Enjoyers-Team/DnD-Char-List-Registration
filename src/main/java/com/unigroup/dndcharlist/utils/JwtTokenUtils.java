package com.unigroup.dndcharlist.utils;

import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {


    private KeyLocator keyLocator;
    private JwtParser jwtParser;

    @Autowired
    public JwtTokenUtils(KeyLocator locator)
    {
        keyLocator = locator;
        jwtParser = Jwts.parserBuilder()
                .setSigningKeyResolver(keyLocator)
                .build();
    }

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
                .signWith(new SecretKeySpec(keyLocator.getOwnSecret().getBytes(), "HmacSHA256"))
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
        Object roleArray = getAllClaimsFromJwt(token).get("roles");
        if (roleArray == null) return Collections.singletonList("ROLE_USER");
        return ((JSONArray) roleArray).toList().stream().map(Object::toString).collect(Collectors.toList());
    }

    public String getIssuer(String token) {
        return getAllClaimsFromJwt(token).get("iss").toString();
    }

    public String getUsername(String token) throws GeneralSecurityException, IOException {
        Claims claims = getAllClaimsFromJwt(token);
        String issuer = getIssuer(token);
        String username = null;
        if (issuer.equals("150501")) {
            username = claims.getSubject();
        } else if (issuer.equals("https://accounts.google.com")) {
            username = (String) claims.get("email");
        }

        return username;
    }

    public Claims getAllClaimsFromJwt(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

/*    public Map<String, Object> convertJsonStringToMap(String jsonString) {
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
    }*/
}