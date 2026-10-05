package com.example.docotubu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.example.docotubu.model.Mutter;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;

// muttersテーブルはUSERSテーブルを参照するため、USERSテーブルの作成後に初期化する
@Repository
@DependsOn("usersDAO")
public class MuttersDAO {

    // 投稿者名はUSERSテーブルから取得する（muttersテーブルには持たない）
    private static final String SELECT_WITH_USER_NAME =
            "SELECT m.id, u.NAME AS userName, m.text "
            + "FROM mutters m JOIN USERS u ON m.user_id = u.ID ";

    /**
     * アプリ起動時にmuttersテーブルが存在しなければ作成する
     * 旧構造（投稿者名をuserNameカラムに直接保持）のテーブルが残っている場合はuser_id方式へ移行する
     */
    @PostConstruct
    public void createTableIfNotExists() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS mutters ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "user_id INT NOT NULL, "
                    + "text VARCHAR(255) NOT NULL, "
                    + "CONSTRAINT fk_mutters_user FOREIGN KEY (user_id) REFERENCES USERS(ID))";
            stmt.executeUpdate(sql);

            if (hasColumn(conn, "MUTTERS", "USERNAME")) {
                migrateUserNameToUserId(stmt);
            }
            System.out.println("[MuttersDAO] muttersテーブルの準備が完了しました");
        } catch (SQLException e) {
            System.out.println("[MuttersDAO] muttersテーブルの作成に失敗しました");
            e.printStackTrace();
        }
    }

    /**
     * 旧構造のmuttersテーブル（userNameカラム）を、USERSテーブルを参照するuser_idカラムへ移行する
     * ※H2のDDLは自動コミットされるため、各手順は途中で失敗しても再実行できるようにしている
     */
    private void migrateUserNameToUserId(Statement stmt) throws SQLException {
        System.out.println("[MuttersDAO] muttersテーブルをuser_id方式へ移行します");

        // 1. user_idカラムを追加（既存データがあるためまずはNULL許容）
        stmt.executeUpdate("ALTER TABLE mutters ADD COLUMN IF NOT EXISTS user_id INT");

        // USERSに存在しない投稿者（AI太郎・サンプルデータの投稿者など）はログイン不可のユーザーとして登録し、投稿を残す
        stmt.executeUpdate("INSERT INTO USERS(NAME, PASS) "
                + "SELECT DISTINCT m.userName, '" + UsersDAO.UNUSABLE_PASS + "' FROM mutters m "
                + "WHERE NOT EXISTS (SELECT 1 FROM USERS u WHERE u.NAME = m.userName)");

        // 2. 既存の投稿に、投稿者名に対応するユーザーのIDを設定
        stmt.executeUpdate("UPDATE mutters m "
                + "SET user_id = (SELECT u.ID FROM USERS u WHERE u.NAME = m.userName) "
                + "WHERE m.user_id IS NULL");

        // 3. user_idをNOT NULLに変更し、USERSテーブルへの外部キーを設定
        stmt.executeUpdate("ALTER TABLE mutters ALTER COLUMN user_id SET NOT NULL");
        stmt.executeUpdate("ALTER TABLE mutters ADD CONSTRAINT IF NOT EXISTS fk_mutters_user "
                + "FOREIGN KEY (user_id) REFERENCES USERS(ID)");

        // 4. 不要になったuserNameカラムを削除
        stmt.executeUpdate("ALTER TABLE mutters DROP COLUMN userName");

        System.out.println("[MuttersDAO] muttersテーブルの移行が完了しました");
    }

    private boolean hasColumn(Connection conn, String tableName, String columnName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        try (PreparedStatement pStmt = conn.prepareStatement(sql)) {
            pStmt.setString(1, tableName);
            pStmt.setString(2, columnName);
            try (ResultSet rs = pStmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public List<Mutter> findAll() {
        List<Mutter> mutterList = new ArrayList<>();
        String sql = SELECT_WITH_USER_NAME + "ORDER BY m.id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pStmt = conn.prepareStatement(sql);
             ResultSet rs = pStmt.executeQuery()) {
            while (rs.next()) {
                mutterList.add(toMutter(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return mutterList;
    }

    /**
     * 本文にキーワードを含むつぶやきを検索する（部分一致）
     * ※キーワード中の「%」「_」はワイルドカードではなく文字そのものとして扱う
     */
    public List<Mutter> search(String keyword) {
        List<Mutter> mutterList = new ArrayList<>();
        String sql = SELECT_WITH_USER_NAME + "WHERE m.text LIKE ? ESCAPE '\\' ORDER BY m.id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pStmt = conn.prepareStatement(sql)) {
            pStmt.setString(1, "%" + escapeLike(keyword) + "%");
            try (ResultSet rs = pStmt.executeQuery()) {
                while (rs.next()) {
                    mutterList.add(toMutter(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return mutterList;
    }

    public boolean create(Mutter mutter) {
        String sql = "INSERT INTO mutters(user_id, text) VALUES(?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pStmt = conn.prepareStatement(sql)) {
            pStmt.setInt(1, mutter.getUserId());
            pStmt.setString(2, mutter.getText());
            int result = pStmt.executeUpdate();
            if (result != 1) return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM mutters WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pStmt = conn.prepareStatement(sql)) {
            pStmt.setInt(1, id);
            int result = pStmt.executeUpdate();
            if (result != 1) return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Mutter toMutter(ResultSet rs) throws SQLException {
        return new Mutter(rs.getInt("id"), rs.getString("userName"), rs.getString("text"));
    }

    private String escapeLike(String keyword) {
        return keyword.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
