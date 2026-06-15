package cine.dp;

import cine.md.PeliculaMD;
import java.util.List;

/** Capa DP: entidad de dominio Película + reglas de negocio. */
public class Pelicula {
    private int idPelicula;
    private String titulo;
    private String genero;
    private int duracion;
    private String clasificacion;
    private boolean estado = true;
    private String mensajeError = "";
    private final PeliculaMD md = new PeliculaMD();

    public Pelicula() {}
    public Pelicula(int idPelicula, String titulo, String genero, int duracion, String clasificacion, boolean estado) {
        this.idPelicula = idPelicula; this.titulo = titulo; this.genero = genero;
        this.duracion = duracion; this.clasificacion = clasificacion; this.estado = estado;
    }

    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int v) { this.idPelicula = v; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String v) { this.titulo = v; }
    public String getGenero() { return genero; }
    public void setGenero(String v) { this.genero = v; }
    public int getDuracion() { return duracion; }
    public void setDuracion(int v) { this.duracion = v; }
    public String getClasificacion() { return clasificacion; }
    public void setClasificacion(String v) { this.clasificacion = v; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean v) { this.estado = v; }
    public String getMensajeError() { return mensajeError; }

    public boolean validarDatos() {
        if (titulo == null || titulo.trim().isEmpty()) { mensajeError = "El título es obligatorio."; return false; }
        if (genero == null || genero.trim().isEmpty()) { mensajeError = "El género es obligatorio."; return false; }
        if (duracion <= 0) { mensajeError = "La duración debe ser mayor a 0 minutos."; return false; }
        if (clasificacion == null || clasificacion.trim().isEmpty()) { mensajeError = "La clasificación es obligatoria."; return false; }
        return true;
    }

    /** Flujo: Guardar -> validar -> verificar duplicado -> insertar. */
    public boolean insertar() {
        if (!validarDatos()) return false;
        if (md.verificarExistencia(titulo)) { mensajeError = "Ya existe una película con ese título."; return false; }
        if (!md.insertar(this)) { mensajeError = "Error al guardar en la base de datos."; return false; }
        return true;
    }
    public boolean modificar() {
        if (!validarDatos()) return false;
        if (!md.modificar(this)) { mensajeError = "Error al actualizar en la base de datos."; return false; }
        return true;
    }
    public boolean borrar() {
        if (md.tieneFunciones(idPelicula)) { mensajeError = "No se puede eliminar: la película está asociada a funciones."; return false; }
        if (!md.borrar(this)) { mensajeError = "Error al eliminar en la base de datos."; return false; }
        return true;
    }
    public List<Pelicula> consultarTodas() { return md.cargarPeliculas(); }
    public List<Pelicula> buscarPeliculas(String criterio, String valor) { return md.buscarPeliculas(criterio, valor); }
}
