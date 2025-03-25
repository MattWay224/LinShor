package com.m.linshor.controllers;

import com.m.linshor.entities.Mapping;
import com.m.linshor.services.LinShorService;

import java.net.URI;
import java.net.URL;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/linshor/v1")
@AllArgsConstructor
public class LinShorController {
    private final LinShorService linShorService;

    @GetMapping("/find/{shortUrl}")
    public Optional<Mapping> findByShortUrl(@PathVariable String shortUrl) {
        return linShorService.findByShortUrl(shortUrl);
    }

    @PutMapping("update")
    public Mapping updateLink(String longUrl) {
        return linShorService.updateLink(longUrl);
    }

    @DeleteMapping("delete/{shortUrl}")
    public int deleteByShortUrl(@PathVariable String shortUrl) {
        linShorService.deleteByShortUrl(shortUrl);
        return 200;
    }

    @PostMapping("/post")
    public ResponseEntity<String> linshorUrl(@RequestBody String longUrl, HttpServletRequest request) {
        Mapping mapping = linShorService.saveLink(longUrl);

        String serverAddress = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String fullShortUrl = serverAddress + "/linshor/v1/" + mapping.getShortUrl();

        return ResponseEntity.ok(fullShortUrl);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Object> redirectToLongUrl(@PathVariable String shortUrl) {
        Optional<Mapping> mapping = linShorService.findByShortUrl(shortUrl);

        if (mapping.isPresent()) {
            String longUrl = mapping.get().getLongUrl().trim().replaceAll("^\"|\"$", ""); // Remove surrounding quotes

            try {
                URI uri = new URL(longUrl).toURI(); // Convert URL to URI safely
                return ResponseEntity.status(302).location(uri).build();
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Invalid URL stored in database: " + longUrl);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
