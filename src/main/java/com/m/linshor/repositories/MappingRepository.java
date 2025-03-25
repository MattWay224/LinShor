package com.m.linshor.repositories;

import com.m.linshor.entities.Mapping;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MappingRepository extends JpaRepository<Mapping, Long> {
    Optional<Mapping> findByShortUrl(String shortUrl);
    Mapping findByLongUrl(String longUrl);
}
