package com.oryanend.tom_perfeito_api.services.utils;

import java.util.concurrent.TimeUnit;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheUtil {
  // Clears `notes` cache
  @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS)
  @CacheEvict(value = "notes", allEntries = true)
  public void clearCacheNotes() {}

  // Clears `musics` cache
  @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
  @CacheEvict(value = "musics", allEntries = true)
  public void clearCacheMusics() {}

  // Clears `chords` cache
  @Scheduled(fixedDelay = 2, timeUnit = TimeUnit.MINUTES)
  @CacheEvict(value = "chords", allEntries = true)
  public void clearCacheChords() {}
}
