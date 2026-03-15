CREATE INDEX idx_note_name_accidental
    ON tb_note (name, accidental);

CREATE INDEX idx_note_name
    ON tb_note (name);