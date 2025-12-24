package com.example.final_pj;

import org.junit.Test;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * API 连通性测试程序
 * 修复了 JSON 字符串拼接错误
 */
public class AiApiTest {

    private static final String TEST_API_KEY = "1df61a20-1dda-4d2d-80c1-b7ccbdb60365";
    private static final String TEST_API_URL = "https://api.openai-proxy.org/v1/chat/completions"; 
    private static final String TEST_MODEL = "doubao-pro-32k";

    @Test
    public void testApiConnection() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        // 修复了引号转义
        String json = "{\"model\":\"" + TEST_MODEL + "\",\"messages\":[{\"role\":\"user\",\"content\":\"你好\"}]}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        
        Request request = new Request.Builder()
                .url(TEST_API_URL)
                .header("Authorization", "Bearer " + TEST_API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("HTTP Code: " + response.code());
            if (response.isSuccessful()) {
                System.out.println("Success!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}