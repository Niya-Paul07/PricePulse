
package com.pricepulse.auth;

import com.pricepulse.model.DBConnection;
import com.pricepulse.model.User;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AuthService {

    private static final int ITERATIONS = 210_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private final SecureRandom random = new SecureRandom();

    // Kept temporarily so existing Main.java still compiles.
    // Authentication is now stored in MySQL, not a CSV file.
    public AuthService(String ignoredDataFile) {
    }

    public void loadData() {
        // Users are read from MySQL when needed.
    }

    public void saveData() {
        // MySQL saves changes immediately.
    }

    public User signUp(String name, String email, String password) {
        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please fill in all required fields.");
        }

        String cleanName = name.trim();
        String cleanEmail = email.trim();

        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);

        String passwordHash = hashPassword(password, salt);
        String storedHash = "pbkdf2$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + passwordHash;

        String sql = """
                INSERT INTO users (full_name, email, password_hash)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, cleanName);
            statement.setString(2, cleanEmail);
            statement.setString(3, storedHash);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Account was not created.");
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return new User(
                            keys.getInt(1), cleanName, cleanEmail, "");
                }
            }

            throw new SQLException("Could not retrieve the new user ID.");

        } catch (SQLIntegrityConstraintViolationException e) {
            return null; // Email already exists.
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not create account. Check your MySQL connection.",
                    e);
        }
    }

    public User login(String email, String password) {
        if (email == null || email.isBlank()
                || password == null || password.isEmpty()) {
            return null;
        }

        String sql = """
                SELECT user_id, full_name, email, password_hash
                FROM users
                WHERE email = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email.trim());

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                String storedHash = result.getString("password_hash");

                if (!verifyPassword(password, storedHash)) {
                    return null;
                }

                return new User(
                        result.getInt("user_id"),
                        result.getString("full_name"),
                        result.getString("email"),
                        "");
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not log in. Check your MySQL connection.", e);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        String sql = """
                SELECT user_id, full_name, email
                FROM users
                ORDER BY user_id
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                users.add(new User(
                        result.getInt("user_id"),
                        result.getString("full_name"),
                        result.getString("email"),
                        ""));
            }

            return users;

        } catch (SQLException e) {
            throw new IllegalStateException("Could not load users.", e);
        }
    }

    private String hashPassword(String password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            return Base64.getEncoder().encodeToString(
                    factory.generateSecret(spec).getEncoded());

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Password hashing is unavailable.", e);
        } finally {
            spec.clearPassword();
        }
    }

    private boolean verifyPassword(String password, String storedHash) {
        if (storedHash == null) {
            return false;
        }

        try {
            String[] parts = storedHash.split("\\$");

            if (parts.length != 4 || !parts[0].equals("pbkdf2")) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);
            if (iterations < 100_000 || iterations > 1_000_000) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    iterations,
                    expectedHash.length * 8);

            try {
                SecretKeyFactory factory =
                        SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

                byte[] actualHash =
                        factory.generateSecret(spec).getEncoded();

                return java.security.MessageDigest.isEqual(
                        expectedHash, actualHash);
            } finally {
                spec.clearPassword();
            }

        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return false;
        }
    }
}
