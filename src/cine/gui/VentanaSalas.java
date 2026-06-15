package cine.gui;

import cine.dp.Sala;
import cine.util.UI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** F2 — Gestión de Salas. */
public class VentanaSalas extends JFrame {
    private final JSpinner spnNumero = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JSpinner spnCapacidad = new JSpinner(new SpinnerNumberModel(60, 1, 1000, 10));
    private final JComboBox<String> cboTipo = new JComboBox<>(new String[]{"2D","3D","IMAX","VIP","4DX"});
    private final JComboBox<String> cboEstado = new JComboBox<>(new String[]{"Activa","Inactiva"});
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID","N° Sala","Capacidad","Tipo","Estado"}, 0) {
        public boolean isCellEditable(int r, int c){ return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JComboBox<String> cboCriterio = new JComboBox<>(new String[]{"Número","Tipo"});
    private final JTextField txtBuscar = new JTextField(14);
    private int editId = 0;
    private List<Sala> lista;
    private final Sala dp = new Sala();

    public VentanaSalas() {
        cboTipo.setEditable(true);
        setTitle("Gestión de Salas");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(860, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UI.BG);
        setLayout(new BorderLayout());
        add(UI.header("Gestión de Salas", "F2 · Salas del complejo"), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setBackground(UI.BG);
        center.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        center.add(formCard(), BorderLayout.NORTH);
        center.add(tablePanel(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
        mostrarListado();
    }

    private JPanel formCard() {
        JPanel card = UI.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6); g.anchor = GridBagConstraints.WEST;
        g.gridy=0;
        g.gridx=0; card.add(UI.field("N° de sala"), g); g.gridx=1; card.add(spnNumero, g);
        g.gridx=2; card.add(UI.field("Capacidad"), g); g.gridx=3; card.add(spnCapacidad, g);
        g.gridy=1;
        g.gridx=0; card.add(UI.field("Tipo de sala"), g); g.gridx=1; card.add(cboTipo, g);
        g.gridx=2; card.add(UI.field("Estado"), g); g.gridx=3; card.add(cboEstado, g);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton btnNuevo = UI.ghost("Nuevo");
        JButton btnGuardar = UI.success("Guardar");
        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        botones.add(btnNuevo); botones.add(btnGuardar);
        g.gridy=2; g.gridx=3; g.anchor=GridBagConstraints.EAST; card.add(botones, g);
        return card;
    }
    private JPanel tablePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(UI.BG);
        JPanel tool = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tool.setOpaque(false);
        tool.add(UI.field("Buscar por")); tool.add(cboCriterio); tool.add(txtBuscar);
        JButton btnBuscar = UI.primary("Buscar");
        JButton btnTodos = UI.ghost("Consulta General");
        JButton btnEliminar = UI.danger("Eliminar");
        btnBuscar.addActionListener(e -> buscar());
        btnTodos.addActionListener(e -> mostrarListado());
        btnEliminar.addActionListener(e -> eliminar());
        tool.add(btnBuscar); tool.add(btnTodos); tool.add(btnEliminar);
        p.add(tool, BorderLayout.NORTH);
        UI.styleTable(tabla);
        p.add(UI.scroll(tabla), BorderLayout.CENTER);
        return p;
    }
    private void mostrarListado() { lista = dp.consultarTodas(); pintar(); }
    private void buscar() {
        lista = dp.buscarSalas((String) cboCriterio.getSelectedItem(), txtBuscar.getText().trim());
        pintar();
        if (lista.isEmpty()) mostrarMensaje("No se encontraron resultados.");
    }
    private void pintar() {
        modelo.setRowCount(0);
        for (Sala s : lista)
            modelo.addRow(new Object[]{s.getIdSala(), s.getNumeroSala(), s.getCapacidad(),
                    s.getTipoSala(), s.isEstado() ? "Activa" : "Inactiva"});
    }
    private void cargarSeleccion() {
        int r = tabla.getSelectedRow();
        if (r < 0 || lista == null || r >= lista.size()) return;
        Sala s = lista.get(r);
        editId = s.getIdSala();
        spnNumero.setValue(s.getNumeroSala());
        spnCapacidad.setValue(s.getCapacidad());
        cboTipo.setSelectedItem(s.getTipoSala());
        cboEstado.setSelectedIndex(s.isEstado() ? 0 : 1);
    }
    private void guardar() {
        Sala s = new Sala();
        s.setIdSala(editId);
        s.setNumeroSala((Integer) spnNumero.getValue());
        s.setCapacidad((Integer) spnCapacidad.getValue());
        s.setTipoSala((String) cboTipo.getEditor().getItem());
        s.setEstado(cboEstado.getSelectedIndex() == 0);
        boolean ok = (editId == 0) ? s.insertar() : s.modificar();
        if (ok) { mostrarMensaje(editId == 0 ? "Sala registrada." : "Sala actualizada."); limpiar(); mostrarListado(); }
        else mostrarMensaje(s.getMensajeError());
    }
    private void eliminar() {
        int r = tabla.getSelectedRow();
        if (r < 0) { mostrarMensaje("Seleccione una sala de la lista."); return; }
        Sala s = lista.get(r);
        int op = JOptionPane.showConfirmDialog(this, "¿Eliminar la sala N° " + s.getNumeroSala() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;
        if (s.borrar()) { mostrarMensaje("Sala eliminada."); limpiar(); mostrarListado(); }
        else mostrarMensaje(s.getMensajeError());
    }
    private void limpiar() {
        editId = 0; spnNumero.setValue(1); spnCapacidad.setValue(60);
        cboTipo.setSelectedIndex(0); cboEstado.setSelectedIndex(0); tabla.clearSelection();
    }
    private void mostrarMensaje(String t) { JOptionPane.showMessageDialog(this, t); }
}
