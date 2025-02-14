package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;
import javafx.concurrent.Task;
import javafx.application.Platform;


/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
	
    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button forgotPasswordButton = new Button("Forgot Password?");
        Button loginButton = new Button("Login");
        
        loginButton.setOnAction(a -> {
            // Retrieve user inputs
            String userName = userNameField.getText();
            String password = passwordField.getText();

            try {
                User user = new User(userName, password, "");
                int userId = databaseHelper.getUserIdByUsername(userName);
                
                // Check if the user has forgotten their password
                boolean forgotPassword = databaseHelper.getForgotPasswordStatus(userId);
                

                if (forgotPassword == true) {
                	databaseHelper.deleteNotificationLine(userId, "Temporary");
                	
                    redirectToPasswordReset(userId, primaryStage);
                    return; // Stop further execution of login logic
                }

                WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper, userName);

                // Retrieve the user's role from the database using userName
                String role = databaseHelper.getUserRole(userName);

                if (role != null) {
                    user.setRole(role);
                    if (databaseHelper.login(user)) {
                        welcomeLoginPage.show(primaryStage, user);
                    } else {
                        // Display an error if the login fails
                        errorLabel.setText("Error logging in");
                    }
                } else {
                    // Display an error if the account does not exist
                    errorLabel.setText("User account doesn't exist");
                }

            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });
        forgotPasswordButton.setOnAction(a -> {
            // Fetch userName and id
            String userName = userNameField.getText();
            int userId = databaseHelper.getUserIdByUsername(userName);

            String notification = "User " + userId + " forgot their password. Send them a temporary one.";

            // Retrieve admin id to contact about resetting password
            int adminId = databaseHelper.getUserIdByUsername(databaseHelper.getFirstAdmin());
            if (databaseHelper.getForgotPasswordStatus(userId) == false) {
	            databaseHelper.addNotificationToUser(notification, adminId);
	            errorLabel.setText("Sent request to Admin");
            }
            // Create a task to periodically check for updates
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (true) {
                        String notifications = databaseHelper.getNotifications(userId);
                        if (notifications != null && notifications.contains("Temporary Password:")) {
                            Platform.runLater(() -> errorLabel.setText(notifications));
                            break;
                        }
                        Thread.sleep(5000); // Check every 5 seconds
                    }
                    return null;
                }
            };

            // Start the task in a new thread
            new Thread(task).start();
        });



        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel, forgotPasswordButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
 // Method to redirect user to the password reset page
    private void redirectToPasswordReset(int userId, Stage primaryStage) {
        PasswordResetPage passwordResetPage = new PasswordResetPage(databaseHelper, userId);
        passwordResetPage.show(primaryStage);
        

        // Once the password is reset, return to login
        passwordResetPage.setOnPasswordReset(() -> {
            UserLoginPage loginPage = new UserLoginPage(databaseHelper);
            loginPage.show(primaryStage);
        });
    }

}
