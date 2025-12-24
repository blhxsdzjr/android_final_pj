package com.example.final_pj.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TodoDao {
    @Insert
    long insert(Todo todo);

    @Update
    void update(Todo todo);

    @Delete
    void delete(Todo todo);

    @Query("SELECT * FROM todos ORDER BY isDone ASC, deadlineTime ASC")
    List<Todo> getAllTodos();

    @Query("SELECT * FROM todos WHERE isDone = 0 ORDER BY deadlineTime ASC")
    List<Todo> getUnfinishedTodos();
}