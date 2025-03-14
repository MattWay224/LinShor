package com.m.linshor.repositories;

import com.m.linshor.entities.Mapping;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MappingRepository extends JpaRepository<Mapping, Long> {
    List<Mapping> findAllLinks(String userId);

    Optional<Mapping> findByShortUrl(String shortUrl);

    Optional<Mapping> findById(int id);

    Mapping findByLongUrl(String longUrl);
}
