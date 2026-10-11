package com.example.docotubu.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class GeminiApiClient {
    @Value("${gemini.api.key}")
    private String API_KEY; 

    /**
     * 会話の1発言（role は "user"（ユーザー）または "model"（AI））
     */
    public record Message(String role, String text) {}

    public String generateResponse(String systemInstruction, String userMessage) {
        return generateResponse(systemInstruction, List.of(new Message("user", userMessage)));
    }

    /**
     * これまでの会話を踏まえて返信を生成する
     * @param history 古い順に並べた会話。最後はユーザーの発言にする
     */
    public String generateResponse(String systemInstruction, List<Message> history) {
        // APIキーはURLに含めず、リクエストヘッダー（x-goog-api-key）で渡す
        // ※URLはプロキシやログに記録されやすく、キーが漏れる原因になるため
        String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

        if (API_KEY == null || API_KEY.equals("ここにAPIキーを貼り付けてください") || API_KEY.isEmpty()) {
            System.err.println("APIキーが設定されていません。application.properties を確認してください。");
            return "※APIキーが未設定のため、AI太郎は沈黙しています…";
        }

        try {
            JsonObject root = new JsonObject();
            if (systemInstruction != null && !systemInstruction.isEmpty()) {
                JsonObject sysInst = new JsonObject();
                JsonObject sysParts = new JsonObject();
                sysParts.addProperty("text", systemInstruction);
                JsonArray sysPartsArray = new JsonArray();
                sysPartsArray.add(sysParts);
                sysInst.add("parts", sysPartsArray);
                root.add("system_instruction", sysInst);
            }

            JsonArray contents = new JsonArray();
            for (Message message : history) {
                JsonObject contentObj = new JsonObject();
                contentObj.addProperty("role", message.role());
                JsonArray partsArray = new JsonArray();
                JsonObject textPart = new JsonObject();
                textPart.addProperty("text", message.text());
                partsArray.add(textPart);
                contentObj.add("parts", partsArray);
                contents.add(contentObj);
            }
            root.add("contents", contents);

            String requestBody = new Gson().toJson(root);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                String aiText = jsonResponse
                        .getAsJsonArray("candidates").get(0).getAsJsonObject()
                        .getAsJsonObject("content")
                        .getAsJsonArray("parts").get(0).getAsJsonObject()
                        .get("text").getAsString();
                return aiText;
            } else {
                System.err.println("Gemini API Error: " + response.statusCode() + " - " + response.body());
                return "※通信エラー（原因コード: " + response.statusCode() + "）";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "※AI太郎は現在お昼寝中です（システムエラー）";
        }
    }
}
