package com.example.docotubu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.stereotype.Repository;

import com.example.docotubu.model.User;

import jakarta.annotation.PostConstruct;

/**
 * USERSテーブルへのアクセスを担当するDAO
 */
@Repository
public class UsersDAO {

    /**
     * ログインできないユーザー（AI太郎など）のPASSに設定する値
     * ※BCryptのハッシュ形式ではないため、どんなパスワードを入力しても照合に失敗する
     */
    public static final String UNUSABLE_PASS = "!";

    /**
     * アプリ起動時にUSERSテーブルが存在しなければ作成する
     * ※PASSにはBCryptでハッシュ化した値（60文字）を格納する
     */
    @PostConstruct
    public void createTableIfNotExists() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS USERS ("
                    + "ID INT PRIMARY KEY AUTO_INCREMENT, "
                    + "NAME VARCHAR(100) NOT NULL UNIQUE, "
                    + "PASS VARCHAR(255) NOT NULL)";
            stmt.executeUpdate(sql);
            System.out.println("[UsersDAO] USERSテーブルの準備が完了しました");
        } catch (SQLException e) {
            System.out.println("[UsersDAO] USERSテーブルの作成に失敗しました");
            e.printStackTrace();
        }
    }

    /**
     * ユーザーを登録する（passはハッシュ化済みの値を渡すこと）
     * @return 登録成功時true、失敗時（ユーザー名重複・DBエラー等）false
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO USERS(NAME, PASS) VALUES(?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pStmt = conn.prepareStatement(sql)) {
            pStmt.setString(1, user.getName());
            pStmt.setString(2, user.getPass());
            int result = pStmt.executeUpdate();
            if (result != 1) {
                System.out.println("[UsersDAO] 登録件数が1件ではありません: " + result);
                return false;
            }
        } catch (SQLException e) {
            // 23505 = 一意制約違反（NAMEの重複）
            if ("23505".equals(e.getSQLState())) {
                System.out.println("[UsersDAO] ユーザー名が重複しています: " + user.getName());
            } else {
                System.out.println("[UsersDAO] ユーザー登録中にDBエラーが発生しました");
                e.printStackTrace();
            }
            return false;
        }
        System.out.println("[UsersDAO] ユーザーを登録しました: " + user.getName());
        return true;
    }

    /**
     * ログインできないシステム用ユーザー（AI太郎など）を、存在しなければ登録する
     * ※つぶやきはuser_idでUSERSテーブルを参照するため、AIの投稿にもユーザーが必要
     */
    public void registerSystemUserIfNotExists(String name) {
        String sql = "MERGE INTO USERS(NAME, PASS) KEY(NAME) VALUES(?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pStmt = conn.prepareStatement(sql)) {
            pStmt.setString(1, name);
            pStmt.setString(2, UNUSABLE_PASS);
            pStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[UsersDAO] システム用ユーザーの登録中にDBエラーが発生しました: " + name);
            e.printStackTrace();
        }
    }

    /**
     * NAMEに一致するユーザーを検索する
     * ※パスワードはハッシュ化されているためSQLでは比較できない。照合はService側で行う
     * @return 一致するユーザー（PASSはハッシュ値）。見つからない・DBエラー時はnull
     */
    public User findByName(String name) {
        String sql = "SELECT ID, NAME, PASS FROM USERS WHERE NAME = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pStmt = conn.prepareStatement(sql)) {
            pStmt.setString(1, name);
            try (ResultSet rs = pStmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[UsersDAO] ユーザーが見つかりました: " + name);
                    return new User(rs.getInt("ID"), rs.getString("NAME"), rs.getString("PASS"));
                }
            }
        } catch (SQLException e) {
            System.out.println("[UsersDAO] ユーザー検索中にDBエラーが発生しました");
            e.printStackTrace();
            return null;
        }
        System.out.println("[UsersDAO] 一致するユーザーはいません: " + name);
        return null;
    }
}
