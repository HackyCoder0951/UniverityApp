package com.university.app;

import com.university.app.ui.LoginDialog;
import com.university.app.ui.MainFrame;

import javax.swing.*;

public class App {

    private static JFrame mainFrame;

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to the system default.
        }
        
        showLogin();
    }

    public static void showLogin() {
        if (mainFrame != null) {
            mainFrame.dispose();
        }

        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);

            if (loginDialog.isAuthenticated()) {
                mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
} 