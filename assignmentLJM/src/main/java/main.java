//090325. Version
//Need to connect SQL, please WhatsApp me to get details.
//Or you can search about how to import SQLite in Java
//Please download SQLite extension

//My program got three role calls: guest, admin, and staff
//These roles got different permission
//This version guest and staff only can login
//Admin can register staff

import java.sql.*;
import java.util.Scanner;

public class main {
    //mydatabase.db can change to your database name (but must end with .db lah)
    static final String url = "jdbc:sqlite:mydatabase.db";
    public static final String ANSI_RED = "\u001B[31m";;
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_RESET = "\u001B[0m";

    //Ok everybody know this is main
    public static void main(String[] args) {
        database();
        Scanner sc = new Scanner(System.in);
        UserCheck userCheck = new UserCheck();

        String role = "guest";

        System.out.println("Staff Page");

        while (true) {
            System.out.println(ANSI_GREEN + "[1] Login" + ANSI_RESET);
            System.out.println(ANSI_YELLOW + "[2] Logout" + ANSI_RESET);
            System.out.println(ANSI_RED + "[0] Exit" + ANSI_RESET);
            System.out.print(ANSI_BLUE + "Enter your choice: " + ANSI_RESET);

            int staffPageChoice;
            if (sc.hasNextInt()) {
                staffPageChoice = sc.nextInt();
            } else {
                System.out.println(ANSI_RED + "Invalid input! Please enter a number." + ANSI_RESET);
                sc.next();
                continue;
            }

            switch (staffPageChoice) {
                case 1:
                    role = UserCheck.login(sc);
                    break;
                case 2:
                    System.out.println(ANSI_PURPLE + "Logout successful!" + ANSI_RESET);
                    role = "guest";
                    break;
                case 0:
                    System.out.println(ANSI_YELLOW + "Exiting..." + ANSI_RESET);
                    sc.close();
                    return;
                default:
                    System.out.println(ANSI_RED + "Invalid choice. Please try again." + ANSI_RESET);
            }
        }
    }

    public static void database() {
        //I create two tables for different types of roles but usually the same lah
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
                    System.out.println(ANSI_RED + "Default admin account created: admin / 12345" + ANSI_RESET);
                }
            }
        } catch (SQLException e) {
            System.out.println(ANSI_RED + "Database Error: " + e.getMessage() + ANSI_RESET);
        }
    }
}

//Check the username user wants to register exists or not
//If the people got same name I dunno
class UserCheck {
    public void checkUser(String username) {
        String queryStaff = "SELECT * FROM staff WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(main.url);
             PreparedStatement stmt = conn.prepareStatement(queryStaff)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println(main.ANSI_YELLOW + "Username already exists!" + main.ANSI_RESET);
                } else {
                    System.out.println(main.ANSI_RED + "Username is available." + main.ANSI_RESET);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Admin role needed to reg
    public static void register(String role, Scanner sc) {
        if (!role.equals("admin")) {
            while(true) {
                System.out.println(main.ANSI_RED + "Warning! Admin permission needed!" + main.ANSI_RESET);
                System.out.println(main.ANSI_YELLOW + "You need to login as an Admin!" + main.ANSI_RESET);
                System.out.println(main.ANSI_GREEN +"[1] Return to Login" + main.ANSI_RESET);
                System.out.println(main.ANSI_GREEN + "[2] Return to Management Page" + main.ANSI_RESET);
                System.out.print(main.ANSI_BLUE + "Enter your choice: " + main.ANSI_RESET);
                int returnChoice;
                if (sc.hasNextInt()) {
                    returnChoice = sc.nextInt();
                } else{
                    System.out.println(main.ANSI_RED + "Invalid input! Please enter a number." + main.ANSI_RESET);
                    sc.next();
                    continue;
                }
                switch (returnChoice) {
                    case 1:
                        role = UserCheck.login(sc);
                        break;
                    case 2:
                        page.managementPage(role, sc);
                        break;
                    default:
                        System.out.println(main.ANSI_RED + "Invalid choice. Please try again." + main.ANSI_RESET);
                        break;
                }
            }
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
                    System.out.println(main.ANSI_RED + "Error: Username already exists!" + main.ANSI_RESET);
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
            System.out.println(main.ANSI_PURPLE + "Successfully registered!" + main.ANSI_RESET);
        } catch (SQLException e) {
            System.out.println(main.ANSI_RED + "Error: " + e.getMessage() + main.ANSI_RESET);
        }
    }

    public static String login(Scanner sc) {
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
                    System.out.println(main.ANSI_PURPLE + "Admin Login Successful!" + main.ANSI_RESET);
                    page.pageChoose("admin", sc);
                    return "admin";
                }
            }

            pstmtStaff.setString(1, username);
            pstmtStaff.setString(2, password);
            try (ResultSet rsStaff = pstmtStaff.executeQuery()) {
                if (rsStaff.next()) {
                    System.out.println(main.ANSI_PURPLE + "Staff Login Successful!" + main.ANSI_RESET);
                    page.pageChoose("staff", sc);
                    return "staff";
                }
            }

        } catch (SQLException e) {
            System.out.println(main.ANSI_RED + "Error: " + e.getMessage() + main.ANSI_RESET);
        }

        System.out.println(main.ANSI_RED + "Invalid credentials." + main.ANSI_RESET);
        return "guest";
    }
}

class page {
    //After login, staff can process Borrow/Return Book here
    //I haven't added the function yet
    //This is just a temp save
    public static void mainPage() {

    }

    //This is backstage
    //Usually I dunno how your guys part
    //So I put a Report page first lah
    public static void managementPage(String role, Scanner sc) {
        while(true) {
            System.out.println(main.ANSI_GREEN + "[1] Register Staff" +main.ANSI_RESET);
            System.out.println(main.ANSI_GREEN + "[2] Report" + main.ANSI_RESET);
            System.out.println(main.ANSI_RED + "[0] Exit" + main.ANSI_RESET);
            System.out.print(main.ANSI_BLUE + "Enter your choice: " + main.ANSI_RESET);
            int choice;
            if (sc.hasNextInt()) {
                choice = sc.nextInt();
            } else {
                System.out.println(main.ANSI_RED + "Invalid input! Please enter a number." + main.ANSI_RESET);
                continue;
            }
            switch (choice) {
                case 1:
                    UserCheck.register(role, sc);
                    break;
                case 2:
                    System.out.println("Report");
                    break;
                case 0:
                    System.out.println("Exit");
                    return;
                default:
                    System.out.println(main.ANSI_RED + "Invalid choice! Please try again." + main.ANSI_RESET);
                    break;
            }
        }
    }

    public static void pageChoose(String role, Scanner sc) {
        while (true) {
            System.out.println(main.ANSI_GREEN + "[1] Main Page" + main.ANSI_RESET);
            System.out.println(main.ANSI_GREEN + "[2] Management Page" + main.ANSI_RESET);
            System.out.println(main.ANSI_RED + "[0] Return" + main.ANSI_RESET);
            System.out.print(main.ANSI_BLUE + "Choose your choice: " + main.ANSI_RESET);

            int pageChoice;
            if (sc.hasNextInt()) {
                pageChoice = sc.nextInt();
            } else {
                System.out.println(main.ANSI_RED + "Invalid input! Please enter a number." + main.ANSI_RESET);
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
                case 0:
                    System.out.println("Return");
                    return;
                default:
                    System.out.println(main.ANSI_RED + "Invalid choice! Please try again." + main.ANSI_RESET);
                    break;
            }
        }
    }
}
