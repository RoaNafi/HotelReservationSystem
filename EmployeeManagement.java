package com.example.fx1;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.function.Predicate;

public class EmployeeManagement extends Application {
    private TableView<Employee> employeeTable = new TableView<>();
    private ObservableList<Employee> employeeData = FXCollections.observableArrayList();
    private TextField searchField;
    private ChoiceBox<String> searchCriteriaBox;

    private FilteredList<Employee> filteredEmployeeData = new FilteredList<>(employeeData);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Employee Management");

        // Main layout container
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));

        // Initialize the TableView with data
        initializeEmployeeTable();

        // Create search bar
        HBox searchBar = createSearchBar();

        // Test the database connection and fetch data
        testDatabaseConnection();

        // Enable sorting and searching
        enableSorting();
        enableSearching();

        // Add everything to the main layout
        mainLayout.getChildren().addAll(searchBar, employeeTable);

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
        searchCriteriaBox.getItems().addAll("All", "Employee ID", "Name", "Phone", "Email", "Address", "Salary");
        searchCriteriaBox.setValue("All");

        // Add a listener to update the filtered list based on search criteria
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilteredEmployees());

        searchCriteriaBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateFilteredEmployees());

        // Create and return the search bar layout
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.getChildren().addAll(searchField, searchCriteriaBox);
        return searchBar;
    }

    private void updateFilteredEmployees() {
        String searchCriteria = searchCriteriaBox.getValue();
        String keyword = searchField.getText().trim().toLowerCase();

        // Create a new Predicate for the FilteredList
        Predicate<Employee> predicate = employee -> {
            if (keyword.isEmpty() || "All".equals(searchCriteria)) {
                return true; // No filtering
            }

            try {
                Connection connection = new Connector().getConnection();
                String query = "";

                switch (searchCriteria) {
                    case "Employee ID":
                        query = "SELECT * FROM Employee WHERE EmployeeId = ?";
                        break;
                    case "Name":
                        query = "SELECT * FROM Employee WHERE Name LIKE ?";
                        break;
                    case "Phone":
                        query = "SELECT * FROM Employee WHERE Phone LIKE ?";
                        break;
                    case "Email":
                        query = "SELECT * FROM Employee WHERE Email LIKE ?";
                        break;
                    case "Address":
                        query = "SELECT * FROM Employee WHERE Address LIKE ?";
                        break;
                    case "Salary":
                        query = "SELECT * FROM Employee WHERE Salary = ?";
                        break;
                }

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, keyword);

                ResultSet resultSet = preparedStatement.executeQuery();

                return resultSet.next(); // If there is a match, return true

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        };

        // Apply the new predicate to the FilteredList
        filteredEmployeeData.setPredicate(predicate);
    }
    private void enableSorting() {
        // Allow sorting by column
        employeeTable.getSortOrder().addListener((ListChangeListener<TableColumn<Employee, ?>>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    applySorting();
                }
            }
        });
    }

    private void applySorting() {
        // Get the current sorting order
        StringBuilder sortOrder = new StringBuilder();
        employeeTable.getSortOrder().forEach(column -> {
            if (sortOrder.length() != 0) sortOrder.append(", ");
            sortOrder.append(column.getText());
        });

        // Create a SortedList from the sorted SQL data
        SortedList<Employee> sortedData = new SortedList<>(fetchSortedData(sortOrder.toString()));

        // Bind the sorted data to the table
        employeeTable.setItems(sortedData);
    }

    private ObservableList<Employee> fetchSortedData(String sortOrder) {
        ObservableList<Employee> sortedData = FXCollections.observableArrayList();

        try {
            Connection connection = new Connector().getConnection();
            String query = "SELECT * FROM Employee ORDER BY " + sortOrder;
            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.setEmployeeId(resultSet.getInt("EmployeeId"));
                employee.setName(resultSet.getString("Name"));
                employee.setPhone(resultSet.getString("Phone"));
                employee.setEmail(resultSet.getString("Email"));
                employee.setAddress(resultSet.getString("Address"));
                employee.setSalary(resultSet.getBigDecimal("Salary"));

                sortedData.add(employee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sortedData;
    }
    private void testDatabaseConnection() {
        Connector connector = new Connector();
        Connection connection = connector.getConnection();
        if (connection != null) {
            System.out.println("Connection successful!");
            fetchEmployeeData(connection);
        } else {
            System.out.println("Connection failed!");
        }
    }

    private void fetchEmployeeData(Connection connection) {
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM Employee");
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setEmployeeId(rs.getInt("EmployeeId"));
                employee.setName(rs.getString("Name"));
                employee.setPhone(rs.getString("Phone"));
                employee.setEmail(rs.getString("Email"));
                employee.setAddress(rs.getString("Address"));
                employee.setSalary(rs.getBigDecimal("Salary"));

                employeeData.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeEmployeeTable() {
        // Setting up columns for the table
        TableColumn<Employee, Integer> employeeIdColumn = new TableColumn<>("Employee ID");
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Employee, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Employee, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Employee, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Employee, BigDecimal> salaryColumn = new TableColumn<>("Salary");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

        // Add columns to the table
        employeeTable.getColumns().addAll(employeeIdColumn, nameColumn, phoneColumn,
                emailColumn, addressColumn, salaryColumn);

        // Set data to the table
        employeeTable.setItems(employeeData);

        // Enable sorting
        employeeTable.getSortOrder().add(employeeIdColumn);
        employeeTable.sort();
    }

    private void enableSearching() {
        // Create a FilteredList to hold the filtered employees
        FilteredList<Employee> filteredEmployees = new FilteredList<>(employeeData, p -> true);

        // Add a listener to the search field to update the filtered list
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredEmployees.setPredicate(employee -> employee.matchesKeyword(newValue.trim())));

        // Bind the filtered list to the table
        SortedList<Employee> sortedData = new SortedList<>(filteredEmployees);
        sortedData.comparatorProperty().bind(employeeTable.comparatorProperty());
        employeeTable.setItems(sortedData);
    }

    public static void main(String[] args) {
        launch(args);
    }
}