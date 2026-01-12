CREATE TABLE tb_user (
                         id UUID PRIMARY KEY,
                         username VARCHAR(40) NOT NULL UNIQUE,
                         email VARCHAR(254) NOT NULL UNIQUE,
                         password VARCHAR(72) NOT NULL,
                         created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                         updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
