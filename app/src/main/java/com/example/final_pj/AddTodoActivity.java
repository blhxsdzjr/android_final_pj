package com.example.final_pj;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.final_pj.data.AppDatabase;
import com.example.final_pj.data.Todo;
import com.google.android.material.textfield.TextInputEditText;
import com.example.final_pj.data.TodoRepository; // 导入
import java.util.Calendar; // 导入 Calendar 类
import java.text.SimpleDateFormat; // 导入 SimpleDateFormat 类
import java.util.Locale; // 导入 Locale 类
import android.app.AlarmManager; // 导入 AlarmManager 类
import android.app.PendingIntent; // 导入 PendingIntent 类
import android.content.Context; // 导入 Context 类
import android.content.Intent; // 导入 Intent 类

public class AddTodoActivity extends AppCompatActivity {

    private TextInputEditText etContent;
    private TextView tvDateTime;
    private Button btnDate, btnTime, btnSave;
    private Calendar calendar = Calendar.getInstance();
    private TodoRepository todoRepository; // 新增

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("AddTodoActivity", "Starting onCreate");
        try {
            setContentView(R.layout.activity_add_todo);
            android.util.Log.d("AddTodoActivity", "Layout set");
            
            todoRepository = new TodoRepository(getApplication());
            android.util.Log.d("AddTodoActivity", "Repository initialized");

            etContent = findViewById(R.id.et_todo_content);
            tvDateTime = findViewById(R.id.tv_selected_datetime);
            btnDate = findViewById(R.id.btn_pick_date);
            btnTime = findViewById(R.id.btn_pick_time);
            btnSave = findViewById(R.id.btn_save_todo);

            updateDateTimeLabel();
            android.util.Log.d("AddTodoActivity", "UI bound and initialized");

            btnDate.setOnClickListener(v -> {
                new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTimeLabel();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            });

            btnTime.setOnClickListener(v -> {
                new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    updateDateTimeLabel();
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            });

            btnSave.setOnClickListener(v -> {
                saveTodo();
            });
        } catch (Exception e) {
            android.util.Log.e("AddTodoActivity", "Error in onCreate", e);
            Toast.makeText(this, "初始化失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateDateTimeLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        tvDateTime.setText("截止时间: " + sdf.format(calendar.getTime()));
    }

    private void saveTodo() {
        String content = etContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "请输入待办内容", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String deadline = sdf.format(calendar.getTime());
        String createTime = sdf.format(System.currentTimeMillis());

        Todo todo = new Todo(content, deadline, createTime, false);
        
        todoRepository.insert(todo, id -> {
            // This callback runs on the main thread
            setAlarm((int) id, content, calendar.getTimeInMillis());
            
            Toast.makeText(this, "保存成功并已设置提醒", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void setAlarm(int id, String content, long timeInMillis) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("todo_content", content);
        intent.putExtra("todo_id", id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_IMMUTABLE);

        if (timeInMillis > System.currentTimeMillis()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                } else {
                    try {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                    } catch (SecurityException e) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                    }
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
        }
    }
}