package com.example.docotubu.dao;

import java.sql.Connection;
import java.sql.DriverManager;
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
    private final String JDBC_URL = "jdbc:h2:~/docoTsubu;AUTO_SERVER=TRUE";
    private final String DB_USER = "sa";
    private final String DB_PASS = "";

    /**
     * アプリ起動時にUSERSテーブルが存在しなければ作成する
     */
    @PostConstruct
    public void createTableIfNotExists() {
        try { Class.forName("org.h2.Driver"); } catch (ClassNotFoundException e) { throw new IllegalStateException(e); }
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
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
     * ユーザーを登録する
     * @return 登録成功時true、失敗時（ユーザー名重複・DBエラー等）false
     */
    public boolean registerUser(User user) {
        try { Class.forName("org.h2.Driver"); } catch (ClassNotFoundException e) { throw new IllegalStateException(e); }
        String sql = "INSERT INTO USERS(NAME, PASS) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
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
     * NAMEとPASSが一致するユーザーを検索する
     * @return 一致するユーザー（見つからない・DBエラー時はnull）
     */
    public User findUser(User user) {
        try { Class.forName("org.h2.Driver"); } catch (ClassNotFoundException e) { throw new IllegalStateException(e); }
        String sql = "SELECT ID, NAME, PASS FROM USERS WHERE NAME = ? AND PASS = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement pStmt = conn.prepareStatement(sql)) {
            pStmt.setString(1, user.getName());
            pStmt.setString(2, user.getPass());
            try (ResultSet rs = pStmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[UsersDAO] ユーザーが見つかりました: " + user.getName());
                    return new User(rs.getInt("ID"), rs.getString("NAME"), rs.getString("PASS"));
                }
            }
        } catch (SQLException e) {
            System.out.println("[UsersDAO] ユーザー検索中にDBエラーが発生しました");
            e.printStackTrace();
            return null;
        }
        System.out.println("[UsersDAO] 一致するユーザーはいません: " + user.getName());
        return null;
    }
}
