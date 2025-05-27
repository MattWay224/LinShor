package com.m.linshor.services;

import com.m.linshor.entities.Mapping;
import com.m.linshor.repositories.MappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DbLinShorServiceTest {

    @Mock
    private MappingRepository repository;

    @InjectMocks
    private DbLinShorService dbLinShorService;

    private Mapping testMapping;
    private String longUrl;
    private String shortUrl;

    @BeforeEach
    void setUp() {
        longUrl = "https://www.example.com/very/long/url/to/shorten";
        shortUrl = "testShort";
        testMapping = new Mapping();
        testMapping.setId(1);
        testMapping.setLongUrl(longUrl);
        testMapping.setShortUrl(shortUrl);
    }

    @Test
    void saveLink_shouldGenerateShortUrlAndSave() {
        when(repository.findByShortUrl(anyString())).thenReturn(Optional.empty()); // Ensure generated short URL is unique
        when(repository.save(any(Mapping.class))).thenAnswer(invocation -> {
            Mapping mappingToSave = invocation.getArgument(0);

            mappingToSave.setId(1);

            return mappingToSave;
        });

        Mapping savedMapping = dbLinShorService.saveLink(longUrl);

        assertNotNull(savedMapping);
        assertEquals(longUrl, savedMapping.getLongUrl());
        assertNotNull(savedMapping.getShortUrl());
        assertFalse(savedMapping.getShortUrl().isEmpty());
        assertEquals(10, savedMapping.getShortUrl().length()); // SHORT_URL_LENGTH

        ArgumentCaptor<Mapping> mappingCaptor = ArgumentCaptor.forClass(Mapping.class);
        verify(repository).save(mappingCaptor.capture());
        assertEquals(longUrl, mappingCaptor.getValue().getLongUrl());
    }

    @Test
    void saveLink_shouldRegenerateShortUrlOnCollision() {
        String collidingShortUrl = "collide123";
        when(repository.findByShortUrl(anyString()))
                .thenReturn(Optional.of(new Mapping()))
                .thenReturn(Optional.empty());
        when(repository.save(any(Mapping.class))).thenReturn(testMapping);

        Mapping savedMapping = dbLinShorService.saveLink(longUrl);

        assertNotNull(savedMapping);
        verify(repository, times(2)).findByShortUrl(anyString()); // Called twice due to collision
        verify(repository, times(1)).save(any(Mapping.class));
    }

    @Test
    void updateLink_shouldUpdateShortUrlAndSave() {
        when(repository.findByLongUrl(longUrl)).thenReturn(testMapping);
        when(repository.save(any(Mapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mapping updatedMapping = dbLinShorService.updateLink(longUrl);

        assertNotNull(updatedMapping);
        assertEquals(longUrl, updatedMapping.getLongUrl());
        assertNotEquals(shortUrl, updatedMapping.getShortUrl()); // New short URL should be generated
        assertNotNull(updatedMapping.getShortUrl());
        assertEquals(10, updatedMapping.getShortUrl().length());

        ArgumentCaptor<Mapping> mappingCaptor = ArgumentCaptor.forClass(Mapping.class);
        verify(repository).save(mappingCaptor.capture());
        assertEquals(testMapping.getId(), mappingCaptor.getValue().getId());
        assertEquals(longUrl, mappingCaptor.getValue().getLongUrl());
    }

    @Test
    void updateLink_shouldThrowExceptionIfMappingNotFound() {
        when(repository.findByLongUrl(longUrl)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> dbLinShorService.updateLink(longUrl),
                "Expected NullPointerException when mapping is not found for update, as findByLongUrl returns null and then mapping.setShortUrl is called.");
    }


    @Test
    void findByShortUrl_shouldReturnMapping() {
        when(repository.findByShortUrl(shortUrl)).thenReturn(Optional.of(testMapping));
        Optional<Mapping> foundMapping = dbLinShorService.findByShortUrl(shortUrl);

        assertTrue(foundMapping.isPresent());
        assertEquals(testMapping, foundMapping.get());
        verify(repository).findByShortUrl(shortUrl);
    }

    @Test
    void findByShortUrl_shouldReturnEmptyOptionalIfNotFound() {
        when(repository.findByShortUrl(shortUrl)).thenReturn(Optional.empty());

        Optional<Mapping> foundMapping = dbLinShorService.findByShortUrl(shortUrl);

        assertFalse(foundMapping.isPresent());
        verify(repository).findByShortUrl(shortUrl);
    }

    @Test
    void deleteByShortUrl_shouldDeleteMappingIfFound() {
        when(repository.findByShortUrl(shortUrl)).thenReturn(Optional.of(testMapping));
        doNothing().when(repository).delete(testMapping);

        dbLinShorService.deleteByShortUrl(shortUrl);

        verify(repository).findByShortUrl(shortUrl);
        verify(repository).delete(testMapping);
    }

    @Test
    void deleteByShortUrl_shouldNotCallDeleteIfMappingNotFound() {
        when(repository.findByShortUrl(shortUrl)).thenReturn(Optional.empty());

        dbLinShorService.deleteByShortUrl(shortUrl);

        verify(repository).findByShortUrl(shortUrl);
        verify(repository, never()).delete(any(Mapping.class));
    }

    @Test
    void findByLongUrl_shouldReturnMapping() {
        when(repository.findByLongUrl(longUrl)).thenReturn(testMapping);

        Mapping foundMapping = dbLinShorService.findByLongUrl(longUrl);

        assertNotNull(foundMapping);
        assertEquals(testMapping, foundMapping);
        verify(repository).findByLongUrl(longUrl);
    }

    @Test
    void findByLongUrl_shouldReturnNullIfNotFound() {
        when(repository.findByLongUrl(longUrl)).thenReturn(null);

        Mapping foundMapping = dbLinShorService.findByLongUrl(longUrl);

        assertNull(foundMapping);
        verify(repository).findByLongUrl(longUrl);
    }
}
