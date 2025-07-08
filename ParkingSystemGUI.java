import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Date;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import javax.swing.DefaultComboBoxModel;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.border.*;
import java.awt.geom.*;
import javax.swing.Timer;

public class ParkingSystemGUI extends JFrame {
    private static ParkingLot parkingLot;
    private static User loggedInUser;
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel contentPanel;
    private Color themeColor = Color.BLACK;
    private Color accentColor = Color.BLACK;
    private Timer animationTimer;
    private float animationAngle = 0;

    public ParkingSystemGUI() {
        setTitle("Parking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        // Add window closing handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (parkingLot != null) {
                    parkingLot.saveState();
                }
                if (animationTimer != null) {
                    animationTimer.stop();
                }
            }
        });

        // Modern header bar
        JPanel headerBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.BLACK, getWidth(), 0, Color.BLACK);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerBar.setPreferredSize(new Dimension(1024, 60));
        headerBar.setLayout(new BorderLayout());
        JLabel logoLabel = new JLabel("P", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 36));
        JLabel nameLabel = new JLabel("Parking Management System", SwingConstants.LEFT);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 28));
        nameLabel.setForeground(Color.WHITE);
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        leftPanel.setOpaque(false);
        leftPanel.add(logoLabel);
        leftPanel.add(nameLabel);
        headerBar.add(leftPanel, BorderLayout.WEST);
        // Add current time
        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        timeLabel.setForeground(Color.WHITE);
        headerBar.add(timeLabel, BorderLayout.EAST);
        Timer timeTimer = new Timer(1000, e -> {
            timeLabel.setText(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        });
        timeTimer.start();
        timeLabel.setText(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        // Add header bar to frame
        setLayout(new BorderLayout());
        add(headerBar, BorderLayout.NORTH);

        // Initialize animation timer
        animationTimer = new Timer(50, e -> {
            animationAngle += 0.1f;
            if (animationAngle > 2 * Math.PI) {
                animationAngle = 0;
            }
            if (contentPanel != null) {
                contentPanel.repaint();
            }
        });
        animationTimer.start();

        // Use BackgroundPanel for mainPanel with the background image
        mainPanel = new BackgroundPanel("download (7).jpg");
        mainPanel.setLayout(new CardLayout());
        // mainPanel.setBackground(new Color(236, 240, 241)); // Remove this line
        add(mainPanel, BorderLayout.CENTER);

        // Create and add login panel
        createLoginPanel();
        createContentPanel();

        // Add panels to main panel
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(contentPanel, "CONTENT");

        // Show login panel first
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "LOGIN");

        // Initialize files on startup
        initializeFiles();
    }

    private void initializeFiles() {
        try {
            createFileIfNotExists("users.txt", true);
            createFileIfNotExists("tickets.txt", false);
            createFileIfNotExists("parking_history.txt", false);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error initializing system files: " + e.getMessage(),
                "System Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void createFileIfNotExists(String filename, boolean addDefaults) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("Could not create " + filename);
            }
            if (addDefaults && filename.equals("users.txt")) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("admin1,Admin User,Admin,admin123\n");
                    writer.write("entry1,Entry Operator 1,EntryOperator,entry123\n");
                    writer.write("exit1,Exit Operator 1,ExitOperator,exit123\n");
                    writer.write("cust1,Customer 1,Customer,cust123\n");
                }
            }
        }
    }

    private void createLoginPanel() {
        // Use a transparent panel for loginPanel so the background shows through
        loginPanel = new JPanel();
        loginPanel.setOpaque(false);
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Translucent form panel
        JPanel translucentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 200)); // white, ~80% opacity
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
            }
        };
        translucentPanel.setOpaque(false);
        translucentPanel.setLayout(new GridBagLayout());
        translucentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints tgbc = new GridBagConstraints();
        tgbc.insets = new Insets(12, 12, 12, 12);
        tgbc.gridx = 0; tgbc.gridy = 0; tgbc.gridwidth = 2; tgbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Parking System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLACK);
        translucentPanel.add(titleLabel, tgbc);
        tgbc.gridwidth = 1;
        tgbc.gridy++;
        tgbc.anchor = GridBagConstraints.EAST;
        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setFont(new Font("Arial", Font.BOLD, 18));
        translucentPanel.add(userIdLabel, tgbc);
        tgbc.gridx = 1; tgbc.anchor = GridBagConstraints.WEST;
        JTextField userIdField = new JTextField(20);
        userIdField.setFont(new Font("Arial", Font.PLAIN, 16));
        translucentPanel.add(userIdField, tgbc);
        tgbc.gridx = 0; tgbc.gridy++; tgbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 18));
        translucentPanel.add(passwordLabel, tgbc);
        tgbc.gridx = 1; tgbc.anchor = GridBagConstraints.WEST;
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        translucentPanel.add(passwordField, tgbc);
        tgbc.gridx = 0; tgbc.gridy++; tgbc.gridwidth = 2; tgbc.anchor = GridBagConstraints.CENTER;
        GameButton loginButton = new GameButton("Login");
        translucentPanel.add(loginButton, tgbc);

        // Button hover
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(accentColor);
                loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(themeColor);
                loginButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        // Login action
        loginButton.addActionListener(e -> {
            String userId = userIdField.getText();
            String password = new String(passwordField.getPassword());
            loggedInUser = UserDatabase.authenticate(userId, password);
            if (loggedInUser == null) {
                JOptionPane.showMessageDialog(this, "Invalid credentials! Try again!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            } else {
                initializeSystem();
                updateContentPanel();
                CardLayout cl = (CardLayout) mainPanel.getLayout();
                cl.show(mainPanel, "CONTENT");
                userIdField.setText("");
                passwordField.setText("");
            }
        });

        // Add translucent panel to center of loginPanel
        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(translucentPanel, gbc);
    }

    private void createContentPanel() {
        contentPanel = new BackgroundPanel("C:/Users/y/Desktop/rrr/PL2 Project (Test Cases Version)/download (7).jpg");
        contentPanel.setLayout(new BorderLayout(20, 20));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    private void updateContentPanel() {
        contentPanel.removeAll();

        // Create header panel with game-like styling
        JPanel headerPanel = new GamePanel();
        headerPanel.setLayout(new BorderLayout(10, 10));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10),
            new EmptyBorder(10, 15, 10, 15)
        ));

        String roleIcon = switch (loggedInUser.getRole().toLowerCase()) {
            case "admin" -> " ";
            case "entryoperator" -> " ";
            case "exitoperator" -> " ";
            default -> " ";
        };

        JLabel welcomeLabel = new GameLabel("Welcome, " + loggedInUser.getName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        GameButton logoutButton = new GameButton("🚪 Logout");
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Create main content based on user role with game-like visualization
        JPanel rolePanel = new JPanel(new BorderLayout(10, 10));
        rolePanel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        switch (loggedInUser.getRole().toLowerCase()) {
            case "admin":
                createAdminGamePanel(buttonPanel);
                break;
            case "entryoperator":
                createEntryOperatorGamePanel(buttonPanel);
                break;
            case "exitoperator":
                createExitOperatorGamePanel(buttonPanel);
                break;
            case "customer":
                createCustomerGamePanel(buttonPanel);
                break;
        }

        rolePanel.add(buttonPanel, BorderLayout.WEST);

        // Add visualization panel
        JPanel visualPanel = new ParkingVisualizationPanel();
        JScrollPane scrollPane = new JScrollPane(visualPanel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rolePanel.add(scrollPane, BorderLayout.CENTER);

        // Add panels to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(rolePanel, BorderLayout.CENTER);

        // Add logout action
        logoutButton.addActionListener(e -> {
            if (parkingLot != null) {
                parkingLot.saveState();
            }
            loggedInUser = null;
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, "LOGIN");
        });

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void createAdminGamePanel(JPanel panel) {
        GameButton viewSystemStatusBtn = new GameButton("View System Status");
        GameButton manageParkingLotsBtn = new GameButton("Manage Parking Lots");
        GameButton viewReportsBtn = new GameButton("View Reports");
        GameButton manageUsersBtn = new GameButton("Manage Users");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(viewSystemStatusBtn, gbc);
        gbc.gridy++;
        panel.add(manageParkingLotsBtn, gbc);
        gbc.gridy++;
        panel.add(viewReportsBtn, gbc);
        gbc.gridy++;
        panel.add(manageUsersBtn, gbc);
        viewSystemStatusBtn.addActionListener(e -> showSystemStatus());
        manageParkingLotsBtn.addActionListener(e -> manageParkingLots());
        viewReportsBtn.addActionListener(e -> viewReports());
        manageUsersBtn.addActionListener(e -> manageUsers());
    }

    private void createEntryOperatorGamePanel(JPanel panel) {
        GameButton issueTicketBtn = new GameButton("Issue Ticket");
        GameButton viewAvailableSpotsBtn = new GameButton("View Available Spots");
        GameButton viewOperatorInfoBtn = new GameButton("View Operator Info");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(issueTicketBtn, gbc);
        gbc.gridy++;
        panel.add(viewAvailableSpotsBtn, gbc);
        gbc.gridy++;
        panel.add(viewOperatorInfoBtn, gbc);
        issueTicketBtn.addActionListener(e -> issueTicket());
        viewAvailableSpotsBtn.addActionListener(e -> viewAvailableSpots());
        viewOperatorInfoBtn.addActionListener(e -> viewOperatorInfo());
    }

    private void createExitOperatorGamePanel(JPanel panel) {
        GameButton processCheckoutBtn = new GameButton("💳", "Process Checkout");
        GameButton viewExitInfoBtn = new GameButton("ℹ️", "View Exit Information");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(processCheckoutBtn, gbc);
        gbc.gridy++;
        panel.add(viewExitInfoBtn, gbc);
        processCheckoutBtn.addActionListener(e -> processCheckout());
        viewExitInfoBtn.addActionListener(e -> viewExitOperatorInfo());
    }

    private void createCustomerGamePanel(JPanel panel) {
        GameButton viewTicketBtn = new GameButton("🎫", "View My Ticket");
        GameButton viewParkingStatusBtn = new GameButton("🅿️", "View Parking Status");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(viewTicketBtn, gbc);
        gbc.gridy++;
        panel.add(viewParkingStatusBtn, gbc);
        viewTicketBtn.addActionListener(e -> viewTicket());
        viewParkingStatusBtn.addActionListener(e -> viewParkingStatus());
    }

    private void initializeSystem() {
        try {
            // Try to load existing parking lot state first
            parkingLot = ParkingLot.loadState();
            if (parkingLot != null) {
                System.out.println("[DEBUG] Loaded parking lot with spots: " + parkingLot.getTotalSpots());
            }
            // Only initialize new parking lot if none exists
            if (parkingLot == null) {
                int totalSpots = 30; // default value

                // If admin is logging in, allow them to set the initial spot count
                if (loggedInUser.getRole().equalsIgnoreCase("Admin")) {
                    String input = JOptionPane.showInputDialog(this,
                        "Enter total number of parking spots:",
                        "Initialize Parking Lot",
                        JOptionPane.QUESTION_MESSAGE);

                    if (input != null) {
                        try {
                            totalSpots = Integer.parseInt(input);
                            if (totalSpots <= 0) {
                                JOptionPane.showMessageDialog(this,
                                    "Invalid number of spots. Using default (30).",
                                    "Warning",
                                    JOptionPane.WARNING_MESSAGE);
                                totalSpots = 30;
                            }
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(this,
                                "Invalid input. Using default (30) spots.",
                                "Warning",
                                JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }

                try {
                    parkingLot = ParkingLot.createNewLot(totalSpots);
                    if (parkingLot == null) {
                        throw new Exception("Failed to create parking lot");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Error creating parking lot: " + e.getMessage(),
                        "System Error",
                        JOptionPane.ERROR_MESSAGE);
                    loggedInUser = null;
                    CardLayout cl = (CardLayout) mainPanel.getLayout();
                    cl.show(mainPanel, "LOGIN");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error initializing system: " + e.getMessage(),
                "System Error",
                JOptionPane.ERROR_MESSAGE);
            loggedInUser = null;
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, "LOGIN");
        }
    }

    private void showSystemStatus() {
        if (!loggedInUser.getRole().equalsIgnoreCase("Admin")) {
            return;
        }

        // Validate state before showing status
        parkingLot.validateState();

        StringBuilder status = new StringBuilder();
        status.append("=== System Status ===\n\n");
        status.append("Total Spots: ").append(parkingLot.getTotalSpots()).append("\n");
        status.append("Occupied Spots: ").append(parkingLot.getOccupiedSpots()).append("\n");
        status.append("Available Spots: ").append(parkingLot.getTotalSpots() - parkingLot.getOccupiedSpots()).append("\n\n");

        status.append("Occupied Spot Details:\n");
        for (ParkingSpot spot : parkingLot.getAllSpots()) {
            if (spot.isOccupied()) {
                status.append("Spot ").append(spot.getSpotId())
                      .append(": ").append(spot.getVehiclePlateNumber())
                      .append(" (since ").append(spot.getEntryTime()).append(")\n");
            }
        }

        showSystemStatusCard(status.toString());
    }

    private void showSystemStatusCard(String statusText) {
        JDialog dialog = new JDialog(this, "System Status", true);
        dialog.setUndecorated(true);
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2d.setColor(new Color(0,0,0,40));
                g2d.fillRoundRect(6, 8, getWidth()-12, getHeight()-12, 24, 24);
                // Card background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 0, 0));
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(Color.BLACK);
        JLabel icon = new JLabel("🅿️"); // or other icon
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setForeground(Color.WHITE);
        icon.setBorder(new EmptyBorder(0, 24, 0, 32));
        JLabel title = new JLabel("System Status");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(icon);
        header.add(title);
        card.add(header, BorderLayout.NORTH);
        // Status text
        JTextArea textArea = new JTextArea(statusText);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        textArea.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        card.add(new JScrollPane(textArea), BorderLayout.CENTER);
        // OK button
        JButton okBtn = new JButton("OK");
        okBtn.setFont(new Font("Arial", Font.BOLD, 16));
        okBtn.setBackground(Color.BLACK);
        okBtn.setForeground(Color.WHITE);
        okBtn.setFocusPainted(false);
        okBtn.setBorder(new RoundedBorder(12));
        okBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(okBtn);
        card.add(btnPanel, BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(500, 350));
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void manageParkingLots() {
        if (!loggedInUser.getRole().equalsIgnoreCase("Admin")) {
            return;
        }

        JDialog dialog = new JDialog(this, "Manage Parking Lots", true);
        dialog.setUndecorated(true);
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0,0,0,40));
                g2d.fillRoundRect(6, 8, getWidth()-12, getHeight()-12, 24, 24);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 0, 0));
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(Color.BLACK);
        JLabel icon = new JLabel("🅿️"); // or other icon
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setForeground(Color.WHITE);
        icon.setBorder(new EmptyBorder(0, 24, 0, 32));
        JLabel title = new JLabel("Manage Parking Lots");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(icon);
        header.add(title);
        card.add(header, BorderLayout.NORTH);
        // Center buttons
        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 0, 16, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        GameButton addSpotsBtn = new GameButton("Add Parking Spots");
        btnPanel.add(addSpotsBtn, gbc);
        gbc.gridy++;
        GameButton removeSpotsBtn = new GameButton("Remove Parking Spots");
        btnPanel.add(removeSpotsBtn, gbc);
        gbc.gridy++;
        GameButton viewSpotsBtn = new GameButton("View All Spots");
        btnPanel.add(viewSpotsBtn, gbc);
        card.add(btnPanel, BorderLayout.CENTER);
        // Close button
        JPanel closePanel = new JPanel();
        closePanel.setOpaque(false);
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));
        closeBtn.setBackground(Color.BLACK);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(new RoundedBorder(12));
        closeBtn.addActionListener(e -> dialog.dispose());
        closePanel.add(closeBtn);
        card.add(closePanel, BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(520, 400));
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        // Add actions
        addSpotsBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(dialog,
                "Enter number of spots to add:",
                "Add Parking Spots",
                JOptionPane.QUESTION_MESSAGE);

            if (input != null) {
                try {
                    int additionalSpots = Integer.parseInt(input);
                    if (additionalSpots <= 0) {
                        JOptionPane.showMessageDialog(dialog,
                            "Number of spots to add must be positive.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Add spots to existing parking lot
                    parkingLot.addSpots(additionalSpots);

                    JOptionPane.showMessageDialog(dialog,
                        additionalSpots + " parking spots added successfully.\nTotal spots now: " + parkingLot.getTotalSpots(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Invalid input. Please enter a number.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Error adding spots: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        removeSpotsBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(dialog,
                "Enter number of spots to remove:",
                "Remove Parking Spots",
                JOptionPane.QUESTION_MESSAGE);
            if (input != null) {
                try {
                    int numToRemove = Integer.parseInt(input);
                    if (numToRemove <= 0) {
                        JOptionPane.showMessageDialog(dialog,
                            "Number of spots to remove must be positive.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    parkingLot.removeSpots(numToRemove);
                    JOptionPane.showMessageDialog(dialog,
                        numToRemove + " parking spots removed successfully.\nTotal spots now: " + parkingLot.getTotalSpots(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Invalid input. Please enter a number.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                } catch (IllegalStateException | IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(dialog,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Error removing spots: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        viewSpotsBtn.addActionListener(e -> {
            StringBuilder spotsInfo = new StringBuilder();
            spotsInfo.append("=== All Parking Spots ===\n\n");

            for (ParkingSpot spot : parkingLot.getAllSpots()) {
                spotsInfo.append("Spot ").append(spot.getSpotId()).append(": ");
                if (spot.isOccupied()) {
                    spotsInfo.append("Occupied by ").append(spot.getVehiclePlateNumber())
                            .append(" since ").append(spot.getEntryTime());
                } else {
                    spotsInfo.append("Available");
                }
                spotsInfo.append("\n");
            }

            JTextArea textArea = new JTextArea(spotsInfo.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(dialog,
                scrollPane,
                "All Spots",
                JOptionPane.INFORMATION_MESSAGE);
        });

        dialog.setVisible(true);
    }

    private void viewReports() {
        if (!loggedInUser.getRole().equalsIgnoreCase("Admin")) {
            return;
        }

        JDialog dialog = new JDialog(this, "View Reports", true);
        dialog.setUndecorated(true);
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0,0,0,40));
                g2d.fillRoundRect(6, 8, getWidth()-12, getHeight()-12, 24, 24);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 0, 0));
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(Color.BLACK);
        JLabel icon = new JLabel("📈"); // bar chart icon
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setForeground(Color.WHITE);
        icon.setBorder(new EmptyBorder(0, 24, 0, 32));
        JLabel title = new JLabel("View Reports");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(icon);
        header.add(title);
        card.add(header, BorderLayout.NORTH);
        // Center buttons
        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 0, 16, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        GameButton parkingHistoryBtn = new GameButton(" ", "View Parking History");
        btnPanel.add(parkingHistoryBtn, gbc);
        gbc.gridy++;
        GameButton currentStatusBtn = new GameButton(" ", "View Current Status");
        btnPanel.add(currentStatusBtn, gbc);
        card.add(btnPanel, BorderLayout.CENTER);
        // Close button
        JPanel closePanel = new JPanel();
        closePanel.setOpaque(false);
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));
        closeBtn.setBackground(Color.BLACK);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(new RoundedBorder(12));
        closeBtn.addActionListener(e -> dialog.dispose());
        closePanel.add(closeBtn);
        card.add(closePanel, BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(520, 320));
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        // Add actions
        parkingHistoryBtn.addActionListener(e -> {
            try {
                java.util.List<String[]> history = new java.util.ArrayList<>();
                double totalRevenue = 0.0;
                try (BufferedReader reader = new BufferedReader(new FileReader("parking_history.txt"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 4) {
                            history.add(new String[]{parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim()});
                            try {
                                String feeStr = parts[3].trim();
                                double fee = Double.parseDouble(feeStr.replace("$", ""));
                                totalRevenue += fee;
                            } catch (Exception ex) {}
                        }
                    }
                }
                showParkingHistoryCard(history, totalRevenue);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error reading parking history: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        currentStatusBtn.addActionListener(e -> showSystemStatus());

        dialog.setVisible(true);
    }

    private void showParkingHistoryCard(List<String[]> history, double totalRevenue) {
        JDialog dialog = new JDialog(this, "Parking History", true);
        dialog.setUndecorated(true);
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2d.setColor(new Color(0,0,0,40));
                g2d.fillRoundRect(6, 8, getWidth()-12, getHeight()-12, 24, 24);
                // Card background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 0, 0));
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(Color.BLACK);
        JLabel icon = new JLabel("📜"); // info icon
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setForeground(Color.WHITE);
        icon.setBorder(new EmptyBorder(0, 24, 0, 32));
        JLabel title = new JLabel("Parking History");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(icon);
        header.add(title);
        card.add(header, BorderLayout.NORTH);
        // Table
        String[] columns = {"Vehicle", "Duration", "Checkout Time", "Fee"};
        Object[][] data = new Object[history.size()][4];
        for (int i = 0; i < history.size(); i++) {
            data[i] = history.get(i);
        }
        JTable table = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table.setFont(new Font("Monospaced", Font.PLAIN, 15));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(230, 240, 250));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setGridColor(new Color(200, 200, 200));
        table.setShowGrid(true);
        // Alternating row colors
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(200, 220, 255));
                } else if (row % 2 == 0) {
                    c.setBackground(new Color(245, 250, 255));
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        card.add(scrollPane, BorderLayout.CENTER);
        // Footer for total revenue and OK button
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        JLabel totalLabel = new JLabel(String.format("Total Revenue: $%.2f", totalRevenue));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(Color.BLACK);
        footer.add(totalLabel, BorderLayout.WEST);
        JButton okBtn = new JButton("OK");
        okBtn.setFont(new Font("Arial", Font.BOLD, 16));
        okBtn.setBackground(Color.BLACK);
        okBtn.setForeground(Color.WHITE);
        okBtn.setFocusPainted(false);
        okBtn.setBorder(new RoundedBorder(12));
        okBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(okBtn);
        footer.add(btnPanel, BorderLayout.EAST);
        card.add(footer, BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(600, 400));
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Modern Manage Users dialog
    private void showManageUsersCard() {
        JDialog dialog = new JDialog(this, "Manage Users", true);
        dialog.setUndecorated(true);
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0,0,0,40));
                g2d.fillRoundRect(6, 8, getWidth()-12, getHeight()-12, 24, 24);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(0, 0, 0, 0));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(Color.BLACK);
        JLabel icon = new JLabel("👥");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setForeground(Color.WHITE);
        icon.setBorder(new EmptyBorder(0, 24, 0, 32));
        JLabel title = new JLabel("Manage Users");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(icon);
        header.add(title);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(0,0,20,0);
        card.add(header, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(10,0,10,0);
        JButton viewBtn = new GameButton("View All Users");
        JButton addBtn = new GameButton("Add New User");
        JButton removeBtn = new GameButton("Remove User");
        card.add(viewBtn, gbc);
        gbc.gridy = 2; card.add(addBtn, gbc);
        gbc.gridy = 3; card.add(removeBtn, gbc);
        viewBtn.addActionListener(e -> { dialog.dispose(); manageUsersViewAll(); });
        addBtn.addActionListener(e -> { dialog.dispose(); manageUsersAdd(); });
        removeBtn.addActionListener(e -> { dialog.dispose(); manageUsersRemove(); });
        card.setPreferredSize(new Dimension(480, 380));
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Call this from manageUsers()
    private void manageUsers() {
        showManageUsersCard();
    }

    private void issueTicket() {
        if (!loggedInUser.getRole().equalsIgnoreCase("EntryOperator")) {
            JOptionPane.showMessageDialog(this,
                "Only Entry Operators can issue tickets.",
                "Access Denied",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Issue Ticket", true);
        dialog.setUndecorated(true);
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0,0,0,40));
                g2d.fillRoundRect(6, 8, getWidth()-12, getHeight()-12, 24, 24);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 0, 0));
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(Color.BLACK);
        JLabel icon = new JLabel(); // Remove icon or emoji
        JLabel title = new JLabel("Issue Ticket");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title);
        card.add(header, BorderLayout.NORTH);
        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 16, 18, 16); // More vertical and horizontal spacing
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel plateLabel = new JLabel("Vehicle Plate Number:");
        plateLabel.setFont(new Font("Arial", Font.BOLD, 16));
        form.add(plateLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JTextField plateField = new JTextField(24);
        plateField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        plateField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(52, 152, 219), 2, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        plateField.setBackground(Color.WHITE);
        plateField.setForeground(Color.BLACK);
        form.add(plateField, gbc);
        gbc.gridx = 0; gbc.gridy++; gbc.anchor = GridBagConstraints.EAST;
        JLabel spotLabel = new JLabel("Select Parking Spot:");
        spotLabel.setFont(new Font("Arial", Font.BOLD, 16));
        form.add(spotLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        DefaultComboBoxModel<String> spotModel = new DefaultComboBoxModel<>();
        JComboBox<String> spotComboBox = new JComboBox<>(spotModel);
        spotComboBox.setFont(new Font("Arial", Font.PLAIN, 15));
        spotComboBox.setBackground(new Color(236, 240, 241));
        spotComboBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(52, 152, 219), 2, true),
            new EmptyBorder(2, 8, 2, 8)
        ));
        updateAvailableSpots(spotModel);
        form.add(spotComboBox, gbc);
        gbc.gridx = 1; gbc.gridy++; gbc.anchor = GridBagConstraints.WEST;
        GameButton refreshButton = new GameButton("Refresh Available Spots") {
            @Override
            public Insets getMargin() { return new Insets(8, 18, 8, 18); }
        };
        form.add(refreshButton, gbc);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        GameButton issueButton = new GameButton("Issue Ticket") {
            @Override
            public Insets getMargin() { return new Insets(8, 18, 8, 18); }
        };
        form.add(issueButton, gbc);
        card.add(form, BorderLayout.CENTER);
        // Close button
        JPanel closePanel = new JPanel();
        closePanel.setOpaque(false);
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));
        closeBtn.setBackground(Color.BLACK);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(new RoundedBorder(12));
        closeBtn.addActionListener(e -> dialog.dispose());
        closePanel.add(closeBtn);
        card.add(closePanel, BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(520, 400)); // Increased size
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        refreshButton.addActionListener(e -> updateAvailableSpots(spotModel));

        issueButton.addActionListener(e -> {
            try {
                String plateNumber = plateField.getText().trim();
                String selectedSpot = (String) spotComboBox.getSelectedItem();

                if (plateNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Plate number cannot be empty.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (selectedSpot == null) {
                    JOptionPane.showMessageDialog(dialog,
                        "Please select a parking spot.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int spotId = Integer.parseInt(selectedSpot.split(":")[0].trim());

                ParkingSpot existingSpot = parkingLot.findSpotByPlate(plateNumber);
                if (existingSpot != null) {
                    JOptionPane.showMessageDialog(dialog,
                        "Error: This vehicle is already parked in spot " + existingSpot.getSpotId(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String entryId = java.util.UUID.randomUUID().toString().substring(0, 8);
                java.util.Date entryTime = new java.util.Date();
                Ticket ticket = new Ticket(entryId, plateNumber, entryTime);

                try {
                    TicketDatabase.saveTicket(ticket);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Error saving ticket: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (parkingLot.assignSpecificSpot(plateNumber, spotId)) {
                    dialog.dispose();
                    ParkingSpot spot = null;
                    for (ParkingSpot s : parkingLot.getAllSpots()) {
                        if (s.getSpotId() == spotId) {
                            spot = s;
                            break;
                        }
                    }
                    if (spot != null) {
                        showTicket(ticket, spot);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Error: Could not assign the selected spot. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    TicketDatabase.deleteTicket(entryId);
                    updateAvailableSpots(spotModel);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error issuing ticket: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void updateAvailableSpots(DefaultComboBoxModel<String> model) {
        model.removeAllElements();
        List<ParkingSpot> availableSpots = parkingLot.getAvailableSpots();
        if (availableSpots.isEmpty()) {
            model.addElement("No spots available");
        } else {
            for (ParkingSpot spot : availableSpots) {
                model.addElement(spot.getSpotId() + ": Available");
            }
        }
    }

    private void viewAvailableSpots() {
        List<ParkingSpot> availableSpots = parkingLot.getAvailableSpots();
        StringBuilder message = new StringBuilder();

        if (availableSpots.isEmpty()) {
            message.append("No spots available.");
        } else {
            message.append("Number of available spots: ").append(availableSpots.size()).append("\n\n");
            message.append("Available spot numbers: ");
            for (ParkingSpot spot : availableSpots) {
                message.append(spot.getSpotId()).append(" ");
            }
        }

        JOptionPane.showMessageDialog(this,
            message.toString(),
            "Available Spots",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewOperatorInfo() {
        if (loggedInUser.getRole().equalsIgnoreCase("EntryOperator")) {
            EntryStationOperator operator = new EntryStationOperator(loggedInUser.getId(), loggedInUser.getName(), parkingLot);
            showOperatorCard("Entry Station Operator", loggedInUser.getId(), loggedInUser.getName());
        }
    }

    private void processCheckout() {
        if (!loggedInUser.getRole().equalsIgnoreCase("ExitOperator")) {
            JOptionPane.showMessageDialog(this,
                "Only Exit Operators can process checkouts.",
                "Access Denied",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Process Checkout", true);
        dialog.setUndecorated(true);
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0,0,0,40));
                g2d.fillRoundRect(6, 8, getWidth()-12, getHeight()-12, 24, 24);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 0, 0));
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(Color.BLACK);
        JLabel icon = new JLabel(); // Remove icon or emoji
        JLabel title = new JLabel("Process Checkout");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title);
        card.add(header, BorderLayout.NORTH);
        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 16, 14, 16);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel ticketLabel = new JLabel("Ticket ID:");
        ticketLabel.setFont(new Font("Arial", Font.BOLD, 16));
        form.add(ticketLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JTextField ticketField = new JTextField(18);
        ticketField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        ticketField.setBackground(Color.WHITE);
        ticketField.setForeground(Color.BLACK);
        ticketField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(52, 152, 219), 2, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        form.add(ticketField, gbc);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JTextArea detailsArea = new JTextArea(5, 24);
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        detailsArea.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        form.add(detailsArea, gbc);
        gbc.gridy++;
        GameButton checkoutButton = new GameButton("Process Checkout") {
            @Override
            public Insets getMargin() { return new Insets(8, 18, 8, 18); }
        };
        checkoutButton.setEnabled(false);
        form.add(checkoutButton, gbc);
        gbc.gridy++;
        GameButton checkButton = new GameButton("Check Ticket") {
            @Override
            public Insets getMargin() { return new Insets(8, 18, 8, 18); }
        };
        form.add(checkButton, gbc);
        card.add(form, BorderLayout.CENTER);
        // Close button
        JPanel closePanel = new JPanel();
        closePanel.setOpaque(false);
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));
        closeBtn.setBackground(Color.BLACK);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(new RoundedBorder(12));
        closeBtn.addActionListener(e -> dialog.dispose());
        closePanel.add(closeBtn);
        card.add(closePanel, BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(520, 400));
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        checkButton.addActionListener(e -> {
            String ticketId = ticketField.getText().trim();
            if (ticketId.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Ticket ID cannot be empty.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            ExitStationOperator exitOperator = new ExitStationOperator(loggedInUser.getId(), loggedInUser.getName(), parkingLot);
            TicketDetails details = exitOperator.checkTicketDetails(ticketId);

            if (details == null) {
                JOptionPane.showMessageDialog(dialog,
                    "Invalid ticket or no matching occupied spot found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            detailsArea.setText(String.format(
                "Ticket ID: %s\n" +
                "Plate Number: %s\n" +
                "Duration: %s\n" +
                "Fee to be paid: $%.2f",
                details.getTicketId(),
                details.getPlateNumber(),
                details.getFormattedDuration(),
                details.getFee()
            ));

            ExitStationOperator.markTicketAsVerified(ticketId);
            checkoutButton.setEnabled(true);
        });

        checkoutButton.addActionListener(e -> {
            String ticketId = ticketField.getText().trim();
            ExitStationOperator exitOperator = new ExitStationOperator(loggedInUser.getId(), loggedInUser.getName(), parkingLot);

            int confirm = JOptionPane.showConfirmDialog(dialog,
                "Confirm payment and complete checkout?",
                "Confirm Checkout",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (exitOperator.processCheckout(ticketId)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Payment complete. Thank you!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Error processing checkout. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dialog.setVisible(true);
    }

    private void viewExitOperatorInfo() {
        if (loggedInUser.getRole().equalsIgnoreCase("ExitOperator")) {
            ExitStationOperator exitOperator = new ExitStationOperator(loggedInUser.getId(), loggedInUser.getName(), parkingLot);
            showOperatorCard("Exit Station Operator", loggedInUser.getId(), loggedInUser.getName());
        }
    }

    private void viewTicket() {
        if (!loggedInUser.getRole().equalsIgnoreCase("Customer")) {
            return;
        }
        // Find ticket for this customer
        List<Ticket> customerTickets = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("tickets.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        String ticketId = parts[0].trim();
                        String plateNumber = parts[1].trim();
                        // More robust date parsing
                        Date entryTime;
                        try {
                            long timestamp = Long.parseLong(parts[2].trim());
                            entryTime = new Date(timestamp);
                        } catch (NumberFormatException ex) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            try {
                                entryTime = sdf.parse(parts[2].trim());
                            } catch (Exception e) {
                                System.err.println("Could not parse date in ticket: " + line);
                                continue;
                            }
                        }
                        Ticket ticket = new Ticket(ticketId, plateNumber, entryTime);
                        ParkingSpot spot = parkingLot.findSpotByPlate(plateNumber);
                        if (spot != null) {
                            customerTickets.add(ticket);
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error processing ticket line: " + line + "\nError: " + ex.getMessage());
                    continue;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error reading tickets: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (customerTickets.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "You have no active parking tickets.",
                "No Tickets",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // Show tickets one by one using TicketPanel
        final int[] index = {0};
        showCustomerTicketDialog(customerTickets, index);
    }

    private void showCustomerTicketDialog(List<Ticket> tickets, int[] index) {
        Ticket ticket = tickets.get(index[0]);
        ParkingSpot spot = parkingLot.findSpotByPlate(ticket.getPlateNumber());
        ExitStationOperator exitOp = new ExitStationOperator("TEMP", "Temporary", parkingLot);
        double fee = 0.0;
        String duration = "0 minutes";
        if (spot != null) {
            long dur = spot.getParkingDuration();
            duration = dur + " minutes";
            fee = exitOp.calculateParkingFee(dur);
        }
        TicketPanel ticketPanel = new TicketPanel(
            ticket.getTicketId(),
            ticket.getPlateNumber(),
            ticket.getEntryTime(),
            (spot != null ? spot.getSpotId() : -1),
            fee,
            duration
        );
        JDialog dialog = new JDialog(this, "Your Ticket", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(ticketPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton prevBtn = new JButton("Previous");
        JButton nextBtn = new JButton("Next");
        JButton closeBtn = new JButton("Close");
        buttonPanel.add(prevBtn);
        buttonPanel.add(nextBtn);
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        prevBtn.setEnabled(index[0] > 0);
        nextBtn.setEnabled(index[0] < tickets.size() - 1);
        prevBtn.addActionListener(e -> {
            dialog.dispose();
            index[0]--;
            showCustomerTicketDialog(tickets, index);
        });
        nextBtn.addActionListener(e -> {
            dialog.dispose();
            index[0]++;
            showCustomerTicketDialog(tickets, index);
        });
        closeBtn.addActionListener(e -> dialog.dispose());
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void viewParkingStatus() {
        if (!loggedInUser.getRole().equalsIgnoreCase("Customer")) {
            return;
        }

        StringBuilder status = new StringBuilder();
        status.append("=== Parking Lot Status ===\n\n");

        int totalSpots = parkingLot.getTotalSpots();
        int occupiedSpots = parkingLot.getOccupiedSpots();
        int availableSpots = totalSpots - occupiedSpots;

        status.append("Total Parking Spots: ").append(totalSpots).append("\n");
        status.append("Available Spots: ").append(availableSpots).append("\n");
        status.append("Occupied Spots: ").append(occupiedSpots).append("\n\n");

        if (availableSpots > 0) {
            status.append("Parking is available! You can get a ticket from the Entry Operator.");
        } else {
            status.append("Sorry, the parking lot is currently full. Please try again later.");
        }

        // Modern card dialog
        JDialog dialog = new JDialog(this, "Parking Status", true);
        dialog.setUndecorated(true);
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0,0,0,40));
                g2d.fillRoundRect(6, 8, getWidth()-12, getHeight()-12, 24, 24);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 0, 0));
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(Color.BLACK);
        JLabel icon = new JLabel(" "); // or other icon
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setForeground(Color.WHITE);
        icon.setBorder(new EmptyBorder(0, 24, 0, 32));
        JLabel title = new JLabel("Parking Status");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(icon);
        header.add(title);
        card.add(header, BorderLayout.NORTH);
        // Status text
        JTextArea textArea = new JTextArea(status.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        textArea.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        card.add(textArea, BorderLayout.CENTER);
        // OK button
        JButton okBtn = new JButton("OK");
        okBtn.setFont(new Font("Arial", Font.BOLD, 16));
        okBtn.setBackground(new Color(41, 128, 185));
        okBtn.setForeground(Color.WHITE);
        okBtn.setFocusPainted(false);
        okBtn.setBorder(new RoundedBorder(12));
        okBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(okBtn);
        card.add(btnPanel, BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(440, 260));
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showGameDialog(String message, String title, int messageType) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new GamePanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new GameLabel(message);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        GameButton okButton = new GameButton("OK");
        okButton.addActionListener(e -> dialog.dispose());

        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(okButton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void playSuccessAnimation() {
        // Add success animation here
        JDialog successDialog = new JDialog(this, false);
        successDialog.setUndecorated(true);
        successDialog.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(46, 204, 113, 200));
                g2d.fillOval(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(5));
                g2d.drawLine(getWidth()/4, getHeight()/2, getWidth()/2, getHeight()*3/4);
                g2d.drawLine(getWidth()/2, getHeight()*3/4, getWidth()*3/4, getHeight()/4);
            }
        };

        successDialog.add(panel);
        successDialog.setSize(100, 100);
        successDialog.setLocationRelativeTo(this);
        successDialog.setVisible(true);

        Timer timer = new Timer(1000, e -> successDialog.dispose());
        timer.setRepeats(false);
        timer.start();
    }

    // Custom components for game-like appearance
    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Create gradient background
            GradientPaint gradient = new GradientPaint(
                0, 0, Color.BLACK,
                getWidth(), getHeight(), Color.BLACK
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Add animated particles
            g2d.setColor(new Color(255, 255, 255, 50));
            for (int i = 0; i < 20; i++) {
                double x = getWidth() * 0.5 + Math.cos(animationAngle + i) * 100;
                double y = getHeight() * 0.5 + Math.sin(animationAngle + i) * 100;
                g2d.fill(new Ellipse2D.Double(x, y, 10, 10));
            }
        }
    }

    private class GameLabel extends JLabel {
        public GameLabel(String text) {
            super(text);
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 16));
        }
    }

    private class GameTextField extends JTextField {
        public GameTextField(int columns) {
            super(columns);
            setFont(new Font("Arial", Font.PLAIN, 14));
            setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(52, 152, 219), 2, true),
                new EmptyBorder(5, 10, 5, 10)
            ));
            setBackground(new Color(236, 240, 241));
        }
    }

    private class GamePasswordField extends JPasswordField {
        public GamePasswordField(int columns) {
            super(columns);
            setFont(new Font("Arial", Font.PLAIN, 14));
            setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(52, 152, 219), 2, true),
                new EmptyBorder(5, 10, 5, 10)
            ));
            setBackground(new Color(236, 240, 241));
        }
    }

    private class GameButton extends JButton {
        public GameButton(String text) {
            super(text);
            setFont(new Font("Arial", Font.BOLD, 18));
            setForeground(Color.WHITE);
            setBackground(Color.BLACK);
            setBorder(new RoundedBorder(16));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(true);
            setMargin(new Insets(14, 28, 14, 28));
        }
        public GameButton(String icon, String text) {
            this(icon + "  " + text);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Shadow
            g2d.setColor(new Color(0,0,0,40));
            g2d.fillRoundRect(4, 6, getWidth()-8, getHeight()-8, 16, 16);
            // Button fill
            g2d.setColor(Color.BLACK);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            // Draw white border
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
            super.paintComponent(g);
        }
    }

    private class RoundedBorder implements Border {
        private int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.WHITE);
            g2d.drawRoundRect(x, y, width, height, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius, radius, radius, radius);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }
    }

    private class ParkingVisualizationPanel extends JPanel {
        public ParkingVisualizationPanel() {
            setOpaque(true);
            setBackground(new Color(236, 240, 241));
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            // Fill the whole background with opaque black
            g2d.setColor(new Color(0, 0, 0, 255));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
            Graphics2D gd = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int laneHeight = 40;
            int totalSpots = parkingLot != null ? parkingLot.getAllSpots().size() : 0;
            int spotsPerRow = (totalSpots + 1) / 2;
            int marginX = 40;
            int marginY = 60;
            int spotMargin = 20;
            int spotWidth = 120;
            int spotHeight = 90;
            int panelWidth = spotsPerRow * (spotWidth + spotMargin) + spotMargin;
            int panelHeight = 2 * spotHeight + laneHeight + 3 * spotMargin;
            // Set preferred size for scroll pane
            setPreferredSize(new Dimension(panelWidth + 2 * marginX, panelHeight + marginY + 60));
            revalidate();
            int startX = marginX;
            int startY = marginY;
            // Draw green lane
            g2d.setColor(new Color(80, 200, 80));
            int laneY = startY + spotHeight + spotMargin;
            g2d.fillRect(startX, laneY, panelWidth, laneHeight);
            // Draw white outline for lot
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawRect(startX, startY, panelWidth, panelHeight);
            // Draw spots and lines
            java.util.List<ParkingSpot> spots = parkingLot != null ? parkingLot.getAllSpots() : new java.util.ArrayList<>();
            g2d.setStroke(new BasicStroke(3));
            for (int i = 0; i < spotsPerRow; i++) {
                // Top row
                int x = startX + i * (spotWidth + spotMargin) + spotMargin;
                int yTop = startY + spotMargin;
                int idxTop = i;
                if (idxTop < spots.size()) {
                    ParkingSpot spot = spots.get(idxTop);
                    g2d.setColor(spot.isOccupied() ? new Color(231, 76, 60) : new Color(46, 204, 113));
                    g2d.fillRect(x, yTop, spotWidth, spotHeight);
                    g2d.setColor(Color.WHITE);
                    g2d.drawRect(x, yTop, spotWidth, spotHeight);
                    String icon = spot.isOccupied() ? "\uD83D\uDE97" : "P";
                    Font iconFont;
                    try {
                        iconFont = new Font("Segoe UI Emoji", Font.BOLD, Math.max(24, spotHeight / 2));
                    } catch (Exception e) {
                        iconFont = new Font("SansSerif", Font.BOLD, Math.max(24, spotHeight / 2));
                    }
                    g2d.setFont(iconFont);
                    g2d.setColor(Color.WHITE);
                    FontMetrics fm = g2d.getFontMetrics();
                    int tx = x + (spotWidth - fm.stringWidth(icon)) / 2;
                    int ty = yTop + (spotHeight + fm.getAscent() - fm.getDescent()) / 2;
                    g2d.drawString(icon, tx, ty);
                }
                // Bottom row
                int yBot = startY + spotHeight + laneHeight + 2 * spotMargin;
                int idxBot = i + spotsPerRow;
                if (idxBot < spots.size()) {
                    ParkingSpot spot = spots.get(idxBot);
                    g2d.setColor(spot.isOccupied() ? new Color(231, 76, 60) : new Color(46, 204, 113));
                    g2d.fillRect(x, yBot, spotWidth, spotHeight);
                    g2d.setColor(Color.WHITE);
                    g2d.drawRect(x, yBot, spotWidth, spotHeight);
                    String icon = spot.isOccupied() ? "\uD83D\uDE97" : "P";
                    Font iconFont;
                    try {
                        iconFont = new Font("Segoe UI Emoji", Font.BOLD, Math.max(24, spotHeight / 2));
                    } catch (Exception e) {
                        iconFont = new Font("SansSerif", Font.BOLD, Math.max(24, spotHeight / 2));
                    }
                    g2d.setFont(iconFont);
                    g2d.setColor(Color.WHITE);
                    FontMetrics fm = g2d.getFontMetrics();
                    int tx = x + (spotWidth - fm.stringWidth(icon)) / 2;
                    int ty = yBot + (spotHeight + fm.getAscent() - fm.getDescent()) / 2;
                    g2d.drawString(icon, tx, ty);
                }
                // Draw vertical white line between spots
                if (i < spotsPerRow) {
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(3));
                    int lineX = x + spotWidth + spotMargin / 2;
                    g2d.drawLine(lineX, startY, lineX, startY + panelHeight);
                }
            }
            // Draw horizontal white lines for lane
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine(startX, laneY, startX + panelWidth, laneY);
            g2d.drawLine(startX, laneY + laneHeight, startX + panelWidth, laneY + laneHeight);
            // Draw legend
            int legendY = 20;
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.setColor(new Color(46, 204, 113));
            g2d.fillRect(startX, legendY, 30, 18);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Available", startX + 35, legendY + 14);
            g2d.setColor(new Color(231, 76, 60));
            g2d.fillRect(startX + 120, legendY, 30, 18);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Occupied", startX + 155, legendY + 14);
        }
    }

    private class TicketPanel extends JPanel {
        private String ticketId;
        private String plateNumber;
        private Date entryTime;
        private int spotId;
        private double fee;
        private String duration;

        public TicketPanel(String ticketId, String plateNumber, Date entryTime, int spotId, double fee, String duration) {
            this.ticketId = ticketId;
            this.plateNumber = plateNumber;
            this.entryTime = entryTime;
            this.spotId = spotId;
            this.fee = fee;
            this.duration = duration;
            setPreferredSize(new Dimension(320, 500));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.BLACK, 2),
                new EmptyBorder(20, 20, 20, 20)
            ));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int y = 30;
            // Logo and header
            g2d.setFont(new Font("Monospaced", Font.BOLD, 28));
            g2d.drawString("🅿️", 135, y);
            y += 35;
            g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
            String header = "PARKING RECEIPT";
            int headerWidth = g2d.getFontMetrics().stringWidth(header);
            g2d.drawString(header, (getWidth() - headerWidth) / 2, y);
            y += 20;

            // Dashed line
            drawDashedLine(g2d, y);
            y += 15;

            // Ticket details
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 14));
            y = drawDetail(g2d, "Ticket ID", ticketId, y);
            y = drawDetail(g2d, "Plate", plateNumber, y);
            y = drawDetail(g2d, "Entry", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(entryTime), y);
            y = drawDetail(g2d, "Spot", String.valueOf(spotId), y);
            y = drawDetail(g2d, "Duration", duration, y);
            y = drawDetail(g2d, "Fee", String.format("$%.2f", fee), y);

            // Dashed line
            y += 10;
            drawDashedLine(g2d, y);
            y += 20;

            // Barcode (simple visual)
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
            drawBarcode(g2d, ticketId, y);
            y += 50;

            // Dashed line (tear here)
            drawDashedLine(g2d, y);
            g2d.setFont(new Font("Monospaced", Font.ITALIC, 12));
            g2d.drawString("--- Tear Here ---", 90, y + 15);
            y += 35;

            // Footer
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
            String thankYou = "Thank you for parking with us!";
            int thankWidth = g2d.getFontMetrics().stringWidth(thankYou);
            g2d.drawString(thankYou, (getWidth() - thankWidth) / 2, y);
            y += 18;
            String keep = "Please keep this ticket for exit.";
            int keepWidth = g2d.getFontMetrics().stringWidth(keep);
            g2d.drawString(keep, (getWidth() - keepWidth) / 2, y);
        }

        private void drawDashedLine(Graphics2D g2d, int y) {
            Stroke oldStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
            g2d.setColor(Color.GRAY);
            g2d.drawLine(10, y, getWidth() - 10, y);
            g2d.setStroke(oldStroke);
            g2d.setColor(Color.BLACK);
        }

        private int drawDetail(Graphics2D g2d, String label, String value, int y) {
            String line = String.format("%-10s: %s", label, value);
            g2d.drawString(line, 20, y);
            return y + 22;
        }

        private void drawBarcode(Graphics2D g2d, String code, int y) {
            int barWidth = 2;
            int barHeight = 35;
            int x = 40;
            for (char c : code.toCharArray()) {
                int value = Character.getNumericValue(c);
                for (int i = 0; i < 4; i++) {
                    if ((value & (1 << i)) != 0) {
                        g2d.fillRect(x, y, barWidth, barHeight);
                    }
                    x += barWidth;
                }
                x += 2; // space between chars
            }
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
            g2d.drawString(code, 40, y + barHeight + 12);
        }
    }

    private void showTicket(Ticket ticket, ParkingSpot spot) {
        JDialog dialog = new JDialog(this, "Parking Ticket", true);
        dialog.setLayout(new BorderLayout());

        ExitStationOperator exitOp = new ExitStationOperator("TEMP", "Temporary", parkingLot);
        double fee = exitOp.calculateParkingFee(spot.getParkingDuration());
        String duration = spot.getParkingDuration() + " minutes";

        TicketPanel ticketPanel = new TicketPanel(
            ticket.getTicketId(),
            ticket.getPlateNumber(),
            ticket.getEntryTime(),
            spot.getSpotId(),
            fee,
            duration
        );

        dialog.add(ticketPanel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showOperatorCard(String type, String id, String name) {
        JDialog dialog = new JDialog(this, "Operator Information", true);
        dialog.setUndecorated(true);
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2d.setColor(new Color(0,0,0,40));
                g2d.fillRoundRect(6, 8, getWidth()-12, getHeight()-12, 24, 24);
                // Card background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 0, 0));
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(Color.BLACK);
        JLabel icon = new JLabel(" "); // or other icon
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setForeground(Color.WHITE);
        icon.setBorder(new EmptyBorder(0, 24, 0, 32));
        JLabel title = new JLabel(type);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(icon);
        header.add(title);
        card.add(header, BorderLayout.NORTH);
        // Info
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        JLabel idLabel = new JLabel("ID: " + id);
        idLabel.setFont(new Font("Arial", Font.BOLD, 18));
        idLabel.setAlignmentX(0.5f);
        JLabel nameLabel = new JLabel("Name: " + name);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        nameLabel.setAlignmentX(0.5f);
        infoPanel.add(Box.createVerticalStrut(18));
        infoPanel.add(idLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(nameLabel);
        card.add(infoPanel, BorderLayout.CENTER);
        // OK button
        JButton okBtn = new JButton("OK");
        okBtn.setFont(new Font("Arial", Font.BOLD, 16));
        okBtn.setBackground(Color.BLACK);
        okBtn.setForeground(Color.WHITE);
        okBtn.setFocusPainted(false);
        okBtn.setBorder(new RoundedBorder(12));
        okBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(okBtn);
        card.add(btnPanel, BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(440, 260));
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void manageUsersViewAll() {
        try {
            List<String[]> users = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    users.add(line.split(","));
                }
            }
            showAllUsersCard(users);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void manageUsersAdd() {
        showAddUserCard(userData -> {
            try {
                try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts[0].equals(userData[0])) {
                            JOptionPane.showMessageDialog(this, "User ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
                    writer.write(String.format("%s,%s,%s,%s\n", userData[0], userData[1], userData[2], userData[3]));
                }
                JOptionPane.showMessageDialog(this, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void manageUsersRemove() {
        showRemoveUserCard(userId -> {
            try {
                List<String> updatedUsers = new ArrayList<>();
                boolean userFound = false;
                try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts[0].equals(userId)) {
                            userFound = true;
                            continue;
                        }
                        updatedUsers.add(line);
                    }
                }
                if (!userFound) {
                    JOptionPane.showMessageDialog(this, "User ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt"))) {
                    for (String user : updatedUsers) {
                        writer.write(user);
                        writer.newLine();
                    }
                }
                JOptionPane.showMessageDialog(this, "User removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error removing user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Modern card dialog to show all users
    private void showAllUsersCard(java.util.List<String[]> users) {
        JDialog dialog = new JDialog(this, "All Users", true);
        dialog.setUndecorated(true);
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0,0,0,40));
                g2d.fillRoundRect(6, 8, getWidth()-12, getHeight()-12, 24, 24);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 0, 0));
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(Color.BLACK);
        JLabel icon = new JLabel("👥");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setForeground(Color.WHITE);
        icon.setBorder(new EmptyBorder(0, 24, 0, 32));
        JLabel title = new JLabel("All Users");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(icon);
        header.add(title);
        card.add(header, BorderLayout.NORTH);
        // Table
        String[] columns = {"User ID", "Name", "Role", "Password"};
        Object[][] data = new Object[users.size()][4];
        for (int i = 0; i < users.size(); i++) {
            String[] row = users.get(i);
            for (int j = 0; j < 4 && j < row.length; j++) {
                data[i][j] = row[j];
            }
        }
        JTable table = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table.setFont(new Font("Monospaced", Font.PLAIN, 15));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(230, 240, 250));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setGridColor(new Color(200, 200, 200));
        table.setShowGrid(true);
        // Alternating row colors
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(200, 220, 255));
                } else if (row % 2 == 0) {
                    c.setBackground(new Color(245, 250, 255));
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        card.add(scrollPane, BorderLayout.CENTER);
        // OK button
        JButton okBtn = new JButton("OK");
        okBtn.setFont(new Font("Arial", Font.BOLD, 16));
        okBtn.setBackground(Color.BLACK);
        okBtn.setForeground(Color.WHITE);
        okBtn.setFocusPainted(false);
        okBtn.setBorder(new RoundedBorder(12));
        okBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(okBtn);
        card.add(btnPanel, BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(600, 400));
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Modern card dialog to add a user
    private void showAddUserCard(java.util.function.Consumer<String[]> onAdd) {
        JDialog dialog = new JDialog(this, "Add User", true);
        dialog.setUndecorated(true);
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0,0,0,40));
                g2d.fillRoundRect(6, 8, getWidth()-12, getHeight()-12, 24, 24);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 0, 0));
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(new Color(41, 128, 185));
        JLabel icon = new JLabel("➕");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setForeground(Color.WHITE);
        icon.setBorder(new EmptyBorder(0, 16, 0, 24));
        JLabel title = new JLabel("Add New User");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(icon);
        header.add(title);
        card.add(header, BorderLayout.NORTH);
        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel idLabel = new JLabel("User ID:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 16));
        form.add(idLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JTextField idField = new JTextField(16);
        idField.setFont(new Font("Arial", Font.PLAIN, 15));
        form.add(idField, gbc);
        gbc.gridx = 0; gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        form.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JTextField nameField = new JTextField(16);
        nameField.setFont(new Font("Arial", Font.PLAIN, 15));
        form.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        form.add(roleLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Admin", "EntryOperator", "ExitOperator", "Customer"});
        roleBox.setFont(new Font("Arial", Font.PLAIN, 15));
        form.add(roleBox, gbc);
        gbc.gridx = 0; gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 16));
        form.add(passLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JPasswordField passField = new JPasswordField(16);
        passField.setFont(new Font("Arial", Font.PLAIN, 15));
        form.add(passField, gbc);
        card.add(form, BorderLayout.CENTER);
        // Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        JButton addBtn = new JButton("Add") {
            @Override
            public void setEnabled(boolean enabled) {
                super.setEnabled(enabled);
                setBackground(Color.BLACK);
                setForeground(Color.WHITE);
            }
        };
        addBtn.setFont(new Font("Arial", Font.BOLD, 16));
        addBtn.setBackground(Color.BLACK);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setBorder(new RoundedBorder(12));
        JButton cancelBtn = new JButton("Cancel") {
            @Override
            public void setEnabled(boolean enabled) {
                super.setEnabled(enabled);
                setBackground(Color.BLACK);
                setForeground(Color.WHITE);
            }
        };
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 16));
        cancelBtn.setBackground(Color.BLACK);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(new RoundedBorder(12));
        btnPanel.add(addBtn);
        btnPanel.add(cancelBtn);
        card.add(btnPanel, BorderLayout.SOUTH);
        addBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String role = (String) roleBox.getSelectedItem();
            String pass = new String(passField.getPassword());
            if (id.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dialog.dispose();
            onAdd.accept(new String[]{id, name, role, pass});
        });
        cancelBtn.addActionListener(e -> dialog.dispose());
        card.setPreferredSize(new Dimension(400, 340));
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Modern card dialog to remove a user
    private void showRemoveUserCard(java.util.function.Consumer<String> onRemove) {
        JDialog dialog = new JDialog(this, "Remove User", true);
        dialog.setUndecorated(true);
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0,0,0,40));
                g2d.fillRoundRect(6, 8, getWidth()-12, getHeight()-12, 24, 24);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 0, 0));
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(new Color(41, 128, 185));
        JLabel icon = new JLabel(" ");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setForeground(Color.WHITE);
        icon.setBorder(new EmptyBorder(0, 16, 0, 24));
        JLabel title = new JLabel("Remove User");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(icon);
        header.add(title);
        card.add(header, BorderLayout.NORTH);
        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel idLabel = new JLabel("User ID:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 16));
        form.add(idLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JTextField idField = new JTextField(16);
        idField.setFont(new Font("Arial", Font.PLAIN, 15));
        form.add(idField, gbc);
        card.add(form, BorderLayout.CENTER);
        // Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        JButton removeBtn = new JButton("Remove") {
            @Override
            public void setEnabled(boolean enabled) {
                super.setEnabled(enabled);
                setBackground(Color.BLACK);
                setForeground(Color.WHITE);
            }
        };
        removeBtn.setFont(new Font("Arial", Font.BOLD, 16));
        removeBtn.setBackground(Color.BLACK);
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFocusPainted(false);
        removeBtn.setBorder(new RoundedBorder(12));
        JButton cancelBtn = new JButton("Cancel") {
            @Override
            public void setEnabled(boolean enabled) {
                super.setEnabled(enabled);
                setBackground(Color.BLACK);
                setForeground(Color.WHITE);
            }
        };
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 16));
        cancelBtn.setBackground(Color.BLACK);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(new RoundedBorder(12));
        btnPanel.add(removeBtn);
        btnPanel.add(cancelBtn);
        card.add(btnPanel, BorderLayout.SOUTH);
        removeBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "User ID is required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dialog.dispose();
            onRemove.accept(id);
        });
        cancelBtn.addActionListener(e -> dialog.dispose());
        card.setPreferredSize(new Dimension(400, 220));
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ParkingSystemGUI gui = new ParkingSystemGUI();
            gui.setVisible(true);
        });
    }
}

// Add this class at the end of ParkingSystemGUI
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        try {
            backgroundImage = new ImageIcon(imagePath).getImage();
        } catch (Exception e) {
            backgroundImage = null;
        }
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}