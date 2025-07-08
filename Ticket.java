import java.util.Date;

public class Ticket {
    private String ticketId;
    private String plateNumber;
    private Date entryTime;

    public Ticket(String ticketId, String plateNumber, Date entryTime) {
        this.ticketId = ticketId;
        this.plateNumber = plateNumber;
        this.entryTime = entryTime;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public Date getEntryTime() {
        return entryTime;
    }
}