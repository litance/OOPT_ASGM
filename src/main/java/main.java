//090325Version
//Need to connect sql, please whatsapp me to get details.
//Or you can search about how to import SQLite in Java
//Please download SQLite extension

//My program got three role call: guest, admin and staff
//These role got different permission
//This version guest and staff only can login
//Admin can register staff

import java.sql.*;
import java.util.Scanner;

public class main {
    //mydatabase.db can change to your database name(but must end with .db lah)
    static final String url = "jdbc:sqlite:mydatabase.db";

    //Ok everybody know this is main
    public static void main(String[] args) {
        database();
        Scanner sc = new Scanner(System.in);
        UserCheck userCheck = new UserCheck();

        String role = "guest";

        System.out.println("Staff Page");

        while (true) {
            System.out.println("[1] Login");
            System.out.println("[2] Logout");
            System.out.println("[3] Exit");
            System.out.print("Enter your choice: ");

            int staffPageChoice;
            if (sc.hasNextInt()) {
                staffPageChoice = sc.nextInt();
            } else {
                System.out.println("Invalid input! Please enter a number.");
                sc.next();
                continue;
            }

            switch (staffPageChoice) {
                case 1:
                    role = userCheck.login(sc);
                    break;
                case 2:
                    System.out.println("Logout successful!");
                    role = "guest";
                    break;
                case 3:
                    System.out.println("Exiting...");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void database() {
        //I create two table for different types of role but usually same lah
        String createAdminTable = "CREATE TABLE IF NOT EXISTS admin ("
                + "username VARCHAR(50) PRIMARY KEY,"
                + "password VARCHAR(50) NOT NULL)";

        String createStaffTable = "CREATE TABLE IF NOT EXISTS staff ("
                + "username VARCHAR(50) PRIMARY KEY,"
                + "password VARCHAR(50) NOT NULL)";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute(createAdminTable);
            stmt.execute(createStaffTable);

            String checkAdmin = "SELECT COUNT(*) FROM admin WHERE username = 'admin'";
            try (Statement checkStmt = conn.createStatement();
                 ResultSet rs = checkStmt.executeQuery(checkAdmin)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    //This is testing insert
                    //Not safe because everybody know admin acc
                    String insertAdmin = "INSERT INTO admin (username, password) VALUES ('admin', '12345')";
                    stmt.execute(insertAdmin);
                    System.out.println("Default admin account created: admin / 12345");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
}

//Check the username user want to register exists or not
//If the people got same name I dunno
class UserCheck {
    public void checkUser(String username) {
        String queryStaff = "SELECT * FROM staff WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(main.url);
             PreparedStatement stmt = conn.prepareStatement(queryStaff)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Username already exists!");
                } else {
                    System.out.println("Username is available.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Admin role needed to reg
    public static void register(String role, Scanner sc) {
        if (!role.equals("admin")) {
            System.out.println("Warning! Admin permission needed!");
            System.out.println("You need to login as an Admin!");
            return;
        }
        sc.nextLine();
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        String checkQuery = "SELECT username FROM staff WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(main.url);
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, username);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Error: Username already exists!");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        String insertRegister = "INSERT INTO staff (username, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(main.url);
             PreparedStatement pstmt = conn.prepareStatement(insertRegister)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("Successfully registered!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public String login(Scanner sc) {
        sc.nextLine();
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        String queryAdmin = "SELECT * FROM admin WHERE username = ? AND password = ?";
        String queryStaff = "SELECT * FROM staff WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(main.url);
             PreparedStatement pstmtAdmin = conn.prepareStatement(queryAdmin);
             PreparedStatement pstmtStaff = conn.prepareStatement(queryStaff)) {

            pstmtAdmin.setString(1, username);
            pstmtAdmin.setString(2, password);
            try (ResultSet rsAdmin = pstmtAdmin.executeQuery()) {
                if (rsAdmin.next()) {
                    System.out.println("Admin Login Successful!");
                    page.pageChoose("admin", sc);
                    return "admin";
                }
            }

            pstmtStaff.setString(1, username);
            pstmtStaff.setString(2, password);
            try (ResultSet rsStaff = pstmtStaff.executeQuery()) {
                if (rsStaff.next()) {
                    System.out.println("Staff Login Successful!");
                    page.pageChoose("staff", sc);
                    return "staff";
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("Invalid credentials.");
        return "guest";
    }
}

class page {
    //After login, staff can process Borrow/Return Book here
    public static void mainPage() {

    }

    //This is backstage
    //Usually I dunno how your guys part
    //So I put a Report page first lah
    public static void managementPage(String role, Scanner sc) {
        while(true) {
            System.out.println("[1] Register Staff");
            System.out.println("[2] Report");
            System.out.println("[3] Exit");
            System.out.print("Enter your choice: ");
            String choice;
            if (sc.hasNextInt()) {
                choice = sc.nextLine();
            } else {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }
            switch (choice) {
                case "1":
                    UserCheck.register(role, sc);
                    break;
                case "2":
                    System.out.println("Report");
                    break;
                case "3":
                    System.out.println("Exit");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
                    break;
            }
        }
    }

    public static void pageChoose(String role, Scanner sc) {
        while (true) {
            System.out.println("1, Main Page");
            System.out.println("2, Management Page");
            System.out.print("Choose your choice: ");

            int pageChoice;
            if (sc.hasNextInt()) {
                pageChoice = sc.nextInt();
            } else {
                System.out.println("Invalid input! Please enter a number.");
                sc.next();
                continue;
            }

            switch(pageChoice){
                case 1:
                    System.out.println("Main Page");
                    page.mainPage();
                    break;
                case 2:
                    System.out.println("Management Page");
                    page.managementPage(role, sc);
                    break;
            }
        }
    }
}
