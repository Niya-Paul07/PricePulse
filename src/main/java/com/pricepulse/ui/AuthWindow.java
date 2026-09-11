package com.pricepulse.ui;

import com.pricepulse.auth.AuthService;
import com.pricepulse.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

/**
 * Login / Sign Up window (Swing).
 * On success, calls onSuccess with the logged-in/newly-created User and
 * disposes itself. Main hooks onSuccess to open MainMenuFrame.
 */
public class AuthWindow extends JFrame {
    private final AuthService authService;
    private final Consumer<User> onSuccess;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    // Login fields
    private final JTextField loginEmail = new JTextField(18);
    private final JPasswordField loginPassword = new JPasswordField(18);
    private final JLabel loginStatus = new JLabel(" ");

    // Sign up fields
    private final JTextField suName = new JTextField(18);
    private final JTextField suEmail = new JTextField(18);
    private final JPasswordField suPassword = new JPasswordField(18);
    private final JLabel suStatus = new JLabel(" ");

    public AuthWindow(AuthService authService, Consumer<User> onSuccess) {
        super("PricePulse — Login");
        this.authService = authService;
        this.onSuccess = onSuccess;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cards.add(buildLoginPanel(), "LOGIN");
        cards.add(buildSignUpPanel(), "SIGNUP");
        add(cards);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(new JLabel("Log in to PricePulse", SwingConstants.CENTER), gbc);
        gbc.gridwidth = 1;

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(loginEmail, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(loginPassword, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        loginStatus.setForeground(Color.RED);
        panel.add(loginStatus, gbc);
        gbc.gridwidth = 1;

        row++;
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(this::handleLogin);
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(loginBtn, gbc);

        JButton toSignUp = new JButton("Need an account? Sign Up");
        toSignUp.addActionListener(e -> cardLayout.show(cards, "SIGNUP"));
        gbc.gridx = 1;
        panel.add(toSignUp, gbc);

        getRootPane().setDefaultButton(loginBtn);
        return panel;
    }

    private JPanel buildSignUpPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(new JLabel("Create your PricePulse account", SwingConstants.CENTER), gbc);
        gbc.gridwidth = 1;

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(suName, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(suEmail, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(suPassword, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        suStatus.setForeground(Color.RED);
        panel.add(suStatus, gbc);
        gbc.gridwidth = 1;

        row++;
        JButton signUpBtn = new JButton("Sign Up");
        signUpBtn.addActionListener(this::handleSignUp);
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(signUpBtn, gbc);

        JButton toLogin = new JButton("Already have an account? Login");
        toLogin.addActionListener(e -> cardLayout.show(cards, "LOGIN"));
        gbc.gridx = 1;
        panel.add(toLogin, gbc);

        getRootPane().setDefaultButton(signUpBtn);
        return panel;
    }

    private void handleLogin(ActionEvent e) {
        String email = loginEmail.getText().trim();
        String password = new String(loginPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            loginStatus.setText("Enter both email and password.");
            return;
        }

        User user = authService.login(email, password);
        if (user == null) {
            loginStatus.setText("Wrong email or password.");
            return;
        }

        dispose();
        onSuccess.accept(user);
    }

    private void handleSignUp(ActionEvent e) {
        String name = suName.getText().trim();
        String email = suEmail.getText().trim();
        String password = new String(suPassword.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            suStatus.setText("Please fill in all fields.");
            return;
        }

        User user = authService.signUp(name, email, password);
        if (user == null) {
            suStatus.setText("That email is already registered.");
            return;
        }

        authService.saveData();
        dispose();
        onSuccess.accept(user);
    }
}
