package com.unigroup.dndcharlist.utils;

import com.unigroup.dndcharlist.mapper.JwkMapper;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;

@Component
public class GoogleKeyKeeper {

    private List<HashMap<String, String>> googleJwkKeys;
    private Long nextUpdateDelay = 0L;

    public void updateGoogleJwk() {
        String url = "https://www.googleapis.com/oauth2/v3/certs";
        RestTemplate template = new RestTemplate();
        HttpEntity<HashMap> response = template.getForEntity(url, HashMap.class);
        String result = response.getHeaders().getCacheControl();
        if (result == null) return;
        googleJwkKeys = ((List<HashMap<String, String>>)response.getBody().get("keys"));
        setNextUpdateDelay(result);
    }

    public PublicKey getPublicKey(String kid) {
        return googleJwkKeys.stream().filter(key -> key.get("kid").equals(kid))
                .map(key -> JwkMapper.mapToRSAPublicKey(key)).findFirst().orElse(null);
    }

    public long getDelay() {
        return nextUpdateDelay;
    }

    private void setNextUpdateDelay(String cacheControl) {
        List<String> params = List.of(cacheControl.split(", "));
        nextUpdateDelay = params.stream()
                .filter(param -> param.startsWith("max-age="))
                .map(string -> Long.valueOf(string.replace("max-age=", "")))
                .findFirst()
                .get();
    }
}
