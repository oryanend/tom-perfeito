package com.oryanend.tom_perfeito_api.services;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheService {
  @Autowired private CacheManager cacheManager;

  public void evictAllCacheValues(String cacheName) {
    Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
  }
}
