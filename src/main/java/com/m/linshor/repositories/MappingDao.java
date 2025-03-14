package com.m.linshor.repositories;

import com.m.linshor.entities.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.stereotype.Repository;

@Repository
public class MappingDao {
    private final List<Mapping> LINKS = new ArrayList<>();

    public Mapping saveLink(Mapping mapping) {
        LINKS.add(mapping);
        return null;
    }

    public Optional<Mapping> findByShortUrl(String shortUrl) {
        return LINKS.stream().filter(link -> link.getShortUrl().equals(shortUrl)).findFirst();
    }

    public Optional<Mapping> findById(int id) {
        return LINKS.stream().filter(element -> element.getId() == id).findFirst();
    }

    public Mapping updateLink(Mapping mapping) {
        var linkIndex =
                IntStream.range(0, LINKS.size())
                        .filter(index -> LINKS.get(index).getId() == mapping.getId())
                        .findFirst()
                        .orElse(-1);
        if (linkIndex > -1) {
            LINKS.set(linkIndex, mapping);
            return mapping;
        }
        return null;
    }

    public void deleteById(int id) {
        var link = findById(id);
        if (link.isPresent()) {
            LINKS.remove(link);
        }
    }

    public Mapping findByLongUrl(String longUrl) {
        var linkIndex =
                IntStream.range(0, LINKS.size())
                        .filter(index -> LINKS.get(index).getLongUrl().equals(longUrl))
                        .findFirst()
                        .orElse(-1);
        if (linkIndex > -1) {
            return LINKS.get(linkIndex);
        } else {
            return null;
        }
    }
}
