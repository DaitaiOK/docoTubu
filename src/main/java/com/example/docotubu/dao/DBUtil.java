package com.example.docotubu.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DB接続情報を一元管理するユーティリティ（各DAOから共通で利用する）
 */
public final class DBUtil {
    // AUTO_SERVER=TRUE：アプリ起動中でもInitDBやH2コンソールから同時接続できるようにする
    private static final String JDBC_URL = "jdbc:h2:~/docoTsubu;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("H2 JDBCドライバが見つかりません", e);
        }
    }

    private DBUtil() {
    }

    /**
     * DBへの接続を取得する（呼び出し側でtry-with-resourcesによりクローズすること）
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
    }
}
