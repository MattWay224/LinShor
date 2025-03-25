package com.m.linshor.controllers;

import com.m.linshor.entities.Mapping;
import com.m.linshor.services.LinShorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
@Tag(name = "URL Shortener API", description = "Operations for URL shortening and redirection")
public class LinShorController {
    private final LinShorService linShorService;

    @PutMapping("/update")
    @Operation(summary = "Update a long URL",
            description = "Updates an existing shortened URL with a new long URL.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid URL format")
    })
    public Mapping updateLink(@RequestParam @Parameter(description = "New long URL") String longUrl) {
        return linShorService.updateLink(longUrl);
    }

    @DeleteMapping("delete/{shortUrl}")
    @Operation(summary = "Delete a shortened URL", description = "Removes a shortened URL from the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Short URL not found")
    })
    public int deleteByShortUrl(@PathVariable @Parameter(description = "Short URL to delete") String shortUrl) {
        linShorService.deleteByShortUrl(shortUrl);
        return 200;
    }

    @PostMapping("/post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Short URL created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<String> linshorUrl(@RequestBody @Parameter(
            description = "The long URL to shorten", required = true)
                                             String longUrl, HttpServletRequest request) {
        Mapping mapping = linShorService.saveLink(longUrl);

        String serverAddress = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String fullShortUrl = serverAddress + "/linshor/v1/" + mapping.getShortUrl();

        return ResponseEntity.ok(fullShortUrl);
    }

    @GetMapping("/{shortUrl}")
    @Operation(summary = "Redirect to long URL",
            description = "Finds the original URL for a given short URL and redirects.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirected successfully"),
            @ApiResponse(responseCode = "404", description = "Short URL not found"),
            @ApiResponse(responseCode = "400", description = "Invalid URL stored in database")
    })
    public ResponseEntity<Object> redirectToLongUrl(@PathVariable @Parameter(
            description = "Short URL to resolve") String shortUrl) {
        Optional<Mapping> mapping = linShorService.findByShortUrl(shortUrl);

        if (mapping.isPresent()) {
            String longUrl = mapping.get().getLongUrl().trim().replaceAll("^\"|\"$", "");

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
