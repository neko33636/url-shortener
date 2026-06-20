package com.maksim.urlshortener.service;

import com.maksim.urlshortener.dto.CreateUrlResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Service
public class UrlService {
    long counter = 1;
    Map<Long, String> storage = new HashMap<>();
    public String createShortUrl(String url) {
        long id = counter++;
        storage.put(id, url);
        return String.valueOf(id);
    }

    public String getOriginalUrl(String shortUrl) {
        long id = Long.parseLong(shortUrl);
        return storage.get(id);
    }

}
