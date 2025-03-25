package com.m.linshor.services;

import com.m.linshor.entities.Mapping;
import com.m.linshor.repositories.MappingDao;

import java.util.Optional;
import java.util.Random;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InMemoryLinShorService implements LinShorService {
    private MappingDao repository;
    private static final String BASE62 =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_URL_LENGTH = 10;

    @Override
    public Optional<Mapping> findByShortUrl(String shortUrl) {
        return repository.findByShortUrl(shortUrl);
    }

    @Override
    public Mapping saveLink(String longUrl) {
        String shortUrl;
        do {
            shortUrl = generateShor();
        } while (repository.findByShortUrl(shortUrl).isPresent());

        Mapping urlMapping = new Mapping();
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setLongUrl(longUrl);
        return repository.saveLink(urlMapping);
    }

    @Override
    public Mapping updateLink(String longUrl) {
        Mapping mapping = findByLongUrl(longUrl);
        String shortUrl = generateShor();

        mapping.setShortUrl(shortUrl);
        return repository.updateLink(mapping);
    }

    @Override
    public void deleteByShortUrl(String shortUrl) {
        repository.deleteByShortUrl(shortUrl);
    }

    private String generateShor() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(SHORT_URL_LENGTH);
        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            sb.append(BASE62.charAt(random.nextInt(BASE62.length())));
        }
        return sb.toString();
    }

    @Override
    public Mapping findByLongUrl(String longUrl) {
        return repository.findByLongUrl(longUrl);
    }
}
