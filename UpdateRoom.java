package com.example.fx1;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.fx1.AddRoom.createAmenitiesGrid;

public class UpdateRoom extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        List<Room> roomList = new ArrayList<>();
        display(primaryStage, roomList);

        // For demonstration purposes, print the room details to the console
        for (Room room : roomList) {
            System.out.println(room);
        }
    }

    public static void display(Stage primaryStage, List<Room> roomList) {
        primaryStage.setTitle("Update Room");

        GridPane grid = createGrid();

        TextField roomIdTextField = createTextField("Room ID:");
        ComboBox<String> roomTypeComboBox = createRoomTypeComboBox();
        List<CheckBox> amenityCheckBoxes = createAmenityCheckBoxes();
        TextField maxOccupancyTextField = createTextField("Maximum Occupancy:");
        TextField roomRateTextField = createTextField("Room Rate:");

        ComboBox<String> updateRoomComboBox = createComboBox(roomList);

        Button updateRoomButton = new Button("Update Room");
        updateRoomButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white;"); // Saddle Brown

        RadioButton isAvailableRadioButton = new RadioButton("Is Available");
        RadioButton isNotAvailableRadioButton = new RadioButton("Is not Available");
        ToggleGroup availabilityToggleGroup = new ToggleGroup();
        isAvailableRadioButton.setToggleGroup(availabilityToggleGroup);
        isNotAvailableRadioButton.setToggleGroup(availabilityToggleGroup);
        grid.add(isAvailableRadioButton, 1, 5);
        grid.add(isNotAvailableRadioButton, 2, 5);

        updateRoomButton.setOnAction(e -> {
            int roomId;
            try {
                roomId = Integer.parseInt(roomIdTextField.getText());
            } catch (NumberFormatException ex) {
                showAlert("Invalid Room ID", "Please enter a valid room ID.");
                return;
            }

            Room selectedRoom = searchRoomById(roomId);
            if (selectedRoom != null) {
                updateRoom(selectedRoom,
                        roomTypeComboBox.getValue(),
                        getSelectedAmenities(amenityCheckBoxes),
                        maxOccupancyTextField.getText(),
                        roomRateTextField.getText()
                );
                updateRoomInDatabase(selectedRoom, isAvailableRadioButton.isSelected());
                clearInputFields(roomIdTextField, roomTypeComboBox, amenityCheckBoxes,
                        maxOccupancyTextField, roomRateTextField);

            } else {
                showAlert("Room Not Found", "Room with ID " + roomId + " not found in the database.");
            }
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #CD5C5C; -fx-text-fill: white;"); // Indian Red
        cancelButton.setOnAction(e -> primaryStage.close());


        grid.add(roomIdTextField, 1, 0);
        grid.add(roomTypeComboBox, 1, 1);
        grid.add(createAmenitiesGrid(amenityCheckBoxes), 1, 2);
        grid.add(maxOccupancyTextField, 1, 3);
        grid.add(roomRateTextField, 1, 4);

        // Adjusted placement and size of the button
        grid.add(updateRoomButton, 1, 8);
        grid.add(cancelButton, 2, 8);
        GridPane.setMargin(updateRoomButton, new Insets(10, 0, 0, 0));

        Scene scene = new Scene(grid, 700, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void clearInputFields(TextField roomIdTextField, ComboBox<String> roomTypeComboBox, List<CheckBox> amenityCheckBoxes, TextField maxOccupancyTextField, TextField roomRateTextField) {
        roomIdTextField.clear();
        roomTypeComboBox.setValue(null);
        amenityCheckBoxes.forEach(checkBox -> checkBox.setSelected(false));
        maxOccupancyTextField.clear();
        roomRateTextField.clear();
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

    private static ComboBox<String> createRoomTypeComboBox() {
        ComboBox<String> roomTypeComboBox = new ComboBox<>();
        // Add your room types to the combo box
        roomTypeComboBox.getItems().addAll("Single Rooms", "Twin or Double Rooms", "Studio Rooms",
                "Deluxe Rooms", "Rooms with a View", "Suites", "Presidential Suites");
        return roomTypeComboBox;
    }

    private static List<CheckBox> createAmenityCheckBoxes() {
        List<CheckBox> amenityCheckBoxes = new ArrayList<>();

        // Add your amenities to the list
        List<String> amenities = List.of("Wi-Fi", "TV", "Air Conditioning", "Mini Bar");

        amenities.forEach(amenity -> {
            CheckBox checkBox = new CheckBox(amenity);
            amenityCheckBoxes.add(checkBox);
        });

        return amenityCheckBoxes;
    }

    private static List<String> getSelectedAmenities(List<CheckBox> checkBoxes) {
        List<String> selectedAmenities = new ArrayList<>();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                selectedAmenities.add(checkBox.getText());
            }
        }
        return selectedAmenities;
    }

    private static ComboBox<String> createComboBox(List<Room> roomList) {
        ComboBox<String> comboBox = new ComboBox<>();
        roomList.forEach(room -> comboBox.getItems().add(String.valueOf(room.getRoomId())));
        return comboBox;
    }

    private static TextField createTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        return textField;
    }

    private static void updateRoomInDatabase(Room room, boolean isAvailable) {
        Connector connector = new Connector();
        try (Connection connection = connector.getConnection()) {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot update room.");
                return;
            }

            // Create the base SQL query
            StringBuilder sqlBuilder = new StringBuilder("UPDATE Room SET");
            boolean hasUpdates = false;

            // Update room type if not null or empty
            if (isNotNullOrEmpty(room.getRoomType())) {
                sqlBuilder.append(" roomType = ?,");
                hasUpdates = true;
            }

            // Update room amenities if not null or empty
            if (isNotNullOrEmptyList(room.getRoomAmenities())) {
                sqlBuilder.append(" roomAmenities = ?,");
                hasUpdates = true;
            }

            // Update floor number if not 0 (assuming 0 is an invalid floor number)
            if (room.getFloorNumber() != 0) {
                sqlBuilder.append(" floorNumber = ?,");
                hasUpdates = true;
            }

            // Update maximum occupancy if not 0
            if (room.getMaximumOccupancy() != 0) {
                sqlBuilder.append(" maximumOccupancy = ?,");
                hasUpdates = true;
            }

            // Update room rate if not null
            if (room.getRoomRate() != null) {
                sqlBuilder.append(" roomRate = ?,");
                hasUpdates = true;
            }

            // Update availability status
            sqlBuilder.append(" isAvailable = ?,");
            hasUpdates = true;

            // Remove the trailing comma if there are updates
            if (hasUpdates) {
                sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
            } else {
                // If no updates, exit without executing the query
                System.out.println("No updates specified. Room not updated in the database.");
                return;
            }

            // Add the WHERE clause
            sqlBuilder.append(" WHERE roomId = ?");

            // Execute the prepared statement
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
                int parameterIndex = 1;

                // Set values for updated fields
                if (isNotNullOrEmpty(room.getRoomType())) {
                    preparedStatement.setString(parameterIndex++, room.getRoomType());
                }

                if (isNotNullOrEmptyList(room.getRoomAmenities())) {
                    preparedStatement.setString(parameterIndex++, String.join(",", room.getRoomAmenities()));
                }

                if (room.getFloorNumber() != 0) {
                    preparedStatement.setInt(parameterIndex++, room.getFloorNumber());
                }

                if (room.getMaximumOccupancy() != 0) {
                    preparedStatement.setInt(parameterIndex++, room.getMaximumOccupancy());
                }

                if (room.getRoomRate() != null) {
                    preparedStatement.setBigDecimal(parameterIndex++, room.getRoomRate());
                }

                // Set availability status
                preparedStatement.setBoolean(parameterIndex++, isAvailable);

                preparedStatement.setInt(parameterIndex, room.getRoomId());

                // Execute the update
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Room updated in the database successfully. RoomId: " + room.getRoomId());
                } else {
                    System.out.println("No rows affected. Room not updated in the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors appropriately
        }
    }

    // Helper method to check if a list is not null or empty
    private static boolean isNotNullOrEmptyList(List<?> list) {
        return list != null && !list.isEmpty();
    }

    // Helper method to check if a string is not null or empty
    private static boolean isNotNullOrEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static void updateRoom(Room room, String roomType, List<String> roomAmenities, String maxOccupancy, String roomRate) {
        // Implement the method as needed
        if (isNotNullOrEmpty(roomType)) {
            room.setRoomType(roomType);
        }

        if (isNotNullOrEmptyList(roomAmenities)) {
            room.setRoomAmenities(roomAmenities);
        }

        if (!maxOccupancy.isEmpty()) {
            room.setMaximumOccupancy(Integer.parseInt(maxOccupancy));
        }

        if (!roomRate.isEmpty()) {
            room.setRoomRate(new BigDecimal(roomRate));
        }
    }

    private static Room searchRoomById(int roomId) {
        Connector connector = new Connector();
        try (Connection connection = connector.getConnection()) {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot search for room.");
                return null;
            }

            String sql = "SELECT * FROM Room WHERE roomId = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, roomId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        Room room = new Room();
                        room.setRoomId(resultSet.getInt("roomId"));
                        room.setRoomType(resultSet.getString("roomType"));
                        room.setRoomAmenities(List.of(resultSet.getString("roomAmenities").split("\\s*,\\s*")));
                        room.setFloorNumber(resultSet.getInt("floorNumber"));
                        room.setMaximumOccupancy(resultSet.getInt("maximumOccupancy"));
                        room.setRoomRate(resultSet.getBigDecimal("roomRate"));
                        return room;
                    } else {
                        System.out.println("Room with ID " + roomId + " not found in the database.");
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors appropriately
            return null;
        }
    }
}