package com.example.docotubu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ユーザー情報を表すエンティティ（USERSテーブルに対応）
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // 全フィールド(id, name, pass)のコンストラクタ ※DB検索結果の格納用
public class User {
    private int id;
    private String name;
    private String pass;

    /**
     * ログイン・ユーザー登録用のコンストラクタ（idはDB側で自動採番されるため不要）
     */
    public User(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }
}
