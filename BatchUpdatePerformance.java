import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import javax.swing.*;

public class BatchUpdatePerformance {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/yourDatabase"; // Update with your database URL
    private static final String USER = "yourUsername"; // Update with your database username
    private static final String PASSWORD = "yourPassword"; // Update with your database password

    public static void main(String[] args) {
        // Connect to the database
        connectToDatabase();
        
        // Insert records with and without batch updates
        insertRecordsWithoutBatch();
        insertRecordsWithBatch();
    }

    private static void connectToDatabase() {
        // Create a dialog box for database connection
        JPanel panel = new JPanel();
        JTextField urlField = new JTextField(DB_URL, 20);
        JTextField userField = new JTextField(USER, 20);
        JTextField passwordField = new JPasswordField(PASSWORD, 20);

        panel.add(new JLabel("Database URL:"));
        panel.add(urlField);
        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Connect to Database", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // Update the static variables with user input
            DB_URL = urlField.getText();
            USER = userField.getText();
            PASSWORD = passwordField.getText();
        }
    }

    private static void insertRecordsWithoutBatch() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Temp(num1, num2, num3) VALUES (?, ?, ?)")) {

            long startTime = System.currentTimeMillis();

            Random rand = new Random();
            for (int i = 0; i < 1000; i++) {
                pstmt.setDouble(1, rand.nextDouble());
                pstmt.setDouble(2, rand.nextDouble());
                pstmt.setDouble(3, rand.nextDouble());
                pstmt.executeUpdate(); // Execute individual insert
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Time taken without batch updates: " + (endTime - startTime) + " ms");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertRecordsWithBatch() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Temp(num1, num2, num3) VALUES (?, ?, ?)")) {

            long startTime = System.currentTimeMillis();

            Random rand = new Random();
            for (int i = 0; i < 1000; i++) {
                pstmt.setDouble(1, rand.nextDouble());
                pstmt.setDouble(2, rand.nextDouble());
                pstmt.setDouble(3, rand.nextDouble());
                pstmt.addBatch(); // Add to batch
            }
            pstmt.executeBatch(); // Execute batch update

            long endTime = System.currentTimeMillis();
            System.out.println("Time taken with batch updates: " + (endTime - startTime) + " ms");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
