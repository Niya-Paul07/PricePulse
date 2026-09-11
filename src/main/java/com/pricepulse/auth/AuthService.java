package com.pricepulse.auth;

import com.pricepulse.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles Sign Up and Login.
 * Follows the design doc's storage rule: in-memory during runtime,
 * loaded once at startup (loadData) and saved on exit (saveData).
 */
public class AuthService {
    private final List<User> users = new ArrayList<>();
    private int nextId = 1;
    private final String dataFile;

    public AuthService(String dataFile) {
        this.dataFile = dataFile;
    }

    /** Creates a new account. Returns the new User, or null if the email is taken. */
    public User signUp(String name, String email, String password) {
        if (findByEmail(email) != null) {
            return null; // email already registered
        }
        User user = new User(nextId++, name, email, password);
        users.add(user);
        return user;
    }

    /** Returns the matching User if credentials are correct, otherwise null. */
    public User login(String email, String password) {
        User user = findByEmail(email);
        if (user != null && user.checkPassword(password)) {
            return user;
        }
        return null;
    }

    private User findByEmail(String email) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        return users;
    }

    /** Loads users from a simple CSV file: id,name,email,password */
    public void loadData() {
        File file = new File(dataFile);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int maxId = 0;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",", 4);
                int id = Integer.parseInt(parts[0]);
                User user = new User(id, parts[1], parts[2], parts[3]);
                users.add(user);
                maxId = Math.max(maxId, id);
            }
            nextId = maxId + 1;
        } catch (IOException e) {
            System.out.println("Could not load users: " + e.getMessage());
        }
    }

    /** Saves all users to the CSV file. Call this on app exit. */
    public void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(dataFile))) {
            for (User u : users) {
                writer.println(u.getId() + "," + u.getName() + "," + u.getEmail() + "," + rawPassword(u));
            }
        } catch (IOException e) {
            System.out.println("Could not save users: " + e.getMessage());
        }
    }

    // Small helper since password is package-private on User by design.
    private String rawPassword(User u) {
        // AuthService lives in a different package from User, so we can't
        // reach the private field directly — this keeps persistence logic
        // here rather than leaking password handling into the model class.
        // Re-checked via checkPassword during login, so this is only for save/load.
        return u.exportPasswordForPersistence();
    }
}
