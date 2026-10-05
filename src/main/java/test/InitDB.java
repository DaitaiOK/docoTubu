package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.example.docotubu.dao.DBUtil;
import com.example.docotubu.dao.MuttersDAO;
import com.example.docotubu.dao.UsersDAO;

/**
 * muttersテーブルを初期化し、サンプルデータを投入する（任意で実行）
 * ※テーブル自体はアプリ起動時に各DAOが自動作成する
 */
public class InitDB {
    public static void main(String[] args) {
        // テーブル作成（旧構造のmuttersテーブルが残っていればuser_id方式へ移行する）
        // muttersはUSERSを参照するため、USERS → mutters の順に作成する
        UsersDAO usersDAO = new UsersDAO();
        usersDAO.createTableIfNotExists();
        new MuttersDAO().createTableIfNotExists();
        System.out.println("テーブルを作成しました。");

        // サンプルの投稿者をログイン不可のユーザーとして用意する（既存ユーザーは消さない）
        String[] sampleUsers = { "湊 雄輔", "綾瀬 吾郎" };
        for (String name : sampleUsers) {
            usersDAO.registerSystemUserIfNotExists(name);
        }

        // H2データベースに接続してデータ挿入を行う
        try (Connection conn = DBUtil.getConnection()) {
            // データリセット（重複を防ぐため一度空にする）
            String truncateSql = "TRUNCATE TABLE mutters";
            PreparedStatement pstmt2 = conn.prepareStatement(truncateSql);
            pstmt2.executeUpdate();

            // 初期データ挿入（投稿者はユーザー名からuser_idを引いて紐付ける）
            String insertSql = "INSERT INTO mutters (user_id, text) "
                    + "SELECT ID, ? FROM USERS WHERE NAME = ?";
            PreparedStatement pstmt3 = conn.prepareStatement(insertSql);

            pstmt3.setString(1, "今日は休みだ");
            pstmt3.setString(2, "湊 雄輔");
            pstmt3.executeUpdate();

            pstmt3.setString(1, "いいな～");
            pstmt3.setString(2, "綾瀬 吾郎");
            pstmt3.executeUpdate();

            System.out.println("初期データを追加しました。H2データベースの準備が完了しました！");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("データベースの準備中にエラーが発生しました。");
        }
    }
}
