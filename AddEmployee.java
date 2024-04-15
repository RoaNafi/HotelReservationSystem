package com.example.fx1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddEmployee extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Add Employee");

        GridPane grid = createGrid();

        TextField employeeIdTextField = createTextField("Employee ID:");
        TextField nameTextField = createTextField("Name:");
        TextField phoneTextField = createTextField("Phone:");
        TextField emailTextField = createTextField("Email:");
        TextField addressTextField = createTextField("Address:");
        TextField salaryTextField = createTextField("Salary:");
        PasswordField passwordField = new PasswordField();

        // Add ComboBox for Role
        Label roleLabel = new Label("Role:");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Employee", "Manager");
        roleComboBox.setValue("Employee"); // Default value
        // Add Label for Password
        Label passwordLabel = new Label("Password:");


        Button addEmployeeButton = new Button("Add Employee");
        addEmployeeButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white;"); // Green color

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #B22222; -fx-text-fill: white;"); // Firebrick color
        cancelButton.setOnAction(e -> primaryStage.close());

        addEmployeeButton.setOnAction(e -> {
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

            Employee newEmployee = new Employee();
            newEmployee.setEmployeeId(employeeId);
            newEmployee.setName(name);
            newEmployee.setPhone(phone);
            newEmployee.setEmail(email);
            newEmployee.setAddress(address);
            newEmployee.setSalary(salary);

            // Set authentication details (password and role) in AuthenticationService table
            String password = passwordField.getText();
            String role = roleComboBox.getValue();
            addEmployeeToDatabase(newEmployee, password, role);

            clearInputFields(employeeIdTextField, nameTextField, phoneTextField, emailTextField, addressTextField, salaryTextField, passwordField);
        });


        grid.add(employeeIdTextField, 1, 0);
        grid.add(nameTextField, 1, 1);
        grid.add(phoneTextField, 1, 2);
        grid.add(emailTextField, 1, 3);
        grid.add(addressTextField, 1, 4);
        grid.add(salaryTextField, 1, 5);
        grid.add(roleLabel, 0, 6);
        grid.add(roleComboBox, 1, 6);
        grid.add(passwordLabel, 0, 7);  // Add Password Label
        grid.add(passwordField, 1, 7);
        grid.add(addEmployeeButton, 1, 8);
        grid.add(cancelButton, 2, 8);
        Scene scene = new Scene(grid, 700, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void clearInputFields(TextField... textFields) {
        for (TextField textField : textFields) {
            textField.clear();
        }
    }

    private static void addEmployeeToDatabase(Employee employee, String password, String role) {
        Connector connector = new Connector();

        try (Connection connection = connector.getConnection()) {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot add employee.");
                return;
            }

            String employeeSql = "INSERT INTO Employee (employeeId, name, phone, email, address, salary) VALUES (?, ?, ?, ?, ?, ?)";
            String authSql = "INSERT INTO AuthenticationService (employeeId, password, role) VALUES (?, ?, ?)";

            try (PreparedStatement employeeStatement = connection.prepareStatement(employeeSql);
                 PreparedStatement authStatement = connection.prepareStatement(authSql)) {

                // Add employee details
                employeeStatement.setInt(1, employee.getEmployeeId());
                employeeStatement.setString(2, employee.getName());
                employeeStatement.setString(3, employee.getPhone());
                employeeStatement.setString(4, employee.getEmail());
                employeeStatement.setString(5, employee.getAddress());
                employeeStatement.setBigDecimal(6, employee.getSalary());

                // Execute employee query
                int employeeRowsAffected = employeeStatement.executeUpdate();

                // Add authentication details (password and role)
                authStatement.setInt(1, employee.getEmployeeId());
                authStatement.setString(2, password);
                authStatement.setString(3, role);

                // Execute authentication query
                int authRowsAffected = authStatement.executeUpdate();

                if (employeeRowsAffected > 0 && authRowsAffected > 0) {
                    showAlert("Employee Added", "Employee added to the database successfully.");
                } else {
                    showAlert("Error", "Failed to add employee to the database.");
                }

            } catch (SQLException e) {
                e.printStackTrace(); // Handle database errors appropriately
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database connection errors appropriately
        }
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