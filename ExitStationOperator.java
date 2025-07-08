import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class ExitStationOperator {
    private String operatorId;
    private String name;
    private ParkingLot parkingLot;
    private static Set<String> verifiedTickets = new HashSet<>();

    public ExitStationOperator(String operatorId, String name, ParkingLot parkingLot) {
        this.operatorId = operatorId;
        this.name = name;
        this.parkingLot = parkingLot;
    }

    public static boolean isTicketVerified(String ticketId) {
        return verifiedTickets.contains(ticketId);
    }

    public static void markTicketAsVerified(String ticketId) {
        verifiedTickets.add(ticketId);
    }

    public static void removeVerifiedTicket(String ticketId) {
        verifiedTickets.remove(ticketId);
    }

    public TicketDetails checkTicketDetails(String ticketId) {
        Ticket ticket = TicketDatabase.getTicketById(ticketId);
        if (ticket == null) {
            return null;
        }

        ParkingSpot spot = parkingLot.findSpotByPlate(ticket.getPlateNumber());
        if (spot == null || !spot.isOccupied()) {
            return null;
        }

        long durationMinutes = spot.getParkingDuration();
        double fee = calculateParkingFee(durationMinutes);
        
        return new TicketDetails(
            ticket.getTicketId(),
            ticket.getPlateNumber(),
            durationMinutes,
            fee,
            getFormattedDuration(durationMinutes)
        );
    }

    public boolean processCheckout(String ticketId) {
        Ticket ticket = TicketDatabase.getTicketById(ticketId);
        if (ticket == null) {
            return false;
        }

        ParkingSpot spot = parkingLot.findSpotByPlate(ticket.getPlateNumber());
        if (spot == null || !spot.isOccupied()) {
            return false;
        }

        // Calculate duration and fee
        long durationMinutes = spot.getParkingDuration();
        double fee = calculateParkingFee(durationMinutes);

        // Log to parking history
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("parking_history.txt", true))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String checkoutTime = sdf.format(new Date());
            writer.write(String.format("%s,%d minutes,%s,$%.2f\n",
                ticket.getPlateNumber(),
                durationMinutes,
                checkoutTime,
                fee));
        } catch (IOException e) {
            System.out.println("Error logging to parking history: " + e.getMessage());
        }

        // Clear the spot
        spot.setOccupied(false);
        spot.setVehiclePlateNumber(null);
        spot.setEntryTime(null);
        parkingLot.updateOccupiedSpots(false);
        parkingLot.saveState();

        // Delete the ticket
        TicketDatabase.deleteTicket(ticketId);

        return true;
    }

    public double calculateParkingFee(long durationMinutes) {
        // $0.10 per minute
        return durationMinutes * 0.10;
    }

    public String getFormattedDuration(long durationMinutes) {
        long hours = durationMinutes / 60;
        long remainingMinutes = durationMinutes % 60;
        
        if (hours > 0) {
            return String.format("%d hour%s %d minute%s", 
                hours, hours != 1 ? "s" : "",
                remainingMinutes, remainingMinutes != 1 ? "s" : "");
        } else {
            return String.format("%d minute%s", 
                remainingMinutes, remainingMinutes != 1 ? "s" : "");
        }
    }

    public String getOperatorInfo() {
        return String.format("Exit Station Operator\nID: %s\nName: %s", operatorId, name);
    }
}

class TicketDetails {
    private String ticketId;
    private String plateNumber;
    private long duration;
    private double fee;
    private String formattedDuration;

    public TicketDetails(String ticketId, String plateNumber, long duration, double fee, String formattedDuration) {
        this.ticketId = ticketId;
        this.plateNumber = plateNumber;
        this.duration = duration;
        this.fee = fee;
        this.formattedDuration = formattedDuration;
    }

    public String getTicketId() { return ticketId; }
    public String getPlateNumber() { return plateNumber; }
    public long getDuration() { return duration; }
    public double getFee() { return fee; }
    public String getFormattedDuration() { return formattedDuration; }
} 