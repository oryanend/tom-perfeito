DELETE FROM tb_lyric_chord;
DELETE FROM tb_chord_note;
DELETE FROM tb_chord;

ALTER TABLE tb_chord ALTER COLUMN id RESTART WITH 1;