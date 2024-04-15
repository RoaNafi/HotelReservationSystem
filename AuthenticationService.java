package com.example.fx1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationService {

    private final Connector connector;

    public AuthenticationService(Connector connector) {
        this.connector = connector;
    }

    public boolean authenticate(String employeeId, String password) {
        String sql = "SELECT password FROM AuthenticationService WHERE employeeId = ?";
        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(employeeId));
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                return storedPassword.equals(password);
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getUserRole(String employeeId) {
        String sql = "SELECT role FROM AuthenticationService WHERE employeeId = ?";
        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(employeeId));
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("role");
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return "Undefined";
    }
}
