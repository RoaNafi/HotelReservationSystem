package com.example.fx1;
import java.sql.*;

public class Connector {
    public Connection databaseLink;
    Statement s;


    public Connection getConnection () {
        String databaseName = "test1hm";
        String databaseUser = "root";
        String databasePassword = "Rr123123Aa";
        String url = "jdbc:mysql://localhost/" + databaseName;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
            s = databaseLink.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return databaseLink;
    }
}
