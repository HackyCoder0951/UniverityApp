package com.university.app.ui;

import javax.swing.*;
import java.awt.*;

public class RoleManagementPanel extends JPanel {
    private DefaultListModel<String> roleListModel;
    private JList<String> roleList;
    private JButton addButton, editButton, deleteButton;

    public RoleManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        roleListModel = new DefaultListModel<>();
        roleList = new JList<>(roleListModel);
        JScrollPane scrollPane = new JScrollPane(roleList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add Role");
        editButton = new JButton("Edit Role");
        deleteButton = new JButton("Delete Role");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Placeholder: populate with sample roles
        roleListModel.addElement("admin");
        roleListModel.addElement("entry");
        roleListModel.addElement("reporting");
        // TODO: Wire up to real DB and implement add/edit/delete actions
    }
} 