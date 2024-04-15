package com.example.fx1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateEmployee extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Update Employee");

        GridPane grid = createGrid();

        TextField employeeIdTextField = createTextField("Employee ID:");
        TextField nameTextField = createTextField("Name:");
        TextField phoneTextField = createTextField("Phone:");
        TextField emailTextField = createTextField("Email:");
        TextField addressTextField = createTextField("Address:");
        TextField salaryTextField = createTextField("Salary:");

        Button updateEmployeeButton = new Button("Update Employee");
        updateEmployeeButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white;"); // Green color

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #B22222; -fx-text-fill: white;"); // Firebrick color
        cancelButton.setOnAction(e -> primaryStage.close());

        updateEmployeeButton.setOnAction(e -> {
            String employeeIdText = employeeIdTextField.getText();
            String name = nameTextField.getText();
            String phone = phoneTextField.getText();
            String email = emailTextField.getText();
            String address = addressTextField.getText();
            BigDecimal salary;

            try {
                salary = new BigDecimal(salaryTextField.getText());
            } catch (NumberFormatException ex) {
                showAlert("Invalid Salary", "Please enter a valid salary.");
                return;
            }

            if (employeeIdText.isEmpty()) {
                showAlert("Missing Employee ID", "Please enter an Employee ID.");
                return;
            }

            int employeeId;
            try {
                employeeId = Integer.parseInt(employeeIdText);
            } catch (NumberFormatException ex) {
                showAlert("Invalid Employee ID", "Please enter a valid Employee ID.");
                return;
            }

            Employee updatedEmployee = new Employee();
            updatedEmployee.setEmployeeId(employeeId);
            updatedEmployee.setName(name);
            updatedEmployee.setPhone(phone);
            updatedEmployee.setEmail(email);
            updatedEmployee.setAddress(address);
            updatedEmployee.setSalary(salary);

            updateEmployeeInDatabase(updatedEmployee);
            clearInputFields(employeeIdTextField, nameTextField, phoneTextField, emailTextField, addressTextField, salaryTextField);
        });

        // Add a listener to the employeeIdTextField to fetch employee info when it changes
        employeeIdTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                int employeeId;
                try {
                    employeeId = Integer.parseInt(newValue);
                } catch (NumberFormatException ex) {
                    return; // Ignore non-integer input
                }

                Employee employee = retrieveEmployeeFromDatabase(employeeId);
                if (employee != null) {
                    nameTextField.setText(employee.getName());
                    phoneTextField.setText(employee.getPhone());
                    emailTextField.setText(employee.getEmail());
                    addressTextField.setText(employee.getAddress());
                    if (employee.getSalary() != null) {
                        salaryTextField.setText(employee.getSalary().toString());
                    } else {
                        salaryTextField.clear();
                    }
                } else {
                    // Employee not found, clear the fields
                    clearInputFields(nameTextField, phoneTextField, emailTextField, addressTextField, salaryTextField);
                }
            }
        });

        grid.add(employeeIdTextField, 1, 0);
        grid.add(nameTextField, 1, 1);
        grid.add(phoneTextField, 1, 2);
        grid.add(emailTextField, 1, 3);
        grid.add(addressTextField, 1, 4);
        grid.add(salaryTextField, 1, 5);
        grid.add(updateEmployeeButton, 1, 6);
        grid.add(cancelButton, 2, 6);

        Scene scene = new Scene(grid, 700, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void clearInputFields(TextField... textFields) {
        for (TextField textField : textFields) {
            textField.clear();
        }
    }

    private static void updateEmployeeInDatabase(Employee employee) {
        Connector connector = new Connector();
        try (Connection connection = connector.getConnection()) {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot update employee.");
                return;
            }

            // Build the base SQL query
            StringBuilder sqlBuilder = new StringBuilder("UPDATE Employee SET");

            // Check each field and add it to the query if it is not null
            boolean hasUpdates = false;
            if (isNotNullOrEmpty(employee.getName())) {
                sqlBuilder.append(" name = ?,");
                hasUpdates = true;
            }

            if (isNotNullOrEmpty(employee.getPhone())) {
                sqlBuilder.append(" phone = ?,");
                hasUpdates = true;
            }

            if (isNotNullOrEmpty(employee.getEmail())) {
                sqlBuilder.append(" email = ?,");
                hasUpdates = true;
            }

            if (isNotNullOrEmpty(employee.getAddress())) {
                sqlBuilder.append(" address = ?,");
                hasUpdates = true;
            }

            if (employee.getSalary() != null) {
                sqlBuilder.append(" salary = ?,");
                hasUpdates = true;
            }

            // Remove the trailing comma if there are updates
            if (hasUpdates) {
                sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
            } else {
                // If no updates, exit without executing the query
                showAlert("No Updates", "No updates specified. Employee not updated in the database.");
                return;
            }

            // Add the WHERE clause
            sqlBuilder.append(" WHERE employeeId = ?");

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
                // Set values for updated fields
                int parameterIndex = 1;
                if (isNotNullOrEmpty(employee.getName())) {
                    preparedStatement.setString(parameterIndex++, employee.getName());
                }

                if (isNotNullOrEmpty(employee.getPhone())) {
                    preparedStatement.setString(parameterIndex++, employee.getPhone());
                }

                if (isNotNullOrEmpty(employee.getEmail())) {
                    preparedStatement.setString(parameterIndex++, employee.getEmail());
                }

                if (isNotNullOrEmpty(employee.getAddress())) {
                    preparedStatement.setString(parameterIndex++, employee.getAddress());
                }

                if (employee.getSalary() != null) {
                    preparedStatement.setBigDecimal(parameterIndex++, employee.getSalary());
                }

                // Set the WHERE clause parameter
                preparedStatement.setInt(parameterIndex, employee.getEmployeeId());

                // Execute the update
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Employee Updated", "Employee updated in the database successfully.");
                } else {
                    showAlert("No Updates", "No updates applied. Employee ID not found in the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors appropriately
        }
    }

    private static Employee retrieveEmployeeFromDatabase(int employeeId) {
        Connector connector = new Connector();
        try (Connection connection = connector.getConnection()) {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot retrieve employee.");
                return null;
            }

            String sql = "SELECT * FROM Employee WHERE employeeId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employeeId);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    Employee employee = new Employee();
                    employee.setEmployeeId(resultSet.getInt("employeeId"));
                    employee.setName(resultSet.getString("name"));
                    employee.setPhone(resultSet.getString("phone"));
                    employee.setEmail(resultSet.getString("email"));
                    employee.setAddress(resultSet.getString("address"));
                    employee.setSalary(resultSet.getBigDecimal("salary"));
                    return employee;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors appropriately
        }
        return null;
    }

    // Helper method to check if a string is not null or empty
    private static boolean isNotNullOrEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    private static GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        // Set background color
        grid.setStyle("-fx-background-color: #F5F5DC;"); // Beige color

        // Add image
        Image image = new Image("/employee.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(400); // Set the height of the image
        imageView.setFitWidth(400); // Set the width of the image

        grid.add(imageView, 2, 0, 1, 5); // Add image to the right of the text boxes

        return grid;
    }

    private static TextField createTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        return textField;
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
