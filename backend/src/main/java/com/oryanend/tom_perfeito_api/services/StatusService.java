package com.oryanend.tom_perfeito_api.services;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class StatusService {
  @Autowired private DataSource dataSource;
  @Autowired private Environment springEnv;
  @Autowired private HealthEndpoint healthEndpoint;

  public Map<String, Object> getStatus() {
    Map<String, Object> response = new HashMap<>();

    response.put(
        "updated_at", Instant.now().atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT));

    Map<String, Object> dependencies = new HashMap<>();
    dependencies.put("database", getDatabaseInfo());
    dependencies.put("webserver", getWebServerInfo());

    response.put("dependencies", dependencies);
    return response;
  }

  private double measureQueryLatency(Connection conn) throws Exception {
    long start = System.nanoTime();
    try (var stmt = conn.prepareStatement("SELECT 1")) {
      stmt.execute();
    }
    return (System.nanoTime() - start) / 1_000_000.0;
  }

  private Map<String, Object> getDatabaseInfo() {
    Map<String, Object> dbInfo = new LinkedHashMap<>();

    try (Connection conn = dataSource.getConnection()) {
      DatabaseMetaData meta = conn.getMetaData();

      dbInfo.put("status", conn.isValid(2) ? "healthy" : "unstable");
      dbInfo.put("version", meta.getDatabaseProductVersion().replaceAll("\\s*\\(.*?\\)", ""));
      dbInfo.put("max_connections", meta.getMaxConnections());

      if (dataSource instanceof HikariDataSource hikari) {
        dbInfo.put("open_connections", hikari.getHikariPoolMXBean().getActiveConnections());
      }

      Map<String, Object> latency = new LinkedHashMap<>();
      latency.put("first_query", measureQueryLatency(conn));
      latency.put("second_query", measureQueryLatency(conn));
      latency.put("third_query", measureQueryLatency(conn));

      dbInfo.put("latency", latency);

    } catch (Exception e) {
      dbInfo.put("status", "down");
      dbInfo.put("error", e.getMessage());
    }

    return dbInfo;
  }

  private String detectProvider() {
    Map<String, String> env = System.getenv();

    if (env.containsKey("DYNO")) return "Heroku";
    if (env.containsKey("RENDER")) return "Render";
    if (env.containsKey("VERCEL")) return "Vercel";
    if (env.containsKey("RAILWAY_ENVIRONMENT")) return "Railway";
    if (env.containsKey("K_SERVICE")) return "GCP (Cloud Run)";
    if (env.containsKey("AWS_EXECUTION_ENV")) return "AWS";
    if (env.containsKey("WEBSITE_SITE_NAME")) return "Azure";

    return "local";
  }

  String provider = System.getenv("APP_PROVIDER");

  private Map<String, Object> getWebServerInfo() {
    Map<String, Object> webInfo = new LinkedHashMap<>();

    webInfo.put(
        "status", healthEndpoint.health().getStatus().getCode().equals("UP") ? "healthy" : "down");

    String[] profiles = springEnv.getActiveProfiles();
    webInfo.put("version", SpringBootVersion.getVersion());
    webInfo.put("provider", provider != null ? provider : detectProvider());
    webInfo.put("environment", profiles.length > 0 ? profiles[0] : "default");

    return webInfo;
  }
}
