package com.github.chrisblutz.networking.debugging.ui;

import com.github.chrisblutz.networking.Client;
import com.github.chrisblutz.networking.Listenable;
import com.github.chrisblutz.networking.Server;
import com.github.chrisblutz.networking.debugging.Debugger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;


/**
 * A user-interface class used to debug connections.  This UI automatically appears
 * when the system property {@code 'networkingdebug'} is set to {@code true} and
 * a {@code Server} or {@code Client} is created and started.
 *
 * @author Christopher Lutz
 */
public class DebugUI extends JFrame {

    private JTabbedPane tabbedPane;

    private Timer updateTimer;

    /**
     * Creates a new instance of the debugging UI.<br>
     * This is called by the {@code Debugger} class when the first
     * {@code Server} or {@code Client} is created and the {@code 'networkingdebug'} property is set to {@code true}.
     */
    public DebugUI() {

        super("Network Debugger");

        try {

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (Exception e) {
        }

        try {

            InputStream inputStream = Debugger.class.getResourceAsStream("/resources/images/icon.png");

            if (inputStream != null) {

                Image icon = ImageIO.read(inputStream);
                setIconImage(icon);
            }

        } catch (Exception e) {
        }

        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        add(tabbedPane);

        updateTimer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                revalidate();
                repaint();
            }
        });

        setSize(500, 500);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    @Override
    public void setVisible(boolean visible) {

        if (visible) {

            updateTimer.start();

        } else {

            updateTimer.stop();
        }

        super.setVisible(visible);
    }

    /**
     * Adds a {@code Listenable} object to be watched by this debugging UI
     *
     * @param listenable The {@code Listenable} to watch
     * @return The {@code SideInfoPanel} created for the new {@code Listenable}
     */
    public SideInfoPanel addListenable(Listenable listenable) {

        SideInfoPanel sideInfoPanel = new SideInfoPanel(listenable);
        sideInfoPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        if (listenable instanceof Server) {

            tabbedPane.add("Server: " + ((Server) listenable).getServerName(), sideInfoPanel);

        } else if (listenable instanceof Client) {

            tabbedPane.add("Client: " + ((Client) listenable).getClientName(), sideInfoPanel);
        }

        return sideInfoPanel;
    }

    /**
     * Removes a {@code Listenable}'s {@code SideInfoPanel} from the debugging UI
     *
     * @param infoPanel The {@code SideInfoPanel} to remove
     */
    public void removeListenable(SideInfoPanel infoPanel) {

        tabbedPane.remove(infoPanel);
    }
}
