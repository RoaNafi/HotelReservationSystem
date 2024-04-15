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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteReservation extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Delete Reservation");

        GridPane grid = createGrid();

        TextField reservationIdTextField = createTextField("Enter Reservation ID:");

        Button deleteReservationButton = new Button("Delete Reservation");
        deleteReservationButton.setStyle("-fx-background-color: #CD5C5C; -fx-text-fill: white;"); // Indian Red
        deleteReservationButton.setOnAction(e -> {
            String reservationId = reservationIdTextField.getText();
            if (!reservationId.isEmpty()) {
                deleteReservationFromDatabase(reservationId);
                reservationIdTextField.clear();
            } else {
                showAlert("Empty Reservation ID", "Please enter a Reservation ID to delete.");
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white;"); // Saddle Brown
        cancelButton.setOnAction(e -> primaryStage.close());

        grid.add(reservationIdTextField, 1, 0);
        grid.add(deleteReservationButton, 1, 1);
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
        Image image = new Image("/deletere.jpg");
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

    private static void deleteReservationFromDatabase(String reservationId) {
        Connector connector = new Connector();
        Connection connection = connector.getConnection();

        try {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot delete reservation.");
                return;
            }

            // Proceed with the deletion
            String sql = "DELETE FROM Reservation WHERE reservationId = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, Integer.parseInt(reservationId));

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Deletion Successful", "Reservation deleted successfully.");
                } else {
                    showAlert("Reservation Not Found", "No reservation with the specified Reservation ID found in the database.");
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