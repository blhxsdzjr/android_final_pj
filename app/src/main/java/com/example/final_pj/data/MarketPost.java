package com.example.final_pj.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "market_posts")
public class MarketPost {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String content;
    private String username;
    private String postTime; // Store as String for simplicity or long
    private int likeCount;

    public MarketPost(String title, String content, String username, String postTime) {
        this.title = title;
        this.content = content;
        this.username = username;
        this.postTime = postTime;
        this.likeCount = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPostTime() { return postTime; }
    public void setPostTime(String postTime) { this.postTime = postTime; }
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
}
