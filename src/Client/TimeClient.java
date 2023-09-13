package Client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class TimeClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    
    private static boolean running = false; // Flag to indicate whether the client is running
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Time Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);

        JLabel timeLabel = new JLabel("Time: ");
        frame.add(timeLabel, BorderLayout.CENTER);

        JButton runButton = new JButton("Run");
        JButton stopButton = new JButton("Stop");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(runButton);
        buttonPanel.add(stopButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread readerThread = new Thread(() -> {
                try {
                    while (true) {
                        if (running) {
                            String currentTime = in.readLine();
                            SwingUtilities.invokeLater(() -> {
                                timeLabel.setText("Time: " + currentTime);
                            });
                        } else {
                            // Sleep when not running to reduce CPU usage
                            Thread.sleep(1000);
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
            readerThread.start();

            runButton.addActionListener(e -> running = true); // Start displaying time
            stopButton.addActionListener(e -> running = false); // Stop displaying time

            frame.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
