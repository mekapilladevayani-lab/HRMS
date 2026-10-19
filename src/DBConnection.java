
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/hrms_db";

    private static final String USERNAME = "root";


    private static final String PASSWORD = "Deva7791@";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL, USERNAME, PASSWORD
        );
    }

    public static void main(String[] args) {
        try (Connection con = getConnection()) {
            System.out.println("MySQL Connected Successfully!");
        } catch (SQLException e) {
            System.out.println("Connection Failed!");
            System.out.println(e.getMessage());
        }
    }
}




