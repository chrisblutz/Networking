package com.github.chrisblutz.debugging;

import com.github.chrisblutz.Listenable;
import com.github.chrisblutz.debugging.ui.DebugUI;
import com.github.chrisblutz.debugging.ui.SideInfoPanel;

import java.util.HashMap;
import java.util.Map;


/**
 * The main debugging class, containing methods for
 * adding/updating/removing {@code Listenables}
 * to the list of those being tracked
 *
 * @author Christopher Lutz
 */
public class Debugger {

    private static DebugUI ui;
    private static Map<Listenable, SideInfoPanel> uiMap = new HashMap<Listenable, SideInfoPanel>();

    /**
     * Checks if debugging is enabled.  Debugging is <i>not</i> enabled by
     * default.  To enable debugging, set the system property {@code 'networkingdebug'}
     * to {@code true}.
     *
     * @return Whether or not debugging is enabled
     */
    public static boolean isEnabled() {

        try {

            return Boolean.parseBoolean(System.getProperty("networkingdebug"));

        } catch (Exception e) {

            return false;
        }
    }

    /**
     * Checks if the debug window is visible.  This method checks if the UI object is {@code null}
     * and then calls its {@code isVisible()} method.
     *
     * @return Whether or not the debug window is visible
     */
    public static boolean isDebugWindowVisible() {

        return ui != null && ui.isVisible();
    }

    /**
     * Registers a {@code Listenable} to be tracked.  If the debug UI does not
     * already exist, this method will create it.
     *
     * @param listenable The {@code Listenable to register}
     */
    public static void registerListenable(Listenable listenable) {

        if (ui == null) {

            ui = new DebugUI();
        }

        uiMap.put(listenable, ui.addListenable(listenable));

        if (!ui.isVisible()) {

            ui.setVisible(true);
        }
    }

    /**
     * Sends a call to update the {@code SideInfoPanel} for a specific {@code Listenable}
     *
     * @param listenable The {@code Listenable} to update
     */
    public static void updateListenable(Listenable listenable) {

        if (uiMap.containsKey(listenable)) {

            uiMap.get(listenable).revalidate();
            uiMap.get(listenable).repaint();
        }
    }

    /**
     * Removes a {@code Listenable} from the list of the {@code Listenables} being tracked
     *
     * @param listenable The {@code Listenable} to remove
     */
    public static void removeListenable(Listenable listenable) {

        if (uiMap.containsKey(listenable)) {

            SideInfoPanel infoPanel = uiMap.get(listenable);
            ui.remove(infoPanel);
            uiMap.remove(listenable);
        }
    }
}
