package com.m.linshor.services;

import com.m.linshor.entities.Mapping;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Service
public interface LinShorService {
    @GetMapping("")
    List<Mapping> getAllLinks();

    Optional<Mapping> findByShortUrl(String shortUrl);

    Mapping saveLink(String url);

    Optional<Mapping> findById(int id);

    Mapping updateLink(String url);

    void deleteById(int id);

    Mapping findByLongUrl(String longUrl);
}
