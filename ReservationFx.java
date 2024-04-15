package com.example.fx1;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.math.BigDecimal;
import java.time.LocalDate;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Collections;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

import java.sql.Timestamp;
import java.util.stream.Collectors;


public class ReservationFx extends Application {

    private int selectedCustomerId;

    private TextField employeeIdTextField;
    private DatePicker checkInDatePicker;
    private DatePicker checkOutDatePicker;
    private ComboBox<String> roomTypeComboBox;
    private TextField maxOccupancyTextField;
    private TextField roomRateTextField;
    private Button searchButton;
    private VBox roomButtonsVBox;

    private TextField nameTextField;
    private TextField dobTextField;
    private TextField phoneTextField;
    private TextField addressTextField;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Room Reservation");

        GridPane grid = createGrid();
        TextField idTextField = createCustomerIdTextField(); // This replaces the original line
        nameTextField = createTextField("Name:");
        dobTextField = createTextField("Date of Birth:");
        phoneTextField = createTextField("Phone:");
        addressTextField = createTextField("Address:");

        Button saveCustomerButton = new Button("Save Customer");
        saveCustomerButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white;"); // Brown color

        roomButtonsVBox = new VBox(10); // Vertical box for room buttons


        checkInDatePicker = new DatePicker();
        checkOutDatePicker = new DatePicker();
        employeeIdTextField = createTextField("Employee ID:");

        checkInDatePicker = new DatePicker();
        checkInDatePicker.setValue(LocalDate.now()); // Set the current date
        checkInDatePicker.setDisable(true); // Disable the DatePicker to prevent changes

        grid.add(new Label("Check-in Date:"), 0, 7);
        grid.add(checkInDatePicker, 1, 7);
        grid.add(new Label("Check-out Date:"), 0, 8);
        grid.add(checkOutDatePicker, 1, 8);
        grid.add(new Label("Employee ID:"), 0, 9);
        grid.add(employeeIdTextField, 1, 9);
        createSearchComponents(grid);

        // Populate the roomButtonsVBox with buttons for each available room
        List<Room> availableRooms = getAvailableRoomsFromDatabase();
        for (Room room : availableRooms) {
            VBox roomButtonContainer = createRoomButton(room, nameTextField, dobTextField, phoneTextField, addressTextField);
            roomButtonsVBox.getChildren().add(roomButtonContainer);
        }

        // Add components to the grid...
        grid.add(new Label("Customer Information:"), 0, 0);
        grid.add(new Label("Customer ID:"), 0, 1);
        grid.add(new Label("Name:"), 0, 2);
        grid.add(new Label("Date of Birth:"), 0, 3);
        grid.add(new Label("Phone:"), 0, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(idTextField, 1, 1);
        grid.add(nameTextField, 1, 2);
        grid.add(dobTextField, 1, 3);
        grid.add(phoneTextField, 1, 4);
        grid.add(addressTextField, 1, 5);
        grid.add(saveCustomerButton, 1, 6);


        grid.add(new Label("Available Rooms:"), 2, 0);
        grid.add(roomButtonsVBox, 2, 1, 1, 6);

        Scene scene = new Scene(grid, 1100, 790);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Set up action for the Save Customer button
        saveCustomerButton.setOnAction(e -> {
            int customerId;
            try {
                customerId = Integer.parseInt(idTextField.getText());
            } catch (NumberFormatException ex) {
                showAlert("Invalid Customer ID", "Please enter a valid Customer ID.");
                return;
            }
            String name = nameTextField.getText();
            LocalDate dob;
            try {
                dob = LocalDate.parse(dobTextField.getText());
            } catch (Exception ex) {
                showAlert("Invalid Date of Birth", "Please enter a valid Date of Birth (yyyy-MM-dd).");
                return;
            }
            String phone = phoneTextField.getText();
            String address = addressTextField.getText();

            // Create a new Customer object
            Customer newCustomer = new Customer(customerId, name, dob, phone, address);

            // Save the customer to the database
            saveCustomerToDatabase(newCustomer);

            // Get the selected customer ID
            selectedCustomerId = getCustomerIdFromDatabase(name, dob, phone, address);

            // Check if a customer is selected
            if (selectedCustomerId == 0) {
                showAlert("No Customer Selected", "Please save a customer before reserving a room.");
                return;
            }


        });
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        // Set background color
        grid.setStyle("-fx-background-color: #F5F5DC;"); // Beige color

        // Add image
        Image image = new Image("/re.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(250); // Set the height of the image
        imageView.setFitWidth(250); // Set the width of the image

        grid.add(imageView, 4, 0, 1, 5); // Add image to a lower position in the grid, spanning 5 rows


        return grid;
    }

    private boolean isEmployeeIdValid(int employeeId) {
        Connector connector = new Connector();
        try (Connection connection = connector.getConnection()) {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot check employee ID.");
                return false;
            }

            String sql = "SELECT * FROM Employee WHERE employeeId = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employeeId);

                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next(); // Return true if employee with the ID is found
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors appropriately
            return false;
        }
    }

