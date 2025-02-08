package luboya_223234095_flight.reservations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Luboya_223234095_FlightReservations {
    private JFrame airplane;
    private JFrame passenger;
    private JTextField passengerNameField;
    private String selectedSeat;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Luboya_223234095_FlightReservations::new);
    }

    public Luboya_223234095_FlightReservations() {
        setupAirplaneWindow();
        setupPassengerWindow();
    }

    private void setupAirplaneWindow() {
        airplane = new JFrame("Reservations Airplane");
        airplane.setSize(300, 300);
        airplane.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        airplane.setLayout(new GridLayout(4, 2, 10, 10));

       
        String[] seats = {"A1", "B1", "A2", "B2", "A3", "B3", "A4", "B4"};
        for (String seat : seats) {
            Button button = new Button(seat);
            button.addActionListener(new SeatButtonListener(seat, button));
            airplane.add(button);
        }

        airplane.setVisible(true);
    }

    private void setupPassengerWindow() {
        passenger = new JFrame("Reservations Passenger");
        passenger.setSize(300, 150);
        passenger.setLayout(new BorderLayout());

        passengerNameField = new JTextField();
        passengerNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    reserveSeat();
                }
            }
        });

        passenger.add(new JLabel("Enter Passenger Name:"), BorderLayout.NORTH);
        passenger.add(passengerNameField, BorderLayout.CENTER);
        passenger.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    private void reserveSeat() {
        String passengerName = passengerNameField.getText().trim();
        if (!passengerName.isEmpty() && selectedSeat != null) {
            new Communicator().sendToServer("Reservations", selectedSeat, passengerName);
            passengerNameField.setText("");
            passenger.setVisible(false);
            airplane.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(passenger, "Please enter a valid name and select a seat.");
        }
    }

    private class Communicator {
        public void sendToServer(String clientType, String selectedSeat, String passengerName) {
            try (Socket socket = new Socket("localHost", 1750);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(clientType);
                out.println(selectedSeat);
                out.println(passengerName);
                JOptionPane.showMessageDialog(airplane, "Reservation confirmed for " + passengerName);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(airplane, "Error connecting to server: " + e.getMessage());
            }
        }
    }

    private class SeatButtonListener implements ActionListener {
        private String seat;
        private Button seatButton;

        public SeatButtonListener(String seat, Button seatButton) {
            this.seat = seat;
            this.seatButton = seatButton;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            selectedSeat = seat;
            seatButton.setEnabled(false);
            airplane.setVisible(false);
            passenger.setVisible(true);
        }
    }
}
