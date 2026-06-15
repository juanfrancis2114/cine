package cine.gui;

import cine.dp.Funcion;
import cine.dp.Pelicula;
import cine.dp.Sala;
import cine.util.UI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** F4 — Gestión de Cartelera (Funciones). */
public class VentanaCartelera extends JFrame {
    private final JComboBox<String> cboPelicula = new JComboBox<>();
    private final JComboBox<String> cboSala = new JComboBox<>();
    private final JTextField txtFecha = new JTextField("2026-06-20", 12);
    private final JTextField txtHorario = new JTextField("19:00", 8);
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID","Película","Sala","Fecha","Horario","Estado"}, 0) {
        public boolean isCellEditable(int r, int c){ return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JComboBox<String> cboCriterio = new JComboBox<>(new String[]{"Película","Sala","Fecha","Horario"});
    private final JTextField txtBuscar = new JTextField(12);

    private int editId = 0;
    private List<Funcion> lista;
    private List<Pelicula> peliculas;
    private List<Sala> salas;
    private final Funcion dp = new Funcion();

    public VentanaCartelera() {
        setTitle("Gestión de Cartelera");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(920, 640));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UI.BG);
        setLayout(new BorderLayout());
        add(UI.header("Gestión de Cartelera", "F4 · Programación de funciones"), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setBackground(UI.BG);
        center.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        center.add(formCard(), BorderLayout.NORTH);
        center.add(tablePanel(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
        cargarCombos();
        mostrarListado();
    }

    private JPanel formCard() {
        JPanel card = UI.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6); g.anchor = GridBagConstraints.WEST;
        g.gridy=0;
        g.gridx=0; card.add(UI.field("Película"), g); g.gridx=1; cboPelicula.setPreferredSize(new Dimension(220,26)); card.add(cboPelicula, g);
        g.gridx=2; card.add(UI.field("Sala"), g); g.gridx=3; cboSala.setPreferredSize(new Dimension(180,26)); card.add(cboSala, g);
        g.gridy=1;
        g.gridx=0; card.add(UI.field("Fecha (AAAA-MM-DD)"), g); g.gridx=1; card.add(txtFecha, g);
        g.gridx=2; card.add(UI.field("Horario (HH:MM)"), g); g.gridx=3; card.add(txtHorario, g);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton btnNuevo = UI.ghost("Nuevo");
        JButton btnGuardar = UI.success("Guardar");
        JButton btnCancelar = UI.danger("Cancelar Función");
        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> cancelar());
        botones.add(btnNuevo); botones.add(btnGuardar); botones.add(btnCancelar);
        g.gridy=2; g.gridx=2; g.gridwidth=2; g.anchor=GridBagConstraints.EAST; card.add(botones, g);
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
        btnBuscar.addActionListener(e -> buscar());
        btnTodos.addActionListener(e -> mostrarListado());
        tool.add(btnBuscar); tool.add(btnTodos);
        p.add(tool, BorderLayout.NORTH);
        UI.styleTable(tabla);
        p.add(UI.scroll(tabla), BorderLayout.CENTER);
        return p;
    }
    private void cargarCombos() {
        peliculas = new Pelicula().consultarTodas();
        salas = new Sala().consultarTodas();
        cboPelicula.removeAllItems();
        for (Pelicula p : peliculas) cboPelicula.addItem(p.getTitulo());
        cboSala.removeAllItems();
        for (Sala s : salas) cboSala.addItem("Sala " + s.getNumeroSala() + " (" + s.getTipoSala() + ")");
    }
    private void mostrarListado() { lista = dp.consultarTodas(); pintar(); }
    private void buscar() {
        lista = dp.buscarFunciones((String) cboCriterio.getSelectedItem(), txtBuscar.getText().trim());
        pintar();
        if (lista.isEmpty()) mostrarMensaje("No se encontraron resultados.");
    }
    private void pintar() {
        modelo.setRowCount(0);
        for (Funcion f : lista)
            modelo.addRow(new Object[]{f.getIdFuncion(), f.getTituloPelicula(), "Sala " + f.getNumeroSala(),
                    f.getFecha(), f.getHorario(), f.isEstado() ? "Programada" : "Cancelada"});
    }
    private void cargarSeleccion() {
        int r = tabla.getSelectedRow();
        if (r < 0 || lista == null || r >= lista.size()) return;
        Funcion f = lista.get(r);
        editId = f.getIdFuncion();
        selectByIdPelicula(f.getIdPelicula());
        selectByIdSala(f.getIdSala());
        txtFecha.setText(f.getFecha());
        txtHorario.setText(f.getHorario());
    }
    private void selectByIdPelicula(int id){ for (int i=0;i<peliculas.size();i++) if (peliculas.get(i).getIdPelicula()==id){ cboPelicula.setSelectedIndex(i); return; } }
    private void selectByIdSala(int id){ for (int i=0;i<salas.size();i++) if (salas.get(i).getIdSala()==id){ cboSala.setSelectedIndex(i); return; } }

    private void guardar() {
        if (peliculas.isEmpty() || salas.isEmpty()) { mostrarMensaje("Debe registrar al menos una película y una sala."); return; }
        Funcion f = new Funcion();
        f.setIdFuncion(editId);
        f.setIdPelicula(peliculas.get(Math.max(0, cboPelicula.getSelectedIndex())).getIdPelicula());
        f.setIdSala(salas.get(Math.max(0, cboSala.getSelectedIndex())).getIdSala());
        f.setFecha(txtFecha.getText().trim());
        f.setHorario(txtHorario.getText().trim());
        boolean ok = (editId == 0) ? f.insertar() : f.modificar();
        if (ok) { mostrarMensaje(editId == 0 ? "Función programada." : "Función actualizada."); limpiar(); mostrarListado(); }
        else mostrarMensaje(f.getMensajeError());
    }
    private void cancelar() {
        int r = tabla.getSelectedRow();
        if (r < 0) { mostrarMensaje("Seleccione una función de la lista."); return; }
        Funcion f = lista.get(r);
        int op = JOptionPane.showConfirmDialog(this, "¿Cancelar la función de \"" + f.getTituloPelicula() + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;
        if (f.cancelar()) { mostrarMensaje("Función cancelada."); limpiar(); mostrarListado(); }
        else mostrarMensaje(f.getMensajeError());
    }
    private void limpiar() {
        editId = 0;
        if (cboPelicula.getItemCount()>0) cboPelicula.setSelectedIndex(0);
        if (cboSala.getItemCount()>0) cboSala.setSelectedIndex(0);
        txtFecha.setText("2026-06-20"); txtHorario.setText("19:00"); tabla.clearSelection();
    }
    private void mostrarMensaje(String t) { JOptionPane.showMessageDialog(this, t); }
}
