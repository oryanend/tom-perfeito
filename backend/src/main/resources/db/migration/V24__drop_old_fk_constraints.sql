ALTER TABLE tb_comment_likes
    DROP CONSTRAINT IF EXISTS tb_comment_likes_comment_id_fkey;

ALTER TABLE tb_comment_likes
    DROP CONSTRAINT IF EXISTS tb_comment_likes_user_id_fkey;

ALTER TABLE tb_comment_likes
    DROP CONSTRAINT IF EXISTS fk_comment_likes_comment;

ALTER TABLE tb_comment_likes
    DROP CONSTRAINT IF EXISTS fk_comment_likes_user;