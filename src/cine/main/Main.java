package cine.main;

import cine.gui.MenuPrincipal;
import cine.util.UI;
import javax.swing.SwingUtilities;

/** Punto de entrada de la aplicación. */
public class Main {
    public static void main(String[] args) {
        UI.initLookAndFeel();
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}
