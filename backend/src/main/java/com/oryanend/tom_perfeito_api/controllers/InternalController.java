package com.oryanend.tom_perfeito_api.controllers;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalController {
  private static final Logger logger = LoggerFactory.getLogger(InternalController.class);

  private final JdbcTemplate jdbcTemplate;

  public InternalController(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @GetMapping("/db-ping")
  public ResponseEntity<Map<String, Object>> dbPing() {
    long start = System.nanoTime();
    try {
      Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
      long durationMs = (System.nanoTime() - start) / 1_000_000;
      Map<String, Object> body = new HashMap<>();
      body.put("dbResult", one);
      body.put("durationMs", durationMs);
      return ResponseEntity.ok(body);
    } catch (Exception ex) {
      long durationMs = (System.nanoTime() - start) / 1_000_000;
      logger.error("db-ping error", ex);
      Map<String, Object> body = new HashMap<>();
      body.put("error", ex.getMessage());
      body.put("durationMs", durationMs);
      return ResponseEntity.status(500).body(body);
    }
  }
}