    private LocalDateTime calculateReservationDateTime(LocalDate checkInDate, LocalDate checkOutDate) {
        // Assume a simple calculation: reservation date-time is the current date and time
        return LocalDateTime.now();
    }

    private TextField createTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        return textField;
    }

    private TextField createCustomerIdTextField() {
        TextField customerIdTextField = new TextField();
        customerIdTextField.setPromptText("Customer ID:");

        // Add a listener to the text property
        customerIdTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                clearCustomerFields();
            } else {
                try {
                    int customerId = Integer.parseInt(newValue.trim());
                    Customer customer = getCustomerInfo(customerId);
                    if (customer != null) {
                        populateCustomerFields(customer);
                    } else {
                        clearCustomerFields();
                    }
                } catch (NumberFormatException e) {
                    clearCustomerFields();
                }
            }
        });

        return customerIdTextField;
    }

    private void clearCustomerFields() {
        nameTextField.clear();
        dobTextField.clear();
        phoneTextField.clear();
        addressTextField.clear();
    }

    private Customer getCustomerInfo(int customerId) {
        Connector connector = new Connector();
        try (Connection connection = connector.getConnection()) {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot retrieve customer info.");
                return null;
            }

            String sql = "SELECT * FROM Customer WHERE customerId = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, customerId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int id = resultSet.getInt("customerId");
                    String name = resultSet.getString("name");
                    LocalDate dob = resultSet.getDate("dateOfBirth").toLocalDate();
                    String phone = resultSet.getString("phone");
                    String address = resultSet.getString("address");

                    return new Customer(id, name, dob, phone, address);
                } else {
                    System.out.println("No customer found with ID: " + customerId);
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors appropriately
            return null;
        }
    }

    private void populateCustomerFields(Customer customer) {
        if (customer != null) {
            nameTextField.setText(customer.getName());
            dobTextField.setText(customer.getDateOfBirth().toString());
            phoneTextField.setText(customer.getPhone());
            addressTextField.setText(customer.getAddress());
        }
    }

    private void saveCustomerToDatabase(Customer customer) {
        Connector connector = new Connector();
        try (Connection connection = connector.getConnection()) {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot save customer.");
                return;
            }

            // Call updateAge to calculate the age based on dateOfBirth
            customer.updateAge();

            String sql = "INSERT INTO Customer (customerId, name, dateOfBirth, age, phone, address) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, customer.getCustomerId());
                preparedStatement.setString(2, customer.getName());

                // Handling LocalDate for dateOfBirth
                if (customer.getDateOfBirth() != null) {
                    preparedStatement.setDate(3, java.sql.Date.valueOf(customer.getDateOfBirth()));
                } else {
                    preparedStatement.setNull(3, Types.DATE);
                }

                preparedStatement.setInt(4, customer.getAge());  // Add age to the query
                preparedStatement.setString(5, customer.getPhone());
                preparedStatement.setString(6, customer.getAddress());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Customer Saved", "Customer information saved to the database successfully.");
                } else {
                    showAlert("Error", "Failed to save customer information to the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors appropriately
            showAlert("Error", "Failed to save customer information to the database. Check the console for details.");
        }
    }

    private int getCustomerIdFromDatabase(String customerName, LocalDate dateOfBirth, String phone, String address) {
        Connector connector = new Connector();
        try (Connection connection = connector.getConnection()) {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot get customer ID.");
                return 0;
            }

            String sql = "SELECT customerId FROM Customer WHERE name = ? AND dateOfBirth = ? AND phone = ? AND address = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, customerName);
                preparedStatement.setDate(2, java.sql.Date.valueOf(dateOfBirth));

                preparedStatement.setString(3, phone);
                preparedStatement.setString(4, address);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("customerId");
                } else {
                    showAlert("Customer Not Found", "Customer with the provided information not found.");
                    return 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


    private VBox createRoomButton(Room room, TextField nameTextField, TextField dobTextField, TextField phoneTextField, TextField addressTextField) {
        VBox buttonContainer = new VBox(5);

        Label roomInfoLabel = new Label("Room " + room.getRoomId() + " - " + room.getRoomType() + ": $" + room.getRoomRate());
        Button reserveButton = new Button("Reserve this Room");
        reserveButton.setOnAction(e -> {
            String customerName = nameTextField.getText();
            BigDecimal billAmount = room.getRoomRate();
            LocalDate dob;
            try {
                dob = LocalDate.parse(dobTextField.getText());
            } catch (Exception ex) {
                showAlert("Invalid Date of Birth", "Please enter a valid Date of Birth (yyyy-MM-dd).");
                return;
            }
            String phone = phoneTextField.getText();
            String address = addressTextField.getText();

            int selectedCustomerId = getCustomerIdFromDatabase(customerName, dob, phone, address);
            if (selectedCustomerId == 0) {
                showAlert("No Customer Selected", "Please save a customer before reserving a room.");
                return;
            }

            int selectedRoomId = room.getRoomId();

            int employeeId;
            try {
                employeeId = Integer.parseInt(employeeIdTextField.getText());
            } catch (NumberFormatException ex) {
                showAlert("Invalid Employee ID", "Please enter a valid Employee ID.");
                return;
            }

            if (!isEmployeeIdValid(employeeId)) {
                showAlert("Invalid Employee ID", "Employee with the provided ID not found.");
                return;
            }

            showBillScreen(selectedCustomerId, customerName, LocalDate.now(), billAmount, selectedRoomId, employeeId);
        });

        buttonContainer.getChildren().addAll(roomInfoLabel, reserveButton);
        return buttonContainer;
    }


    private void showBillScreen(int customerId, String customerName, LocalDate billDate, BigDecimal billAmount, int selectedRoomId, int employeeId) {
        Stage billStage = new Stage();
        billStage.setTitle("Bill Details");

        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background-color: #F5F5DC;");

        Image image = new Image("/pay.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(250);
        imageView.setFitWidth(250);
        mainContainer.getChildren().add(imageView);

        GridPane billGrid = new GridPane();
        billGrid.setAlignment(Pos.CENTER);
        billGrid.setHgap(10);
        billGrid.setVgap(10);
        billGrid.setPadding(new Insets(10, 10, 10, 10));
        mainContainer.getChildren().add(billGrid);

        Label billIdLabel = new Label("Bill ID:");
        TextField billIdTextField = new TextField();
        billIdTextField.setEditable(false);
        Label customerNameLabel = new Label("Customer Name:");
        TextField customerNameTextField = new TextField(customerName);
        customerNameTextField.setEditable(false);
        Label billDateLabel = new Label("Bill Date:");
        DatePicker billDatePicker = new DatePicker(billDate);
        billDatePicker.setEditable(false);
        Label billAmountLabel = new Label("Bill Amount:");
        TextField billAmountTextField = new TextField(billAmount.toString());
        billAmountTextField.setEditable(false);

        Label paymentMethodLabel = new Label("Payment Method:");
        ToggleGroup paymentMethodGroup = new ToggleGroup();
        RadioButton cashRadioButton = new RadioButton("Cash");
        cashRadioButton.setToggleGroup(paymentMethodGroup);
        RadioButton creditCardRadioButton = new RadioButton("Credit Card");
        creditCardRadioButton.setToggleGroup(paymentMethodGroup);
        RadioButton paypalRadioButton = new RadioButton("PayPal");
        paypalRadioButton.setToggleGroup(paymentMethodGroup);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String selectedPaymentMethod = ((RadioButton)paymentMethodGroup.getSelectedToggle()).getText();

            Bill bill = new Bill();
            bill.setCustomerId(customerId);
            bill.setBillDate(billDatePicker.getValue());
            bill.setBillAmount(new BigDecimal(billAmountTextField.getText()));
            bill.setPaymentMethod(selectedPaymentMethod);
            Bill savedBill = saveBillToDatabase(bill);

            if (savedBill != null) {
                showAlert("Success", "Bill saved successfully. Bill ID: " + savedBill.getBillId());

                Reservation reservation = new Reservation();
                reservation.setCustomerId(customerId);
                reservation.setRoomId(selectedRoomId);
                reservation.setEmployeeId(employeeId);
                reservation.setCheckInDate(checkInDatePicker.getValue());
                reservation.setCheckOutDate(checkOutDatePicker.getValue());
                reservation.setReservationDateTime(LocalDateTime.now());
                saveReservationToDatabase(reservation);

                // Update room availability after successfully saving the reservation
                updateRoomAvailability(selectedRoomId, false); // Set room availability to false (not available)

            } else {
                showAlert("Error", "There was an error saving the bill.");
            }

            billStage.close();
        });

        billGrid.add(billIdLabel, 0, 0);
        billGrid.add(billIdTextField, 1, 0);
        billGrid.add(customerNameLabel, 0, 1);
        billGrid.add(customerNameTextField, 1, 1);
        billGrid.add(billDateLabel, 0, 2);
        billGrid.add(billDatePicker, 1, 2);
        billGrid.add(billAmountLabel, 0, 3);
        billGrid.add(billAmountTextField, 1, 3);
        billGrid.add(paymentMethodLabel, 0, 4);
        billGrid.add(cashRadioButton, 1, 4);
        billGrid.add(creditCardRadioButton, 1, 5);
        billGrid.add(paypalRadioButton, 1, 6);
        billGrid.add(submitButton, 1, 7);

        Scene scene = new Scene(mainContainer, 500, 600);
        billStage.setScene(scene);
        billStage.show();
    }



    private Bill saveBillToDatabase(Bill bill) {
        Connector connector = new Connector();
        try (Connection connection = connector.getConnection()) {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot save bill.");
                return null;
            }

            // Include paymentMethod in the SQL query
            String sql = "INSERT INTO Bill (customerId, billDate, billAmount, paymentMethod) VALUES (?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                // Set parameters for customerId, billDate, billAmount, and paymentMethod
                preparedStatement.setInt(1, bill.getCustomerId());
                preparedStatement.setDate(2, Date.valueOf(bill.getBillDate()));
                preparedStatement.setBigDecimal(3, bill.getBillAmount());
                preparedStatement.setString(4, bill.getPaymentMethod());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    // Retrieve the generated billId
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int generatedBillId = generatedKeys.getInt(1);
                        bill.setBillId(generatedBillId);
                        return bill; // Return the updated Bill object
                    } else {
                        showAlert("Error", "Failed to retrieve generated billId from the database.");
                    }
                } else {
                    showAlert("Error", "Failed to save bill information to the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors appropriately
        }

        return null; // Return null in case of an error
    }

    private void updateRoomAvailability(int roomId, boolean isAvailable) {
        Connector connector = new Connector();
        try (Connection connection = connector.getConnection()) {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot update room availability.");
                return;
            }

            String sql = "UPDATE Room SET isAvailable = ? WHERE roomId = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setBoolean(1, isAvailable);
                preparedStatement.setInt(2, roomId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected <= 0) {
                    showAlert("Error", "Failed to update room availability in the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors appropriately
        }
    }

    private List<Room> getAvailableRoomsFromDatabase() {
        List<Room> availableRooms = new ArrayList<>();

        Connector connector = new Connector();
        try (Connection connection = connector.getConnection()) {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot retrieve available rooms.");
                return availableRooms;
            }

            String sql = "SELECT * FROM Room WHERE isAvailable = true";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    Room room = new Room();
                    room.setRoomId(resultSet.getInt("roomId"));
                    room.setAvailable(resultSet.getBoolean("isAvailable"));
                    room.setRoomType(resultSet.getString("roomType"));
                    room.setRoomAmenities(getAmenitiesFromResultSet(resultSet, "roomAmenities"));
                    room.setFloorNumber(resultSet.getInt("floorNumber"));
                    room.setMaximumOccupancy(resultSet.getInt("maximumOccupancy"));
                    room.setRoomRate(resultSet.getBigDecimal("roomRate"));

                    availableRooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors appropriately
        }

        return availableRooms;
    }

    private List<String> getAmenitiesFromResultSet(ResultSet resultSet, String columnName) throws SQLException {
        String amenitiesString = resultSet.getString(columnName);
        if (amenitiesString != null && !amenitiesString.isEmpty()) {
            return Arrays.asList(amenitiesString.split(","));
        } else {
            return Collections.emptyList();
        }
    }

    private void saveReservationToDatabase(Reservation reservation) {
        Connector connector = new Connector();
        try (Connection connection = connector.getConnection()) {
            if (connection == null) {
                System.out.println("Connection to the database is null. Cannot save reservation.");
                return;
            }

            String sql = "INSERT INTO Reservation (customerId, roomId, employeeId, checkInDate, checkOutDate, reservationDateTime) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, reservation.getCustomerId());
                preparedStatement.setInt(2, reservation.getRoomId());
                preparedStatement.setInt(3, reservation.getEmployeeId());
                preparedStatement.setDate(4, Date.valueOf(reservation.getCheckInDate()));
                preparedStatement.setDate(5, Date.valueOf(reservation.getCheckOutDate()));
                preparedStatement.setTimestamp(6, Timestamp.valueOf(reservation.getReservationDateTime()));

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Reservation Saved", "Reservation information saved to the database successfully.");
                } else {
                    showAlert("Error", "Failed to save reservation information to the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors appropriately
        }
    }

    public static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }



    // Add this method to create search components
    private void createSearchComponents(GridPane grid) {
        roomTypeComboBox = new ComboBox<>();
        roomTypeComboBox.getItems().addAll("Single Rooms", "Twin or Double Rooms", "Studio Rooms",
                "Deluxe Rooms", "Rooms with a View", "Suites", "Presidential Suites"); // Add your room types

        maxOccupancyTextField = createTextField("Max Occupancy:");
        roomRateTextField = createTextField("Room Rate:");
        searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #008000; -fx-text-fill: white;"); // Green color

        // Add components to the grid
        grid.add(new Label("Room Type:"), 2, 7);
        grid.add(roomTypeComboBox, 3, 7);
        grid.add(new Label("Max Occupancy:"), 2, 8);
        grid.add(maxOccupancyTextField, 3, 8);
        grid.add(new Label("Room Rate:"), 2, 9);
        grid.add(roomRateTextField, 3, 9);
        grid.add(searchButton, 3, 10);

        // Add the "Reset" button
        Button resetButton = new Button("Reset");
        resetButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;"); // Red color
        grid.add(resetButton, 4, 10);

        // Set up action for the Reset button
        resetButton.setOnAction(e -> {
            // Clear search criteria
            roomTypeComboBox.setValue(null);
            maxOccupancyTextField.clear();
            roomRateTextField.clear();

            // Reload all available rooms
            List<Room> availableRooms = getAvailableRoomsFromDatabase();
            roomButtonsVBox.getChildren().clear();
            for (Room room : availableRooms) {
                VBox roomButtonContainer = createRoomButton(room, nameTextField, dobTextField, phoneTextField, addressTextField);
                roomButtonsVBox.getChildren().add(roomButtonContainer);
            }
        });


        // Set up action for the Search button
        searchButton.setOnAction(e -> {
            String selectedRoomType = roomTypeComboBox.getValue();
            int maxOccupancy;


            try {
                maxOccupancy = Integer.parseInt(maxOccupancyTextField.getText());
            } catch (NumberFormatException ex) {
                showAlert("Invalid Max Occupancy", "Please enter a valid Max Occupancy.");
                return;
            }

            BigDecimal roomRate; // Corrected variable name
            try {
                roomRate = new BigDecimal(roomRateTextField.getText()); // Corrected variable name
            } catch (NumberFormatException ex) {
                showAlert("Invalid Room Rate", "Please enter a valid Room Rate.");
                return;
            }

            // Get available rooms from the database
            List<Room> availableRooms = getAvailableRoomsFromDatabase();

            // Filter rooms based on search criteria
            List<Room> filteredRooms = availableRooms.stream()
                    .filter(room -> (selectedRoomType == null || room.getRoomType().equals(selectedRoomType))
                            && (maxOccupancyTextField.getText().isEmpty() || room.getMaximumOccupancy() >= maxOccupancy)
                            && (roomRateTextField.getText().isEmpty() || room.getRoomRate().compareTo(roomRate) >= 0))
                    .collect(Collectors.toList());

            // Update the roomButtonsVBox with the filtered rooms
            roomButtonsVBox.getChildren().clear();
            for (Room room : filteredRooms) {
                VBox roomButtonContainer = createRoomButton(room, nameTextField, dobTextField, phoneTextField, addressTextField);
                roomButtonsVBox.getChildren().add(roomButtonContainer);
            }
        });
    }
}