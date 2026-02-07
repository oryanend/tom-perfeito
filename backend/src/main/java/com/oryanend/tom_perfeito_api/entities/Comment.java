package com.oryanend.tom_perfeito_api.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 280)
    private String body;

    private Long likes = 0L;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false, updatable = false, name = "updated_at")
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "music_id", nullable = false)
    private Music music;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> replies = new ArrayList<>();

    public Comment() {
    }

    public Comment(String body) {
        this.body = body;
    }

    public Comment(String body, Comment parent) {
        this.body = body;
        this.parent = parent;
    }

    public Long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void addReply(Comment reply) {
        replies.add(reply);
        reply.setParent(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id) && Objects.equals(body, comment.body) && Objects.equals(likes, comment.likes) && Objects.equals(createdAt, comment.createdAt) && Objects.equals(updatedAt, comment.updatedAt) && Objects.equals(author, comment.author) && Objects.equals(music, comment.music);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
