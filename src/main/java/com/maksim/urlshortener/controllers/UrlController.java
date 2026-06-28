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
    public CreateUrlResponse create(@RequestBody CreateUrlRequest request) {

        String shortCode = urlService.createShortUrl(request.getUrl());

        CreateUrlResponse response = new CreateUrlResponse();
        response.setShortUrl("http://localhost:8080/r/" + shortCode);

        return response;
    }
}