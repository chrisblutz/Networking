package com.github.lutzblox.debugging.ui;

import com.github.lutzblox.Client;
import com.github.lutzblox.Listenable;
import com.github.lutzblox.Server;
import com.github.lutzblox.sockets.Connection;
import com.github.lutzblox.states.State;

import javax.swing.table.AbstractTableModel;


/**
 * The {@code TreeModel} used by the {@code JTable}
 * that holds the connection info in the debugging UI
 *
 * @author Christopher Lutz
 */
public class ConnectionInfoModel extends AbstractTableModel {

    private Listenable listenable;

    /**
     * Creates a new {@code ConnectionInfoModel} for a specific {@code Listenable}
     *
     * @param listenable The {@code Listenable} to use
     */
    public ConnectionInfoModel(Listenable listenable) {

        this.listenable = listenable;
    }

    @Override
    public int getColumnCount() {

        return 4;
    }

    @Override
    public int getRowCount() {

        return getConnections().length;
    }

    @Override
    public boolean isCellEditable(int row, int col) {

        return false;
    }

    @Override
    public Object getValueAt(int row, int col) {

        switch (col) {

            case 0:

                return getConnections()[row].getIp();

            case 1:

                return getConnections()[row].getAveragePing() + " ms";

            case 2:

                Connection c = getConnections()[row];

                return (c.getCurrentState() == State.RECEIVING || c.getCurrentState() == State.SENDING) && (!c.isClosed() && !c.isRemoteClosed() && c.isConnected()) ? "<html><strong><p style='font-size: 8px; color:#009900;'>\u2713</p></strong></html>" : "<html><strong><p style='font-size: 8px; color:#E60000;'>\u2715</p></strong></html>";

            case 3:

                return getConnections()[row].getEncrypted() ? "<html><strong><p style='font-size: 8px; color:#009900;'>\u2713</p></strong></html>" : "<html><strong><p style='font-size: 8px; color:#E60000;'>\u2715</p></strong></html>";

            default:

                return "null";
        }
    }

    @Override
    public String getColumnName(int col) {

        switch (col) {

            case 0:

                return "IP";

            case 1:

                return "Avg. Ping";

            case 2:

                return "Active";

            case 3:

                return "Encrypted";

            default:

                return "NULL";
        }
    }

    /**
     * Gets the {@code Connection} array representing all current
     * {@code Connections} into this model's {@code Listenable}
     *
     * @return The connections for this model's {@code Listenable}
     */
    private Connection[] getConnections() {

        if (listenable instanceof Server) {

            return ((Server) listenable).getConnections().getConnectionsAsArray();

        } else if (listenable instanceof Client) {

            return new Connection[]{((Client) listenable).getConnection()};

        } else {

            return new Connection[0];
        }
    }
}
