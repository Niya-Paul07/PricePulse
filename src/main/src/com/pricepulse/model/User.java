package com.pricepulse.model;

/**
 * Represents an app user who can log in and submit price entries.
 * Core class from the system design (docs/design.md).
 * Note: `password` was added here for Login/Sign Up — not shown in the
 * original class diagram, since that diagram predates the auth feature.
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String password;

    public User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /** Used by AuthService.login() to verify credentials. */
    public boolean checkPassword(String attempt) {
        return this.password != null && this.password.equals(attempt);
    }

    public void setPassword(String password) { this.password = password; }

    /**
     * Used only by AuthService for saving/loading users to file.
     * Note: this is a plain-text password for course-project simplicity —
     * a real app would store a hash instead, never the raw password.
     */
    public String exportPasswordForPersistence() {
        return this.password;
    }

    /**
     * Placeholder for Price Entry CRUD teammate (Vaishnavi) to hook into —
     * actual submission logic belongs in the CRUD module, this just marks
     * the intended entry point named in the design doc.
     */
    public void submitPriceEntry() {
        throw new UnsupportedOperationException(
            "Implement in com.pricepulse.crud (Price Entry CRUD module)");
    }

    @Override
    public String toString() {
        return name + " <" + email + ">";
    }
}
