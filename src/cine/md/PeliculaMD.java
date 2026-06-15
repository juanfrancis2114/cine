package cine.md;

import cine.dp.Pelicula;
import cine.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Capa MD: acceso a datos de Películas. Único lugar con SQL del módulo. */
public class PeliculaMD {

    public boolean insertar(Pelicula p) {
        String sql = "INSERT INTO Peliculas(titulo,genero,duracion,clasificacion,estado) VALUES(?,?,?,?,?)";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getTitulo()); ps.setString(2, p.getGenero());
            ps.setInt(3, p.getDuracion()); ps.setString(4, p.getClasificacion());
            ps.setBoolean(5, p.isEstado());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public boolean modificar(Pelicula p) {
        String sql = "UPDATE Peliculas SET titulo=?,genero=?,duracion=?,clasificacion=?,estado=? WHERE idPelicula=?";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getTitulo()); ps.setString(2, p.getGenero());
            ps.setInt(3, p.getDuracion()); ps.setString(4, p.getClasificacion());
            ps.setBoolean(5, p.isEstado()); ps.setInt(6, p.getIdPelicula());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public boolean borrar(Pelicula p) {
        String sql = "DELETE FROM Peliculas WHERE idPelicula=?";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, p.getIdPelicula());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public boolean verificarExistencia(String titulo) {
        String sql = "SELECT 1 FROM Peliculas WHERE LOWER(titulo)=LOWER(?)";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, titulo);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public boolean tieneFunciones(int idPelicula) {
        String sql = "SELECT 1 FROM Funciones WHERE idPelicula=? AND estado=true";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPelicula);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public List<Pelicula> cargarPeliculas() {
        return query("SELECT * FROM Peliculas ORDER BY idPelicula", null);
    }
    public List<Pelicula> buscarPeliculas(String criterio, String valor) {
        String col = "titulo";
        if ("Género".equalsIgnoreCase(criterio)) col = "genero";
        else if ("Clasificación".equalsIgnoreCase(criterio)) col = "clasificacion";
        return query("SELECT * FROM Peliculas WHERE LOWER(" + col + ") LIKE LOWER(?) ORDER BY idPelicula", "%" + valor + "%");
    }
    private List<Pelicula> query(String sql, String param) {
        List<Pelicula> lista = new ArrayList<>();
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            if (param != null) ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Pelicula(rs.getInt("idPelicula"), rs.getString("titulo"),
                            rs.getString("genero"), rs.getInt("duracion"),
                            rs.getString("clasificacion"), rs.getBoolean("estado")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }
}
