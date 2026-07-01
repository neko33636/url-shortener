package com.maksim.urlshortener.controllers;

import com.maksim.urlshortener.dto.CreateUrlRequest;
import com.maksim.urlshortener.dto.CreateUrlResponse;
import com.maksim.urlshortener.dto.UrlListItem;
import com.maksim.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/urls")
    public CreateUrlResponse create(@RequestBody CreateUrlRequest request, HttpServletRequest httpRequest) {
        if (request.getUrl() == null || request.getUrl().isBlank()) {
            throw new IllegalArgumentException("URL обязателен");
        }

        String shortCode = urlService.createShortUrl(request.getUrl().trim());
        String baseUrl = resolveBaseUrl(httpRequest);

        CreateUrlResponse response = new CreateUrlResponse();
        response.setShortUrl(baseUrl + "/r/" + shortCode);
        return response;
    }

    @GetMapping("/urls")
    public List<UrlListItem> listAll(HttpServletRequest httpRequest) {
        return urlService.findAllUrls(resolveBaseUrl(httpRequest));
    }

    private String resolveBaseUrl(HttpServletRequest httpRequest) {
        return ServletUriComponentsBuilder.fromRequestUri(httpRequest)
                .replacePath(null)
                .build()
                .toUriString();
    }
}
