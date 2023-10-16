package Student;

import com.project.grpc.register.Student;
import com.project.grpc.register.clientGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();
        clientGrpc.clientBlockingStub clientBlockingStub = clientGrpc.newBlockingStub(managedChannel);
        boolean auth = false;
        Scanner inp = new Scanner(System.in);
        while (!auth) {
            System.out.print("Enter user name: ");
            String name = inp.next();
            System.out.print("Enter password: ");
            String pass = inp.next();
            Student.LoginRequest loginRequest = Student.LoginRequest.newBuilder()
                    .setUserName(name)
                    .setPassword(pass)
                    .build();
            Student.Response response = clientBlockingStub.login(loginRequest);
            if (response.getResponseCode() == 200) {
                auth = true;
            }
            System.out.println(response.getResponse());
        }
        System.out.print("Enter registration ID: ");
        long ID = inp.nextLong();
        System.out.print("Enter student name: ");
        String studentName = inp.next();

        Student.RegisterRequest registerRequest = Student.RegisterRequest.newBuilder()
                .setRegistrationID(ID)
                .setStudentName(studentName)
                .build();
        Student.RegResponse regResponse = clientBlockingStub.register(registerRequest);
        System.out.println(regResponse.getResponse());
        managedChannel.shutdown();
    }
}
