//140325. Version
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
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_RESET = "\u001B[0m";

    //Ok everybody know this is main
    public static void main(String[] args) {
        database();
        Scanner sc = new Scanner(System.in);
        //If didn't get any operation, the role will be guest
        //Guest cannot do anything lah
        //So user needs to login
        String role = "guest";

        System.out.println(ANSI_PURPLE + "Staff Page" + ANSI_RESET);
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
                    //Role check
                    //If role = admin will straight to management choose page
                    //Else if role = staff will go to main page lah
                    //This is a simple screening method
                    role = user.login(sc);
                    if (!role.equals("guest")) {
                        if (role.equals("admin")) {
                            page.pageChoose(role, sc);
                            break;
                        } else {
                            page.mainPage();
                            break;
                        }
                    }
                    break;
                case 2:
                    System.out.println(ANSI_PURPLE + "Logout successful!" + ANSI_RESET);
                    //If logout successful
                    //That's mean roles will change to guest(Haven't login)
                    role = "guest";
                    break;
                case 0:
                    System.out.println(ANSI_YELLOW + "Exiting..." + ANSI_RESET);
                    sc.close();
                    System.exit(0);
                default:
                    System.out.println(ANSI_RED + "Invalid choice. Please try again." + ANSI_RESET);
            }
        }
    }

    public static void database() {
        //Create two table(Admin&Staff)
        //If XiaoBing or ZhengYu code got database can put here
        String createAdminTable = "CREATE TABLE IF NOT EXISTS admin (" +
                "username VARCHAR(50) PRIMARY KEY," +
                "password VARCHAR(50) NOT NULL);";

        String createStaffTable = "CREATE TABLE IF NOT EXISTS staff (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "gender CHAR(1), " +
                "phonenumber TEXT, " +
                "email VARCHAR(50) NOT NULL, " +
                "icnumber TEXT, " +
                "position VARCHAR(50), " +
                "password VARCHAR(50) NOT NULL);";

        Connection conn = null;
        //I close SQLite auto connection
        //Because it'll have bug call database lock
        //I dunno how to fix it
        //So I change to manual upload
        Statement stmt = null;
        Statement checkStmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(main.url);
            stmt = conn.createStatement();
            stmt.execute(createAdminTable);
            stmt.execute(createStaffTable);

            String checkAdmin = "SELECT COUNT(*) FROM admin WHERE username = 'admin'";
            checkStmt = conn.createStatement();
            rs = checkStmt.executeQuery(checkAdmin);

            if (rs.next() && rs.getInt(1) == 0) {
                //Test insert
                //Also can use at original factory account lah
                //But we need to private or hide it loh
                String insertAdmin = "INSERT INTO admin (username, password) VALUES ('admin', '12345')";
                stmt.execute(insertAdmin);
                System.out.println(main.ANSI_RED + "Default admin account created: admin / 12345" + main.ANSI_RESET);
            }
        } catch (SQLException e) {
            System.out.println(main.ANSI_RED + "Database Error: " + e.getMessage() + main.ANSI_RESET);
        } finally {
            try {
                if (rs != null) rs.close();
                if (checkStmt != null) checkStmt.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

//Check the username user wants to register exists or not
//If the people got same name I dunno
class user {
    //Admin role needed to reg
    public static void register(String role, Scanner sc) {
        if (!role.equals("admin")) {
            System.out.println(main.ANSI_RED + "Warning! Admin permission needed!" + main.ANSI_RESET);
            return;
        }

        sc.nextLine();
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter gender (M/F): ");
        String gender = sc.nextLine();
        System.out.print("Enter phone number: ");
        String phonenumber = sc.nextLine();
        System.out.print("Enter email: ");
        String email = sc.nextLine();
        System.out.print("Enter IC number: ");
        String icnumber = sc.nextLine();
        System.out.print("Enter position: ");
        String position = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(main.url);
            //Before upload check the username exists or not
            //If they got the same name
            //Can register as test/test-1/test_01/test01
            String checkQuery = "SELECT username FROM staff WHERE username = ?";
            checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println(main.ANSI_RED + "Error: Username already exists!" + main.ANSI_RESET);
                return;
            }

            String insertRegister = "INSERT INTO staff (username, gender, phonenumber, email, icnumber, position, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertRegister);
            pstmt.setString(1, username);
            pstmt.setString(2, gender);
            pstmt.setString(3, phonenumber);
            pstmt.setString(4, email);
            pstmt.setString(5, icnumber);
            pstmt.setString(6, position);
            pstmt.setString(7, password);
            pstmt.executeUpdate();
            System.out.println(main.ANSI_PURPLE + "Successfully registered!" + main.ANSI_RESET);

        } catch (SQLException e) {
            System.out.println(main.ANSI_RED + "Error: " + e.getMessage() + main.ANSI_RESET);
        } finally {
            try {
                if (rs != null) rs.close();
                if (checkStmt != null) checkStmt.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static String login(Scanner sc) {
        sc.nextLine();
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        Connection conn = null;
        PreparedStatement pstmtAdmin = null;
        PreparedStatement pstmtStaff = null;
        ResultSet rsAdmin = null;
        ResultSet rsStaff = null;

        try {
            conn = DriverManager.getConnection(main.url);

            //I think everyone can understand this lah
            //System see see our database got the user or not
            //If got and password is correct, then it'll login
            String queryAdmin = "SELECT * FROM admin WHERE username = ? AND password = ?";
            String queryStaff = "SELECT * FROM staff WHERE username = ? AND password = ?";

            pstmtAdmin = conn.prepareStatement(queryAdmin);
            pstmtAdmin.setString(1, username);
            pstmtAdmin.setString(2, password);
            rsAdmin = pstmtAdmin.executeQuery();

            if (rsAdmin.next()) {
                System.out.println(main.ANSI_PURPLE + "Admin Login Successful!" + main.ANSI_RESET);
                return "admin";
            }

            pstmtStaff = conn.prepareStatement(queryStaff);
            pstmtStaff.setString(1, username);
            pstmtStaff.setString(2, password);
            rsStaff = pstmtStaff.executeQuery();

            if (rsStaff.next()) {
                System.out.println(main.ANSI_PURPLE + "Staff Login Successful!" + main.ANSI_RESET);
                return "staff";
            }

            else {
                System.out.println("Unavailable username or password");
                return "guest";
            }

        } catch (SQLException e) {
            System.out.println(main.ANSI_RED + "Error: " + e.getMessage() + main.ANSI_RESET);
        } finally {
            try {
                if (rsAdmin != null) rsAdmin.close();
                if (rsStaff != null) rsStaff.close();
                if (pstmtAdmin != null) pstmtAdmin.close();
                if (pstmtStaff != null) pstmtStaff.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "guest";
    }

    public static void deleteStaff() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Username to delete: ");
        String username = sc.nextLine();
        String query = "DELETE FROM staff WHERE username = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(main.url);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println(main.ANSI_GREEN + "Successfully deleted staff: " + username +main.ANSI_RESET);
            } else {
                System.out.println(main.ANSI_RED + "Staff member not found: " + username +main.ANSI_RESET);
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

class page {
    //After login, staff can process Borrow/Return Book here
    //I haven't added the function yet
    //This is just a temp save
    public static void mainPage() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println(main.ANSI_PURPLE + "Main Page" + main.ANSI_RESET);
            System.out.println(main.ANSI_RED + "[0] Return" + main.ANSI_RESET);
            System.out.print(main.ANSI_BLUE + "Enter your choice: " + main.ANSI_RESET);

            int choice;
            if (sc.hasNextInt()){
                choice = sc.nextInt();
            } else {
                System.out.println(main.ANSI_RED + "Invalid choice" + main.ANSI_RESET);
                sc.next();
                continue;
            }
            switch (choice) {
                case 0:
                    System.out.println(main.ANSI_PURPLE + "Returning..." + main.ANSI_RESET);
                    return;
            }
        }
    }

    static class manageStaff {
        public static void printTableData(String tableName) {
            String query = "SELECT * FROM " + tableName;

            try (Connection conn = DriverManager.getConnection(main.url);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(metaData.getColumnName(i) + "\t");
                }
                System.out.println("\n-------------------------------------------------");

                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(rs.getString(i) + "\t");
                    }
                    System.out.println();
                }

            } catch (SQLException e) {
                System.out.println("Database Error: " + e.getMessage());
            }
        }

        public static void pageChoose() {
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println(main.ANSI_GREEN + "[1] Edit Staff" + main.ANSI_RESET);
                System.out.println(main.ANSI_GREEN + "[2] View Staff" + main.ANSI_RESET);
                System.out.println(main.ANSI_RED + "[3] Delete Staff" + main.ANSI_RESET);
                System.out.println(main.ANSI_YELLOW + "[0] Return" + main.ANSI_RESET);
                System.out.print(main.ANSI_BLUE + "Please enter your choice: " + main.ANSI_RESET);
                int pageChoice;
                if (sc.hasNextInt()) {
                    pageChoice = sc.nextInt();
                } else {
                    System.out.println(main.ANSI_RED + "Please enter a number!" + main.ANSI_RESET);
                    sc.next();
                    continue;
                }
                switch (pageChoice) {
                    case 1:
                        break;
                    case 2:
                        printTableData("staff");
                        break;
                    case 3:
                        user.deleteStaff();
                        break;
                    case 0:
                        return;
                    default:
                        break;
                }
            }
        }
    }

    //This is backstage
    //Usually I dunno how your guys part
    //So I put a Report page first lah
    public static void managementPage(String role, Scanner sc) {
        while(true) {
            System.out.println(main.ANSI_PURPLE + "Management Page" + main.ANSI_RESET);
            System.out.println(main.ANSI_GREEN + "[1] Register Staff" +main.ANSI_RESET);
            System.out.println(main.ANSI_GREEN + "[2] Manage Staff" +main.ANSI_RESET);
            System.out.println(main.ANSI_GREEN + "[3] Report" + main.ANSI_RESET);
            System.out.println(main.ANSI_RED + "[0] Return" + main.ANSI_RESET);
            System.out.print(main.ANSI_BLUE + "Enter your choice: " + main.ANSI_RESET);
            int choice;
            if (sc.hasNextInt()) {
                choice = sc.nextInt();
            } else {
                System.out.println(main.ANSI_RED + "Invalid input! Please enter a number." + main.ANSI_RESET);
                sc.next();
                continue;
            }
            switch (choice) {
                case 1:
                    user.register(role, sc);
                    break;
                case 2:
                    System.out.println("Manage Staff");
                    manageStaff.pageChoose();
                    break;
                case 3:
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
                    page.mainPage();
                    break;
                case 2:
                    page.managementPage(role, sc);
                    break;
                case 0:
                    System.out.println("Return");
                    main.main(new String[]{});
                    break;
                default:
                    System.out.println(main.ANSI_RED + "Invalid choice! Please try again." + main.ANSI_RESET);
                    break;
            }
        }
    }
}