package com.example.fx1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Optional;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
public class DeleteRoom extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Delete Room");

        GridPane grid = createGrid();

        TextField roomIdTextField = createTextField("Enter Room ID:");

        Button deleteRoomButton = new Button("Delete Room");
        deleteRoomButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white;"); // Saddle Brown
        deleteRoomButton.setOnAction(e -> {
            String roomId = roomIdTextField.getText();
            if (!roomId.isEmpty()) {
                deleteRoomFromDatabase(roomId);
                roomIdTextField.clear();
            } else {
                showAlert("Empty Room ID", "Please enter a Room ID to delete.");
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #CD5C5C; -fx-text-fill: white;"); // Indian Red
        cancelButton.setOnAction(e -> primaryStage.close());

        grid.add(roomIdTextField, 1, 0);
        grid.add(deleteRoomButton, 1, 1);
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
        Image image = new Image("/room.jpg");
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
    private static boolean confirmDeletion(String roomId) {
        // Check if there are associated reservations
        int reservationCount = getReservationCount(roomId);
        if (reservationCount > 0) {
            // Display a confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Warning: Associated Reservations");
            alert.setContentText("This room has associated reservations. Deleting it will cancel these reservations. Are you sure you want to proceed?");
            Optional<ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == ButtonType.OK;
        }
        return true; // No associated reservations, proceed with deletion
    }

    private static int getReservationCount(String roomId) {
        Connector connector = new Connector();
        try (Connection connection = connector.getConnection()) {
            if (connection != null) {
                String sql = "SELECT COUNT(*) FROM Reservation WHERE roomId = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, Integer.parseInt(roomId));
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

    private static void deleteRoomFromDatabase(String roomId) {
        Connector connector = new Connector();
        Connection connection = connector.getConnection();

        try {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot delete room.");
                return;
            }

            // Check for associated reservations
            int reservationCount = getReservationCount(roomId);

            if (reservationCount > 0) {
                // Display a confirmation dialog
                boolean confirmed = confirmDeletion(roomId);
                if (!confirmed) {
                    return; // User canceled the deletion
                }

                // Delete associated reservations
                deleteReservations(roomId);
            }

            // Proceed with the deletion
            String sql = "DELETE FROM Room WHERE roomId = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, Integer.parseInt(roomId));

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Room deleted from the database successfully. RoomId: " + roomId);

                    // Display success message
                    showAlert("Deletion Successful", "Room and associated reservations deleted successfully.");
                } else {
                    showAlert("Room Not Found", "No room with the specified Room ID found in the database.");
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

    // New method to delete associated reservations
    private static void deleteReservations(String roomId) {
        Connector connector = new Connector();
        Connection connection = connector.getConnection();

        try {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot delete reservations.");
                return;
            }

            // Delete reservations associated with the specified room
            String sql = "DELETE FROM Reservation WHERE roomId = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, Integer.parseInt(roomId));

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

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}