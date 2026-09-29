package com.example.docotubu.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.example.docotubu.model.Mutter;
import org.springframework.stereotype.Repository;

@Repository
public class MuttersDAO {
    private final String JDBC_URL = "jdbc:h2:~/docoTsubu;AUTO_SERVER=TRUE";
    private final String DB_USER = "sa";
    private final String DB_PASS = "";

    public List<Mutter> findAll() {
        List<Mutter> mutterList = new ArrayList<>();
        try { Class.forName("org.h2.Driver"); } catch (ClassNotFoundException e) { throw new IllegalStateException(e); }
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT id, userName, text FROM mutters ORDER BY id DESC";
            PreparedStatement pStmt = conn.prepareStatement(sql);
            ResultSet rs = pStmt.executeQuery();
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
        try { Class.forName("org.h2.Driver"); } catch (ClassNotFoundException e) { throw new IllegalStateException(e); }
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {
            String sql = "INSERT INTO mutters(userName, text) VALUES(?, ?)";
            PreparedStatement pStmt = conn.prepareStatement(sql);
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
        try { Class.forName("org.h2.Driver"); } catch (ClassNotFoundException e) { throw new IllegalStateException(e); }
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {
            String sql = "DELETE FROM mutters WHERE id = ?";
            PreparedStatement pStmt = conn.prepareStatement(sql);
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
