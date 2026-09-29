package com.example.docotubu.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.example.docotubu.dao.MuttersDAO;
import com.example.docotubu.model.Mutter;
import org.springframework.scheduling.annotation.Async;

@Service
@RequiredArgsConstructor
public class AIPostService {
    // AIの投稿者名（ユーザー登録時の予約名チェックでも参照する）
    public static final String AI_NAME = "AI太郎";
    private final String SYSTEM_PROMPT = 
        "あなたは「AI太郎」という名前の元気で熱血漢なAIです。" +
        "これからユーザーのつぶやきが送られてくるので、それに対して1〜2文で、元気よく、少し暑苦しいぐらいのテンションで返信・ツッコミを入れてください。";

    private final GeminiApiClient geminiApiClient;
    private final MuttersDAO muttersDAO;

    @Async
    public void execute(Mutter userMutter) {
        if (AI_NAME.equals(userMutter.getUserName())) {
            return;
        }

        System.out.println("AI太郎が考え中...");
        
        String aiReplyText = geminiApiClient.generateResponse(SYSTEM_PROMPT, userMutter.getText());
        Mutter aiMutter = new Mutter(AI_NAME, aiReplyText);
        
        muttersDAO.create(aiMutter);
        
        System.out.println("AI太郎がつぶやきました！");
    }
}
