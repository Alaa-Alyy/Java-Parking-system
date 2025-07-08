import java.io.*;
import java.util.*;

public class UserDatabase {
    private static final String USER_FILE = "users.txt";

    public static User authenticate(String userId, String password) {
        File userFile = new File(USER_FILE);
        if (!userFile.exists()) {
            System.out.println("User database not found.");
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(userId) && parts[3].equals(password)) {
                    return new User(parts[0], parts[1], parts[2], parts[3]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user database: " + e.getMessage());
        }
        return null;
    }

    public static boolean userExists(String userId) {
        File userFile = new File(USER_FILE);
        if (!userFile.exists()) return false;
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(userId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error checking user existence: " + e.getMessage());
        }
        return false;
    }

    public static void addUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            writer.write(user.getId() + "," + user.getName() + "," + user.getRole() + "," + user.getPassword() + "\n");
        } catch (IOException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }

    public static void updateUser(String userId, String newName, String newPassword, String newRole) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(userId)) {
                    String updatedName = newName.isEmpty() ? parts[1] : newName;
                    String updatedPassword = newPassword.isEmpty() ? parts[3] : newPassword;
                    String updatedRole = newRole.isEmpty() ? parts[2] : newRole;
                    line = userId + "," + updatedName + "," + updatedRole + "," + updatedPassword;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading user database: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    public static void deleteUser(String userId) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && !parts[0].equals(userId)) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user database: " + e.getMessage());
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }
}