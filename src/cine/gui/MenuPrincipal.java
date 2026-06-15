package cine.gui;

import cine.util.UI;
import javax.swing.*;
import java.awt.*;

/** Menú principal (dashboard). Muestra los 6 módulos; F3 y F6 quedan deshabilitados (Ciclo 2). */
public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("Sistema Integral de Complejo de Cine — Ciclo 1");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UI.BG);
        setLayout(new BorderLayout());

        add(UI.header("Sistema Integral de Complejo de Cine",
                "Panel de gestión · Ciclo 1 · Equipo 3"), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 3, 18, 18));
        grid.setBackground(UI.BG);
        grid.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        grid.add(card("Películas", "Gestión del catálogo de películas", true,
                () -> new VentanaPeliculas().setVisible(true)));
        grid.add(card("Salas", "Gestión de salas del complejo", true,
                () -> new VentanaSalas().setVisible(true)));
        grid.add(card("Cartelera", "Programación de funciones", true,
                () -> new VentanaCartelera().setVisible(true)));
        grid.add(card("Venta de Boletos", "Registro y anulación de ventas", true,
                () -> new VentanaVentaBoletos().setVisible(true)));
        grid.add(card("Empleados", "Disponible en el Ciclo 2", false, null));
        grid.add(card("Reservas", "Disponible en el Ciclo 2", false, null));

        add(grid, BorderLayout.CENTER);

        JLabel footer = new JLabel("F3 Empleados y F6 Reservas se habilitan en el Ciclo 2.", SwingConstants.CENTER);
        footer.setFont(UI.SMALL); footer.setForeground(UI.MUTED);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        add(footer, BorderLayout.SOUTH);
    }

    private JButton card(String titulo, String desc, boolean habilitado, final Runnable accion) {
        Color bg = habilitado ? UI.CARD : new Color(0xEDEFF3);
        JButton b = UI.flatButton("", bg, UI.TEXT);
        b.setLayout(new BorderLayout());
        b.setEnabled(habilitado);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UI.BORDER, 1, true),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));

        JPanel txt = new JPanel(new GridLayout(2, 1, 0, 6));
        txt.setOpaque(false);
        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 18));
        t.setForeground(habilitado ? UI.PRIMARY : UI.MUTED);
        JLabel d = new JLabel(desc);
        d.setFont(UI.SMALL); d.setForeground(UI.MUTED);
        txt.add(t); txt.add(d);

        // Franja de color de acento a la izquierda
        JPanel stripe = new JPanel();
        stripe.setPreferredSize(new Dimension(6, 0));
        stripe.setBackground(habilitado ? UI.ACCENT : new Color(0xC2C8D0));

        b.add(stripe, BorderLayout.WEST);
        b.add(txt, BorderLayout.CENTER);

        if (habilitado && accion != null) {
            b.addActionListener(e -> accion.run());
        } else {
            b.setToolTipText("Disponible en el Ciclo 2");
        }
        return b;
    }
}
