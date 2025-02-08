package luboya_223234095_airlineserver;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Luboya_223234095_AirlineServer {
    private static final int SERVER_PORT = 1750;
    private static final String[][] seatAssignments = new String[2][4];

    public static void main(String[] args) {
        initializeSeatAssignments();

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server started, waiting for connections on port " + SERVER_PORT + "...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    handleClientRequest(clientSocket);
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeSeatAssignments() {
        
        for (int input = 0; input < seatAssignments.length; input++) {
            Arrays.fill(seatAssignments[input], "Available");
        }
    }

    private static void handleClientRequest(Socket clientSocket) {
        try (
            BufferedReader clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter clientOutput = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String clientType = clientInput.readLine(); 
            System.out.println("Connected client type: " + clientType);

            if ("Reservations".equalsIgnoreCase(clientType)) {
                processReservation(clientInput, clientOutput); 
            } else if ("CheckIn".equalsIgnoreCase(clientType)) {
                processCheckIn(clientOutput); 
            } else {
                clientOutput.println("Invalid client type.");
                System.out.println("Invalid client type received from client.");
            }

        } catch (IOException e) {
            System.err.println("Error handling client request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processReservation(BufferedReader clientInput, PrintWriter clientOutput) throws IOException {
        String seatNumber = clientInput.readLine();
        String passengerName = clientInput.readLine();

        if (seatNumber == null || passengerName == null) {
            clientOutput.println("Invalid input. Please provide seat number and passenger name.");
            System.err.println("Invalid reservation data received.");
            return;
        }

        
        if (updateSeatAssignment(seatNumber, passengerName)) {
            clientOutput.println("Reservation confirmed: " + seatNumber + " for " + passengerName);
            System.out.println("Seat " + seatNumber + " reserved for " + passengerName);
        } else {
            clientOutput.println("Error: Seat " + seatNumber + " is not available.");
            System.out.println("Reservation attempt failed for seat " + seatNumber + ": Already taken.");
        }
    }

    private static void processCheckIn(PrintWriter clientOutput) {
        clientOutput.println("SEAT_UPDATE");
        displaySeatAssignments(clientOutput);
    }

    private static boolean updateSeatAssignment(String seatNumber, String passengerName) {
        try {
            int row = seatNumber.charAt(0) - 'A'; 
            int column = Integer.parseInt(seatNumber.substring(1)) - 1; 

            if ("Available".equalsIgnoreCase(seatAssignments[row][column])) {
                seatAssignments[row][column] = passengerName;
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error updating seat assignment: Invalid seat number " + seatNumber);
        }
        return false; 
    }

    private static void displaySeatAssignments(PrintWriter clientOutput) {
        StringBuilder seatChart = new StringBuilder("Seat Availability:\n");

        for (int input_1 = 0; input_1 < seatAssignments.length; input_1++) {
            for (int input_2 = 0; input_2 < seatAssignments[input_1].length; input_2++) {
                seatChart.append("Seat ").append((char) ('A' + input_1)).append(input_2 + 1).append(": ")
                         .append(seatAssignments[input_1][input_2]).append("\n");
            }
        }

        clientOutput.println(seatChart.toString());
        System.out.println("Sent seat availability to client.");
    }
}
