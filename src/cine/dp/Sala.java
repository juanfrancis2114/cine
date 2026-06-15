package cine.dp;

import cine.md.SalaMD;
import java.util.List;

/** Capa DP: entidad de dominio Sala + reglas de negocio. */
public class Sala {
    private int idSala;
    private int numeroSala;
    private int capacidad;
    private String tipoSala;
    private boolean estado = true;
    private String mensajeError = "";
    private final SalaMD md = new SalaMD();

    public Sala() {}
    public Sala(int idSala, int numeroSala, int capacidad, String tipoSala, boolean estado) {
        this.idSala = idSala; this.numeroSala = numeroSala; this.capacidad = capacidad;
        this.tipoSala = tipoSala; this.estado = estado;
    }

    public int getIdSala() { return idSala; }
    public void setIdSala(int v) { this.idSala = v; }
    public int getNumeroSala() { return numeroSala; }
    public void setNumeroSala(int v) { this.numeroSala = v; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int v) { this.capacidad = v; }
    public String getTipoSala() { return tipoSala; }
    public void setTipoSala(String v) { this.tipoSala = v; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean v) { this.estado = v; }
    public String getMensajeError() { return mensajeError; }

    public boolean validarDatos() {
        if (numeroSala <= 0) { mensajeError = "El número de sala debe ser mayor a 0."; return false; }
        if (capacidad <= 0) { mensajeError = "La capacidad debe ser mayor a 0."; return false; }
        if (tipoSala == null || tipoSala.trim().isEmpty()) { mensajeError = "El tipo de sala es obligatorio."; return false; }
        return true;
    }
    public boolean insertar() {
        if (!validarDatos()) return false;
        if (md.verificarExistencia(numeroSala)) { mensajeError = "Ya existe una sala con ese número."; return false; }
        if (!md.insertar(this)) { mensajeError = "Error al guardar en la base de datos."; return false; }
        return true;
    }
    public boolean modificar() {
        if (!validarDatos()) return false;
        if (!md.modificar(this)) { mensajeError = "Error al actualizar en la base de datos."; return false; }
        return true;
    }
    public boolean borrar() {
        if (md.tieneFunciones(idSala)) { mensajeError = "No se puede eliminar: la sala tiene funciones programadas."; return false; }
        if (!md.borrar(this)) { mensajeError = "Error al eliminar en la base de datos."; return false; }
        return true;
    }
    public List<Sala> consultarTodas() { return md.cargarSalas(); }
    public List<Sala> buscarSalas(String criterio, String valor) { return md.buscarSalas(criterio, valor); }
}
