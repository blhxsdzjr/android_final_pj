package com.example.final_pj;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.final_pj.data.AppDatabase;
import com.example.final_pj.data.Course;
import com.example.final_pj.data.CourseRepository;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddCourseActivity extends AppCompatActivity {

    private TextInputEditText etName, etRoom, etTeacher, etStart, etEnd, etStartWeek, etEndWeek;
    private Spinner spinnerDay;
    private Button btnSave;
    private CourseRepository courseRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        
        courseRepository = new CourseRepository(getApplication());

        etName = findViewById(R.id.et_course_name);
        etRoom = findViewById(R.id.et_course_room);
        etTeacher = findViewById(R.id.et_course_teacher);
        etStart = findViewById(R.id.et_start_period);
        etEnd = findViewById(R.id.et_end_period);
        etStartWeek = findViewById(R.id.et_start_week);
        etEndWeek = findViewById(R.id.et_end_week);
        spinnerDay = findViewById(R.id.spinner_day);
        btnSave = findViewById(R.id.btn_save_course);

        btnSave.setOnClickListener(v -> saveCourse());
    }

    private void saveCourse() {
        String name = etName.getText().toString().trim();
        String room = etRoom.getText().toString().trim();
        String teacher = etTeacher.getText().toString().trim();
        String startStr = etStart.getText().toString().trim();
        String endStr = etEnd.getText().toString().trim();
        String startWeekStr = etStartWeek.getText().toString().trim();
        String endWeekStr = etEndWeek.getText().toString().trim();
        int dayOfWeek = spinnerDay.getSelectedItemPosition() + 1;

        if (name.isEmpty() || startStr.isEmpty() || endStr.isEmpty() || startWeekStr.isEmpty() || endWeekStr.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int start = Integer.parseInt(startStr);
            int end = Integer.parseInt(endStr);
            int startW = Integer.parseInt(startWeekStr);
            int endW = Integer.parseInt(endWeekStr);

            Course course = new Course(name, room, teacher, dayOfWeek, start, end, startW, endW);
            
            courseRepository.insert(course);
            
            // Notify Widget
            Intent intent = new Intent(this, CourseWidgetProvider.class);
            intent.setAction(CourseWidgetProvider.ACTION_REFRESH);
            sendBroadcast(intent);

            Toast.makeText(AddCourseActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效数字", Toast.LENGTH_SHORT).show();
        }
    }
}