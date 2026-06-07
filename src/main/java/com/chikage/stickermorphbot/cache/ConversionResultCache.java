package com.chikage.stickermorphbot.cache;

import com.chikage.stickermorphbot.converter.ConversionFormat;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ConversionResultCache {

    public enum Kind {
        DOCUMENT,
        ANIMATION,
        VIDEO
    }

    public record CachedFile(String fileId, Kind kind){}

    private final Cache<String, CachedFile> cache = Caffeine.newBuilder()
            .maximumSize(50_000)
            .expireAfterWrite(Duration.ofDays(7))
            .build();

    public String key(String uniqueId, ConversionFormat format) {
        return uniqueId + ":" + format.getCode();
    }

    public CachedFile get(String uniqueId, ConversionFormat format) {
        return cache.getIfPresent(key(uniqueId, format));
    }

    public void put(String uniqueId, ConversionFormat format, CachedFile value) {
        cache.put(key(uniqueId, format), value);
    }
}
