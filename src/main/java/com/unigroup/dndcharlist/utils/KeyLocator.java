package com.unigroup.dndcharlist.utils;

import lombok.Data;
import org.apache.commons.codec.binary.Base64;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;



@Component
public class KeyLocator implements SigningKeyResolver {

    @Autowired
    GoogleKeyKeeper googleKeyKeeper;

    private String ownSecret = "984hg493gh0439rthr0429uruj2309yh937gc763fe87t3f89723gf";

    public String getOwnSecret() {
        return ownSecret;
    }

    @Override
    public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
        switch ((String) claims.get("iss"))
        {
            case "150501" -> {
                return new SecretKeySpec(ownSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            }
            case "https://accounts.google.com" -> {
                return googleKeyKeeper.getPublicKey(jwsHeader.getKeyId());
            }
        }
        return null;
    }

    @Override
    public Key resolveSigningKey(JwsHeader jwsHeader, String s) {
        return null;
    }
}
