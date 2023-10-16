package Service;

import com.project.grpc.register.Student;
import com.project.grpc.register.studentGrpc;
import io.grpc.stub.StreamObserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class RegistrationService extends studentGrpc.studentImplBase {

    // MySQL info
    private static final String URL = "jdbc:mysql://localhost:3306/student_registration";
    private static final String USER = "root";
    private static final String PASS = "";

    @Override
    public void login(Student.LoginRequest request, StreamObserver<Student.Response> responseObserver) throws SQLException, ClassNotFoundException {
        String userName = request.getUserName();
        String password = request.getPassword();

        ResultSet resultSet = checkLoginInfo(userName, password);

        Student.Response.Builder response = Student.Response.newBuilder();
        while (resultSet.next()) {
            if (resultSet.getInt(1) == 1) {
                response.setResponseCode(200).setResponse("Successfully logged in");
            } else {
                response.setResponseCode(404).setResponse("Wrong username or password");
            }
            break;
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    private ResultSet checkLoginInfo(String userName, String password) throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(URL, USER, PASS);
        PreparedStatement statement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM login_info" +
                " WHERE username = ? && pass = ?)");
        statement.setString(1, userName);
        statement.setString(2, password);
        return statement.executeQuery();
    }

    @Override
    public void register(Student.RegisterRequest request, StreamObserver<Student.RegResponse> responseObserver) throws SQLException {
        long regID = request.getRegistrationID();
        String studentName = request.getStudentName();

        ResultSet resultSet = checkRegInfo(regID);

        Student.RegResponse.Builder regResponse = Student.RegResponse.newBuilder();
        while (resultSet.next()) {
            if (resultSet.getInt(1) == 1) {
                regResponse.setResponse("Registration ID " + regID + " is already registered").setResponseCode(500);
            } else {
                Connection connection = DriverManager.getConnection(URL, USER, PASS);

                PreparedStatement statement = connection.prepareStatement("INSERT INTO registration_list VALUES(?, ?)");
                statement.setLong(1, regID);
                statement.setString(2, studentName);
                statement.executeUpdate();
                regResponse.setResponse(studentName +
                        " with registration ID " + regID + " is now registered successfully").setResponseCode(300);
            }
            break;
        }
        responseObserver.onNext(regResponse.build());
        responseObserver.onCompleted();
    }

    private ResultSet checkRegInfo(long regID) throws SQLException {

        Connection connection = DriverManager.getConnection(URL, USER, PASS);
        PreparedStatement statement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM registration_list WHERE Reg_ID = ?)");
        statement.setLong(1, regID);
        return statement.executeQuery();
    }
}
