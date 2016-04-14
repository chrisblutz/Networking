package com.github.chrisblutz.debugging.ui;

import com.github.chrisblutz.Client;
import com.github.chrisblutz.Listenable;
import com.github.chrisblutz.Server;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * The {@code JPanel} that holds information about a specific {@code Listenable}, such as
 * {@code Connection} statuses, current activity, encryption status, etc.
 *
 * @author Christopher Lutz
 */
public class SideInfoPanel extends JPanel {

    private Listenable listenable;
    private JPanel infoPanel, infoTextPanel;
    private JLabel sideLabel, nameLabel, statusLabel;
    private JButton closeSide;
    private JScrollPane tableScroll;
    private ConnectionInfoModel model;
    private JTable table;

    /**
     * Creates a new {@code SideInfoPanel} to track a specific {@code Listenable}
     *
     * @param listenable The {@code Listenable} to track
     */
    public SideInfoPanel(final Listenable listenable) {

        this.listenable = listenable;

        setLayout(new BorderLayout());

        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true), "Network Information", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, infoPanel.getFont().deriveFont(Font.BOLD)));

        infoTextPanel = new JPanel(new GridLayout(3, 1));

        sideLabel = new JLabel();
        sideLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 1, 10));
        nameLabel = new JLabel();
        nameLabel.setBorder(BorderFactory.createEmptyBorder(1, 10, 1, 10));
        statusLabel = new JLabel();
        statusLabel.setBorder(BorderFactory.createEmptyBorder(1, 10, 5, 10));

        updateLabels();

        closeSide = new JButton("Close");
        closeSide.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    if (listenable instanceof Server) {

                        Server server = (Server) listenable;

                        if (server.isOpen()) {

                            server.close();
                        }

                    } else if (listenable instanceof Client) {

                        Client client = (Client) listenable;

                        if (client.isOpen()) {

                            client.close();
                        }
                    }

                } catch (Exception ex) {

                }
            }
        });

        infoTextPanel.add(sideLabel);
        infoTextPanel.add(nameLabel);
        infoTextPanel.add(statusLabel);

        infoPanel.add(infoTextPanel, BorderLayout.CENTER);
        infoPanel.add(closeSide, BorderLayout.EAST);

        model = new ConnectionInfoModel(listenable);

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(table.getRowHeight() + 5);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(230, 230, 255));
        table.setSelectionForeground(table.getForeground());
        table.setDefaultRenderer(Object.class, new ConnectionInfoCellRenderer());

        tableScroll = new JScrollPane(table);

        add(infoPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
    }

    @Override
    public void repaint() {

        super.repaint();

        updateLabels();
    }

    /**
     * Updates the labels in the panel to the most current information available
     */
    public void updateLabels() {

        if (listenable instanceof Client) {

            Client client = (Client) listenable;

            sideLabel.setText("Side: Client");
            nameLabel.setText("Name: " + client.getClientName());
            statusLabel.setText("Status: " + (client.isOpen() ? "Open" : "Closed"));

        } else if (listenable instanceof Server) {

            Server server = (Server) listenable;

            sideLabel.setText("Side: Server");
            nameLabel.setText("Name: " + server.getServerName());
            statusLabel.setText("Status: " + (server.isOpen() ? "Open" : "Closed"));
        }
    }
}
