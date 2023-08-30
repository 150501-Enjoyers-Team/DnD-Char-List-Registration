package com.unigroup.dndcharlist.mapper;

import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;

public class JwkMapper {

    public static PublicKey mapToRSAPublicKey (HashMap<String, String> jwkKey ) {
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
