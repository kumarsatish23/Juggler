package in.vanna;

import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.Random;

public class Main {
    private static final Random random = new Random();
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static volatile boolean running = false;

    public static void main(String[] args) {
        showSplashScreen(); // Show splash screen
        try {
            // Set macOS-like theme
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void showSplashScreen() {
        // Load splash screen image
        ImageIcon splashImage = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/OMUNMO0.png")));
        JWindow splashWindow = new JWindow();

        // Add the splash image to the window
        JLabel splashLabel = new JLabel(splashImage);
        splashWindow.getContentPane().add(splashLabel);

        // Set splash screen size and position
        splashWindow.setSize(splashImage.getIconWidth(), splashImage.getIconHeight());
        splashWindow.setLocationRelativeTo(null);

        // Show the splash screen
        splashWindow.setVisible(true);

        // Simulate loading delay
        try {
            Thread.sleep(3000); // 3 seconds delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Close the splash screen
        splashWindow.setVisible(false);
        splashWindow.dispose();
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Juggler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        // Set the icon for the JFrame (this will set the taskbar icon too)
        setWindowIcon(frame);

        // Create a modern layout
        frame.setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Mouse Juggler", JLabel.CENTER);
        titleLabel.setFont(new Font("San Francisco", Font.BOLD, 24));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton startStopButton = new JButton("Start");
        startStopButton.setFont(new Font("San Francisco", Font.PLAIN, 16));
        startStopButton.setFocusPainted(false);
        buttonPanel.add(startStopButton);
        frame.add(buttonPanel, BorderLayout.CENTER);

        // Add padding and alignments
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        startStopButton.addActionListener((ActionEvent e) -> {
            if (running) {
                running = false;
                startStopButton.setText("Start");
            } else {
                running = true;
                startStopButton.setText("Stop");
                new Thread(Main::runApplication).start();
            }
        });

        // Center the window
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void runApplication() {
        try {
            Robot robot = new Robot();
            while (running) {
                if (!running) break;
                switchScreens(robot);
                if (!running) break;
                wiggleMouse(robot);
            }
        } catch (Exception e) {
            System.out.println("\nProgram terminated.");
        }
    }

    private static void switchScreens(Robot robot) {
        int maxSwitches = random.nextInt(5) + 1;

        robot.keyPress(KeyEvent.VK_ALT);
        for (int i = 0; i < maxSwitches; i++) {
            if (!running) break;
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_TAB);
            delay(500);
        }
        robot.keyRelease(KeyEvent.VK_ALT);
    }

    private static void wiggleMouse(Robot robot) {
        int maxWiggles = random.nextInt(6) + 4;

        for (int i = 0; i < maxWiggles; i++) {
            if (!running) break;
            int[] startCoords = getCurrentMousePosition();
            int[] endCoords = getRandomCoords();

            int duration = random.nextInt(4000) + 1000;
            smoothMouseMove(robot, startCoords[0], startCoords[1], endCoords[0], endCoords[1], duration);

            delay(10000);
        }
    }

    private static void smoothMouseMove(Robot robot, int x1, int y1, int x2, int y2, int duration) {
        int distance = (int) Math.hypot(x2 - x1, y2 - y1);
        int steps = Math.max(10, Math.min(500, distance / 5));
        int delay = duration / steps;

        for (int i = 0; i <= steps; i++) {
            if (!running) break;
            int x = x1 + (x2 - x1) * i / steps;
            int y = y1 + (y2 - y1) * i / steps;
            robot.mouseMove(x, y);
            delay(delay);
        }
    }

    private static int[] getCurrentMousePosition() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point point = pointerInfo.getLocation();
        return new int[]{(int) point.getX(), (int) point.getY()};
    }

    private static int[] getRandomCoords() {
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();

        int x = random.nextInt(width - 300) + 100;
        int y = random.nextInt(height - 300) + 100;

        return new int[]{x, y};
    }

    private static void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Sets the application window and taskbar icon.
     */
    private static void setWindowIcon(JFrame frame) {
        try {
            // Load the icon from resources folder
            ImageIcon icon = new ImageIcon(Main.class.getResource("/Juggler.png"));
            frame.setIconImage(icon.getImage());  // Set the window and taskbar icon
            Taskbar taskbar = Taskbar.getTaskbar();
            Image tIcon = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/Juggler.png"));
            taskbar.setIconImage(tIcon);
        } catch (Exception e) {
            System.out.println("Error setting icon: " + e.getMessage());
        }
    }
}
