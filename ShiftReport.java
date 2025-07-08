import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ShiftReport {
    private String date;
    private int totalTransactions;
    private double totalRevenue;
    private List<String> transactionDetails;

    public ShiftReport() {
        this.totalTransactions = 0;
        this.totalRevenue = 0.0;
        this.transactionDetails = new ArrayList<>();
    }

    public void generateReport(String date) {
        this.date = date;
        this.totalTransactions = 0;
        this.totalRevenue = 0.0;
        this.transactionDetails.clear();

        File historyFile = new File("parking_history.txt");
        if (!historyFile.exists()) {
            System.out.println("Error: parking_history.txt does not exist.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(historyFile))) {
            String line;
            SimpleDateFormat historyDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy/MM/dd");

            Date inputDate = inputFormat.parse(date);
            String formattedInputDate = displayFormat.format(inputDate);

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String checkoutTime = parts[2].trim();
                    Date parsedDate = historyDateFormat.parse(checkoutTime);
                    String formattedDate = displayFormat.format(parsedDate);

                    if (formattedDate.equals(formattedInputDate)) {
                        totalTransactions++;
                        double fee = Double.parseDouble(parts[3].substring(1));
                        totalRevenue += fee;
                        transactionDetails.add("Plate: " + parts[0] + ", Duration: " + parts[1] + ", Fee: $" + fee);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading parking_history.txt: " + e.getMessage());
        } catch (ParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing fee in parking_history.txt: " + e.getMessage());
        }
    }

    public void viewShiftReport() {
        System.out.println("\n=== Shift Report ===");
        System.out.println("Date: " + date);
        if (totalTransactions == 0) {
            System.out.println("No transactions found for this date.");
        } else {
            System.out.println("Transactions:");
            System.out.println("-----------------------------");
            for (String detail : transactionDetails) {
                System.out.println(detail);
            }
            System.out.println("-----------------------------");
            System.out.println("Total Transactions: " + totalTransactions);
            System.out.println("Total Revenue: $" + totalRevenue);
        }
        System.out.println("===================\n");
    }
}