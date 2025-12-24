package com.example.final_pj.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "todos")
public class Todo {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String content;
    private String deadlineTime; // Format: YYYY-MM-DD HH:mm
    private String createTime;
    private boolean isDone;      // Mapped from "status" for easier UI binding

    public Todo(String content, String deadlineTime, String createTime, boolean isDone) {
        this.content = content;
        this.deadlineTime = deadlineTime;
        this.createTime = createTime;
        this.isDone = isDone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getDeadlineTime() { return deadlineTime; }
    public void setDeadlineTime(String deadlineTime) { this.deadlineTime = deadlineTime; }
    
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    
    public boolean isDone() { return isDone; }
    public void setDone(boolean done) { isDone = done; }
}