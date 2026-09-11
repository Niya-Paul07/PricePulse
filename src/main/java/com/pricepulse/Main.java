package com.pricepulse;

import com.pricepulse.auth.AuthService;
import com.pricepulse.ui.AuthWindow;
import com.pricepulse.ui.MainMenuFrame;

import javax.swing.*;

/**
 * App entry point (Swing).
 *
 * INTEGRATION TODO (Niya): once teammates' modules are ready as JPanels,
 * add them to MainMenuFrame as tabs — see MainMenuFrame for the convention
 * each teammate should follow.
 */
public class Main {
    public static void main(String[] args) {
        AuthService authService = new AuthService("data/users.csv");
        authService.loadData();

        SwingUtilities.invokeLater(() -> {
            AuthWindow authWindow = new AuthWindow(authService, user -> {
                MainMenuFrame mainMenu = new MainMenuFrame(user);
                mainMenu.setVisible(true);
                // Save users on close of the main window (simple approach for now).
                mainMenu.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        authService.saveData();
                    }
                });
            });
            authWindow.setVisible(true);
        });
    }
}
