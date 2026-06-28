package com.maksim.urlshortener.service;

import com.maksim.urlshortener.entity.UrlEntity;
import com.maksim.urlshortener.repository.UrlRepository;
import com.maksim.urlshortener.util.ShortCodeGenerator;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private final UrlRepository repository;

    public UrlService(UrlRepository repository) {
        this.repository = repository;
    }

    public String createShortUrl(String url) {
        UrlEntity entity = new UrlEntity();
        entity.setOriginalUrl(url);

        String shortCode;
        do {
            shortCode = ShortCodeGenerator.generate();
        } while (repository.findByShortCode(shortCode).isPresent());

        entity.setShortCode(shortCode);

        repository.save(entity);

        return shortCode;
    }

    public String getOriginalUrl(String shortCode) {
        return repository.findByShortCode(shortCode)
                .map(UrlEntity::getOriginalUrl)
                .orElseThrow(() -> new RuntimeException("URL не найден"));
    }
}