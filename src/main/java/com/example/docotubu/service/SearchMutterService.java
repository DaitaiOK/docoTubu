package com.example.docotubu.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.docotubu.dao.MuttersDAO;
import com.example.docotubu.model.Mutter;

import lombok.RequiredArgsConstructor;

/**
 * つぶやき検索に関する業務ロジック
 */
@Service
@RequiredArgsConstructor
public class SearchMutterService {

    private final MuttersDAO muttersDAO;

    /**
     * 本文にキーワードを含むつぶやきを検索する（キーワード未指定時は全件）
     * @return 検索結果（新しい順）。DBエラー時はnull
     */
    public List<Mutter> execute(String keyword) {
        String target = (keyword == null) ? "" : keyword.strip();
        System.out.println("[SearchMutterService] つぶやきを検索します: " + target);
        return muttersDAO.search(target);
    }
}
