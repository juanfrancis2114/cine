package cine.gui;

import cine.dp.Funcion;
import cine.dp.VentaBoleto;
import cine.util.UI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/** F5 — Gestión de Venta de Boletos, con mapa de asientos interactivo. */
public class VentanaVentaBoletos extends JFrame {
    private static final int POR_FILA = 10;

    private final JComboBox<String> cboFuncion = new JComboBox<>();
    private final JLabel lblInfo = new JLabel("Seleccione una función para ver los asientos.");
    private final JLabel lblSeleccion = new JLabel("Asientos: (ninguno)");
    private final JPanel mapaPanel = new JPanel();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID","Función","Asientos","Fecha","Estado"}, 0) {
        public boolean isCellEditable(int r, int c){ return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JComboBox<String> cboCriterio = new JComboBox<>(new String[]{"Película","Fecha","Boleto"});
    private final JTextField txtBuscar = new JTextField(12);

    private List<Funcion> funciones;
    private List<VentaBoleto> ventas;
    private final LinkedHashSet<String> seleccion = new LinkedHashSet<>();
    private final Set<String> ocupados = new HashSet<>();
    private final VentaBoleto dp = new VentaBoleto();

    public VentanaVentaBoletos() {
        setTitle("Gestión de Venta de Boletos");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 720));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UI.BG);
        setLayout(new BorderLayout());
        add(UI.header("Venta de Boletos", "F5 · Selección de asientos y registro de ventas"), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setBackground(UI.BG);
        center.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));
        center.add(panelVenta(), BorderLayout.NORTH);
        center.add(panelVentas(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        cargarFunciones();
        cboFuncion.addActionListener(e -> onFuncionSeleccionada());
        tabla.getSelectionModel().addListSelectionListener(e -> {});
        if (cboFuncion.getItemCount() > 0) onFuncionSeleccionada();
        mostrarListado();
    }

    private JPanel panelVenta() {
        JPanel card = UI.card();
        card.setLayout(new BorderLayout(0, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);
        top.add(UI.field("Función"));
        cboFuncion.setPreferredSize(new Dimension(360, 28));
        top.add(cboFuncion);
        lblInfo.setFont(UI.SMALL); lblInfo.setForeground(UI.MUTED);
        top.add(lblInfo);
        card.add(top, BorderLayout.NORTH);

        mapaPanel.setLayout(new BoxLayout(mapaPanel, BoxLayout.Y_AXIS));
        mapaPanel.setBackground(Color.WHITE);
        JScrollPane sp = UI.scroll(mapaPanel);
        sp.setPreferredSize(new Dimension(0, 230));
        card.add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        lblSeleccion.setFont(UI.BODY_B); lblSeleccion.setForeground(UI.PRIMARY);
        bottom.add(lblSeleccion, BorderLayout.WEST);
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        leyenda.setOpaque(false);
        leyenda.add(chip("Disponible", Color.WHITE));
        leyenda.add(chip("Seleccionado", UI.ACCENT));
        leyenda.add(chip("Ocupado", UI.DANGER));
        JButton btnVender = UI.success("Registrar Venta");
        btnVender.addActionListener(e -> registrar());
        leyenda.add(btnVender);
        bottom.add(leyenda, BorderLayout.EAST);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private JComponent chip(String texto, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JPanel sw = new JPanel(); sw.setBackground(color);
        sw.setPreferredSize(new Dimension(16, 16));
        sw.setBorder(BorderFactory.createLineBorder(UI.BORDER));
        JLabel l = new JLabel(texto); l.setFont(UI.SMALL); l.setForeground(UI.MUTED);
        p.add(sw); p.add(l);
        return p;
    }

    private JPanel panelVentas() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(UI.BG);
        JPanel tool = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tool.setOpaque(false);
        tool.add(UI.field("Ventas — Buscar por")); tool.add(cboCriterio); tool.add(txtBuscar);
        JButton btnBuscar = UI.primary("Buscar");
        JButton btnTodos = UI.ghost("Consulta General");
        JButton btnAnular = UI.danger("Anular Venta");
        btnBuscar.addActionListener(e -> buscar());
        btnTodos.addActionListener(e -> mostrarListado());
        btnAnular.addActionListener(e -> anular());
        tool.add(btnBuscar); tool.add(btnTodos); tool.add(btnAnular);
        p.add(tool, BorderLayout.NORTH);
        UI.styleTable(tabla);
        JScrollPane sp = UI.scroll(tabla);
        sp.setPreferredSize(new Dimension(0, 220));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void cargarFunciones() {
        funciones = new Funcion().consultarVigentes();
        cboFuncion.removeAllItems();
        for (Funcion f : funciones)
            cboFuncion.addItem(f.getTituloPelicula() + " — Sala " + f.getNumeroSala()
                    + " · " + f.getFecha() + " " + f.getHorario());
    }

    private Funcion funcionActual() {
        int i = cboFuncion.getSelectedIndex();
        if (i < 0 || funciones == null || i >= funciones.size()) return null;
        return funciones.get(i);
    }

    private void onFuncionSeleccionada() {
        Funcion f = funcionActual();
        seleccion.clear();
        ocupados.clear();
        if (f == null) { mapaPanel.removeAll(); mapaPanel.revalidate(); mapaPanel.repaint(); return; }
        ocupados.addAll(dp.asientosOcupados(f.getIdFuncion()));
        int disp = f.getCapacidadSala() - ocupados.size();
        lblInfo.setText("Sala " + f.getNumeroSala() + " · Capacidad " + f.getCapacidadSala()
                + " · Disponibles " + disp);
        renderMapa(f.getCapacidadSala());
        actualizarSeleccion();
    }

    private void renderMapa(int capacidad) {
        mapaPanel.removeAll();
        // Pantalla
        JLabel pantalla = new JLabel("P A N T A L L A", SwingConstants.CENTER);
        pantalla.setOpaque(true); pantalla.setBackground(new Color(0xDCE3EF));
        pantalla.setForeground(UI.PRIMARY); pantalla.setFont(UI.SMALL);
        pantalla.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        pantalla.setBorder(BorderFactory.createEmptyBorder(3, 0, 6, 0));
        mapaPanel.add(pantalla);

        int filas = (int) Math.ceil(capacidad / (double) POR_FILA);
        int asignados = 0;
        for (int r = 0; r < filas; r++) {
            char letra = (char) ('A' + r);
            JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 3));
            fila.setOpaque(false);
            JLabel lbl = new JLabel(String.valueOf(letra));
            lbl.setFont(UI.BODY_B); lbl.setForeground(UI.MUTED);
            lbl.setPreferredSize(new Dimension(18, 28));
            fila.add(lbl);
            for (int c = 1; c <= POR_FILA && asignados < capacidad; c++, asignados++) {
                final String codigo = letra + String.valueOf(c);
                fila.add(seatButton(codigo));
            }
            mapaPanel.add(fila);
        }
        mapaPanel.revalidate(); mapaPanel.repaint();
    }

    private JButton seatButton(final String codigo) {
        final boolean ocupado = ocupados.contains(codigo);
        final JButton b = new JButton(codigo);
        b.setPreferredSize(new Dimension(42, 28));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        b.setFocusPainted(false);
        b.setMargin(new Insets(0,0,0,0));
        b.setCursor(new Cursor(ocupado ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
        if (ocupado) {
            b.setBackground(UI.DANGER); b.setForeground(Color.WHITE); b.setEnabled(false);
        } else {
            b.setBackground(Color.WHITE); b.setForeground(UI.TEXT);
            b.addActionListener(e -> {
                if (seleccion.contains(codigo)) { seleccion.remove(codigo); b.setBackground(Color.WHITE); b.setForeground(UI.TEXT); }
                else { seleccion.add(codigo); b.setBackground(UI.ACCENT); b.setForeground(Color.WHITE); }
                actualizarSeleccion();
            });
        }
        b.setOpaque(true);
        b.setBorder(BorderFactory.createLineBorder(UI.BORDER));
        return b;
    }

    private void actualizarSeleccion() {
        lblSeleccion.setText("Asientos: " + (seleccion.isEmpty() ? "(ninguno)" : String.join(", ", seleccion))
                + "  ·  Total: " + seleccion.size());
    }

    private void registrar() {
        Funcion f = funcionActual();
        if (f == null) { mostrarMensaje("Seleccione una función."); return; }
        if (seleccion.isEmpty()) { mostrarMensaje("Seleccione al menos un asiento."); return; }
        VentaBoleto v = new VentaBoleto();
        v.setIdFuncion(f.getIdFuncion());
        v.setIdEmpleado(VentaBoleto.OPERADOR_DEFECTO);
        v.setAsientos(String.join(",", seleccion));
        v.setFechaVenta(LocalDate.now().toString());
        if (v.registrarVenta()) {
            mostrarMensaje("Venta registrada: " + seleccion.size() + " boleto(s).");
            onFuncionSeleccionada();
            mostrarListado();
        } else {
            mostrarMensaje(v.getMensajeError());
            onFuncionSeleccionada();
        }
    }

    private void mostrarListado() { ventas = dp.consultarTodas(); pintar(); }
    private void buscar() {
        ventas = dp.buscarVentas((String) cboCriterio.getSelectedItem(), txtBuscar.getText().trim());
        pintar();
        if (ventas.isEmpty()) mostrarMensaje("No se encontraron resultados.");
    }
    private void pintar() {
        modelo.setRowCount(0);
        for (VentaBoleto v : ventas)
            modelo.addRow(new Object[]{v.getIdVenta(), v.getDescripcionFuncion(), v.getAsientos(),
                    v.getFechaVenta(), v.isEstado() ? "Vigente" : "Anulada"});
    }
    private void anular() {
        int r = tabla.getSelectedRow();
        if (r < 0) { mostrarMensaje("Seleccione una venta de la lista."); return; }
        VentaBoleto v = ventas.get(r);
        if (!v.isEstado()) { mostrarMensaje("La venta ya fue anulada."); return; }
        int op = JOptionPane.showConfirmDialog(this, "¿Anular la venta N° " + v.getIdVenta() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;
        if (v.anular()) { mostrarMensaje("Venta anulada. Los asientos quedan disponibles."); mostrarListado(); onFuncionSeleccionada(); }
        else mostrarMensaje(v.getMensajeError());
    }
    private void mostrarMensaje(String t) { JOptionPane.showMessageDialog(this, t); }
}
