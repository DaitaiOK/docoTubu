package com.example.docotubu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.example.docotubu.model.Mutter;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;

@Repository
public class MuttersDAO {

    /**
     * アプリ起動時にmuttersテーブルが存在しなければ作成する
     */
    @PostConstruct
    public void createTableIfNotExists() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS mutters ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "userName VARCHAR(255) NOT NULL, "
                    + "text VARCHAR(255) NOT NULL)";
            stmt.executeUpdate(sql);
            System.out.println("[MuttersDAO] muttersテーブルの準備が完了しました");
        } catch (SQLException e) {
            System.out.println("[MuttersDAO] muttersテーブルの作成に失敗しました");
            e.printStackTrace();
        }
    }

    public List<Mutter> findAll() {
        List<Mutter> mutterList = new ArrayList<>();
        String sql = "SELECT id, userName, text FROM mutters ORDER BY id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pStmt = conn.prepareStatement(sql);
             ResultSet rs = pStmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String userName = rs.getString("userName");
                String text = rs.getString("text");
                Mutter mutter = new Mutter(id, userName, text);
                mutterList.add(mutter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return mutterList;
    }

    public boolean create(Mutter mutter) {
        String sql = "INSERT INTO mutters(userName, text) VALUES(?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pStmt = conn.prepareStatement(sql)) {
            pStmt.setString(1, mutter.getUserName());
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
}
