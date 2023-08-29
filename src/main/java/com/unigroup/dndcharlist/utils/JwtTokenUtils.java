package com.unigroup.dndcharlist.utils;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.unigroup.dndcharlist.entities.User;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
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
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
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

    @Value("1m")
    private Duration accessExpiration;

    @Value("30d")
    private Duration refreshExpiration;

    public String generateToken(UserDetails userDetails) {
        return buildToken(userDetails, accessExpiration.toMillis());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, refreshExpiration.toMillis());
    }

    private String buildToken(UserDetails userDetails, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", rolesList);
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + expiration);
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

    public String getIssuer(String token) {
        return getAllClaimsFromJwt(token).get("iss").toString();
    }

    public String getUsername(String token) throws GeneralSecurityException {
        String username = null;
        Claims claims = null;
        String issuer = null;
        try {
            claims = getAllClaimsFromJwt(token);
            issuer = getIssuer(token);
            if (issuer.equals("150501")) {
                username = claims.getSubject();
            } else if (issuer.equals("https://accounts.google.com")) {
                username = (String) claims.get("email");
            }
        } catch (ExpiredJwtException e) {
            log.error("Время жизни токена вышло");
        } catch (SignatureException e) {
            log.error("Подпись неправильная");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return username;
    }

    public Claims getAllClaimsFromJwt(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) throws GeneralSecurityException, IOException {
        final String username = getUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromJwt(token);
        return claimsResolver.apply(claims);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public List<String> getRoles(String token) {
        List<String> roleArray = (List<String>) getAllClaimsFromJwt(token).get("roles");
        if (roleArray == null) return Collections.singletonList("ROLE_USER");
        else return roleArray;
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