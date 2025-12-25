package com.example.final_pj;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.final_pj.data.AppDatabase;
import com.example.final_pj.data.MarketPost;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AddPostActivity extends AppCompatActivity {

    private EditText etTitle, etContent;
    private Button btnPublish;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        db = AppDatabase.getInstance(this);

        etTitle = findViewById(R.id.et_post_title);
        etContent = findViewById(R.id.et_post_content);
        btnPublish = findViewById(R.id.btn_publish);

        btnPublish.setOnClickListener(v -> publishPost());
    }

    private void publishPost() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "标题和内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String username = prefs.getString("username", "匿名用户");
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        MarketPost post = new MarketPost(title, content, username, time);

        Executors.newSingleThreadExecutor().execute(() -> {
            db.marketDao().insertPost(post);
            runOnUiThread(() -> {
                Toast.makeText(AddPostActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
