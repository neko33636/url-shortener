package com.maksim.urlshortener.service;

import com.maksim.urlshortener.dto.UrlListItem;
import com.maksim.urlshortener.entity.UrlEntity;
import com.maksim.urlshortener.entity.UserEntity;
import com.maksim.urlshortener.exception.UrlNotFoundException;
import com.maksim.urlshortener.repository.UrlRepository;
import com.maksim.urlshortener.repository.UserRepository;
import com.maksim.urlshortener.util.ShortCodeGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UrlService {

    private final UrlRepository repository;
    private final UserRepository userRepository;

    public UrlService(UrlRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public String createShortUrl(String url) {
        UrlEntity entity = new UrlEntity();
        entity.setOriginalUrl(url);

        String shortCode;
        do {
            shortCode = ShortCodeGenerator.generate();
        } while (repository.findByShortCode(shortCode).isPresent());

        entity.setShortCode(shortCode);
        getCurrentUser().ifPresent(entity::setOwner);

        repository.save(entity);

        return shortCode;
    }

    public String getOriginalUrl(String shortCode) {
        return repository.findByShortCode(shortCode)
                .map(UrlEntity::getOriginalUrl)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));
    }

    @Transactional(readOnly = true)
    public List<UrlListItem> findAllUrls(String baseUrl) {
        return repository.findAllByOrderByIdDesc().stream()
                .map(entity -> toListItem(entity, baseUrl))
                .toList();
    }

    private UrlListItem toListItem(UrlEntity entity, String baseUrl) {
        UrlListItem item = new UrlListItem();
        item.setOriginalUrl(entity.getOriginalUrl());
        item.setShortCode(entity.getShortCode());
        item.setShortUrl(baseUrl + "/r/" + entity.getShortCode());
        if (entity.getOwner() != null) {
            item.setOwnerUsername(entity.getOwner().getUsername());
        }
        return item;
    }

    private java.util.Optional<UserEntity> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return java.util.Optional.empty();
        }
        return userRepository.findByUsername(authentication.getName());
    }
}