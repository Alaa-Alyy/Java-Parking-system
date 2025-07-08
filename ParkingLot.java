import java.util.*;
import java.io.*;

public class ParkingLot {
    private List<ParkingSpot> spots;
    private int totalSpots;
    private int occupiedSpots;
    private static final String STATE_FILE = "parking_lot_state.txt";
    private static final Object fileLock = new Object();

    private ParkingLot(int totalSpots, boolean isNewLot) {
        this.totalSpots = totalSpots;
        this.occupiedSpots = 0;
        spots = new ArrayList<>();
        for (int i = 1; i <= totalSpots; i++) {
            spots.add(new ParkingSpot(i));
        }
        if (isNewLot) {
            saveState();
        }
    }

    public static ParkingLot createNewLot(int totalSpots) {
        return new ParkingLot(totalSpots, true);
    }

    public List<ParkingSpot> getAvailableSpots() {
        List<ParkingSpot> available = new ArrayList<>();
        for (ParkingSpot s : spots) if (!s.isOccupied()) available.add(s);
        return Collections.unmodifiableList(available);
    }

    public ParkingSpot getSpotById(int id) {
        for (ParkingSpot s : spots) if (s.getSpotId() == id) return s;
        return null;
    }

    public List<ParkingSpot> getAllSpots() { return Collections.unmodifiableList(spots); }

    public int getTotalSpots() { return totalSpots; }

    public int getOccupiedSpots() { return occupiedSpots; }

    public void addSpots(int additionalSpots) {
        if (additionalSpots <= 0) {
            throw new IllegalArgumentException("Additional spots must be positive");
        }
        synchronized(fileLock) {
            for (int i = totalSpots + 1; i <= totalSpots + additionalSpots; i++) {
                spots.add(new ParkingSpot(i));
            }
            totalSpots += additionalSpots;
            saveState();
        }
    }

    public synchronized void updateOccupiedSpots(boolean isOccupied) {
        int newCount = occupiedSpots + (isOccupied ? 1 : -1);
        if (newCount < 0 || newCount > totalSpots) {
            throw new IllegalStateException("Invalid occupied spots count: " + newCount);
        }
        occupiedSpots = newCount;
        saveState();
    }

    public ParkingSpot findSpotByPlate(String plateNumber) {
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            return null;
        }
        for (ParkingSpot spot : spots) {
            if (spot.isOccupied() && plateNumber.equals(spot.getVehiclePlateNumber())) {
                return spot;
            }
        }
        return null;
    }

    public synchronized ParkingSpot assignFirstAvailableSpot(String plateNumber) {
        List<ParkingSpot> availableSpots = getAvailableSpots();
        if (availableSpots.isEmpty()) {
            return null;
        }
        ParkingSpot spot = availableSpots.get(0);
        spot.setOccupied(true);
        spot.setVehiclePlateNumber(plateNumber);
        spot.setEntryTime(new Date());
        updateOccupiedSpots(true);
        saveState();
        return spot;
    }

    public synchronized boolean assignSpecificSpot(String plateNumber, int spotId) {
        ParkingSpot spot = getSpotById(spotId);
        if (spot != null && !spot.isOccupied()) {
            spot.setOccupied(true);
            spot.setVehiclePlateNumber(plateNumber);
            spot.setEntryTime(new Date());
            updateOccupiedSpots(true);
            saveState();
            return true;
        }
        return false;
    }

    public void saveState() {
        synchronized(fileLock) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(STATE_FILE))) {
                writer.write(totalSpots + "\n");
                writer.write(occupiedSpots + "\n");
                for (ParkingSpot spot : spots) {
                    writer.write(String.format("%d,%b,%s,%s\n",
                        spot.getSpotId(),
                        spot.isOccupied(),
                        spot.getVehiclePlateNumber() != null ? spot.getVehiclePlateNumber() : "null",
                        spot.getEntryTime() != null ? spot.getEntryTime().getTime() : "null"));
                }
            } catch (IOException e) {
                System.out.println("Error saving parking lot state: " + e.getMessage());
            }
        }
    }

    public void validateState() {
        synchronized(fileLock) {
            try {
                // Read all current tickets
                Set<String> plateNumbers = new HashSet<>();
                try (BufferedReader reader = new BufferedReader(new FileReader("tickets.txt"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 2) {
                            plateNumbers.add(parts[1].trim());
                        }
                    }
                }

                // Check each occupied spot against tickets
                boolean needsSave = false;
                for (ParkingSpot spot : spots) {
                    if (spot.isOccupied()) {
                        String plate = spot.getVehiclePlateNumber();
                        if (plate != null && !plateNumbers.contains(plate)) {
                            // Found occupied spot without ticket - clear it
                            spot.setOccupied(false);
                            spot.setVehiclePlateNumber(null);
                            spot.setEntryTime(null);
                            needsSave = true;
                        }
                    }
                }

                // Update occupied spots count and save if needed
                if (needsSave) {
                    int count = 0;
                    for (ParkingSpot spot : spots) {
                        if (spot.isOccupied()) count++;
                    }
                    this.occupiedSpots = count;
                    saveState();
                }
            } catch (IOException e) {
                System.out.println("Error validating parking lot state: " + e.getMessage());
            }
        }
    }

    public static ParkingLot loadState() {
        synchronized(fileLock) {
            try (BufferedReader reader = new BufferedReader(new FileReader(STATE_FILE))) {
                int totalSpots = Integer.parseInt(reader.readLine());
                int occupiedSpots = Integer.parseInt(reader.readLine());
                
                ParkingLot lot = new ParkingLot(totalSpots, false);
                lot.occupiedSpots = occupiedSpots;
                
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    int spotId = Integer.parseInt(parts[0]);
                    boolean occupied = Boolean.parseBoolean(parts[1]);
                    String plateNumber = parts[2].equals("null") ? null : parts[2];
                    String timeStr = parts[3].equals("null") ? null : parts[3];
                    
                    ParkingSpot spot = lot.spots.get(spotId - 1);
                    spot.setOccupied(occupied);
                    spot.setVehiclePlateNumber(plateNumber);
                    if (timeStr != null) {
                        spot.setEntryTime(new Date(Long.parseLong(timeStr)));
                    }
                }
                
                // Validate state on load
                lot.validateState();
                return lot;
            } catch (IOException | NumberFormatException e) {
                System.out.println("Error loading parking lot state: " + e.getMessage());
                return null;
            }
        }
    }

    public void removeSpots(int numToRemove) {
        if (numToRemove <= 0) {
            throw new IllegalArgumentException("Number of spots to remove must be positive");
        }
        synchronized(fileLock) {
            if (numToRemove > totalSpots) {
                throw new IllegalArgumentException("Cannot remove more spots than exist");
            }
            // Check if any of the spots to be removed are occupied
            for (int i = 0; i < numToRemove; i++) {
                ParkingSpot spot = spots.get(spots.size() - 1 - i);
                if (spot.isOccupied()) {
                    throw new IllegalStateException("Cannot remove occupied spots. Please make sure the last " + numToRemove + " spots are empty.");
                }
            }
            // Remove the spots
            for (int i = 0; i < numToRemove; i++) {
                spots.remove(spots.size() - 1);
            }
            totalSpots -= numToRemove;
            saveState();
        }
    }
}