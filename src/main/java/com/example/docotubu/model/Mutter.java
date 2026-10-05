package com.example.docotubu.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * つぶやきを表すエンティティ（muttersテーブルに対応）
 * ※投稿者名はmuttersテーブルには持たず、USERSテーブルとJOINして取得する
 */
@Getter
@Setter
@NoArgsConstructor
public class Mutter {
    private int id;
    private int userId;
    private String userName;
    private String text;

    /**
     * 投稿登録用のコンストラクタ（idはDB側で自動採番されるため不要）
     */
    public Mutter(int userId, String text) {
        this.userId = userId;
        this.text = text;
    }

    /**
     * 画面表示用のコンストラクタ（muttersとUSERSのJOIN結果の格納用）
     */
    public Mutter(int id, String userName, String text) {
        this.id = id;
        this.userName = userName;
        this.text = text;
    }
}
