package com.university.app.ui;

import com.university.app.dao.GenericDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DynamicRecordDialog extends JDialog {
    private GenericDAO genericDAO;
    private String tableName;
    private Map<String, JTextField> fieldMap = new LinkedHashMap<>();
    private Map<String, Object> initialData;

    public DynamicRecordDialog(Frame owner, String tableName, Map<String, Object> initialData) {
        super(owner, (initialData == null ? "Add" : "Update") + " Record", true);
        this.genericDAO = new GenericDAO();
        this.tableName = tableName;
        this.initialData = initialData; // Will be null for "Add" mode

        try {
            List<String> columnNames = genericDAO.getColumnNames(tableName);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            int gridy = 0;
            for (String columnName : columnNames) {
                gbc.gridx = 0;
                gbc.gridy = gridy;
                gbc.weightx = 0;
                add(new JLabel(columnName + ":"), gbc);

                gbc.gridx = 1;
                gbc.gridy = gridy;
                gbc.weightx = 1;
                JTextField textField = new JTextField();
                textField.setPreferredSize(new Dimension(250, 25)); // Set preferred size
                if (initialData != null) {
                    Object value = initialData.get(columnName);
                    textField.setText(value != null ? value.toString() : "");
                }
                add(textField, gbc);
                fieldMap.put(columnName, textField);
                gridy++;
            }

            gbc.gridx = 1;
            gbc.gridy = gridy;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.fill = GridBagConstraints.NONE;
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> saveRecord());
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(saveButton);
            gbc.gridwidth = 2;
            gbc.gridx = 0;
            add(buttonPanel, gbc);

            pack();
            setMinimumSize(new Dimension(Math.max(400, getWidth()), Math.max(200, getHeight())));
            setLocationRelativeTo(owner);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error building form: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void saveRecord() {
        Map<String, Object> values = new HashMap<>();
        for (Map.Entry<String, JTextField> entry : fieldMap.entrySet()) {
            values.put(entry.getKey(), entry.getValue().getText());
        }

        try {
            if (initialData == null) { // Add mode
                genericDAO.insertRecord(tableName, values);
                JOptionPane.showMessageDialog(this, "Record added successfully!");
            } else { // Update mode
                Map<String, Object> whereClauses = new HashMap<>();
                List<String> primaryKeys = genericDAO.getPrimaryKeys(tableName);
                for(String pk : primaryKeys){
                    whereClauses.put(pk, initialData.get(pk));
                }
                
                if(whereClauses.isEmpty()){
                     JOptionPane.showMessageDialog(this, "Cannot update record: No primary key defined for this table.", "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                }
                
                genericDAO.updateRecord(tableName, values, whereClauses);
                JOptionPane.showMessageDialog(this, "Record updated successfully!");
            }
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving record: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 