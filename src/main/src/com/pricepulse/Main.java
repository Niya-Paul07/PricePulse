
package com.pricepulse;

import com.pricepulse.auth.AuthService;
import com.pricepulse.ui.AuthWindow;
import com.pricepulse.ui.MainMenuFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AuthService authService = new AuthService(null);

            AuthWindow authWindow = new AuthWindow(authService, user -> {
                MainMenuFrame mainMenu = new MainMenuFrame(user);
                mainMenu.setVisible(true);
            });

            authWindow.setVisible(true);
        });
    }
}
