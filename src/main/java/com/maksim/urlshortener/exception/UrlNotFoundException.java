package com.maksim.urlshortener.exception;

public class UrlNotFoundException extends RuntimeException {

    public UrlNotFoundException(String shortCode) {
        super("URL не найден: " + shortCode);
    }
}
