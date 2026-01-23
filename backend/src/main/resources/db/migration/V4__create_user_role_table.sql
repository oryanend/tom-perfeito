CREATE TABLE IF NOT EXISTS tb_user_role (
                              user_id UUID NOT NULL,
                              role_id BIGINT NOT NULL,

                              CONSTRAINT pk_user_role PRIMARY KEY (user_id, role_id),

                              CONSTRAINT fk_user_role_user
                                  FOREIGN KEY (user_id)
                                      REFERENCES tb_user (id)
                                      ON DELETE CASCADE,

                              CONSTRAINT fk_user_role_role
                                  FOREIGN KEY (role_id)
                                      REFERENCES tb_role (id)
                                      ON DELETE CASCADE
);
