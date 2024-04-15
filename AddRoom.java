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

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddRoom extends Application {

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
        primaryStage.setTitle("Add Room");

        GridPane grid = createGrid();

        TextField roomIdTextField = createTextField("Room ID:");
        ComboBox<String> roomTypeComboBox = createRoomTypeComboBox();
        List<CheckBox> amenityCheckBoxes = createAmenityCheckBoxes();
        TextField maxOccupancyTextField = createTextField("Maximum Occupancy:");
        TextField roomRateTextField = createTextField("Room Rate:");

        Button addRoomButton = new Button("Add Room");
        addRoomButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white;"); // Saddle Brown
        addRoomButton.setOnAction(e -> {
            Room newRoom = createRoomFromInput(
                    roomIdTextField.getText(),
                    roomTypeComboBox.getValue(),
                    getSelectedAmenities(amenityCheckBoxes),
                    maxOccupancyTextField.getText(),
                    roomRateTextField.getText()
            );
            roomList.add(newRoom);
            addRoomToDatabase(newRoom);
            clearInputFields(roomIdTextField, roomTypeComboBox, amenityCheckBoxes,
                    maxOccupancyTextField, roomRateTextField);
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #CD5C5C; -fx-text-fill: white;"); // Indian Red
        cancelButton.setOnAction(e -> primaryStage.close());

        grid.add(roomIdTextField, 1, 0);
        grid.add(roomTypeComboBox, 1, 1);
        grid.add(createAmenitiesGrid(amenityCheckBoxes), 1, 2);
        grid.add(maxOccupancyTextField, 1, 3);
        grid.add(roomRateTextField, 1, 4);
        grid.add(addRoomButton, 1, 5);
        grid.add(cancelButton, 2, 5);

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
        System.out.println("Image File Path: " + new File("src/main/resources/room.jpg").getAbsolutePath());
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
        roomTypeComboBox.setPromptText("Select Room Type");
        roomTypeComboBox.getItems().addAll(
                "Single Rooms", "Twin or Double Rooms", "Studio Rooms",
                "Deluxe Rooms", "Rooms with a View", "Suites", "Presidential Suites"
        );
        return roomTypeComboBox;
    }

    private static List<CheckBox> createAmenityCheckBoxes() {
        List<CheckBox> amenityCheckBoxes = new ArrayList<>();
        amenityCheckBoxes.add(new CheckBox("Wi-Fi"));
        amenityCheckBoxes.add(new CheckBox("Television"));
        amenityCheckBoxes.add(new CheckBox("Minibar"));
        amenityCheckBoxes.add(new CheckBox("Air Conditioning"));
        return amenityCheckBoxes;
    }

    static GridPane createAmenitiesGrid(List<CheckBox> amenityCheckBoxes) {
        GridPane amenitiesGrid = new GridPane();
        amenitiesGrid.setHgap(10);
        amenitiesGrid.setVgap(5);
        amenitiesGrid.setAlignment(Pos.CENTER_LEFT);

        Label amenitiesLabel = new Label("Room Amenities:");
        amenitiesGrid.add(amenitiesLabel, 0, 0);

        int row = 1;
        for (CheckBox checkBox : amenityCheckBoxes) {
            amenitiesGrid.add(checkBox, 0, row);
            row++;
        }

        return amenitiesGrid;
    }

    private static TextField createTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        return textField;
    }

    static List<String> getSelectedAmenities(List<CheckBox> amenityCheckBoxes) {
        List<String> selectedAmenities = new ArrayList<>();
        for (CheckBox checkBox : amenityCheckBoxes) {
            if (checkBox.isSelected()) {
                selectedAmenities.add(checkBox.getText());
            }
        }
        return selectedAmenities;
    }

    private static Room createRoomFromInput(String roomId, String roomType, List<String> roomAmenities,
                                            String maxOccupancy, String roomRate) {
        Room newRoom = new Room();
        newRoom.setRoomId(Integer.parseInt(roomId));
        newRoom.setFloorNumber(getFloorNumberFromRoomId(roomId));
        newRoom.setRoomType(roomType);
        newRoom.setRoomAmenities(roomAmenities);
        newRoom.setMaximumOccupancy(Integer.parseInt(maxOccupancy));
        newRoom.setRoomRate(new BigDecimal(roomRate));
        return newRoom;
    }
    private static int getFloorNumberFromRoomId(String roomId) {
        // Assuming roomId is a valid numeric string
        return Integer.parseInt(roomId.substring(0, 1));
    }

    private static void addRoomToDatabase(Room newRoom) {
        Connector connector = new Connector();
        Connection connection = connector.getConnection();

        try {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot add room.");
                return;
            }



            String sql = "INSERT INTO Room (roomId, isAvailable, roomType, roomAmenities, floorNumber, maximumOccupancy, roomRate) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                // Set default value for is_available to true (1)
                preparedStatement.setInt(1, newRoom.getRoomId());
                preparedStatement.setBoolean(2, true);
                preparedStatement.setString(3, newRoom.getRoomType());
                preparedStatement.setString(4, String.join(",", newRoom.getRoomAmenities()));
                preparedStatement.setInt(5, newRoom.getFloorNumber());
                preparedStatement.setInt(6, newRoom.getMaximumOccupancy());
                preparedStatement.setBigDecimal(7, newRoom.getRoomRate());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Room added to the database successfully. RoomId: " + newRoom.getRoomId());
                } else {
                    System.out.println("No rows affected. Room not added to the database.");
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


    private static void clearInputFields(TextField roomIdTextField, ComboBox<String> roomTypeComboBox,
                                         List<CheckBox> amenityCheckBoxes,
                                         TextField maxOccupancyTextField, TextField roomRateTextField) {
        roomIdTextField.clear();
        roomTypeComboBox.setValue(null);
        amenityCheckBoxes.forEach(checkBox -> checkBox.setSelected(false));
        maxOccupancyTextField.clear();
        roomRateTextField.clear();
    }
}
