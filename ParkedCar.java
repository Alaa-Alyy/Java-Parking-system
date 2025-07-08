public class ParkedCar {
    private String plateNumber;
    private String entryTime;

    public ParkedCar(String plateNumber, String entryTime) {
        this.plateNumber = plateNumber;
        this.entryTime = entryTime;
    }

    public String getPlateNumber() { return plateNumber; }
    public String getEntryTime() { return entryTime; }
}