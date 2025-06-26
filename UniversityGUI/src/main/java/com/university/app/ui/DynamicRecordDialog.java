package com.university.app.ui;

import com.university.app.dao.GenericDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DynamicRecordDialog extends JDialog {
    private GenericDAO genericDAO;
    private String tableName;
    private Map<String, JComponent> fieldMap = new LinkedHashMap<>();
    private Map<String, Object> initialData;
    private Map<String, Map<String, String>> fkDisplayToIdMap = new HashMap<>();

    public DynamicRecordDialog(Frame owner, String tableName, Map<String, Object> initialData) {
        super(owner, (initialData == null ? "Add" : "Update") + " Record", true);
        this.genericDAO = new GenericDAO();
        this.tableName = tableName;
        this.initialData = initialData; // Will be null for "Add" mode

        try {
            List<String> columnNames = genericDAO.getColumnNames(tableName);
            Map<String, String> foreignKeys = genericDAO.getForeignKeys(tableName);
            Map<String, Integer> columnTypes = genericDAO.getColumnTypes(tableName);
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

                JComponent inputField;
                int sqlType = columnTypes.getOrDefault(columnName, Types.VARCHAR);
                if (foreignKeys.containsKey(columnName)) {
                    // Foreign key: use dropdown with ID - Label
                    String[] ref = foreignKeys.get(columnName).split("\\.");
                    List<String[]> pairs = genericDAO.getReferenceIdLabelPairs(ref[0], ref[1]);
                    Map<String, String> displayToId = new HashMap<>();
                    List<String> displayList = new ArrayList<>();
                    for (String[] pair : pairs) {
                        String display = pair[0] + (pair[1].equals(pair[0]) ? "" : (" - " + pair[1]));
                        displayToId.put(display, pair[0]);
                        displayList.add(display);
                    }
                    fkDisplayToIdMap.put(columnName, displayToId);
                    JComboBox<String> comboBox = new JComboBox<>(displayList.toArray(new String[0]));
                    if (initialData != null) {
                        Object value = initialData.get(columnName);
                        if (value != null) {
                            // Try to select the display string matching the ID
                            for (String display : displayList) {
                                if (displayToId.get(display).equals(value.toString())) {
                                    comboBox.setSelectedItem(display);
                                    break;
                                }
                            }
                        }
                    }
                    inputField = comboBox;
                } else if (sqlType == Types.DATE) {
                    JTextField dateField = new JTextField();
                    dateField.setToolTipText("Format: yyyy-MM-dd");
                    if (initialData != null) {
                        Object value = initialData.get(columnName);
                        dateField.setText(value != null ? value.toString() : "");
                    }
                    inputField = dateField;
                } else if (sqlType == Types.TIME) {
                    JTextField timeField = new JTextField();
                    timeField.setToolTipText("Format: HH:mm:ss");
                    if (initialData != null) {
                        Object value = initialData.get(columnName);
                        timeField.setText(value != null ? value.toString() : "");
                    }
                    inputField = timeField;
                } else if (sqlType == Types.INTEGER || sqlType == Types.BIGINT || sqlType == Types.SMALLINT || sqlType == Types.TINYINT) {
                    JTextField intField = new JTextField();
                    intField.setToolTipText("Enter a whole number");
                    if (initialData != null) {
                        Object value = initialData.get(columnName);
                        intField.setText(value != null ? value.toString() : "");
                    }
                    inputField = intField;
                } else if (sqlType == Types.DECIMAL || sqlType == Types.NUMERIC || sqlType == Types.FLOAT || sqlType == Types.DOUBLE || sqlType == Types.REAL) {
                    JTextField numField = new JTextField();
                    numField.setToolTipText("Enter a number");
                    if (initialData != null) {
                        Object value = initialData.get(columnName);
                        numField.setText(value != null ? value.toString() : "");
                    }
                    inputField = numField;
                } else {
                    JTextField textField = new JTextField();
                    textField.setPreferredSize(new Dimension(250, 25));
                    if (initialData != null) {
                        Object value = initialData.get(columnName);
                        textField.setText(value != null ? value.toString() : "");
                    }
                    inputField = textField;
                }
                add(inputField, gbc);
                fieldMap.put(columnName, inputField);
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
        try {
            Map<String, Integer> columnTypes = genericDAO.getColumnTypes(tableName);
            for (Map.Entry<String, JComponent> entry : fieldMap.entrySet()) {
                String col = entry.getKey();
                JComponent comp = entry.getValue();
                Object value;
                int sqlType = columnTypes.getOrDefault(col, Types.VARCHAR);
                if (comp instanceof JComboBox) {
                    String display = (String) ((JComboBox<?>) comp).getSelectedItem();
                    // Map display string back to ID
                    value = fkDisplayToIdMap.containsKey(col) ? fkDisplayToIdMap.get(col).get(display) : display;
                } else if (comp instanceof JTextField) {
                    String text = ((JTextField) comp).getText();
                    if (sqlType == Types.DATE && !text.isEmpty()) {
                        // Validate date
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        sdf.setLenient(false);
                        try { sdf.parse(text); } catch (ParseException ex) {
                            JOptionPane.showMessageDialog(this, "Please enter date in yyyy-MM-dd format for '" + col + "'", "Input Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        value = text;
                    } else if (sqlType == Types.TIME && !text.isEmpty()) {
                        if (!text.matches("\\d{2}:\\d{2}:\\d{2}")) {
                            JOptionPane.showMessageDialog(this, "Please enter time in HH:mm:ss format for '" + col + "'", "Input Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        value = text;
                    } else if ((sqlType == Types.INTEGER || sqlType == Types.BIGINT || sqlType == Types.SMALLINT || sqlType == Types.TINYINT) && !text.isEmpty()) {
                        try { Integer.parseInt(text); } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Please enter a valid whole number for '" + col + "'", "Input Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        value = text;
                    } else if ((sqlType == Types.DECIMAL || sqlType == Types.NUMERIC || sqlType == Types.FLOAT || sqlType == Types.DOUBLE || sqlType == Types.REAL) && !text.isEmpty()) {
                        try { Double.parseDouble(text); } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Please enter a valid number for '" + col + "'", "Input Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        value = text;
                    } else {
                        value = text;
                    }
                } else {
                    value = null;
                }
                values.put(col, value);
            }

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