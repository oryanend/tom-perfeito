CREATE TABLE IF NOT EXISTS tb_chord_note (
                               note_id BIGINT NOT NULL,
                               chord_id BIGINT NOT NULL,

                               CONSTRAINT pk_chord_note PRIMARY KEY (chord_id, note_id),

                               CONSTRAINT fk_chord_note_chord
                                   FOREIGN KEY (chord_id)
                                       REFERENCES tb_chord (id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_chord_note_note
                                   FOREIGN KEY (note_id)
                                       REFERENCES tb_note (id)
                                       ON DELETE CASCADE
);