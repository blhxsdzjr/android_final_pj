package com.example.final_pj.data;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CourseRepository {
    private CourseDao courseDao;
    private ExecutorService executorService;
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public CourseRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        courseDao = database.courseDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public interface CourseLoadCallback {
        void onCoursesLoaded(List<Course> courses);
    }

    public void getAllCourses(CourseLoadCallback callback) {
        executorService.execute(() -> {
            List<Course> courses = courseDao.getAllCourses();
            mainThreadHandler.post(() -> callback.onCoursesLoaded(courses));
        });
    }

    public void insert(Course course) {
        executorService.execute(() -> courseDao.insert(course));
    }
    
    public void delete(Course course) {
        executorService.execute(() -> courseDao.delete(course));
    }
}
