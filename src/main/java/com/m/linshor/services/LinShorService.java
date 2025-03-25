package com.m.linshor.services;

import com.m.linshor.entities.Mapping;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public interface LinShorService {

    Optional<Mapping> findByShortUrl(String shortUrl);

    Mapping saveLink(String longUrl);

    Mapping updateLink(String longUrl);

    void deleteByShortUrl(String id);

    Mapping findByLongUrl(String longUrl);
}
