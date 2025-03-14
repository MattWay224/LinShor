package com.m.linshor.controllers;

import com.m.linshor.entities.Mapping;
import com.m.linshor.services.LinShorService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/linshor/v1")
@AllArgsConstructor
public class LinShorController {
    private final LinShorService linShorService;

    @GetMapping("")
    public List<Mapping> findAllLinks() {
        return linShorService.getAllLinks();
    }

    @GetMapping("/{shortUrl}")
    public Optional<Mapping> findByShortUrl(@PathVariable String shortUrl) {
        return linShorService.findByShortUrl(shortUrl);
    }

    @PostMapping("post")
    public Mapping saveLink(String url) {
        return linShorService.saveLink(url);
    }

    @GetMapping("/{id}")
    public Optional<Mapping> findById(@PathVariable int id) {
        return linShorService.findById(id);
    }

    @PutMapping("update")
    public Mapping updateLink(String url) {
        return linShorService.updateLink(url);
    }

    @DeleteMapping("delete/{id}")
    public int deleteById(@PathVariable int id) {
        linShorService.deleteById(id);
        return 200;
    }

    @PostMapping("/linshor")
    public ResponseEntity<String> linshorUrl(@RequestBody String longUrl) {
        Mapping mapping = linShorService.saveLink(longUrl);
        return ResponseEntity.ok(mapping.getShortUrl());
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Object> redirectToLongUrl(@PathVariable String shortUrl) {
        Optional<Mapping> mapping = linShorService.findByShortUrl(shortUrl);
        return mapping
                .map(m -> ResponseEntity.status(302).location(URI.create(m.getUrl())).build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
