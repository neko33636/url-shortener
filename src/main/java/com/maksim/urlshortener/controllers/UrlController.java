package com.maksim.urlshortener.controllers;

import com.maksim.urlshortener.dto.CreateUrlRequest;
import com.maksim.urlshortener.dto.CreateUrlResponse;
import com.maksim.urlshortener.service.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/urls")
    public CreateUrlResponse post(@RequestBody CreateUrlRequest createUrlRequest) {
        String shortUrl = urlService.createShortUrl(createUrlRequest.getUrl());

        CreateUrlResponse response = new CreateUrlResponse();
        response.setShortUrl(shortUrl);

        return response;
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl) {

        String originalUrl = urlService.getOriginalUrl(shortUrl);

        return ResponseEntity
                .status(302)
                .header("Location", originalUrl)
                .build();
    }
}