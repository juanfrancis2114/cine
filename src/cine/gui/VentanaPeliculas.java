package cine.gui;

import cine.dp.Pelicula;
import cine.util.UI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** F1 — Gestión de Películas. */
public class VentanaPeliculas extends JFrame {
    private final JTextField txtTitulo = new JTextField(18);
    private final JComboBox<String> cboGenero = new JComboBox<>(new String[]{"Acción","Animación","Ciencia Ficción","Comedia","Drama","Terror","Romance"});
    private final JSpinner spnDuracion = new JSpinner(new SpinnerNumberModel(90, 1, 600, 1));
    private final JComboBox<String> cboClasif = new JComboBox<>(new String[]{"A","PG","PG-13","R","B15"});
    private final JComboBox<String> cboEstado = new JComboBox<>(new String[]{"Activa","Inactiva"});
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID","Título","Género","Duración","Clasificación","Estado"}, 0) {
        public boolean isCellEditable(int r, int c){ return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JComboBox<String> cboCriterio = new JComboBox<>(new String[]{"Título","Género","Clasificación"});
    private final JTextField txtBuscar = new JTextField(14);
    private int editId = 0;
    private List<Pelicula> lista;
    private final Pelicula dp = new Pelicula();

    public VentanaPeliculas() {
        cboGenero.setEditable(true); cboClasif.setEditable(true);
        setTitle("Gestión de Películas");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 640));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UI.BG);
        setLayout(new BorderLayout());
        add(UI.header("Gestión de Películas", "F1 · Catálogo de películas del complejo"), BorderLayout.NORTH);

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
        int y = 0;
        addRow(card, g, y++, "Título", txtTitulo, "Género", cboGenero);
        addRow(card, g, y++, "Duración (min)", spnDuracion, "Clasificación", cboClasif);
        g.gridx=0; g.gridy=y; card.add(UI.field("Estado"), g);
        g.gridx=1; card.add(cboEstado, g);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton btnNuevo = UI.ghost("Nuevo");
        JButton btnGuardar = UI.success("Guardar");
        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        botones.add(btnNuevo); botones.add(btnGuardar);
        g.gridx=3; g.gridy=y; g.anchor=GridBagConstraints.EAST; card.add(botones, g);
        return card;
    }

    private void addRow(JPanel p, GridBagConstraints g, int y, String l1, JComponent c1, String l2, JComponent c2) {
        g.gridy=y; g.anchor=GridBagConstraints.WEST;
        g.gridx=0; p.add(UI.field(l1), g);
        g.gridx=1; p.add(c1, g);
        g.gridx=2; p.add(UI.field(l2), g);
        g.gridx=3; p.add(c2, g);
    }

    private JPanel tablePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(UI.BG);
        JPanel tool = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tool.setOpaque(false);
        tool.add(UI.field("Buscar por"));
        tool.add(cboCriterio); tool.add(txtBuscar);
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

    private void mostrarListado() {
        lista = dp.consultarTodas();
        pintar();
    }
    private void buscar() {
        lista = dp.buscarPeliculas((String) cboCriterio.getSelectedItem(), txtBuscar.getText().trim());
        pintar();
        if (lista.isEmpty()) mostrarMensaje("No se encontraron resultados.");
    }
    private void pintar() {
        modelo.setRowCount(0);
        for (Pelicula p : lista)
            modelo.addRow(new Object[]{p.getIdPelicula(), p.getTitulo(), p.getGenero(),
                    p.getDuracion(), p.getClasificacion(), p.isEstado() ? "Activa" : "Inactiva"});
    }
    private void cargarSeleccion() {
        int r = tabla.getSelectedRow();
        if (r < 0 || lista == null || r >= lista.size()) return;
        Pelicula p = lista.get(r);
        editId = p.getIdPelicula();
        txtTitulo.setText(p.getTitulo());
        cboGenero.setSelectedItem(p.getGenero());
        spnDuracion.setValue(p.getDuracion());
        cboClasif.setSelectedItem(p.getClasificacion());
        cboEstado.setSelectedIndex(p.isEstado() ? 0 : 1);
    }
    private void guardar() {
        Pelicula p = new Pelicula();
        p.setIdPelicula(editId);
        p.setTitulo(txtTitulo.getText().trim());
        p.setGenero((String) cboGenero.getEditor().getItem());
        p.setDuracion((Integer) spnDuracion.getValue());
        p.setClasificacion((String) cboClasif.getEditor().getItem());
        p.setEstado(cboEstado.getSelectedIndex() == 0);
        boolean ok = (editId == 0) ? p.insertar() : p.modificar();
        if (ok) { mostrarMensaje(editId == 0 ? "Película registrada." : "Película actualizada."); limpiar(); mostrarListado(); }
        else mostrarMensaje(p.getMensajeError());
    }
    private void eliminar() {
        int r = tabla.getSelectedRow();
        if (r < 0) { mostrarMensaje("Seleccione una película de la lista."); return; }
        Pelicula p = lista.get(r);
        int op = JOptionPane.showConfirmDialog(this, "¿Eliminar la película \"" + p.getTitulo() + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;
        if (p.borrar()) { mostrarMensaje("Película eliminada."); limpiar(); mostrarListado(); }
        else mostrarMensaje(p.getMensajeError());
    }
    private void limpiar() {
        editId = 0; txtTitulo.setText(""); cboGenero.setSelectedIndex(0);
        spnDuracion.setValue(90); cboClasif.setSelectedIndex(0); cboEstado.setSelectedIndex(0);
        tabla.clearSelection();
    }
    private void mostrarMensaje(String texto) { JOptionPane.showMessageDialog(this, texto); }
}
