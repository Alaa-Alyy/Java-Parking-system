import java.util.*;

public class EntryStationOperator {
    private String operatorId, operatorName;
    private ParkingLot lot;

    public EntryStationOperator(String id, String name, ParkingLot lot) {
        this.operatorId = id;
        this.operatorName = name;
        this.lot = lot;
    }

    public boolean assignSpotToTicket(String ticketId, int spotNumber) {
        // Validate ticket
        Ticket ticket = TicketDatabase.getTicketById(ticketId);
        if (ticket == null) {
            return false;
        }

        // Check if vehicle already has a spot
        if (lot.findSpotByPlate(ticket.getPlateNumber()) != null) {
            return false;
        }

        // Validate and assign spot
        if (spotNumber > 0 && spotNumber <= lot.getTotalSpots()) {
            ParkingSpot spot = lot.getSpotById(spotNumber);
            if (spot != null && !spot.isOccupied()) {
                spot.setOccupied(true);
                spot.setVehiclePlateNumber(ticket.getPlateNumber());
                spot.setEntryTime(new Date());
                lot.updateOccupiedSpots(true);
                lot.saveState();
                return true;
            }
        }
        return false;
    }

    public String adviseCustomerWithFreeSpot() {
        List<ParkingSpot> free = lot.getAvailableSpots();
        if (free.isEmpty()) {
            return "Sorry, no parking spots are available at the moment.";
        }
        StringBuilder advice = new StringBuilder();
        advice.append("Available spots: ");
        for (ParkingSpot spot : free) {
            advice.append(spot.getSpotId()).append(" ");
        }
        return advice.toString();
    }

    public boolean assignSpecificSpot(String plate, int spotId) {
        ParkingSpot spot = lot.getSpotById(spotId);
        if (spot != null && !spot.isOccupied()) {
            spot.setOccupied(true);
            spot.setVehiclePlateNumber(plate);
            spot.setEntryTime(new Date());
            lot.updateOccupiedSpots(true);
            lot.saveState();
            return true;
        }
        return false;
    }

    public ParkingSpot findSpotByPlate(String plate) {
        for (ParkingSpot s : lot.getAllSpots()) {
            if (s.isOccupied() && plate.equalsIgnoreCase(s.getVehiclePlateNumber())) return s;
        }
        return null;
    }

    public String getOperatorInfo() {
        return String.format("Entry Station Operator\nID: %s\nName: %s", operatorId, operatorName);
    }
}