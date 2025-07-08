import java.util.Date;

public class ParkingSpot {
    private int spotId;
    private boolean isOccupied;
    private String vehiclePlateNumber;
    private Long entryTime; // Store as milliseconds

    public ParkingSpot(int id) {
        this.spotId = id;
        this.isOccupied = false;
        this.entryTime = null;
    }

    public int getSpotId() { return spotId; }
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean val) { isOccupied = val; }
    public String getVehiclePlateNumber() { return vehiclePlateNumber; }
    public void setVehiclePlateNumber(String val) { vehiclePlateNumber = val; }
    public Date getEntryTime() { return entryTime == null ? null : new Date(entryTime); }
    public void setEntryTime(Date val) { entryTime = (val == null) ? null : val.getTime(); }

    public long getParkingDuration() {
        if (entryTime == null) return 0;
        long now = new Date().getTime();
        return (now - entryTime) / 60000;
    }
}