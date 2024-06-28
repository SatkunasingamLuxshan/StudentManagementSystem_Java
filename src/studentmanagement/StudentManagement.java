/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package studentmanagement;
import java.io.IOException;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class StudentManagement {

    static Connection con;
    static Statement st;
    public static void main(String[] args) {
        try {
            connectDB();
            dashboard();
        } catch (Exception e) {
            
        }
    }

    public static void connectDB() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/student_management_system";
        String user = "root";
        String password = "";
        con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();
    }


    public static void dashboard() throws Exception {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.println("..........................................................");
            System.out.println(" | Welcome to Developer Stack Student Management System | ");
            System.out.println("..........................................................\n");
            System.out.println(" 1 Add Student");
            System.out.println(" 2 Edit Student");
            System.out.println(" 3 Delete Student");
            System.out.println(" 4 View All Students");
            System.out.println(" 5 View Student by NIC");

            System.out.print("Enter Your Choice: ");
            try {
                int choice = s.nextInt();
                s.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        addStudent();
                        break;
                    case 2:
                        editStudent();
                        break;
                    case 3:
                        deleteStudent();
                        break;
                    case 4:
                        viewAllStudents();
                        break;
                    case 5:
                        viewStudentByNIC();
                        break;
                    default:
                        System.out.println("Invalid input. Please enter a number between 1 and 5.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
                s.next(); // Clear the invalid input from the scanner
            }
        }
    }

  
    
    public static void addStudent() throws Exception {
    Scanner s = new Scanner(System.in);
 
    System.out.println("..........................................................");
    System.out.println(" | Welcome to Developer Stack Student Management System | ");
    System.out.println("..........................................................\n");
    System.out.println("-----------------");
    System.out.println(" | Add Student | ");
    System.out.println("-----------------\n");

    System.out.print("Enter Student Name: ");
    String name = s.nextLine();

    System.out.print("Enter Student NIC: ");
    String nic = s.nextLine();

    // Check if NIC already exists
    String checkQuery = "SELECT COUNT(*) FROM students WHERE nic = ?";
    PreparedStatement checkPs = con.prepareStatement(checkQuery);
    checkPs.setString(1, nic);
    ResultSet checkRs = checkPs.executeQuery();
    checkRs.next();
    int count = checkRs.getInt(1);
    if (count > 0) {
        System.out.println("This NIC is already registered. Please use a different NIC.");
        System.out.print("Press any key to return to the dashboard.\n");
        s.nextLine();
        dashboard();
        return;
    }

    System.out.print("Enter Student Gender: ");
    String gender = s.nextLine();

    System.out.print("Enter Student Age: ");
    int age = 0;
    try {
        age = s.nextInt();
        s.nextLine(); // consume newline
    } catch (InputMismatchException e) {
        System.out.println("Invalid input for age. Please enter a valid number.");
        s.next(); // clear the invalid input
        addStudent();
        return;
    }

    String query = "INSERT INTO students (name, nic, gender, age) VALUES (?, ?, ?, ?)";
    PreparedStatement ps = con.prepareStatement(query);
    ps.setString(1, name);
    ps.setString(2, nic);
    ps.setString(3, gender);
    ps.setInt(4, age);
    ps.executeUpdate();

    System.out.println("Successfully Added Student\n");
    System.out.println("Do you want to add another student? Press 'Y' or any other key to return to dashboard\n");
    String selection = s.nextLine();
    if (selection.equalsIgnoreCase("Y")) {
        addStudent();
    } else {
        dashboard();
    }
}


    public static void editStudent() throws Exception {
    Scanner s = new Scanner(System.in);
    System.out.println("..........................................................");
    System.out.println(" | Welcome to Developer Stack Student Management System | ");
    System.out.println("..........................................................\n");
    System.out.println("------------------");
    System.out.println(" | Edit Student | ");
    System.out.println("------------------\n");

    System.out.print("Enter Student NIC Number: ");
    String nic = s.nextLine();

    String query = "SELECT * FROM students WHERE nic = ?";
    PreparedStatement ps = con.prepareStatement(query);
    ps.setString(1, nic);
    ResultSet rs = ps.executeQuery();

    if (!rs.next()) {
        System.out.println("Student not found!");
        dashboard();
        return;
    }

    int id = rs.getInt("id");
    String currentNic = rs.getString("nic");

    System.out.println("Student Details: ");
    System.out.println("ID: " + id);
    System.out.println("Current Name: " + rs.getString("name"));
    System.out.println("Current NIC: " + currentNic);
    System.out.println("Current Gender: " + rs.getString("gender"));
    System.out.println("Current Age: " + rs.getInt("age"));
    System.out.println();

    System.out.println("Which field to edit?");
    System.out.println(" 0-Name ");
    System.out.println(" 1-NIC ");
    System.out.println(" 2-Gender ");
    System.out.println(" 3-Age ");
    int choice = 0;
    try {
        choice = s.nextInt();
        s.nextLine(); // consume newline
    } catch (InputMismatchException e) {
        System.out.println("Invalid input. Please enter a number between 0 and 3.");
        s.next(); // clear the invalid input
        editStudent();
        return;
    }

    switch (choice) {
        case 0:
            System.out.print("Enter new name: ");
            String name = s.nextLine();
            query = "UPDATE students SET name = ? WHERE id = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, name);
            ps.setInt(2, id);
            ps.executeUpdate();
            break;
        case 1:
            System.out.print("Enter new NIC: ");
            String newNic = s.nextLine();

            // Check if the new NIC already exists
            query = "SELECT * FROM students WHERE nic = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, newNic);
            rs = ps.executeQuery();

            if (rs.next() && !newNic.equals(currentNic)) {
                System.out.println("NIC already exists. Please enter a different NIC.");
                editStudent();
                return;
            }

            query = "UPDATE students SET nic = ? WHERE id = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, newNic);
            ps.setInt(2, id);
            ps.executeUpdate();
            break;
        case 2:
            System.out.print("Enter new gender: ");
            String gender = s.nextLine();
            query = "UPDATE students SET gender = ? WHERE id = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, gender);
            ps.setInt(2, id);
            ps.executeUpdate();
            break;
        case 3:
            System.out.print("Enter new age: ");
            int age = s.nextInt();
            query = "UPDATE students SET age = ? WHERE id = ?";
            ps = con.prepareStatement(query);
            ps.setInt(1, age);
            ps.setInt(2, id);
            ps.executeUpdate();
            break;
        default:
            System.out.println("Invalid choice");
            break;
    }

    System.out.println("Student details updated successfully\n");
    System.out.print("Press any key to go to Dashboard \n");
    s.nextLine(); // consume newline
    s.nextLine();
    dashboard();
}


    public static void deleteStudent() throws Exception {
        Scanner s = new Scanner(System.in);
        System.out.println("..........................................................");
        System.out.println(" | Welcome to Developer Stack Student Management System | ");
        System.out.println("..........................................................\n");
        System.out.println("--------------------");
        System.out.println(" | Delete Student | ");
        System.out.println("--------------------\n");

        System.out.print("Enter Student NIC Number: ");
        String nic = s.nextLine();

        String query = "DELETE FROM students WHERE nic = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, nic);
        int rowsAffected = ps.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Student deleted successfully\n");
        } else {
            System.out.println("Student not found\n");
        }

        System.out.print("Press any key to go to Dashboard \n");
        s.nextLine();
        dashboard();
    }

    public static void viewAllStudents() throws Exception {
        Scanner s = new Scanner(System.in);
        System.out.println("..........................................................");
        System.out.println(" | Welcome to Developer Stack Student Management System | ");
        System.out.println("..........................................................\n");
        System.out.println("-----------------");
        System.out.println(" | View All Students | ");
        System.out.println("-----------------\n");

        String query = "SELECT * FROM students";
        ResultSet rs = st.executeQuery(query);

        while (rs.next()) {
            String name = rs.getString("name");
            String nic = rs.getString("nic");
            String gender = rs.getString("gender");
            int age = rs.getInt("age");
            System.out.println("Name: " + name + ", NIC: " + nic + ", Gender: " + gender + ", Age: " + age);
        }

        System.out.print("Press any key to go to Dashboard \n");
        s.nextLine();
        dashboard();
    }

    public static void viewStudentByNIC() throws Exception {
        Scanner s = new Scanner(System.in);
        System.out.println("..........................................................");
        System.out.println(" | Welcome to Developer Stack Student Management System | ");
        System.out.println("..........................................................\n");
        System.out.println("-----------------");
        System.out.println(" | View Student by NIC | ");
        System.out.println("-----------------\n");

        System.out.print("Enter Student NIC Number: ");
        String nic = s.nextLine();

        String query = "SELECT * FROM students WHERE nic = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, nic);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String name = rs.getString("name");
            String gender = rs.getString("gender");
            int age = rs.getInt("age");
            System.out.println("Name: " + name + ", NIC: " + nic + ", Gender: " + gender + ", Age: " + age);
        } else {
            System.out.println("Student not found");
        }

        System.out.print("Press any key to go to Dashboard \n");
        s.nextLine();
        dashboard();
    }

    
}
