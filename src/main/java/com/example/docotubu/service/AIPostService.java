package com.example.docotubu.service;

import java.util.ArrayList;
import java.util.List;
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
        "これからユーザーのつぶやきが送られてくるので、それに対して1〜2文で、元気よく、少し暑苦しいぐらいのテンションで返信・ツッコミを入れてください。" +
        "これまでのやり取りがある場合は、その流れを踏まえて、最後のつぶやきに返信してください。" +
        "返信は必ず100文字以内にしてください。";

    // 会話の履歴として渡す最大件数（ユーザーのつぶやきとAI太郎の返信の合計）
    private static final int HISTORY_LIMIT = 10;
    // APIキー未設定・通信エラー時の返信の先頭文字（会話の履歴には含めない）
    private static final String ERROR_REPLY_PREFIX = "※";

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

        List<GeminiApiClient.Message> history = buildHistory(userMutter);
        String aiReplyText = truncate(geminiApiClient.generateResponse(SYSTEM_PROMPT, history));

        // どのつぶやきへの返信かを記録する（次回以降、会話の履歴として使う）
        Mutter aiMutter = new Mutter(aiUserId, aiReplyText);
        if (userMutter.getId() > 0) {
            aiMutter.setReplyToId(userMutter.getId());
        }
        muttersDAO.create(aiMutter);

        System.out.println("AI太郎がつぶやきました！");
    }

    /**
     * 投稿者とAI太郎のこれまでのやり取りを、Gemini APIに渡す会話の形に組み立てる
     * ※最後は必ず今回のつぶやきにする（直前の返信の生成が遅れて、今回のつぶやきより後に保存されている場合があるため）
     */
    private List<GeminiApiClient.Message> buildHistory(Mutter userMutter) {
        List<GeminiApiClient.Message> history = new ArrayList<>();
        List<Mutter> conversation = muttersDAO.findConversation(userMutter.getUserId(), HISTORY_LIMIT);
        if (conversation != null) {
            for (Mutter mutter : conversation) {
                if (mutter.getId() == userMutter.getId()) {
                    continue;
                }
                boolean isAi = mutter.getUserId() == aiUserId;
                if (isAi && mutter.getText().startsWith(ERROR_REPLY_PREFIX)) {
                    continue;
                }
                addMessage(history, isAi ? "model" : "user", mutter.getText());
            }
        }
        addMessage(history, "user", userMutter.getText());

        // 会話はユーザーの発言から始める
        while (!history.isEmpty() && history.get(0).role().equals("model")) {
            history.remove(0);
        }
        System.out.println("[AIPostService] 会話の履歴 " + history.size() + " 件を踏まえて返信します");
        return history;
    }

    /**
     * 返信がDBに保存できる文字数を超えた場合は、末尾を「…」にして切り詰める
     * ※プロンプトで短くするよう指示しているが、守られなかった場合に返信が保存されず消えてしまうのを防ぐ
     */
    private String truncate(String text) {
        if (text.length() <= Mutter.MAX_TEXT_LENGTH) {
            return text;
        }
        int end = Mutter.MAX_TEXT_LENGTH - 1;
        // 絵文字など2つのcharで1文字になる文字を、途中で切らない
        if (Character.isHighSurrogate(text.charAt(end - 1))) {
            end--;
        }
        System.out.println("[AIPostService] 返信が" + text.length() + "文字と長いため、切り詰めて保存します");
        return text.substring(0, end) + "…";
    }

    /**
     * 会話に発言を追加する（AIの返信が無かった場合など、同じ話者の発言が続くときは1つにまとめる）
     */
    private void addMessage(List<GeminiApiClient.Message> history, String role, String text) {
        if (!history.isEmpty()) {
            GeminiApiClient.Message last = history.get(history.size() - 1);
            if (last.role().equals(role)) {
                history.set(history.size() - 1, new GeminiApiClient.Message(role, last.text() + "\n" + text));
                return;
            }
        }
        history.add(new GeminiApiClient.Message(role, text));
    }
}
