
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

public class hrms {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n===== HRMS MENU =====");
            System.out.println("1. Create New Organization");
            System.out.println("2. Login to Organization");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    createOrganization();
                    break;
                case "2":
                    loginOrganization();
                    break;
                case "3":
                    System.out.println("Thank you for using HRMS!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // CREATE ORGANIZATION
    static void createOrganization() {

        System.out.println("\n--- Create New Organization ---");

        System.out.print("Organization Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Address: ");
        String address = sc.nextLine().trim();

        System.out.print("PAN: ");
        String pan = sc.nextLine().trim().toUpperCase();

        System.out.print("GST: ");
        String gst = sc.nextLine().trim().toUpperCase();

        System.out.print("Department: ");
        String department = sc.nextLine().trim();

        System.out.print("Login Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine();

        System.out.print("Number of Employees: ");
        String countInput = sc.nextLine().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("Name, email and password are required!");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            System.out.println("Invalid email address!");
            return;
        }

        int employeeCount;

        try {
            employeeCount = Integer.parseInt(countInput);

            if (employeeCount < 0) {
                System.out.println("Employee count cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Enter a valid employee count.");
            return;
        }

        String sql = "INSERT INTO organizations " +
                "(organization_name, address, pan, gst, department, " +
                "email, password, employee_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, pan);
            ps.setString(4, gst);
            ps.setString(5, department);
            ps.setString(6, email);
            ps.setString(7, password);
            ps.setInt(8, employeeCount);

            ps.executeUpdate();

            System.out.println("Organization created successfully!");

        } catch (SQLException e) {
            System.out.println("Organization creation failed: "
                    + e.getMessage());
        }
    }

    // ORGANIZATION LOGIN
    static void loginOrganization() {

        System.out.println("\n--- Organization Login ---");

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine();

        String sql = "SELECT organization_id, organization_name " +
                "FROM organizations WHERE email = ? AND password = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    int organizationId = rs.getInt("organization_id");

                    System.out.println("Login successful!");
                    System.out.println("Welcome, "
                            + rs.getString("organization_name"));

                    organizationMenu(organizationId);

                } else {
                    System.out.println("Invalid email or password!");
                }
            }

        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    // ORGANIZATION MENU
    static void organizationMenu(int organizationId) {

        while (true) {
            System.out.println("\n===== ORGANIZATION MENU =====");
            System.out.println("1. View Organization Details");
            System.out.println("2. Employee Management");
            System.out.println("3. Attendance Management");
            System.out.println("4. Salary Management");
            System.out.println("5. Notices Management");
            System.out.println("6. Holidays Management");
            System.out.println("7. Logout");

            System.out.print("Enter your choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    viewOrganizationDetails(organizationId);
                    break;
                case "2":
                    crudMenu(organizationId, "employees");
                    break;
                case "3":
                    crudMenu(organizationId, "attendance");
                    break;
                case "4":
                    crudMenu(organizationId, "salary");
                    break;
                case "5":
                    crudMenu(organizationId, "notices");
                    break;
                case "6":
                    crudMenu(organizationId, "holidays");
                    break;
                case "7":
                    System.out.println("Logged out successfully!");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // VIEW ORGANIZATION DETAILS
    static void viewOrganizationDetails(int organizationId) {

        String sql = "SELECT organization_name, address, pan, gst, " +
                "department, email, employee_count FROM organizations " +
                "WHERE organization_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, organizationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n--- Organization Details ---");
                    System.out.println("Name: "
                            + rs.getString("organization_name"));
                    System.out.println("Address: "
                            + rs.getString("address"));
                    System.out.println("PAN: " + rs.getString("pan"));
                    System.out.println("GST: " + rs.getString("gst"));
                    System.out.println("Department: "
                            + rs.getString("department"));
                    System.out.println("Email: " + rs.getString("email"));
                    System.out.println("Employees: "
                            + rs.getInt("employee_count"));
                } else {
                    System.out.println("Organization not found!");
                }
            }

        } catch (SQLException e) {
            System.out.println("Unable to view details: " + e.getMessage());
        }
    }

    // COLUMN NAMES FOR EACH MODULE
    static String[] getColumns(String table) {

        switch (table) {
            case "employees":
                return new String[]{"employee_name", "email",
                        "phone", "department", "designation"};

            case "attendance":
                return new String[]{"employee_id",
                        "attendance_date", "status"};

            case "salary":
                return new String[]{"employee_id", "salary_month",
                        "basic_salary", "deductions", "net_salary"};

            case "notices":
                return new String[]{"title", "description", "notice_date"};

            case "holidays":
                return new String[]{"holiday_name", "holiday_date",
                        "description"};

            default:
                throw new IllegalArgumentException("Invalid module");
        }
    }

    // PRIMARY KEY COLUMN FOR EACH MODULE
    static String getIdColumn(String table) {

        switch (table) {
            case "employees":
                return "employee_id";
            case "attendance":
                return "attendance_id";
            case "salary":
                return "salary_id";
            case "notices":
                return "notice_id";
            case "holidays":
                return "holiday_id";
            default:
                throw new IllegalArgumentException("Invalid module");
        }
    }

    // CRUD MENU
    static void crudMenu(int organizationId, String table) {

        while (true) {
            System.out.println("\n===== " + table.toUpperCase() + " =====");
            System.out.println("1. Create");
            System.out.println("2. View");
            System.out.println("3. Update");
            System.out.println("4. Delete");
            System.out.println("5. Back");

            System.out.print("Enter your choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    createRecord(organizationId, table);
                    break;
                case "2":
                    viewRecords(organizationId, table);
                    break;
                case "3":
                    updateRecord(organizationId, table);
                    break;
                case "4":
                    deleteRecord(organizationId, table);
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // CREATE RECORD
    static void createRecord(int organizationId, String table) {

        String[] columns = getColumns(table);

        StringBuilder sql = new StringBuilder(
                "INSERT INTO " + table + " (organization_id");

        for (String column : columns) {
            sql.append(", ").append(column);
        }

        sql.append(") VALUES (?");

        for (int i = 0; i < columns.length; i++) {
            sql.append(", ?");
        }

        sql.append(")");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            ps.setInt(1, organizationId);

            for (int i = 0; i < columns.length; i++) {
                System.out.print("Enter " + columns[i] + ": ");
                ps.setString(i + 2, sc.nextLine().trim());
            }

            if (ps.executeUpdate() > 0) {
                System.out.println("Record created successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Create failed: " + e.getMessage());
        }
    }

    // VIEW RECORDS
    static void viewRecords(int organizationId, String table) {

        String sql = "SELECT * FROM " + table
                + " WHERE organization_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, organizationId);

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int count = meta.getColumnCount();
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    System.out.println("\n--------------------");

                    for (int i = 1; i <= count; i++) {
                        System.out.println(meta.getColumnLabel(i) + ": "
                                + rs.getString(i));
                    }
                }

                if (!found) {
                    System.out.println("No records found!");
                }
            }

        } catch (SQLException e) {
            System.out.println("View failed: " + e.getMessage());
        }
    }

    // UPDATE RECORD
    static void updateRecord(int organizationId, String table) {

        String[] columns = getColumns(table);
        String idColumn = getIdColumn(table);

        StringBuilder sql = new StringBuilder(
                "UPDATE " + table + " SET ");

        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]).append(" = ?");

            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }

        sql.append(" WHERE ").append(idColumn)
                .append(" = ? AND organization_id = ?");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < columns.length; i++) {
                System.out.print("Enter new " + columns[i] + ": ");
                ps.setString(i + 1, sc.nextLine().trim());
            }

            System.out.print("Enter " + idColumn + " to update: ");
            int id = Integer.parseInt(sc.nextLine().trim());

            ps.setInt(columns.length + 1, id);
            ps.setInt(columns.length + 2, organizationId);

            if (ps.executeUpdate() > 0) {
                System.out.println("Record updated successfully!");
            } else {
                System.out.println("Record not found!");
            }

        } catch (SQLException | NumberFormatException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    // DELETE RECORD
    static void deleteRecord(int organizationId, String table) {

        String idColumn = getIdColumn(table);

        System.out.print("Enter " + idColumn + " to delete: ");

        try {
            int id = Integer.parseInt(sc.nextLine().trim());

            String sql = "DELETE FROM " + table
                    + " WHERE " + idColumn
                    + " = ? AND organization_id = ?";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, id);
                ps.setInt(2, organizationId);

                if (ps.executeUpdate() > 0) {
                    System.out.println("Record deleted successfully!");
                } else {
                    System.out.println("Record not found!");
                }
            }

        } catch (SQLException | NumberFormatException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }
}
