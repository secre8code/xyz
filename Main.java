import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String Url = "jdbc:mysql://localhost:3306/database2";
        String Username = "aashi";
        String Password = "aashi@123";
        try (Connection connect = DriverManager.getConnection(Url, Username, Password)) {
            System.out.println("Connection build SUCCESSFULLY!..");
            String Employees = "Create table if not exists EMPLOYEES (" +
                    "Emp_id int AUTO_INCREMENT Primary key," +
                    "Emp_name varchar(50) not null," +
                    "Emp_position varchar(50) not null," +
                    "Emp_Basic_Salary double not null," +
                    "Emp_Allowance double default 0," +
                    "Emp_Deduction double default 0);";
            try (Statement stmnt = connect.createStatement()) {
                stmnt.executeUpdate(Employees);
                System.out.println("Table created SUCCCESSFULLY!...");
            }
            Boolean status = true;
            while (status) {
                System.out.println(" EMPLOYEES STATUS ");
                System.out.println("(i)   Add new Employee ");
                System.out.println("(ii)  Check existing employee details ");
                System.out.println("(iii) payroll ");
                System.out.println("(iv) Update Salary/ Allowance/ Deduction ");
                System.out.println("(v) Generate salary Slip ");
                System.out.println("(vi) Exit");
                System.out.println(" What you want?  \n Type Here..... ");
                String choice = scan.nextLine();

                switch (choice) {
                    case "i":
                        AddingEmployee(connect, scan);
                        break;

                    case "ii":
                        ExistingEmployee(connect, scan);
                        break;

                    case "iii":
                        Payroll(connect, scan);
                        break;

                    case "iv":
                        UpdateSal(connect, scan);
                        break;

                    case "v":
                        GenerateSlip(connect, scan);
                        break;

                    case "vi":
                        status = false;
                        System.out.println(" Exiting....");
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again later...");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void AddingEmployee(Connection connect, Scanner scan) {
        String ADDEMP = "Insert into EMPLOYEES ( Emp_name, Emp_position, Emp_Basic_Salary, Emp_Allowance, Emp_Deduction) Values (?, ?, ?, ?, ?)";
        boolean addMore = true;

        try {
            while (addMore) {
                System.out.println(" Employee name: ");
                String Emp_name = scan.nextLine();

                System.out.println(" Position of the Employee: ");
                String Emp_position = scan.nextLine();

                System.out.println(" Basic salary of the Employee: ");
                Double Emp_Basic_Salary = scan.nextDouble();

                System.out.println(" Allowances of the Employee: ");
                double Emp_Allowance = scan.nextDouble();

                System.out.println(" Deductions of the Employee: ");
                double Emp_Deduction = scan.nextDouble();
                scan.nextLine();

                try (PreparedStatement pstmnt = connect.prepareStatement(ADDEMP)) {
                    pstmnt.setString(1, Emp_name);
                    pstmnt.setString(2, Emp_position);
                    pstmnt.setDouble(3, Emp_Basic_Salary);
                    pstmnt.setDouble(4, Emp_Allowance);
                    pstmnt.setDouble(5, Emp_Deduction);
                    pstmnt.executeUpdate();
                    System.out.println(" ADDING THE NEW EMPLOYEE SUCCESSFULLY!... ");
                }

                System.out.println(" Add more Employees (YES/No): ");
                String choice = scan.nextLine();
                if (choice.equalsIgnoreCase("NO")) {
                    addMore = false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while inserting the data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ExistingEmployee(Connection connect, Scanner scan) {
        System.out.println(" Search Employees details by...");
        System.out.println("1. Employee id ");
        System.out.println("2. Employee name ");
        System.out.println("3. What you want? ");
        int choice = scan.nextInt();
        scan.nextLine();

        String Data = " ";
        try {
            if (choice == 1) {
                System.out.println(" Employee id: ");
                int Emp_id = scan.nextInt();
                Data = "Select * from EMPLOYEES Where Emp_id = ?";
                try (PreparedStatement pstmnt = connect.prepareStatement(Data)) {
                    pstmnt.setInt(1, Emp_id);
                    showResults(pstmnt);
                }
            } else if (choice == 2) {
                System.out.print("Employee Name: ");
                String name = scan.nextLine();
                Data = "SELECT * FROM EMPLOYEES WHERE Emp_name = ?";
                try (PreparedStatement pstmt = connect.prepareStatement(Data)) {
                    pstmt.setString(1, name);
                    showResults(pstmt);
                }

            } else {
                System.out.println("Invalid option selected.");
            }
        } catch (SQLException e) {
            System.out.println(" Error !!! : " + e.getMessage());
        }
    }
    private static void showResults(PreparedStatement pstmnt) throws SQLException {
        try (ResultSet rs = pstmnt.executeQuery()) {
            boolean found = false;
            System.out.println(" SEARCH RECORDS ");
            while (rs.next()) {
                found = true;
                System.out.println("EMPLOYEE ID: " + rs.getInt("Emp_id"));
                System.out.println("Employee Name: " + rs.getString("Emp_name"));
                System.out.println("Position: " + rs.getString("Emp_position"));
                System.out.println("Basic salary: " + rs.getDouble("Emp_Basic_Salary"));
                System.out.println("Allowance: " + rs.getDouble("Emp_Allowance"));
                System.out.println("Deductions: " + rs.getDouble("Emp_Deduction"));
            }
            if (!found) {
                System.out.println(" 404! No records found!!!");
            }
        }
    }
    private static void Payroll(Connection connect, Scanner scan) {
        String payroll = "SELECT * FROM EMPLOYEES";
        try (Statement stmnt = connect.createStatement()) {
            try (ResultSet rs = stmnt.executeQuery(payroll)) {
                while (rs.next()) {
                    System.out.println("EMPLOYEE ID: " + rs.getInt("Emp_id"));
                    System.out.println("Employee Name: " + rs.getString("Emp_name"));
                    System.out.println("Position: " + rs.getString("Emp_position"));
                    System.out.println("Basic salary: " + rs.getDouble("Emp_Basic_Salary"));
                    System.out.println("Allowance: " + rs.getDouble("Emp_Allowance"));
                    System.out.println("Deductions: " + rs.getDouble("Emp_Deduction"));
                }
            }
        } catch (SQLException e) {
            System.out.println(" Error !!! : " + e.getMessage());
        }   
    }
    private static void GenerateSlip(Connection connect, Scanner scan) {
        System.out.println(" Enter Employee ID to generate payroll slip: ");
        int empId = scan.nextInt();
        scan.nextLine();

        String query = "SELECT Emp_name, Emp_position, Emp_Basic_Salary, Emp_Allowance, Emp_Deduction FROM EMPLOYEES WHERE Emp_id = ?";
        try (PreparedStatement pstmnt = connect.prepareStatement(query)) {
            pstmnt.setInt(1, empId);
            try (ResultSet rs = pstmnt.executeQuery()) {
                if (rs.next()) {
                    String empName = rs.getString("Emp_name");
                    String empPosition = rs.getString("Emp_position");
                    double basicSalary = rs.getDouble("Emp_Basic_Salary");
                    double allowance = rs.getDouble("Emp_Allowance");
                    double deduction = rs.getDouble("Emp_Deduction");
                    double netSalary = basicSalary + allowance - deduction;

                    System.out.println("\n--- PAYROLL SLIP ---");
                    System.out.println("Employee ID: " + empId);
                    System.out.println("Employee Name: " + empName);
                    System.out.println("Position: " + empPosition);
                    System.out.println("--------------------");
                    System.out.printf("Basic Salary: %.2f%n", basicSalary);
                    System.out.printf("Allowance:    %.2f%n", allowance);
                    System.out.printf("Deductions:   %.2f%n", deduction);
                    System.out.println("--------------------");
                    System.out.printf("Net Salary:   %.2f%n", netSalary);
                    System.out.println("--------------------\n");
                } else {
                    System.out.println("No employee found with ID: " + empId);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error generating payroll slip: " + e.getMessage());
            e.printStackTrace();
        }
        
    }    
}