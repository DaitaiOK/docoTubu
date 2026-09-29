package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.example.docotubu.dao.DBUtil;

/**
 * muttersテーブルを初期化し、サンプルデータを投入する（任意で実行）
 * ※テーブル自体はアプリ起動時に各DAOが自動作成する
 */
public class InitDB {
    public static void main(String[] args) {
        // H2データベースに接続してテーブル作成とデータ挿入を行う
        try (Connection conn = DBUtil.getConnection()) {
            // テーブル作成
            String createTableSql = "CREATE TABLE IF NOT EXISTS mutters ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "userName VARCHAR(255) NOT NULL, "
                    + "text VARCHAR(255) NOT NULL)";
            PreparedStatement pstmt1 = conn.prepareStatement(createTableSql);
            pstmt1.executeUpdate();
            System.out.println("テーブルを作成しました。");

            // USERSテーブル作成（ユーザー登録機能用。既存ユーザーは消さない）
            String createUsersSql = "CREATE TABLE IF NOT EXISTS USERS ("
                    + "ID INT PRIMARY KEY AUTO_INCREMENT, "
                    + "NAME VARCHAR(100) NOT NULL UNIQUE, "
                    + "PASS VARCHAR(255) NOT NULL)";
            PreparedStatement pstmtUsers = conn.prepareStatement(createUsersSql);
            pstmtUsers.executeUpdate();
            System.out.println("USERSテーブルを作成しました。");

            // データリセット（重複を防ぐため一度空にする）
            String truncateSql = "TRUNCATE TABLE mutters";
            PreparedStatement pstmt2 = conn.prepareStatement(truncateSql);
            pstmt2.executeUpdate();

            // 初期データ挿入
            String insertSql = "INSERT INTO mutters (userName, text) VALUES (?, ?)";
            PreparedStatement pstmt3 = conn.prepareStatement(insertSql);
            
            pstmt3.setString(1, "湊 雄輔");
            pstmt3.setString(2, "今日は休みだ");
            pstmt3.executeUpdate();

            pstmt3.setString(1, "綾瀬 吾郎");
            pstmt3.setString(2, "いいな～");
            pstmt3.executeUpdate();
            
            System.out.println("初期データを追加しました。H2データベースの準備が完了しました！");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("データベースの準備中にエラーが発生しました。");
        }
    }
}
