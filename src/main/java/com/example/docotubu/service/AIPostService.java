package com.example.docotubu.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.example.docotubu.dao.MuttersDAO;
import com.example.docotubu.dao.UsersDAO;
import com.example.docotubu.model.Mutter;
import com.example.docotubu.model.User;
import jakarta.annotation.PostConstruct;
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
    private final UsersDAO usersDAO;

    // AI太郎のユーザーID（つぶやきの投稿者として使う）。取得できなかった場合は0
    private int aiUserId;

    /**
     * AI太郎をログイン不可のユーザーとしてUSERSテーブルに用意し、そのIDを保持する
     */
    @PostConstruct
    public void init() {
        usersDAO.registerSystemUserIfNotExists(AI_NAME);
        User aiUser = usersDAO.findByName(AI_NAME);
        if (aiUser != null) {
            aiUserId = aiUser.getId();
        } else {
            System.out.println("[AIPostService] AI太郎のユーザーを取得できなかったため、AI返信は行いません");
        }
    }

    @Async
    public void execute(Mutter userMutter) {
        if (aiUserId == 0 || userMutter.getUserId() == aiUserId) {
            return;
        }

        System.out.println("AI太郎が考え中...");

        String aiReplyText = geminiApiClient.generateResponse(SYSTEM_PROMPT, userMutter.getText());
        Mutter aiMutter = new Mutter(aiUserId, aiReplyText);

        muttersDAO.create(aiMutter);

        System.out.println("AI太郎がつぶやきました！");
    }
}
