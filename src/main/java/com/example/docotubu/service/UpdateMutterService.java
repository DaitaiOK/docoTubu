package com.example.docotubu.service;

import org.springframework.stereotype.Service;

import com.example.docotubu.dao.MuttersDAO;
import com.example.docotubu.model.Mutter;

import lombok.RequiredArgsConstructor;

/**
 * つぶやき編集に関する業務ロジック
 * ※編集できるのは投稿者本人のつぶやきだけ
 */
@Service
@RequiredArgsConstructor
public class UpdateMutterService {

    private final MuttersDAO muttersDAO;

    /**
     * 編集対象のつぶやきを取得する
     * @return ログインユーザー本人のつぶやきならそのつぶやき。IDが不正・該当なし・本人以外の場合はnull
     */
    public Mutter findOwnMutter(String idStr, int userId) {
        Integer id = parseId(idStr);
        if (id == null) {
            return null;
        }
        Mutter mutter = muttersDAO.findById(id);
        if (mutter == null || mutter.getUserId() != userId) {
            return null;
        }
        return mutter;
    }

    /**
     * つぶやきの本文を更新する
     * @return 更新できた場合はtrue（IDが不正・本人以外・DBエラー時はfalse）
     */
    public boolean execute(String idStr, int userId, String text) {
        Integer id = parseId(idStr);
        if (id == null) {
            return false;
        }
        System.out.println("[UpdateMutterService] つぶやきを更新します: id=" + id);
        return muttersDAO.update(id, userId, text);
    }

    private Integer parseId(String idStr) {
        try {
            return Integer.valueOf(idStr);
        } catch (NumberFormatException e) {
            System.out.println("[UpdateMutterService] つぶやきのIDが不正です: " + idStr);
            return null;
        }
    }
}
