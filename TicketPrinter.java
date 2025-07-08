public class TicketPrinter {
    public static void print(Ticket ticket) {
        System.out.println("\n=== Parking Ticket ===");
        System.out.println("Ticket ID: " + ticket.getTicketId());
        System.out.println("Plate Number: " + ticket.getPlateNumber());
        System.out.println("Entry Time: " + ticket.getEntryTime());
        System.out.println("======================\n");
    }
}