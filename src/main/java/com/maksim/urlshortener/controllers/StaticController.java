package com.maksim.urlshortener.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class StaticController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/about")
    public String about(){
        return "about";
    }
    @GetMapping("/contacts")
    public String contacts(){
        return "contacts";
    }

    @GetMapping("/all-urls")
    public String allUrls(){
        return "all-urls";
    }

}
