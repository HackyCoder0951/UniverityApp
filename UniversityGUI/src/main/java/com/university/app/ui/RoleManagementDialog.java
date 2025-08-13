package com.university.app.ui;

import javax.swing.*;
import java.awt.*;

public class RoleManagementDialog extends JDialog {
    public RoleManagementDialog(Frame owner) {
        super(owner, "Role Management", true);
        setLayout(new BorderLayout());
        add(new RoleManagementPanel(), BorderLayout.CENTER);
        setSize(400, 300);
        setLocationRelativeTo(owner);
    }
} 