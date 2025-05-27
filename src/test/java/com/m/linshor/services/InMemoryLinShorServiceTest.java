package com.m.linshor.services;

import com.m.linshor.entities.Mapping;
import com.m.linshor.repositories.MappingDao;
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
class InMemoryLinShorServiceTest {

    @Mock
    private MappingDao repository;

    @InjectMocks
    private InMemoryLinShorService inMemoryLinShorService;

    private Mapping testMapping;
    private String longUrl;
    private String shortUrl;

    @BeforeEach
    void setUp() {
        longUrl = "https://www.example-inmemory.com/very/long/url";
        shortUrl = "memShort";
        testMapping = new Mapping();
        testMapping.setId(1);
        testMapping.setLongUrl(longUrl);
        testMapping.setShortUrl(shortUrl);
    }

    @Test
    void saveLink_shouldGenerateShortUrlAndSave() {
        when(repository.findByShortUrl(anyString())).thenReturn(Optional.empty());
        when(repository.saveLink(any(Mapping.class))).thenAnswer(invocation -> {
            Mapping mappingToSave = invocation.getArgument(0);

            mappingToSave.setId(2);

            return mappingToSave;
        });

        Mapping savedMapping = inMemoryLinShorService.saveLink(longUrl);

        assertNotNull(savedMapping);
        assertEquals(longUrl, savedMapping.getLongUrl());
        assertNotNull(savedMapping.getShortUrl());
        assertFalse(savedMapping.getShortUrl().isEmpty());
        assertEquals(10, savedMapping.getShortUrl().length());

        ArgumentCaptor<Mapping> mappingCaptor = ArgumentCaptor.forClass(Mapping.class);
        verify(repository).saveLink(mappingCaptor.capture());
        assertEquals(longUrl, mappingCaptor.getValue().getLongUrl());
    }

    @Test
    void saveLink_shouldRegenerateShortUrlOnCollision() {
        when(repository.findByShortUrl(anyString()))
                .thenReturn(Optional.of(new Mapping()))
                .thenReturn(Optional.empty());
        when(repository.saveLink(any(Mapping.class))).thenReturn(testMapping);

        Mapping savedMapping = inMemoryLinShorService.saveLink(longUrl);

        assertNotNull(savedMapping);
        verify(repository, times(2)).findByShortUrl(anyString());
        verify(repository, times(1)).saveLink(any(Mapping.class));
    }

    @Test
    void updateLink_shouldUpdateShortUrlAndSave() {
        when(repository.findByLongUrl(longUrl)).thenReturn(testMapping);
        when(repository.updateLink(any(Mapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mapping updatedMapping = inMemoryLinShorService.updateLink(longUrl);

        assertNotNull(updatedMapping);
        assertEquals(longUrl, updatedMapping.getLongUrl());
        assertNotEquals(shortUrl, updatedMapping.getShortUrl()); // New short URL
        assertNotNull(updatedMapping.getShortUrl());
        assertEquals(10, updatedMapping.getShortUrl().length());

        ArgumentCaptor<Mapping> mappingCaptor = ArgumentCaptor.forClass(Mapping.class);
        verify(repository).updateLink(mappingCaptor.capture());
        assertEquals(testMapping.getId(), mappingCaptor.getValue().getId());
        assertEquals(longUrl, mappingCaptor.getValue().getLongUrl());
    }

    @Test
    void updateLink_shouldThrowExceptionIfMappingNotFound() {
        when(repository.findByLongUrl(longUrl)).thenReturn(null);
        assertThrows(NullPointerException.class, () -> inMemoryLinShorService.updateLink(longUrl),
                "Expected NullPointerException when mapping is not found for update, as findByLongUrl (which calls repository.findByLongUrl) returns null.");

        verify(repository).findByLongUrl(longUrl); // Verify this was called
        verify(repository, never()).updateLink(any(Mapping.class)); // updateLink should not be called
    }


    @Test
    void findByShortUrl_shouldReturnMapping() {
        when(repository.findByShortUrl(shortUrl)).thenReturn(Optional.of(testMapping));

        Optional<Mapping> foundMapping = inMemoryLinShorService.findByShortUrl(shortUrl);

        assertTrue(foundMapping.isPresent());
        assertEquals(testMapping, foundMapping.get());
        verify(repository).findByShortUrl(shortUrl);
    }

    @Test
    void findByShortUrl_shouldReturnEmptyOptionalIfNotFound() {
        when(repository.findByShortUrl(shortUrl)).thenReturn(Optional.empty());

        Optional<Mapping> foundMapping = inMemoryLinShorService.findByShortUrl(shortUrl);

        assertFalse(foundMapping.isPresent());
        verify(repository).findByShortUrl(shortUrl);
    }

    @Test
    void deleteByShortUrl_shouldCallRepositoryDelete() {
        doNothing().when(repository).deleteByShortUrl(shortUrl);

        inMemoryLinShorService.deleteByShortUrl(shortUrl);

        verify(repository).deleteByShortUrl(shortUrl);
    }

    @Test
    void findByLongUrl_shouldReturnMapping() {
        when(repository.findByLongUrl(longUrl)).thenReturn(testMapping);

        Mapping foundMapping = inMemoryLinShorService.findByLongUrl(longUrl);

        assertNotNull(foundMapping);
        assertEquals(testMapping, foundMapping);
        verify(repository).findByLongUrl(longUrl);
    }

    @Test
    void findByLongUrl_shouldReturnNullIfNotFound() {
        when(repository.findByLongUrl(longUrl)).thenReturn(null);

        Mapping foundMapping = inMemoryLinShorService.findByLongUrl(longUrl);
        assertNull(foundMapping);
        verify(repository).findByLongUrl(longUrl);
    }
}
