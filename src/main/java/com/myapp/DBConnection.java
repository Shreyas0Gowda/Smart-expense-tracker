package com.myapp;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(
                "jdbc:mysql://acela.proxy.rlwy.net:22079/railway",
                "root",
                "UcJoPKcstuNaWKxdwughmJtwiaZRmhia"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return con;
    }
}