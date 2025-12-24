package com.example.final_pj;

import android.os.Bundle;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_pj.data.AppDatabase;
import com.example.final_pj.data.Course;
import com.example.final_pj.data.CourseRepository;
import com.example.final_pj.ui.ManageCourseAdapter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageCoursesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ManageCourseAdapter adapter;
    private ImageView ivBack;
    private CourseRepository courseRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_courses);
        
        courseRepository = new CourseRepository(getApplication());

        ivBack = findViewById(R.id.iv_back);
        recyclerView = findViewById(R.id.rv_manage_courses);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ManageCourseAdapter();
        recyclerView.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());

        adapter.setOnDeleteClickListener(course -> showDeleteDialog(course));

        loadCourses();
    }

    private void loadCourses() {
        courseRepository.getAllCourses(courses -> adapter.setCourses(courses));
    }

    private void showDeleteDialog(Course course) {
        new AlertDialog.Builder(this)
                .setTitle("删除课程")
                .setMessage("确定要删除课程 \"" + course.getName() + "\" 吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    courseRepository.delete(course);
                    
                    // Notify Widget
                    Intent intent = new Intent(this, CourseWidgetProvider.class);
                    intent.setAction(CourseWidgetProvider.ACTION_REFRESH);
                    sendBroadcast(intent);

                    loadCourses(); // Refresh list
                    Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
