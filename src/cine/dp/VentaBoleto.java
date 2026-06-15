package cine.dp;

import cine.md.VentaBoletoMD;
import java.util.List;

/** Capa DP: entidad de dominio Venta de Boleto + reglas de negocio. */
public class VentaBoleto {
    /** Operador por defecto del Ciclo 1 (no hay autenticación; ver instrucciones §10). */
    public static final int OPERADOR_DEFECTO = 1;

    private int idVenta;
    private int idFuncion;
    private int idEmpleado = OPERADOR_DEFECTO;
    private String asientos;
    private String fechaVenta;
    private boolean estado = true;
    private String descripcionFuncion = ""; // apoyo visual (JOIN)
    private String mensajeError = "";
    private final VentaBoletoMD md = new VentaBoletoMD();

    public VentaBoleto() {}
    public VentaBoleto(int idVenta, int idFuncion, int idEmpleado, String asientos, String fechaVenta, boolean estado) {
        this.idVenta = idVenta; this.idFuncion = idFuncion; this.idEmpleado = idEmpleado;
        this.asientos = asientos; this.fechaVenta = fechaVenta; this.estado = estado;
    }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int v) { this.idVenta = v; }
    public int getIdFuncion() { return idFuncion; }
    public void setIdFuncion(int v) { this.idFuncion = v; }
    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int v) { this.idEmpleado = v; }
    public String getAsientos() { return asientos; }
    public void setAsientos(String v) { this.asientos = v; }
    public String getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(String v) { this.fechaVenta = v; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean v) { this.estado = v; }
    public String getDescripcionFuncion() { return descripcionFuncion; }
    public void setDescripcionFuncion(String v) { this.descripcionFuncion = v; }
    public String getMensajeError() { return mensajeError; }

    public boolean validarDatos() {
        if (idFuncion <= 0) { mensajeError = "Debe seleccionar una función."; return false; }
        if (asientos == null || asientos.trim().isEmpty()) { mensajeError = "Debe seleccionar al menos un asiento."; return false; }
        return true;
    }
    /** Flujo: Guardar -> validar -> verificar disponibilidad de asientos -> insertar. */
    public boolean registrarVenta() {
        if (!validarDatos()) return false;
        if (!md.verificarAsientos(idFuncion, asientos)) {
            mensajeError = "Alguno de los asientos ya no está disponible."; return false;
        }
        if (!md.insertar(this)) { mensajeError = "Error al registrar la venta."; return false; }
        return true;
    }
    public boolean anular() {
        if (!md.verificarEstado(idVenta)) { mensajeError = "La venta ya fue anulada."; return false; }
        if (!md.actualizarEstado(idVenta, false)) { mensajeError = "Error al anular la venta."; return false; }
        return true;
    }
    public List<VentaBoleto> consultarTodas() { return md.cargarVentas(); }
    public List<VentaBoleto> buscarVentas(String criterio, String valor) { return md.buscarVentas(criterio, valor); }
    public List<String> asientosOcupados(int idFuncion) { return md.asientosOcupados(idFuncion); }
}
