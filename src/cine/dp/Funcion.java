package cine.dp;

import cine.md.FuncionMD;
import java.util.List;

/** Capa DP: entidad de dominio Función (cartelera) + reglas de negocio. */
public class Funcion {
    private int idFuncion;
    private int idPelicula;
    private int idSala;
    private String fecha;
    private String horario;
    private boolean estado = true;
    // Campos de apoyo para visualización (no persistidos): provienen de JOIN.
    private String tituloPelicula = "";
    private int numeroSala;
    private int capacidadSala;
    private String mensajeError = "";
    private final FuncionMD md = new FuncionMD();

    public Funcion() {}
    public Funcion(int idFuncion, int idPelicula, int idSala, String fecha, String horario, boolean estado) {
        this.idFuncion = idFuncion; this.idPelicula = idPelicula; this.idSala = idSala;
        this.fecha = fecha; this.horario = horario; this.estado = estado;
    }

    public int getIdFuncion() { return idFuncion; }
    public void setIdFuncion(int v) { this.idFuncion = v; }
    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int v) { this.idPelicula = v; }
    public int getIdSala() { return idSala; }
    public void setIdSala(int v) { this.idSala = v; }
    public String getFecha() { return fecha; }
    public void setFecha(String v) { this.fecha = v; }
    public String getHorario() { return horario; }
    public void setHorario(String v) { this.horario = v; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean v) { this.estado = v; }
    public String getTituloPelicula() { return tituloPelicula; }
    public void setTituloPelicula(String v) { this.tituloPelicula = v; }
    public int getNumeroSala() { return numeroSala; }
    public void setNumeroSala(int v) { this.numeroSala = v; }
    public int getCapacidadSala() { return capacidadSala; }
    public void setCapacidadSala(int v) { this.capacidadSala = v; }
    public String getMensajeError() { return mensajeError; }

    public boolean validarDatos() {
        if (idPelicula <= 0) { mensajeError = "Debe seleccionar una película."; return false; }
        if (idSala <= 0) { mensajeError = "Debe seleccionar una sala."; return false; }
        if (fecha == null || fecha.trim().isEmpty()) { mensajeError = "La fecha es obligatoria."; return false; }
        if (horario == null || horario.trim().isEmpty()) { mensajeError = "El horario es obligatorio."; return false; }
        return true;
    }
    /** Flujo: Guardar -> validar -> verificar disponibilidad de sala/horario -> insertar. */
    public boolean insertar() {
        if (!validarDatos()) return false;
        if (!md.verificarDisponibilidad(idSala, fecha, horario, 0)) {
            mensajeError = "La sala ya tiene una función en esa fecha y horario."; return false;
        }
        if (!md.insertar(this)) { mensajeError = "Error al guardar en la base de datos."; return false; }
        return true;
    }
    public boolean modificar() {
        if (!validarDatos()) return false;
        if (!md.verificarDisponibilidad(idSala, fecha, horario, idFuncion)) {
            mensajeError = "El nuevo horario genera un cruce en esa sala."; return false;
        }
        if (!md.modificar(this)) { mensajeError = "Error al actualizar en la base de datos."; return false; }
        return true;
    }
    public boolean cancelar() {
        if (md.tieneVentasOReservas(idFuncion)) {
            mensajeError = "No se puede cancelar: la función tiene ventas o reservas asociadas."; return false;
        }
        if (!md.actualizarEstado(idFuncion, false)) { mensajeError = "Error al cancelar en la base de datos."; return false; }
        return true;
    }
    public List<Funcion> consultarTodas() { return md.cargarFunciones(); }
    public List<Funcion> consultarVigentes() { return md.cargarFuncionesVigentes(); }
    public List<Funcion> buscarFunciones(String criterio, String valor) { return md.buscarFunciones(criterio, valor); }
}
