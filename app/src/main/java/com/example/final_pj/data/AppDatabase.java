package com.example.final_pj.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.Executors;

@Database(entities = {Course.class, Todo.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract CourseDao courseDao();
    public abstract TodoDao todoDao();

    public static synchronized AppDatabase getInstance(android.content.Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "campus_assistant_db")
                    .fallbackToDestructiveMigration()
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // Prepopulate data
                            Executors.newSingleThreadExecutor().execute(() -> {
                                AppDatabase database = getInstance(context);
                                // Sample Courses
                                database.courseDao().insert(new Course("高等数学", "教三 101", "张老师", 1, 1, 2, 1, 16));
                                database.courseDao().insert(new Course("大学物理", "理学楼 202", "李老师", 2, 3, 4, 1, 16));
                                database.courseDao().insert(new Course("移动开发", "实验楼 505", "王老师", 3, 5, 7, 1, 16));
                                
                                // Sample Todo
                                database.todoDao().insert(new Todo("提交 Android 大作业", "2025-12-31 23:59", "2025-12-23 10:00", false));
                            });
                        }
                    })
                    .build();
        }
        return instance;
    }
}
