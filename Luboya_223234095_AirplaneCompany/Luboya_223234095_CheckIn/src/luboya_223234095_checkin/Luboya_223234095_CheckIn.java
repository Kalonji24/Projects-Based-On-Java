package luboya_223234095_checkin;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Luboya_223234095_CheckIn extends Application {
    private static final String SERVER_ADDRESS = "localHost"; 
    private static final int SERVER_PORT = 1750;

    @Override
    public void start(Stage primaryStage) {
        TextArea seatAvailability = new TextArea();
        seatAvailability.setEditable(false);
        seatAvailability.setText("Connecting to server...");

        StackPane root = new StackPane(seatAvailability);
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("CheckIn Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        
        connectToServer(seatAvailability);
    }

    private void connectToServer(TextArea seatAvailability) {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                
                out.println("CheckIn");
                seatAvailability.setText("Connected to server. Retrieving seat data...");

                String response;
                while ((response = in.readLine()) != null) {
                    if ("SEAT_UPDATE".equals(response)) {
                        StringBuilder seatData = new StringBuilder("Seat Availability:\n");
                        String line;
                        while ((line = in.readLine()) != null && !line.isEmpty()) {
                            seatData.append(line).append("\n");
                        }
                        seatAvailability.setText(seatData.toString());
                    }
                }

            } catch (IOException e) {
                seatAvailability.setText("Connection error. Retrying...");
                System.err.println("Connection error: " + e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);  
    }

    public static void main(String[] args) {
        launch(args);
    }
}