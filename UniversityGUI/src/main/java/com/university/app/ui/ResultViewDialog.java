package com.university.app.ui;

import javax.swing.*;
import java.awt.*;

public class ResultViewDialog extends JDialog {
    public ResultViewDialog(Frame owner) {
        super(owner, "Results View", true);
        setLayout(new BorderLayout());
        add(new ResultViewPanel(), BorderLayout.CENTER);
        setSize(600, 400);
        setLocationRelativeTo(owner);
    }
} 