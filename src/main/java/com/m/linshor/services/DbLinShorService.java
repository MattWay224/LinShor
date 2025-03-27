package com.m.linshor.services;

import com.m.linshor.entities.Mapping;
import com.m.linshor.repositories.MappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@Primary
@AllArgsConstructor
public class DbLinShorService implements LinShorService {
    private final MappingRepository repository;
    private static final String BASE62 =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_URL_LENGTH = 10;

    private String generateShor() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(SHORT_URL_LENGTH);
        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            sb.append(BASE62.charAt(random.nextInt(BASE62.length())));
        }
        return sb.toString();
    }

    public Mapping saveLink(String longUrl) {
        String shortUrl;
        do {
            shortUrl = generateShor();
        } while (repository.findByShortUrl(shortUrl).isPresent());

        Mapping urlMapping = new Mapping();
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setLongUrl(longUrl);
        return repository.save(urlMapping);
    }

    @Override
    public Mapping updateLink(String longUrl) {
        Mapping mapping = findByLongUrl(longUrl);
        String shortUrl = generateShor();

        mapping.setShortUrl(shortUrl);
        return repository.save(mapping);
    }

    public Optional<Mapping> findByShortUrl(String shortUrl) {
        return repository.findByShortUrl(shortUrl);
    }

    public void deleteByShortUrl(String shortUrl) {
        repository.findByShortUrl(shortUrl).ifPresent(repository::delete);
    }

    @Override
    public Mapping findByLongUrl(String longUrl) {
        return repository.findByLongUrl(longUrl);
    }
}
