CREATE TABLE IF NOT EXISTS tb_music (
    id UUID PRIMARY KEY,
    created_by_id UUID,
    title VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    release_date DATE NOT NULL DEFAULT now(),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),

    FOREIGN KEY (created_by_id) REFERENCES tb_user(id) ON DELETE SET NULL
);