package com.example.final_pj.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "courses")
public class Course {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String name;        // 课程名称
    private String room;        // 教室
    private String teacher;     // 教师
    private int dayOfWeek;      // 周几 (1-7)
    private int startPeriod;    // 开始节次
    private int endPeriod;      // 结束节次
    
    // New fields for week range
    private int startWeek;      // 起始周 (e.g. 1)
    private int endWeek;        // 结束周 (e.g. 16)

    // Updated Constructor
    public Course(String name, String room, String teacher, int dayOfWeek, int startPeriod, int endPeriod, int startWeek, int endWeek) {
        this.name = name;
        this.room = room;
        this.teacher = teacher;
        this.dayOfWeek = dayOfWeek;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }
    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public int getStartPeriod() { return startPeriod; }
    public void setStartPeriod(int startPeriod) { this.startPeriod = startPeriod; }
    public int getEndPeriod() { return endPeriod; }
    public void setEndPeriod(int endPeriod) { this.endPeriod = endPeriod; }
    public int getStartWeek() { return startWeek; }
    public void setStartWeek(int startWeek) { this.startWeek = startWeek; }
    public int getEndWeek() { return endWeek; }
    public void setEndWeek(int endWeek) { this.endWeek = endWeek; }
}