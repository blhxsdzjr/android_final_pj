package com.example.final_pj.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface CourseDao {
    @Insert
    void insert(Course course);

    @Update
    void update(Course course);

    @Delete
    void delete(Course course);

    @Query("SELECT * FROM courses ORDER BY dayOfWeek, startPeriod")
    List<Course> getAllCourses();

    @Query("SELECT * FROM courses WHERE dayOfWeek = :day ORDER BY startPeriod")
    List<Course> getCoursesByDay(int day);
}