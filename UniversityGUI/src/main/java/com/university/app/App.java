package com.university.app;

import com.university.app.ui.LoginDialog;
import com.university.app.ui.MainFrame;

import javax.swing.*;

/**
 * The main entry point for the University ERP application.
 * This class is responsible for setting the application's look and feel
 * and managing the main login loop.
 */
public class App {

    private static JFrame mainFrame;

    /**
     * The main method that starts the application.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Set a modern "Nimbus" look and feel for the UI.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, the application will use the system default look and feel.
            e.printStackTrace();
        }
        
        showLogin();
    }

    /**
     * Displays the login dialog and controls the application flow based on authentication.
     * If a main frame is already open, it will be disposed before showing the login screen,
     * effectively handling a logout-then-login sequence.
     */
    public static void showLogin() {
        if (mainFrame != null) {
            mainFrame.dispose();
        }

        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);

            // If authentication is successful, show the main application frame.
            // Otherwise, exit the application.
            if (loginDialog.isAuthenticated()) {
                mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
} 