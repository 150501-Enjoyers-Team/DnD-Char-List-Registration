package com.unigroup.dndcharlist.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.List;

@Component
public class GoogleKeyKeeper {

    private List<HashMap<String, String>> googleJwkKeys;
    private Long nextUpdateDelay = 0L;

    public void updateGoogleJwk()
    {
        String url = "https://www.googleapis.com/oauth2/v3/certs";
        RestTemplate template = new RestTemplate();
        HttpEntity<HashMap> response = template.getForEntity(url, HashMap.class);
        String result = response.getHeaders().getCacheControl();
        if (result == null) return;

        googleJwkKeys = ((List<HashMap<String, String>>)response.getBody().get("keys"));
        setNextUpdateDelay(result);
    }

    public PublicKey getPublicKey(String kid)
    {
        return googleJwkKeys.stream().filter(key -> key.get("kid").equals(kid)).map(key -> getPublicKeyFromJwk(key)).findFirst().orElse(null);
    }
    public long getDelay()
    {
        return nextUpdateDelay;
    }

    private void setNextUpdateDelay(String cacheControl)
    {
        List<String> params = List.of(cacheControl.split(", "));
        nextUpdateDelay = params.stream()
                .filter(param -> param.startsWith("max-age="))
                .map(string -> Long.valueOf(string.replace("max-age=", "")))
                .findFirst()
                .get();

    }

    private PublicKey getPublicKeyFromJwk(HashMap<String, String> jwkKey){

        Base64 decoder = new Base64(true);
        BigInteger mod = new BigInteger(1, decoder.decode(jwkKey.get("n")));
        BigInteger exp = new BigInteger(1, decoder.decode(jwkKey.get("e")));

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(mod, exp);
        PublicKey publicKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            publicKey = kf.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return null;
        }
        return publicKey;
    }
}
