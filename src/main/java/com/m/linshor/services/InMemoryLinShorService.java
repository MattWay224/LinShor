package com.m.linshor.services;

import com.m.linshor.entities.Mapping;
import com.m.linshor.repositories.MappingDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class InMemoryLinShorService implements LinShorService {
    private MappingDao repository;
    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_URL_LENGTH = 10;

    @Override
    public List<Mapping> getAllLinks() {
        return repository.findAllLinks();
    }

    @Override
    public Optional<Mapping> findByShortUrl(String shortUrl) {
        return repository.findByShortUrl(shortUrl);
    }

    @Override
    public Mapping saveLink(String url) {
        String shortUrl;
        do {
            shortUrl = generateShor();
        } while (repository.findByShortUrl(shortUrl).isPresent());

        Mapping urlMapping = new Mapping();
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setUrl(url);
        return repository.saveLink(urlMapping);
    }

    @Override
    public Optional<Mapping> findById(int id) {
        return repository.findById(id);
    }

    @Override
    public Mapping updateLink(String url) {
        Mapping mapping = findByLongUrl(url);
        String shortUrl = generateShor();

        mapping.setShortUrl(shortUrl);
        return repository.updateLink(mapping);
    }

    @Override
    public void deleteById(int id) {
        repository.deleteById(id);
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
