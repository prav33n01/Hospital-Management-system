package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HMS {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/?user=root";

    private static final String username = "root";

    private static final String password = "584826";

    public static void main(String args[]) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while(true) {
                System.out.println(" HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patient.");
                System.out.println("2. View Patients.");
                System.out.println("3. View Doctors.");
                System.out.println("4. Book Appointments.");
                System.out.println("5. Exit. ");
                System.out.println("Enter your choice - ");
                int choice = scanner.nextInt();

                switch(choice) {
                    case 1:
                        patient.addPatient();
                        System.out.println();

                    case 2:
                        patient.viewPatients();
                        System.out.println();

                    case 3:
                        doctor.viewDoctors();
                        System.out.println();


                    case 4:
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();

                    case 5:
                        return;

                    default:
                        System.out.println("Enter valid choice !");
                }

            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter patient id - ");
        int patientId = scanner.nextInt();
        System.out.print("Enter doctor id - ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter the appointment date (YYYY-MM-DD) - ");
        String appointmentDate = scanner.next();

        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if(checkDoctorAvailabiltiy(doctorId, appointmentDate, connection)) {
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                preparedStatement.setInt(1, patientId);
                preparedStatement.setInt(2, doctorId);
                preparedStatement.setString(3, appointmentDate);
                int rowsAffected = preparedStatement.executeUpdate();

                if(rowsAffected > 0) {
                    System.out.println("Appointment Booked.");
                }

            } else {
                System.out.println("Doctor not available on this date.");
            }

        } else {
            System.out.println("Either doctor or patient does'nt exist !");
        }
    }

    private static boolean checkDoctorAvailabiltiy(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT (*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                int count = resultSet.getInt(1);
                if(count == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}




