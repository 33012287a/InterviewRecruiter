package com.learn.interviewrecruiter;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.function.Consumer;

public class OpenAiClient {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("OPENROUTER_API_KEY");
    private static final String ENDPOINT = "https://openrouter.ai/api/v1/chat/completions";
    private static final OkHttpClient client = new OkHttpClient();
    private String quustion;
    private String goldAnswer;
    private String userAnswer;

    public void getConnection(String question, String goldAnswer, String userAnswer, Consumer<String> callback) {
        this.quustion = question;
        this.goldAnswer = goldAnswer;
        this.userAnswer = userAnswer;


        JSONObject json = new JSONObject();
        json.put("model", "openai/gpt-4o-mini");

        JSONArray messages = new JSONArray();

        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", "Ты - рекрутер по Java. Твоя задача: задавать вопросы, принимать ответы кандидата и сравнивать их с эталонным ответом. После этого давай объективный фидбэк: что хорошо, что нужно улучшить. Будь кратким и конструктивным."));

        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", String.format("Вопрос: %s\n\nЭталонный ответ: %s\n\nОтвет кандидата: %s",question,goldAnswer,userAnswer)));

        json.put("messages", messages);

        Request request = new Request.Builder()
                .url(ENDPOINT)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                callback.accept("Ошибка подключения: " + e.getMessage());
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    String feedback = extractContent(responseBody);
                    callback.accept(feedback);
                } else {
                    callback.accept("Ошибка " + response.code() + " - " + response.message());
                }
            }
        });
    }

    private static String extractContent(String responseBody) {
        JSONObject obj = new JSONObject(responseBody);
        JSONArray choices = obj.getJSONArray("choices");
        JSONObject firstChoice = choices.getJSONObject(0);
        JSONObject message = firstChoice.getJSONObject("message");
        String content = message.getString("content");

        return content;
    }


}
