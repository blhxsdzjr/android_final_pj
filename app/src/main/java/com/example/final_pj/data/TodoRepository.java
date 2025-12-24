package com.example.final_pj.data;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TodoRepository {
    private TodoDao todoDao;
    private ExecutorService executorService;
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public TodoRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        todoDao = database.todoDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public interface TodoLoadCallback {
        void onTodosLoaded(List<Todo> todos);
    }
    
    public interface TodoInsertCallback {
        void onTodoInserted(long id);
    }

    public void getAllTodos(TodoLoadCallback callback) {
        executorService.execute(() -> {
            List<Todo> todos = todoDao.getAllTodos();
            mainThreadHandler.post(() -> callback.onTodosLoaded(todos));
        });
    }

    public void insert(Todo todo, TodoInsertCallback callback) {
        executorService.execute(() -> {
            long id = todoDao.insert(todo);
            mainThreadHandler.post(() -> callback.onTodoInserted(id));
        });
    }

    public void update(Todo todo) {
        executorService.execute(() -> todoDao.update(todo));
    }

    public void delete(Todo todo) {
        executorService.execute(() -> todoDao.delete(todo));
    }
}
