package com.oryanend.tom_perfeito_api.db.migration;

import com.oryanend.tom_perfeito_api.db.migration.exceptions.PathNotFoundException;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import org.flywaydb.core.extensibility.MigrationType;
import org.flywaydb.core.internal.jdbc.StatementInterceptor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import org.springframework.stereotype.Component;

@Component
public class V9__InsertChords extends BaseJavaMigration {
    private static final String FILE_PATH = "db/chords.csv";

    @Override
    public ResolvedMigration getResolvedMigration(Configuration config, StatementInterceptor statementInterceptor) {
        return super.getResolvedMigration(config, statementInterceptor);
    }

    @Override
    public void migrate(Context context) throws Exception {
        Connection conn = context.getConnection();

        InputStream is = V9__InsertChords.class
                .getClassLoader()
                .getResourceAsStream(FILE_PATH);

        if (is == null) {
            throw new PathNotFoundException("Arquivo chords.csv não encontrado");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");

                String name = parts[0].trim();
                String type = parts[1].trim();
                String[] noteIds = parts[2].split("\\|");

                if (noteIds.length < 3) {
                    throw new IllegalStateException(
                            "Chord deve ter no mínimo 3 notes: " + name
                    );
                }

                Long chordId = insertChord(conn, name, type);
                insertChordNotes(conn, chordId, noteIds);
            }
        }
    }

    private Long insertChord(Connection conn, String name, String type) throws SQLException {
        String sql = """
            INSERT INTO tb_chord (name, type)
            VALUES (?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, type);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new SQLException("Falha ao gerar ID do chord");
        }
    }

    private void insertChordNotes(
            Connection conn,
            Long chordId,
            String[] noteIds) throws SQLException {

        String sql = """
            INSERT INTO tb_chord_note (note_id, chord_id)
            VALUES (?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String noteId : noteIds) {
                ps.setLong(1, Long.parseLong(noteId.trim()));
                ps.setLong(2, chordId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public MigrationType getType() {
        return super.getType();
    }
}
