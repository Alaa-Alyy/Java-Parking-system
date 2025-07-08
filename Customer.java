import java.util.Date;
import java.util.Scanner;

public class Customer {
    private String customerId;
    private String name;
    private ParkingLot parkingLot;

    public Customer(String customerId, String name, ParkingLot parkingLot) {
        this.customerId = customerId;
        this.name = name;
        this.parkingLot = parkingLot;
    }

    public boolean customerModule(Scanner scanner) {
        while (true) {
            System.out.println("\n=== Customer Menu ===");
            System.out.println("1. Print Ticket");
            System.out.println("2. Pay for Parking");
            System.out.println("3. Logout");
            System.out.print("Enter your choice (1-3): ");

            int choice = 0;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    printTicket(scanner);
                    continue;
                case 2:
                    payForParking(scanner);
                    continue;
                case 3:
                    System.out.println("\nThank you for using our parking system. Goodbye!");
                    return false;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
                    continue;
            }
        }
    }

    private void printTicket(Scanner scanner) {
        System.out.println("\n=== Print Ticket ===");
        System.out.print("Enter your vehicle plate number: ");
        String plateNumber = scanner.nextLine();
        
        if (plateNumber.trim().isEmpty()) {
            System.out.println("Plate number cannot be empty.");
            return;
        }

        // Check if the plate number already has a ticket
        if (parkingLot.findSpotByPlate(plateNumber) != null) {
            System.out.println("Error: This vehicle already has an active ticket and spot.");
            return;
        }

        // Generate ticket
        String entryId = java.util.UUID.randomUUID().toString().substring(0, 8);
        Ticket ticket = new Ticket(entryId, plateNumber, new Date());
        
        // Save ticket to database
        TicketDatabase.saveTicket(ticket);
        
        // Print ticket
        System.out.println("\nTicket generated successfully!");
        System.out.println("Please show this ticket to the entry operator to get your parking spot assigned.");
        System.out.println("\nTicket Details:");
        System.out.println("Ticket ID: " + ticket.getTicketId());
        System.out.println("Plate Number: " + ticket.getPlateNumber());
        System.out.println("Entry Time: " + ticket.getEntryTime());
    }

    private void payForParking(Scanner scanner) {
        System.out.println("\n=== Pay for Parking ===");
        System.out.print("Enter your ticket ID: ");
        String ticketId = scanner.nextLine();
        
        if (ticketId.trim().isEmpty()) {
            System.out.println("Ticket ID cannot be empty.");
            return;
        }

        // Check if ticket has been verified by exit operator
        if (!ExitStationOperator.isTicketVerified(ticketId)) {
            System.out.println("\nThis ticket has not been verified by an exit operator.");
            System.out.println("Please visit the exit station to have your ticket checked before payment.");
            return;
        }

        ExitStationOperator exitOperator = new ExitStationOperator("SELF_CHECKOUT", "Self Checkout", parkingLot);
        TicketDetails details = exitOperator.checkTicketDetails(ticketId);
        
        if (details == null) {
            System.out.println("Invalid or non-existent ticket ID.");
            return;
        }

        System.out.println("\nParking Details:");
        System.out.println("Ticket ID: " + details.getTicketId());
        System.out.println("Plate Number: " + details.getPlateNumber());
        System.out.println("Duration: " + details.getFormattedDuration());
        System.out.println("Fee: $" + String.format("%.2f", details.getFee()));
        System.out.print("\nConfirm payment? (yes/no): ");
        
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            if (exitOperator.processCheckout(ticketId)) {
                logCheckout(details.getPlateNumber(), details.getDuration(), details.getFee());
                // Remove the ticket from verified list after successful payment
                ExitStationOperator.removeVerifiedTicket(ticketId);
                System.out.println("Payment complete. Thank you!");
            } else {
                System.out.println("Error processing payment. Please try again or seek assistance.");
            }
        } else {
            System.out.println("Payment cancelled.");
        }
    }

    private void logCheckout(String plate, long duration, double fee) {
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter("parking_history.txt", true))) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String checkoutTime = sdf.format(new Date());
            bw.write(plate + "," + duration + " minutes," + checkoutTime + ",$" + fee + "\n");
        } catch (java.io.IOException e) {
            System.out.println("Error logging checkout: " + e.getMessage());
        }
    }
} 