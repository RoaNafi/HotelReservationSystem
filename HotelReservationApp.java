package com.example.fx1;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HotelReservationApp extends Application {

    private AuthenticationService authService;
    private Stage primaryStage;  // Declare primaryStage as an instance variable


    //login
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.authService = new AuthenticationService(new Connector());

        primaryStage.setTitle("Hotel Reservation App");
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);

        // Load image from resources
        Image img = loadImage("/welcome2.jpg");
        if (img != null) {
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(primaryStage.getWidth());
            imageView.setPreserveRatio(true);
            root.getChildren().add(imageView);
        }

        // Welcome message
        Text welcomeText = new Text("Welcome to Our Hotel");
        welcomeText.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        welcomeText.setStyle("-fx-fill: white;");
        welcomeText.setTranslateY(-250);

        // Login VBox
        VBox loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);

        // User ID TextField
        TextField userIdField = new TextField();
        userIdField.setPromptText("User ID");
        userIdField.setMaxWidth(200);

        // Password Field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(200);


        // Login Button
        Button startNowButton = new Button("LOGIN");
        startNowButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white;");
        startNowButton.setPrefSize(150, 50);
        startNowButton.setOnAction(e -> handleLogin(userIdField.getText(), passwordField.getText()));

        // Add components to login VBox
        loginBox.getChildren().addAll(userIdField, passwordField, startNowButton);
        loginBox.setTranslateY(100); // Adjust this value as needed

        // Add all components to root StackPane
        root.getChildren().addAll(welcomeText, loginBox);

        Scene scene = new Scene(root, 700, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void changeScene(Scene scene) {
        primaryStage.setScene(scene);
    }

    private Scene login() {
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);

        // Load image from resources
        Image img = loadImage("/welcome2.jpg");
        if (img != null) {
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(700); // Adjust this value as needed
            imageView.setPreserveRatio(true);
            root.getChildren().add(imageView);
        }

        // Welcome message
        Text welcomeText = new Text("Welcome to Our Hotel");
        welcomeText.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        welcomeText.setStyle("-fx-fill: white;");
        welcomeText.setTranslateY(-250);

        // Login VBox
        VBox loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);

        // User ID TextField
        TextField userIdField = new TextField();
        userIdField.setPromptText("User ID");
        userIdField.setMaxWidth(200); // Set the max width for the text field

        // Password Field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(200); // Set the max width for the text field

        // Login Button
        Button startNowButton = new Button("LOGIN");
        startNowButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white;");
        startNowButton.setPrefSize(200, 40); // Match the width of the text fields and set a fixed height
        startNowButton.setOnAction(e -> handleLogin(userIdField.getText(), passwordField.getText()));

        // Add components to login VBox
        loginBox.getChildren().addAll(userIdField, passwordField, startNowButton);
        loginBox.setTranslateY(100); // Adjust this value as needed

        // Add all components to root StackPane
        root.getChildren().addAll(welcomeText, loginBox);

        // Create the scene with the same dimensions as the stage
        Scene scene = new Scene(root, 700, 700);
        return scene;
    }


    private void handleLogin(String userId, String password) {
        if (authService.authenticate(userId, password)) {
            String role = authService.getUserRole(userId);
            if ("Manager".equals(role)) {
                changeScene(createManagerUI());
            } else if ("Employee".equals(role)) {
                changeScene(createEmployeeUI());
            }
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }


    //ManagerUI
    private Scene createManagerUI() {

        // Create a VBox to hold the title and GridPane
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);

        // Set up the root pane and background image
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10); // Horizontal gap between columns
        gridPane.setVgap(10); // Vertical gap between rows

        // Load and set the background image
        Image backgroundImage = loadImage("/welcome2.jpg"); // Replace with your actual image path
        BackgroundImage bgImage = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        vbox.setBackground(new Background(bgImage));

        // Define a uniform button size
        int buttonWidth = 150;
        int buttonHeight = 40;

        // Adding title and subtitle
        Label titleLabel = new Label("R.S Hotel");
        titleLabel.setFont(new Font("Arial", 36)); // Set font size and family for the title
        titleLabel.setTextFill(Color.WHITE); // Set text color to white if needed
        Label helloLabel = new Label("Hello Manager");
        helloLabel.setFont(new Font("Arial", 18)); // Smaller font size for the subtitle
        helloLabel.setTextFill(Color.WHITE); // Set text color to white if needed

        vbox.getChildren().addAll(titleLabel, helloLabel);

        // First column of buttons
        Button reservationManagementButton = createButton("Reservations", buttonWidth, buttonHeight);
        Button billsButton = createButton("Bills", buttonWidth, buttonHeight);
        Button customerManagementButton = createButton("Customer Management", buttonWidth, buttonHeight);

        // Second column of buttons
        Button roomManagementButton = createButton("Rooms", buttonWidth, buttonHeight);
        Button addRoomButton = createButton("Add Rooms", buttonWidth, buttonHeight);
        Button deleteRoomButton = createButton("Remove Rooms", buttonWidth, buttonHeight);
        Button updateRoomButton = createButton("Update Rooms", buttonWidth, buttonHeight);

        // Third column of buttons
        Button employeeButton = createButton("Employee", buttonWidth, buttonHeight);
        Button addEmployeeButton = createButton("Add employee", buttonWidth, buttonHeight);
        Button deleteEmployeeButton = createButton("Remove employee", buttonWidth, buttonHeight);
        Button updateEmployeeButton = createButton("Update employee", buttonWidth, buttonHeight);

        // Add buttons to the grid
        gridPane.add(reservationManagementButton, 0, 0);
        gridPane.add(billsButton, 0, 1);
        gridPane.add(customerManagementButton, 0, 2);
        gridPane.add(roomManagementButton, 1, 0);
        gridPane.add(addRoomButton, 1, 1);
        gridPane.add(deleteRoomButton, 1, 2);
        gridPane.add(updateRoomButton, 1, 3);
        gridPane.add(employeeButton, 2, 0);
        gridPane.add(addEmployeeButton, 2, 1);
        gridPane.add(deleteEmployeeButton, 2, 2);
        gridPane.add(updateEmployeeButton, 2, 3);


        // Logout button at the bottom
        Button logoutButton = createButton("Logout", buttonWidth, buttonHeight);
        HBox logoutBox = new HBox(); // Use an HBox for centering and padding
        logoutBox.setAlignment(Pos.BOTTOM_CENTER);
        logoutBox.getChildren().add(logoutButton);
        gridPane.add(logoutBox, 0, 4, 3, 1); // Span 3 columns

        // Set column widths proportionally
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(33);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(33);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(33);
        gridPane.getColumnConstraints().addAll(column1, column2, column3);

        // Configure the logout button's action
        logoutButton.setOnAction(e -> {
            Scene startScene = login();
            primaryStage.setScene(startScene);
            primaryStage.show();
        });

        RoomManagement roomManagement = new RoomManagement();
        AddRoom addRoom = new AddRoom();
        UpdateRoom updateRoom = new UpdateRoom();
        DeleteRoom deleteRoom = new DeleteRoom();
        ReservationManagement reservationManagement = new ReservationManagement();
        CustomerManagement customerManagement = new CustomerManagement();
        UpdateEmployee updateEmployee = new UpdateEmployee();
        DeleteEmployee deleteEmployee = new DeleteEmployee();
        EmployeeManagement employeeManamement = new EmployeeManagement();
        AddEmployee addEmployee = new AddEmployee();
        BillManagement billManagement=new BillManagement();


        customerManagementButton.setOnAction(e -> {
            // Call the methods or perform actions related to customer management
            System.out.println("Customer Management button pressed");
            customerManagement.start(new Stage());
        });

        roomManagementButton.setOnAction(e -> {
            // Perform the desired action when "Room Management" button is pressed
            System.out.println("Room Management button pressed");
            // Display the RoomManagement UI
            roomManagement.start(new Stage());
        });

        employeeButton.setOnAction(e -> {
            // Perform the desired action when "Room Management" button is pressed
            System.out.println("Room Management button pressed");
            // Display the RoomManagement UI
            employeeManamement.start(new Stage());
        });
        addRoomButton.setOnAction(e -> {
            // Perform the desired action when "Add Room" button is pressed
            System.out.println("Add Room button pressed");
            // Display the AddRoom UI
            addRoom.start(new Stage());
        });
        addEmployeeButton.setOnAction(e -> {
            // Perform the desired action when "Add Room" button is pressed
            System.out.println("Add Room button pressed");
            // Display the AddRoom UI
            addEmployee.start(new Stage());
        });

        updateRoomButton.setOnAction(e -> {
            // Perform the desired action when "Add Room" button is pressed
            System.out.println("Updata Room button pressed");
            // Display the AddRoom UI
            updateRoom.start(new Stage());
        });
        deleteRoomButton.setOnAction(e -> {
            // Perform the desired action when "Delete Room" button is pressed
            System.out.println("Delete Room button pressed");
            // Display the DeleteRoom UI
            deleteRoom.start(new Stage());
        });
        reservationManagementButton.setOnAction(e -> {
            // Perform the desired action when "Delete Room" button is pressed
            System.out.println("Delete Room button pressed");
            // Display the DeleteRoom UI
            reservationManagement.start(new Stage());
        });
        updateEmployeeButton.setOnAction(e -> {
            // Perform the desired action when "Add Room" button is pressed
            System.out.println("Updata Room button pressed");
            // Display the AddRoom UI
            updateEmployee.start(new Stage());
        });
        deleteEmployeeButton.setOnAction(e -> {
            // Perform the desired action when "Delete Room" button is pressed
            System.out.println("Delete Room button pressed");
            // Display the DeleteRoom UI
            deleteEmployee.start(new Stage());
        });

        billsButton.setOnAction(e -> {
            // Perform the desired action when "Delete Room" button is pressed
            System.out.println("Bill management button pressed");
            // Display the DeleteRoom UI
            try {
                billManagement.start(new Stage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });



        // Add the GridPane to the VBox below the title and subtitle
        vbox.getChildren().add(gridPane);

        // Set VBox padding and spacing to separate title from the GridPane
        vbox.setSpacing(20); // Space between title and button grid
        vbox.setPadding(new Insets(10, 20, 20, 20)); // Padding around the VBox

        // Create the scene with the VBox instead of the GridPane
        Scene scene = new Scene(vbox, 700, 700); // Adjust size as needed
        return scene;
    }

    private Button createButton(String text, int width, int height) {
        Button button = new Button(text);
        button.setMinWidth(width);
        button.setMinHeight(height);
        button.setMaxWidth(Double.MAX_VALUE); // Allow button to grow
        GridPane.setHalignment(button, HPos.CENTER); // Center button in grid cell
        return button;
    }


    private Scene createEmployeeUI() {

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);




        // Load and set the background image
        Image backgroundImage = loadImage("/welcome2.jpg"); // Replace with your actual image path
        BackgroundImage bgImage = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        vbox.setBackground(new Background(bgImage));

        // Define a uniform button size
        int buttonWidth = 150;
        int buttonHeight = 40;


        // Adding title and subtitle
        Label titleLabel = new Label("R.S Hotel");
        titleLabel.setFont(new Font("Arial", 36)); // Set font size and family for the title
        titleLabel.setTextFill(Color.WHITE); // Set text color to white
        Label subtitleLabel = new Label("Welcome Employee");
        subtitleLabel.setFont(new Font("Arial", 18)); // Smaller font size for the subtitle
        subtitleLabel.setTextFill(Color.WHITE); // Set text color to white

        vbox.getChildren().addAll(titleLabel, subtitleLabel);

        // Customer Management Section
        Button customerManagementButton = createButton("Customer Management", buttonWidth, buttonHeight);
        Button deleteCustomerButton = createButton("Delete Customer", buttonWidth, buttonHeight);

        // Room Management Section
        Button roomManagementButton = createButton("Room Management", buttonWidth, buttonHeight);
        Button addRoomButton = createButton("Add Room", buttonWidth, buttonHeight);
        Button updateRoomButton = createButton("Update Room", buttonWidth, buttonHeight);
        Button deleteRoomButton = createButton("Delete Room", buttonWidth, buttonHeight);

        // Reservation Management Section
        Button reservationManagementButton = createButton("Reservation Management", buttonWidth, buttonHeight);
        Button reservation2ManagementButton = createButton("Make Reservation", buttonWidth, buttonHeight);
        Button deleteReservationButton = createButton("Delete Reservation", buttonWidth, buttonHeight);


        // Add buttons to the grid
        gridPane.add(customerManagementButton, 0, 0);
        gridPane.add(deleteCustomerButton, 0, 1);
        gridPane.add(roomManagementButton, 1, 0);
        gridPane.add(addRoomButton, 1, 1);
        gridPane.add(updateRoomButton, 1, 2);
        gridPane.add(deleteRoomButton, 1, 3);
        gridPane.add(reservationManagementButton, 2, 0);
        gridPane.add(reservation2ManagementButton, 2, 1);
        gridPane.add(deleteReservationButton, 2, 2);


        // Instantiate the CustomerManagement class
        CustomerManagement customerManagement = new CustomerManagement();
        DeleteCustomer deleteCustomer = new DeleteCustomer();
        RoomManagement roomManagement = new RoomManagement();
        AddRoom addRoom = new AddRoom();
        UpdateRoom updateRoom = new UpdateRoom();
        DeleteRoom deleteRoom = new DeleteRoom();
        ReservationManagement reservationManagement = new ReservationManagement();
        DeleteReservation deleteReservation =new DeleteReservation();
        ReservationFx reservationFx = new ReservationFx();


        customerManagementButton.setOnAction(e -> {
            System.out.println("Customer Management button pressed");
            customerManagement.start(new Stage());
        });


        deleteCustomerButton.setOnAction(e -> {
            System.out.println("Delete Customer button pressed");
            deleteCustomer.start(new Stage());
        });

        roomManagementButton.setOnAction(e -> {
            System.out.println("Room Management button pressed");
            roomManagement.start(new Stage());
        });
        addRoomButton.setOnAction(e -> {
            System.out.println("Add Room button pressed");
            addRoom.start(new Stage());
        });

        updateRoomButton.setOnAction(e -> {
            System.out.println("Updata Room button pressed");
            updateRoom.start(new Stage());
        });
        deleteRoomButton.setOnAction(e -> {
            System.out.println("Delete Room button pressed");
            deleteRoom.start(new Stage());
        });
        reservationManagementButton.setOnAction(e -> {
            System.out.println("Delete Room button pressed");
            reservationManagement.start(new Stage());
        });
        deleteReservationButton.setOnAction(e -> {
            System.out.println("Delete Room button pressed");
            deleteReservation.start(new Stage());
        });
        reservation2ManagementButton.setOnAction(e -> {
            System.out.println("Customer Management button pressed");
            reservationFx.start(new Stage());
        });

        Button logoutButton = createButton("Logout", buttonWidth, buttonHeight);
        HBox logoutBox = new HBox(); // Use an HBox for centering and padding
        logoutBox.setAlignment(Pos.BOTTOM_CENTER);
        logoutBox.getChildren().add(logoutButton);
        gridPane.add(logoutBox, 0, 4, 3, 1); // Span 3 columns


        logoutButton.setOnAction(e -> {
            Scene startScene = login();
            primaryStage.setScene(startScene);
            primaryStage.show();
        });
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(33);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(33);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(33);
        gridPane.getColumnConstraints().addAll(column1, column2, column3);


        vbox.getChildren().add(gridPane);


        vbox.setSpacing(20);
        vbox.setPadding(new Insets(10, 20, 20, 20));


        Scene scene = new Scene(vbox, 700, 700);
        return scene;
    }




    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }


    private Image loadImage(String imageName) {
        try {
            // Load image from resources
            return new Image(getClass().getResourceAsStream(imageName));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}