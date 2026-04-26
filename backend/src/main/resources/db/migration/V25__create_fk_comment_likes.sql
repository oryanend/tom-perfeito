ALTER TABLE tb_comment_likes
    ADD CONSTRAINT fk_comment_likes_comment
        FOREIGN KEY (comment_id)
            REFERENCES tb_comments(id)
            ON DELETE CASCADE;

ALTER TABLE tb_comment_likes
    ADD CONSTRAINT fk_comment_likes_user
        FOREIGN KEY (user_id)
            REFERENCES tb_user(id)
            ON DELETE CASCADE;