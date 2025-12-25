package com.example.final_pj.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_pj.R;
import com.example.final_pj.data.AppDatabase;
import com.example.final_pj.data.Course;
import com.example.final_pj.data.Todo;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import android.content.Intent;
import com.example.final_pj.MarketActivity;

public class HomeFragment extends Fragment {

    private RecyclerView rvCourses, rvTodos;
    private TextView tvDate, tvNoCourses, tvNoTodos;
    private View btnOpenMarket;
    private CourseAdapter courseAdapter;
    private TodoAdapter todoAdapter;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Views
        rvCourses = view.findViewById(R.id.rv_today_courses);
        rvTodos = view.findViewById(R.id.rv_home_todos);
        tvDate = view.findViewById(R.id.tv_home_date);
        tvNoCourses = view.findViewById(R.id.tv_no_courses);
        tvNoTodos = view.findViewById(R.id.tv_no_todos);
        btnOpenMarket = view.findViewById(R.id.card_open_market);

        // Initialize DB
        db = AppDatabase.getInstance(requireContext());

        // Setup Adapters
        setupCourses();
        setupTodos();

        // Update Date
        updateDateDisplay();
        
        // Setup Listeners
        btnOpenMarket.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), MarketActivity.class));
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.getDefault());
        tvDate.setText("今天是 " + sdf.format(new Date()));
    }

    private void setupCourses() {
        rvCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        courseAdapter = new CourseAdapter();
        rvCourses.setAdapter(courseAdapter);
    }

    private void setupTodos() {
        rvTodos.setLayoutManager(new LinearLayoutManager(getContext()));
        todoAdapter = new TodoAdapter();
        
        // Handle Todo clicks (update status or delete)
        todoAdapter.setOnTodoItemClickListener(new TodoAdapter.OnTodoItemClickListener() {
            @Override
            public void onStatusChanged(Todo todo, boolean isDone) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    todo.setDone(isDone);
                    db.todoDao().update(todo);
                    // Refresh to remove if it was just marked done (since we show unfinished)
                    loadData(); 
                });
            }

            @Override
            public void onDeleteClick(Todo todo) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    db.todoDao().delete(todo);
                    loadData();
                });
            }
        });

        rvTodos.setAdapter(todoAdapter);
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 1. Load Courses
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            // Convert Sunday(1) -> 7, Monday(2) -> 1, ...
            int appDay = (dayOfWeek == Calendar.SUNDAY) ? 7 : dayOfWeek - 1;
            
            List<Course> courses = db.courseDao().getCoursesByDay(appDay);

            // 2. Load Unfinished Todos
            List<Todo> todos = db.todoDao().getUnfinishedTodos();

            getActivity().runOnUiThread(() -> {
                // Update Courses UI
                if (courses == null || courses.isEmpty()) {
                    rvCourses.setVisibility(View.GONE);
                    tvNoCourses.setVisibility(View.VISIBLE);
                } else {
                    rvCourses.setVisibility(View.VISIBLE);
                    tvNoCourses.setVisibility(View.GONE);
                    courseAdapter.setCourses(courses);
                }

                // Update Todos UI
                if (todos == null || todos.isEmpty()) {
                    rvTodos.setVisibility(View.GONE);
                    tvNoTodos.setVisibility(View.VISIBLE);
                } else {
                    rvTodos.setVisibility(View.VISIBLE);
                    tvNoTodos.setVisibility(View.GONE);
                    todoAdapter.setTodos(todos);
                }
            });
        });
    }
}
