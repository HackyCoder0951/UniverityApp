package com.university.app.ui;

import com.university.app.dao.PasswordRequestDAO;
import com.university.app.dao.UserDAO;
import com.university.app.model.PasswordRequest;
import com.university.app.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PasswordRequestPanel extends JPanel {
    private JTable requestTable;
    private DefaultTableModel tableModel;
    private PasswordRequestDAO passwordRequestDAO;
    private List<PasswordRequest> currentRequests;

    public PasswordRequestPanel() {
        super(new BorderLayout(10, 10));
        passwordRequestDAO = new PasswordRequestDAO();

        // Table setup
        String[] columnNames = {"Request ID", "Username", "Request Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are non-editable
            }
        };
        requestTable = new JTable(tableModel);
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(requestTable), BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton approveButton = new JButton("Approve Request");
        JButton denyButton = new JButton("Deny Request");
        JButton historyButton = new JButton("View History");
        
        approveButton.addActionListener(e -> handleRequest("approved"));
        denyButton.addActionListener(e -> handleRequest("denied"));
        historyButton.addActionListener(e -> showHistoryDialog());
        
        buttonPanel.add(approveButton);
        buttonPanel.add(denyButton);
        buttonPanel.add(historyButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadPendingRequests();
    }

    private void loadPendingRequests() {
        tableModel.setRowCount(0);
        currentRequests = passwordRequestDAO.getPendingRequests();
        for (PasswordRequest request : currentRequests) {
            tableModel.addRow(new Object[]{
                    request.getRequestId(),
                    request.getUsername(),
                    request.getRequestDate()
            });
        }
    }

    private void handleRequest(String status) {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to " + status + ".", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PasswordRequest selectedRequest = currentRequests.get(selectedRow);
        
        if ("approved".equals(status)) {
            String newPassword = JOptionPane.showInputDialog(this, "Enter new temporary password for " + selectedRequest.getUsername() + ":");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                UserDAO userDAO = new UserDAO();
                // Update the user's actual password
                userDAO.updatePassword(selectedRequest.getUsername(), newPassword);

                // For entry users, require them to change it on next login
                User user = userDAO.getUserByUsername(selectedRequest.getUsername());
                if (user != null && "entry".equals(user.getRole())) {
                    userDAO.setRequiresPasswordResetFlag(user.getUsername(), true);
                }

                // Mark the request as approved
                passwordRequestDAO.updateRequestStatus(selectedRequest.getRequestId(), status);
                JOptionPane.showMessageDialog(this, "Request has been approved and password has been set.");
                loadPendingRequests(); // Refresh
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error processing request: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else { // Denied
            try {
                passwordRequestDAO.updateRequestStatus(selectedRequest.getRequestId(), status);
                JOptionPane.showMessageDialog(this, "Request has been " + status + ".");
                loadPendingRequests(); // Refresh the list
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating request: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void showHistoryDialog() {
        List<PasswordRequest> allRequests = passwordRequestDAO.getAllRequests();
        String[] columns = {"Request ID", "Username", "Request Date", "Status"};
        DefaultTableModel historyModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (PasswordRequest req : allRequests) {
            historyModel.addRow(new Object[]{
                req.getRequestId(), req.getUsername(), req.getRequestDate(), req.getStatus()
            });
        }
        JTable historyTable = new JTable(historyModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        JOptionPane.showMessageDialog(this, scrollPane, "Password Request History", JOptionPane.INFORMATION_MESSAGE);
    }
} 