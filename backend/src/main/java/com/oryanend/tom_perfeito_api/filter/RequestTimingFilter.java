package com.oryanend.tom_perfeito_api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Minimal filter that logs the duration of each HTTP request and adds a response header with the
 * measured duration in milliseconds. This is safe for production use and only reads timing
 * information; it doesn't change request/response semantics.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestTimingFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(RequestTimingFilter.class);

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    long start = System.nanoTime();
    try {
      filterChain.doFilter(request, response);
    } finally {
      long durationMs = (System.nanoTime() - start) / 1_000_000;
      response.addHeader("X-Request-Duration-ms", String.valueOf(durationMs));
      logger.info(
          "REQUEST_TIMING method={} uri={} status={} durationMs={} remoteIp={}",
          request.getMethod(),
          request.getRequestURI(),
          response.getStatus(),
          durationMs,
          request.getRemoteAddr());
    }
  }
}
