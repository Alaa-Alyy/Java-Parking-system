import java.util.*;
import java.io.*;
import java.text.*;

public class Admin {
    private String Name;
    private String Role;
    private ParkingLot parkingLot;

    public Admin(String Name, String Role, ParkingLot parkingLot) {
        this.Name = Name;
        this.Role = Role;
        this.parkingLot = parkingLot;
    }

    public void adminModule(Scanner input) {
        while (true) {
            System.out.println("\n=== Admin Module ===");
            System.out.println("1. Add Parking Spots");
            System.out.println("2. View Total Spots");
            System.out.println("3. Manage Users");
            System.out.println("4. View Shift Report");
            System.out.println("5. View Parked Cars Report");
            System.out.println("6. Exit");
            System.out.print("Select Option: ");

            int choice = 0;
            try {
                choice = input.nextInt();
                input.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                input.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    addParkingSpot(input);
                    break;
                case 2:
                    viewTotalSpots();
                    break;
                case 3:
                    manageUsers(input);
                    break;
                case 4:
                    viewShiftReport(input);
                    break;
                case 5:
                    viewParkedCarsReport();
                    break;
                case 6:
                    System.out.println("Returning to main menu.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void addParkingSpot(Scanner input) {
        System.out.print("Enter number of spots to add: ");
        int newSpots = 0;
        try {
            newSpots = input.nextInt();
            input.nextLine();
            if (newSpots <= 0) {
                System.out.println("Number of spots must be positive.");
                return;
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            input.nextLine();
            return;
        }
        parkingLot.addSpots(newSpots);
        System.out.println("Added " + newSpots + " spots. Total spots: " + parkingLot.getTotalSpots() + ", Available spots: " + (parkingLot.getTotalSpots() - parkingLot.getOccupiedSpots()));
    }

    private void viewTotalSpots() {
        System.out.println("\n=== Total Spots ===");
        System.out.println("Total parking spots: " + parkingLot.getTotalSpots());
        System.out.println("Occupied spots: " + parkingLot.getOccupiedSpots());
        System.out.println("Available spots: " + (parkingLot.getTotalSpots() - parkingLot.getOccupiedSpots()));
    }

    private void manageUsers(Scanner input) {
        while (true) {
            System.out.println("\n=== Manage Users ===");
            System.out.println("1. Add User");
            System.out.println("2. Update User");
            System.out.println("3. Delete User");
            System.out.println("4. Back");
            System.out.print("Select Option: ");

            int choice = 0;
            try {
                choice = input.nextInt();
                input.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                input.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    addUser(input);
                    break;
                case 2:
                    updateUser(input);
                    break;
                case 3:
                    deleteUser(input);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void addUser(Scanner input) {
        System.out.print("Enter user ID: ");
        String id = input.nextLine();
        if (UserDatabase.userExists(id)) {
            System.out.println("User ID already exists.");
            return;
        }
        System.out.print("Enter name: ");
        String name = input.nextLine();
        System.out.println("\nAvailable roles:");
        System.out.println("1. Admin");
        System.out.println("2. EntryOperator");
        System.out.println("3. ExitOperator");
        System.out.println("4. Customer");
        System.out.print("Select role (1-4): ");
        
        String role;
        try {
            int roleChoice = input.nextInt();
            input.nextLine(); // Consume newline
            switch (roleChoice) {
                case 1:
                    role = "Admin";
                    break;
                case 2:
                    role = "EntryOperator";
                    break;
                case 3:
                    role = "ExitOperator";
                    break;
                case 4:
                    role = "Customer";
                    break;
                default:
                    System.out.println("Invalid role selection. Defaulting to Customer.");
                    role = "Customer";
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Defaulting to Customer role.");
            input.nextLine();
            role = "Customer";
        }

        System.out.print("Enter password: ");
        String password = input.nextLine();
        User user = new User(id, name, role, password);
        UserDatabase.addUser(user);
        System.out.println("User added successfully as " + role + ".");
    }

    private void updateUser(Scanner input) {
        System.out.print("Enter user ID to update: ");
        String id = input.nextLine();
        if (!UserDatabase.userExists(id)) {
            System.out.println("User not found.");
            return;
        }
        System.out.print("Enter new name (press Enter to keep unchanged): ");
        String newName = input.nextLine();
        
        String newRole = "";
        System.out.println("\nUpdate role?");
        System.out.println("1. Yes");
        System.out.println("2. No (keep unchanged)");
        System.out.print("Select option (1-2): ");
        
        try {
            int updateRole = input.nextInt();
            input.nextLine(); // Consume newline
            if (updateRole == 1) {
                System.out.println("\nAvailable roles:");
                System.out.println("1. Admin");
                System.out.println("2. EntryOperator");
                System.out.println("3. ExitOperator");
                System.out.println("4. Customer");
                System.out.print("Select new role (1-4): ");
                
                int roleChoice = input.nextInt();
                input.nextLine(); // Consume newline
                switch (roleChoice) {
                    case 1:
                        newRole = "Admin";
                        break;
                    case 2:
                        newRole = "EntryOperator";
                        break;
                    case 3:
                        newRole = "ExitOperator";
                        break;
                    case 4:
                        newRole = "Customer";
                        break;
                    default:
                        System.out.println("Invalid role selection. Role will remain unchanged.");
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Role will remain unchanged.");
            input.nextLine();
        }

        System.out.print("Enter new password (press Enter to keep unchanged): ");
        String newPassword = input.nextLine();
        
        UserDatabase.updateUser(id, newName, newPassword, newRole);
        System.out.println("User updated successfully.");
    }

    private void deleteUser(Scanner input) {
        System.out.print("Enter user ID to delete: ");
        String id = input.nextLine();
        if (!UserDatabase.userExists(id)) {
            System.out.println("User not found.");
            return;
        }
        UserDatabase.deleteUser(id);
        System.out.println("User deleted successfully.");
    }

    private void viewShiftReport(Scanner input) {
        System.out.println("\n=== Shift Report ===");
        System.out.println("Enter date (yyyy/MM/dd) or press Enter for today's report: ");
        String dateStr = input.nextLine();
        Date reportDate;
        
        try {
            if (dateStr.trim().isEmpty()) {
                reportDate = new Date();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                sdf.setLenient(false);  // Strict date parsing
                reportDate = sdf.parse(dateStr);
            }
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy/MM/dd");
            return;
        }

        double totalPayments = 0;
        int totalTransactions = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader("parking_history.txt"))) {
            String line;
            SimpleDateFormat historyDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        String checkoutTime = parts[2].trim();
                        Date checkoutDate = historyDateFormat.parse(checkoutTime);
                        if (isSameDay(checkoutDate, reportDate)) {
                            String feeStr = parts[3].trim();
                            double payment = Double.parseDouble(feeStr.substring(1)); // Remove $ sign
                            totalPayments += payment;
                            totalTransactions++;
                        }
                    } catch (ParseException e) {
                        System.out.println("Error parsing date in history file: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading parking history: " + e.getMessage());
            return;
        }

        // Display report
        SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy/MM/dd");
        System.out.println("\nShift Report for " + displayFormat.format(reportDate));
        System.out.println("Total Transactions: " + totalTransactions);
        System.out.println("Total Payments: $" + String.format("%.2f", totalPayments));
        System.out.println("Average Payment: $" + String.format("%.2f", totalTransactions > 0 ? totalPayments/totalTransactions : 0));
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
               cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private void viewParkedCarsReport() {
        List<ParkedCar> parkedCars = new ArrayList<>();
        for (ParkingSpot spot : parkingLot.getAllSpots()) {
            if (spot.isOccupied()) {
                parkedCars.add(new ParkedCar(spot.getVehiclePlateNumber(), spot.getEntryTime().toString()));
            }
        }
        if (parkedCars.isEmpty()) {
            System.out.println("\n=== Parked Cars Report ===");
            System.out.println("No cars currently parked.");
        } else {
            System.out.println("\n=== Parked Cars Report ===");
            System.out.println("Total parked cars: " + parkedCars.size());
            for (ParkedCar car : parkedCars) {
                System.out.println("Plate Number: " + car.getPlateNumber() + ", Entry Time: " + car.getEntryTime());
            }
        }
    }
}