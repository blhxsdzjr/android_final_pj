package com.example.final_pj.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_pj.R;
import com.example.final_pj.data.ChatMessage;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DiscoveryFragment extends Fragment {

    private RecyclerView rvChat;
    private EditText etMessage;
    private MaterialButton btnSend;
    private ChatAdapter chatAdapter;
    private OkHttpClient client;
    private Gson gson = new Gson();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 使用最基础的 Builder，只保留超时设置
        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);

        rvChat = view.findViewById(R.id.rv_chat);
        etMessage = view.findViewById(R.id.et_message);
        btnSend = view.findViewById(R.id.btn_send);

        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter();
        rvChat.setAdapter(chatAdapter);

        chatAdapter.addMessage(new ChatMessage("您好！我是您的校园学习助手。请在设置中配置好 Key、URL 和模型名后开始提问。", ChatMessage.TYPE_AI));

        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {
        try {
            String content = etMessage.getText().toString().trim();
            if (content.isEmpty()) return;

            if (chatAdapter != null) {
                chatAdapter.addMessage(new ChatMessage(content, ChatMessage.TYPE_USER));
                etMessage.setText("");
                rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }

            callAiApi(content);
        } catch (Exception e) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "发送异常", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void callAiApi(String userMessage) {
        if (!isAdded() || getContext() == null) return;
        
        SharedPreferences prefs = requireContext().getSharedPreferences("ai_config", Context.MODE_PRIVATE);
        String apiKey = prefs.getString("api_key", "");
        String apiUrl = prefs.getString("api_url", ""); 
        String modelName = prefs.getString("model_name", "");

        if (apiKey.isEmpty() || apiUrl.isEmpty() || modelName.isEmpty()) {
            showError("请先在“个人中心”完成 AI 配置");
            return;
        }

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", modelName); 
        
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMsgMap = new HashMap<>();
        userMsgMap.put("role", "user");
        userMsgMap.put("content", userMessage);
        messages.add(userMsgMap);
        requestMap.put("messages", messages);

        String json = gson.toJson(requestMap);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        
        Request request;
        try {
            request = new Request.Builder()
                    .url(apiUrl.trim())
                    .header("Authorization", "Bearer " + apiKey)
                    .post(body)
                    .build();
        } catch (Exception e) {
            showError("URL 格式错误");
            return;
        }

        final String finalApiUrl = apiUrl;
        final String finalModelName = modelName;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showError("网络异常: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (Response res = response) {
                    String responseBody = res.body() != null ? res.body().string() : "";
                    if (res.isSuccessful()) {
                        handleSuccess(responseBody);
                    } else {
                        String errorMsg = "错误 " + res.code();
                        try {
                            JsonObject errJson = gson.fromJson(responseBody, JsonObject.class);
                            if (errJson.has("error")) {
                                JsonObject errorObj = errJson.getAsJsonObject("error");
                                if (errorObj.has("message")) {
                                    errorMsg += ": " + errorObj.get("message").getAsString();
                                }
                            }
                        } catch (Exception ignored) {}
                        errorMsg += "\n[调试] URL: " + finalApiUrl + "\nModel: " + finalModelName;
                        showError(errorMsg);
                    }
                }
            }
        });
    }

    private void handleSuccess(String responseData) {
        if (!isAdded() || getActivity() == null) return;
        try {
            JsonObject jsonResponse = gson.fromJson(responseData, JsonObject.class);
            String aiReply = "无回复";
            if (jsonResponse.has("choices")) {
                aiReply = jsonResponse.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();
            }

            final String finalReply = aiReply;
            getActivity().runOnUiThread(() -> {
                if (isAdded() && chatAdapter != null) {
                    chatAdapter.addMessage(new ChatMessage(finalReply, ChatMessage.TYPE_AI));
                    rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                }
            });
        } catch (Exception e) {
            showError("解析失败");
        }
    }

    private void showError(String msg) {
        if (isAdded() && getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (chatAdapter != null) {
                    chatAdapter.addMessage(new ChatMessage("错误: " + msg, ChatMessage.TYPE_AI));
                    rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                }
            });
        }
    }
}
