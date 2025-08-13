package com.university.app.ui;

import javax.swing.*;
import java.awt.*;

public class MarksEntryDialog extends JDialog {
    public MarksEntryDialog(Frame owner) {
        super(owner, "Marks Entry", true);
        setLayout(new BorderLayout());
        add(new MarksEntryPanel(), BorderLayout.CENTER);
        setSize(600, 400);
        setLocationRelativeTo(owner);
    }
} 