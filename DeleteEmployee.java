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
import javafx.scene.control.ButtonType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DeleteEmployee extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Delete Employee");

        GridPane grid = createGrid();

        TextField employeeIdTextField = createTextField("Enter Employee ID:");

        Button deleteEmployeeButton = new Button("Delete Employee");
        deleteEmployeeButton.setStyle("-fx-background-color: #CD5C5C; -fx-text-fill: white;"); // Indian Red
        deleteEmployeeButton.setOnAction(e -> {


            String employeeId = employeeIdTextField.getText();
            if (!employeeId.isEmpty()) {
                deleteEmployeeFromDatabase(employeeId);
                employeeIdTextField.clear();
            } else {
                showAlert("Empty Employee ID", "Please enter an Employee ID to delete.");
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white;"); // Saddle Brown
        cancelButton.setOnAction(e -> primaryStage.close());

        grid.add(employeeIdTextField, 1, 0);
        grid.add(deleteEmployeeButton, 1, 1);
        grid.add(cancelButton, 1, 2);

        Scene scene = new Scene(grid, 700, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(30, 30, 30, 30));

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

    private static boolean confirmDeletion(String employeeId) {
        // Check if there are associated reservations
        int reservationCount = getReservationCount(employeeId);
        if (reservationCount > 0) {
            // Display a confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Warning: Associated Reservations");
            alert.setContentText("This employee has associated reservations. Deleting them will cancel these reservations. Are you sure you want to proceed?");
            Optional<ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == ButtonType.OK;
        }
        return true; // No associated reservations, proceed with deletion
    }

    private static int getReservationCount(String employeeId) {
        Connector connector = new Connector();
        try (Connection connection = connector.getConnection()) {
            if (connection != null) {
                String sql = "SELECT COUNT(*) FROM Reservation WHERE employeeId = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, Integer.parseInt(employeeId));
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static void deleteReservations(String employeeId) {
        Connector connector = new Connector();
        Connection connection = connector.getConnection();

        try {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot delete reservations.");
                return;
            }

            // Delete reservations associated with the specified employee
            String sql = "DELETE FROM Reservation WHERE employeeId = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, Integer.parseInt(employeeId));

                int rowsAffected = preparedStatement.executeUpdate();
                System.out.println("Deleted " + rowsAffected + " associated reservations.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors appropriately
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void deleteRelatedAuthentications(String employeeId) {
        // Establish database connection
        Connector connector = new Connector();
        Connection connection = connector.getConnection();

        try {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot delete authentications.");
                return;
            }

            // SQL query to delete related authentication records
            String sql = "DELETE FROM authenticationservice WHERE employeeId = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, Integer.parseInt(employeeId));
                int rowsAffected = preparedStatement.executeUpdate();
                System.out.println("Deleted " + rowsAffected + " related authentication records.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors appropriately
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    private static void deleteEmployeeFromDatabase(String employeeId) {
        Connector connector = new Connector();
        Connection connection = connector.getConnection();
        deleteRelatedAuthentications(employeeId);
        try {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot delete employee.");
                return;
            }

            // Check for associated reservations
            int reservationCount = getReservationCount(employeeId);

            if (reservationCount > 0) {
                // Display a confirmation dialog
                boolean confirmed = confirmDeletion(employeeId);
                if (!confirmed) {
                    return; // User canceled the deletion
                }

                // Delete associated reservations
                deleteReservations(employeeId);

            }

            // Proceed with the deletion
            String sql = "DELETE FROM Employee WHERE employeeId = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, Integer.parseInt(employeeId));

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Deletion Successful", "Employee and associated reservations deleted successfully.");
                } else {
                    showAlert("Employee Not Found", "No employee with the specified Employee ID found in the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors appropriately
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}