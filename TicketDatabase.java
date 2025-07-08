import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class TicketDatabase {
    private static final String TICKET_FILE = "tickets.txt";
    private static final Object fileLock = new Object();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static void saveTicket(Ticket ticket) {
        synchronized(fileLock) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(TICKET_FILE, true))) {
                writer.write(String.format("%s,%s,%d\n",
                    ticket.getTicketId(),
                    ticket.getPlateNumber(),
                    ticket.getEntryTime().getTime()));
            } catch (IOException e) {
                System.out.println("Error saving ticket: " + e.getMessage());
            }
        }
    }

    public static Ticket getTicketById(String ticketId) {
        synchronized(fileLock) {
            try (BufferedReader reader = new BufferedReader(new FileReader(TICKET_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(ticketId)) {
                        try {
                            long timestamp = Long.parseLong(parts[2].trim());
                            return new Ticket(parts[0], parts[1], new Date(timestamp));
                        } catch (NumberFormatException e) {
                            System.out.println("Error parsing ticket timestamp: " + e.getMessage());
                            return null;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading ticket: " + e.getMessage());
            }
            return null;
        }
    }

    public static boolean deleteTicket(String ticketId) {
        synchronized(fileLock) {
            File inputFile = new File(TICKET_FILE);
            File tempFile = new File(TICKET_FILE + ".tmp");
            boolean found = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (!parts[0].equals(ticketId)) {
                        writer.write(line + "\n");
                    } else {
                        found = true;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error deleting ticket: " + e.getMessage());
                return false;
            }

            if (!found) {
                tempFile.delete();
                return false;
            }

            return inputFile.delete() && tempFile.renameTo(inputFile);
        }
    }
}