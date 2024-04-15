package com.example.fx1;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.function.Predicate;

public class BillManagement extends Application {
    private TableView<Bill> billTable = new TableView<>();
    private ObservableList<Bill> billData = FXCollections.observableArrayList();
    private TextField searchField;
    private ChoiceBox<String> searchCriteriaBox;

    private FilteredList<Bill> filteredBillData = new FilteredList<>(billData, p -> true);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bill Management");

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));

        initializeBillTable();
        HBox searchBar = createSearchBar();

        testDatabaseConnection();

        SortedList<Bill> sortedData = new SortedList<>(filteredBillData);
        sortedData.comparatorProperty().bind(billTable.comparatorProperty());
        billTable.setItems(sortedData);

        mainLayout.getChildren().addAll(searchBar, billTable);

        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createSearchBar() {
        searchField = new TextField();
        searchField.setPromptText("Search...");
        searchCriteriaBox = new ChoiceBox<>();
        searchCriteriaBox.getItems().addAll("All", "Customer ID", "Bill Date", "Payment Method", "Bill Amount");
        searchCriteriaBox.setValue("All");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilteredBills());
        searchCriteriaBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateFilteredBills());

        HBox searchBar = new HBox(10);
        searchBar.getChildren().addAll(searchField, searchCriteriaBox);
        return searchBar;
    }

    private void updateFilteredBills() {
        String searchCriteria = searchCriteriaBox.getValue();
        String keyword = searchField.getText().trim().toLowerCase();

        Predicate<Bill> predicate = bill -> {
            if (keyword.isEmpty() || "All".equals(searchCriteria)) {
                return true;
            }
            switch (searchCriteria) {
                case "Customer ID":
                    return String.valueOf(bill.getCustomerId()).contains(keyword);
                case "Bill Date":
                    return bill.getBillDate().toString().contains(keyword);
                case "Payment Method":
                    return bill.getPaymentMethod().toLowerCase().contains(keyword);
                case "Bill Amount":
                    return bill.getBillAmount().toString().contains(keyword);
                default:
                    return false;
            }
        };

        filteredBillData.setPredicate(predicate);
    }

    private void testDatabaseConnection() {
        Connector connector = new Connector(); // Replace with your actual database connector
        Connection connection = connector.getConnection();
        if (connection != null) {
            System.out.println("Connection successful!");
            fetchData(connection);
        } else {
            System.out.println("Connection failed!");
        }
    }

    // Inside BillManagement class

    private void initializeBillTable() {


        TableColumn<Bill, Integer> customerIdColumn = new TableColumn<>("Customer ID");
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<Bill, LocalDate> billDateColumn = new TableColumn<>("Bill Date");
        billDateColumn.setCellValueFactory(new PropertyValueFactory<>("billDate"));

        TableColumn<Bill, String> paymentMethodColumn = new TableColumn<>("Payment Method");
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

        TableColumn<Bill, BigDecimal> billAmountColumn = new TableColumn<>("Bill Amount");
        billAmountColumn.setCellValueFactory(new PropertyValueFactory<>("billAmount"));

        billTable.getColumns().addAll( customerIdColumn, billDateColumn, paymentMethodColumn, billAmountColumn);
    }

    private void fetchData(Connection connection) {
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM bill"); // Update as per your database
            while (rs.next()) {
                Bill bill = new Bill(
                        rs.getInt("billId"),
                        rs.getInt("customerId"),
                        rs.getDate("billDate").toLocalDate(),
                        rs.getString("paymentMethod"),
                        rs.getBigDecimal("billAmount")
                );
                billData.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}