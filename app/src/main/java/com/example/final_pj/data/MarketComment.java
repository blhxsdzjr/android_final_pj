package com.example.final_pj.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "market_comments",
        foreignKeys = @ForeignKey(entity = MarketPost.class,
                                  parentColumns = "id",
                                  childColumns = "postId",
                                  onDelete = CASCADE),
        indices = {@Index("postId")})
public class MarketComment {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int postId;
    private int parentId; // 0 for root comment, otherwise id of the comment being replied to
    private String username;
    private String content;
    private String commentTime;

    public MarketComment(int postId, int parentId, String username, String content, String commentTime) {
        this.postId = postId;
        this.parentId = parentId;
        this.username = username;
        this.content = content;
        this.commentTime = commentTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
    public int getParentId() { return parentId; }
    public void setParentId(int parentId) { this.parentId = parentId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCommentTime() { return commentTime; }
    public void setCommentTime(String commentTime) { this.commentTime = commentTime; }
}
