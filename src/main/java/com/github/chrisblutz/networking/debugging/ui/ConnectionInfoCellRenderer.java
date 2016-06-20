package com.github.chrisblutz.networking.debugging.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;


/**
 * The {@code TableCellRenderer} used by the {@code JTable}
 * that holds the connection info in the debugging UI
 *
 * @author Christopher Lutz
 */
public class ConnectionInfoCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        setBorder(noFocusBorder);

        return this;
    }
}