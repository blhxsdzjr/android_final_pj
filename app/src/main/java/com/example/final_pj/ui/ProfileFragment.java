package com.example.final_pj.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.example.final_pj.R;
import com.example.final_pj.data.AppDatabase;
import com.example.final_pj.data.Course;
import com.example.final_pj.data.Todo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private MaterialButton btnExport, btnImport, btnSaveConfig, btnLogout;
    private TextInputEditText etApiKey, etApiUrl, etModelName;
    private TextView tvUsername;
    private Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnExport = view.findViewById(R.id.btn_export_data);
        btnImport = view.findViewById(R.id.btn_import_data);
        btnSaveConfig = view.findViewById(R.id.btn_save_ai_config);
        btnLogout = view.findViewById(R.id.btn_logout);
        etApiKey = view.findViewById(R.id.et_api_key);
        etApiUrl = view.findViewById(R.id.et_api_url);
        etModelName = view.findViewById(R.id.et_model_name);
        tvUsername = view.findViewById(R.id.tv_profile_username);

        loadAiConfig();
        displayUserInfo();

        btnExport.setOnClickListener(v -> exportData());
        btnImport.setOnClickListener(v -> importDataLauncher.launch(new String[]{"application/json"}));
        btnSaveConfig.setOnClickListener(v -> saveAiConfig());
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void displayUserInfo() {
        if (getContext() == null) return;
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "校园助手用户");
        if (tvUsername != null) tvUsername.setText(username);
    }

    private void logout() {
        if (getContext() == null) return;
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("isLoggedIn", false).apply();

        Intent intent = new Intent(requireContext(), com.example.final_pj.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void loadAiConfig() {
        if (getContext() == null) return;
        SharedPreferences prefs = requireContext().getSharedPreferences("ai_config", Context.MODE_PRIVATE);
        String key = prefs.getString("api_key", "");
        String url = prefs.getString("api_url", ""); 
        String model = prefs.getString("model_name", "");
        
        if (etApiKey != null) etApiKey.setText(key);
        if (etApiUrl != null) etApiUrl.setText(url);
        if (etModelName != null) etModelName.setText(model);
    }

    private void saveAiConfig() {
        if (etApiKey == null || etApiUrl == null || etModelName == null) return;
        String key = etApiKey.getText().toString().trim();
        String url = etApiUrl.getText().toString().trim();
        String model = etModelName.getText().toString().trim();

        if (key.isEmpty()) {
            Toast.makeText(getContext(), "API Key 不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (model.isEmpty()) {
            Toast.makeText(getContext(), "模型名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 简单的 URL 校验与自动纠错
        if (!url.isEmpty() && !url.startsWith("http")) {
            url = "https://" + url;
            etApiUrl.setText(url);
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("ai_config", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("api_key", key)
                .putString("api_url", url)
                .putString("model_name", model)
                .apply();
        Toast.makeText(getContext(), "配置已保存", Toast.LENGTH_SHORT).show();
    }

    private void exportData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            List<Course> courses = db.courseDao().getAllCourses();
            List<Todo> todos = db.todoDao().getAllTodos();

            // 构建备份对象
            JsonObject root = new JsonObject();
            root.add("courses", gson.toJsonTree(courses));
            root.add("todos", gson.toJsonTree(todos));

            String jsonString = gson.toJson(root);

            try {
                // 写入临时文件
                File cachePath = new File(requireContext().getCacheDir(), "backups");
                cachePath.mkdirs();
                File tempFile = new File(cachePath, "campus_assistant_backup.json");
                FileOutputStream stream = new FileOutputStream(tempFile);
                stream.write(jsonString.getBytes());
                stream.close();

                // 获取共享 URI
                Uri contentUri = FileProvider.getUriForFile(requireContext(), 
                        requireContext().getPackageName() + ".fileprovider", tempFile);

                if (contentUri != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); 
                    shareIntent.setDataAndType(contentUri, "application/json");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    startActivity(Intent.createChooser(shareIntent, "备份数据到..."));
                }
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), "导出失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private final ActivityResultLauncher<String[]> importDataLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    readAndImportFile(uri);
                }
            }
    );

    private void readAndImportFile(Uri uri) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();

                // 解析 JSON
                JsonObject root = gson.fromJson(stringBuilder.toString(), JsonObject.class);
                
                Type courseListType = new TypeToken<List<Course>>(){}.getType();
                Type todoListType = new TypeToken<List<Todo>>(){}.getType();
                
                List<Course> importedCourses = gson.fromJson(root.get("courses"), courseListType);
                List<Todo> importedTodos = gson.fromJson(root.get("todos"), todoListType);

                // 批量插入数据库
                AppDatabase db = AppDatabase.getInstance(requireContext());
                if (importedCourses != null) {
                    for (Course c : importedCourses) {
                        c.setId(0); // 确保主键由数据库生成，避免冲突
                        db.courseDao().insert(c);
                    }
                }
                if (importedTodos != null) {
                    for (Todo t : importedTodos) {
                        t.setId(0);
                        db.todoDao().insert(t);
                    }
                }

                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), "恢复成功", Toast.LENGTH_SHORT).show());

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), "恢复失败: 格式错误", Toast.LENGTH_SHORT).show());
            }
        });
    }
}