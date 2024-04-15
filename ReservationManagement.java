package com.example.fx1;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.ListChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.function.Predicate;

public class ReservationManagement extends Application {
    private TableView<Reservation> reservationTable = new TableView<>();
    private ObservableList<Reservation> reservationData = FXCollections.observableArrayList();
    private TextField searchField;
    private ChoiceBox<String> searchCriteriaBox;

    private FilteredList<Reservation> filteredReservationData = new FilteredList<>(reservationData);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Reservation Management");

        // Main layout container
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));

        // Initialize the TableView with data
        initializeReservationTable();

        // Create search bar
        HBox searchBar = createSearchBar();

        // Test the database connection and fetch data
        testDatabaseConnection();

        // Enable sorting and searching
        enableSorting();
        enableSearching();

        // Add everything to the main layout
        mainLayout.getChildren().addAll(searchBar, reservationTable);

        // Scene setup
        Scene scene = new Scene(mainLayout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createSearchBar() {
        // Create search components
        searchField = new TextField();
        searchField.setPromptText("Search...");
        searchCriteriaBox = new ChoiceBox<>();
        searchCriteriaBox.getItems().addAll("All", "Room", "Employee", "Customer", "Check-In Date", "Check-Out Date");
        searchCriteriaBox.setValue("All");

        // Add a listener to update the filtered list based on search criteria
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilteredReservations());

        searchCriteriaBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateFilteredReservations());

        // Create and return the search bar layout
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.getChildren().addAll(searchField, searchCriteriaBox);
        return searchBar;
    }

    private void updateFilteredReservations() {
        String searchCriteria = searchCriteriaBox.getValue();
        String keyword = searchField.getText().trim().toLowerCase();

        // Create a new Predicate for the FilteredList
        Predicate<Reservation> predicate = reservation -> {
            if (keyword.isEmpty() || "All".equals(searchCriteria)) {
                return true; // No filtering
            }

            switch (searchCriteria) {
                case "Room":
                    return String.valueOf(reservation.getRoomId()).contains(keyword);
                case "Employee":
                    return String.valueOf(reservation.getEmployeeId()).contains(keyword);
                case "Customer":
                    return String.valueOf(reservation.getCustomerId()).contains(keyword);
                case "Check-In Date":
                    return reservation.getCheckInDate().toString().contains(keyword);
                case "Check-Out Date":
                    return reservation.getCheckOutDate().toString().contains(keyword);
                default:
                    return false;
            }
        };

        // Apply the new predicate to the FilteredList
        filteredReservationData.setPredicate(predicate);
    }

    private void enableSorting() {
        // Allow sorting by column
        reservationTable.getSortOrder().addListener((ListChangeListener<TableColumn<Reservation, ?>>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    applySorting();
                }
            }
        });
    }

    private void applySorting() {
        // Get the current sorting order
        Comparator<Reservation> comparator = (Comparator<Reservation>) reservationTable.getSortOrder()
                .stream()
                .map(TableColumn::getComparator)
                .reduce(Comparator::thenComparing)
                .orElse((r1, r2) -> 0);

        // Create a SortedList from the filtered data
        SortedList<Reservation> sortedData = new SortedList<>(filteredReservationData);

        // Set the comparator to the SortedList
        sortedData.setComparator(comparator);

        // Bind the sorted data to the table
        reservationTable.setItems(sortedData);
    }

    private void testDatabaseConnection() {
        Connector connector = new Connector();
        Connection connection = connector.getConnection();
        if (connection != null) {
            System.out.println("Connection successful!");
            fetchReservationData(connection);
        } else {
            System.out.println("Connection failed!");
        }
    }

    private void fetchReservationData(Connection connection) {
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM Reservation");
            while (rs.next()) {
                // Assuming you have the correct columns in your "Reservation" table
                Reservation reservation = new Reservation();
                reservation.setReservationId(rs.getInt("ReservationId"));
                reservation.setCustomerId(rs.getInt("CustomerId"));
                reservation.setRoomId(rs.getInt("RoomId"));
                reservation.setEmployeeId(rs.getInt("EmployeeId"));
                reservation.setCheckInDate(rs.getDate("CheckInDate").toLocalDate());
                reservation.setCheckOutDate(rs.getDate("CheckOutDate").toLocalDate());
                reservation.setReservationDateTime(rs.getTimestamp("ReservationDateTime").toLocalDateTime());

                reservationData.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeReservationTable() {
        // Setting up columns for the table
        TableColumn<Reservation, Integer> reservationIdColumn = new TableColumn<>("Reservation ID");
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));

        TableColumn<Reservation, Integer> customerIdColumn = new TableColumn<>("Customer ID");
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<Reservation, Integer> roomIdColumn = new TableColumn<>("Room ID");
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("roomId"));

        TableColumn<Reservation, Integer> employeeIdColumn = new TableColumn<>("Employee ID");
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        TableColumn<Reservation, LocalDate> checkInDateColumn = new TableColumn<>("Check-In Date");
        checkInDateColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));

        TableColumn<Reservation, LocalDate> checkOutDateColumn = new TableColumn<>("Check-Out Date");
        checkOutDateColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        // Add columns to the table
        reservationTable.getColumns().addAll(reservationIdColumn, customerIdColumn, roomIdColumn,
                employeeIdColumn, checkInDateColumn, checkOutDateColumn);

        // Set data to the table
        reservationTable.setItems(reservationData);

        // Enable sorting
        reservationTable.getSortOrder().add(reservationIdColumn);
        reservationTable.sort();
    }

    private void enableSearching() {
        // Create a FilteredList to hold the filtered reservations
        FilteredList<Reservation> filteredReservations = new FilteredList<>(reservationData, p -> true);

        // Add a listener to the search field to update the filtered list
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredReservations.setPredicate(reservation -> reservation.matchesKeyword(newValue.trim())));

        // Bind the filtered list to the table
        SortedList<Reservation> sortedData = new SortedList<>(filteredReservations);
        sortedData.comparatorProperty().bind(reservationTable.comparatorProperty());
        reservationTable.setItems(sortedData);
    }

    public static void main(String[] args) {
        launch(args);
    }
}