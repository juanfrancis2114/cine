package cine.md;

import cine.dp.Funcion;
import cine.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Capa MD: acceso a datos de Funciones (cartelera), con JOIN para visualización. */
public class FuncionMD {

    private static final String BASE =
        "SELECT f.idFuncion, f.idPelicula, f.idSala, f.fecha, f.horario, f.estado, " +
        "p.titulo AS titulo, s.numeroSala AS numeroSala, s.capacidad AS capacidad " +
        "FROM Funciones f " +
        "JOIN Peliculas p ON p.idPelicula = f.idPelicula " +
        "JOIN Salas s ON s.idSala = f.idSala ";

    public boolean insertar(Funcion f) {
        String sql = "INSERT INTO Funciones(idPelicula,idSala,fecha,horario,estado) VALUES(?,?,?,?,?)";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, f.getIdPelicula()); ps.setInt(2, f.getIdSala());
            ps.setString(3, f.getFecha()); ps.setString(4, f.getHorario()); ps.setBoolean(5, f.isEstado());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public boolean modificar(Funcion f) {
        String sql = "UPDATE Funciones SET idPelicula=?,idSala=?,fecha=?,horario=?,estado=? WHERE idFuncion=?";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, f.getIdPelicula()); ps.setInt(2, f.getIdSala());
            ps.setString(3, f.getFecha()); ps.setString(4, f.getHorario());
            ps.setBoolean(5, f.isEstado()); ps.setInt(6, f.getIdFuncion());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public boolean actualizarEstado(int idFuncion, boolean estado) {
        String sql = "UPDATE Funciones SET estado=? WHERE idFuncion=?";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBoolean(1, estado); ps.setInt(2, idFuncion);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    /** true si NO hay otra función activa en esa sala/fecha/horario (excluye idFuncionExcluir). */
    public boolean verificarDisponibilidad(int idSala, String fecha, String horario, int idFuncionExcluir) {
        String sql = "SELECT 1 FROM Funciones WHERE idSala=? AND fecha=? AND horario=? AND estado=true AND idFuncion<>?";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idSala); ps.setString(2, fecha); ps.setString(3, horario); ps.setInt(4, idFuncionExcluir);
            try (ResultSet rs = ps.executeQuery()) { return !rs.next(); }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public boolean tieneVentasOReservas(int idFuncion) {
        String sql = "SELECT 1 FROM VentaBoletos WHERE idFuncion=? AND estado=true " +
                     "UNION SELECT 1 FROM Reservas WHERE idFuncion=? AND estado=true";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idFuncion); ps.setInt(2, idFuncion);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public List<Funcion> cargarFunciones() { return query(BASE + "ORDER BY f.idFuncion", null); }
    public List<Funcion> cargarFuncionesVigentes() { return query(BASE + "WHERE f.estado=true ORDER BY f.fecha, f.horario", null); }
    public List<Funcion> buscarFunciones(String criterio, String valor) {
        String cond;
        if ("Sala".equalsIgnoreCase(criterio))         cond = "CAST(s.numeroSala AS TEXT) LIKE ?";
        else if ("Fecha".equalsIgnoreCase(criterio))   cond = "f.fecha LIKE ?";
        else if ("Horario".equalsIgnoreCase(criterio)) cond = "f.horario LIKE ?";
        else                                           cond = "LOWER(p.titulo) LIKE LOWER(?)";
        return query(BASE + "WHERE " + cond + " ORDER BY f.idFuncion", "%" + valor + "%");
    }
    private List<Funcion> query(String sql, String param) {
        List<Funcion> lista = new ArrayList<>();
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            if (param != null) ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Funcion f = new Funcion(rs.getInt("idFuncion"), rs.getInt("idPelicula"), rs.getInt("idSala"),
                            rs.getString("fecha"), rs.getString("horario"), rs.getBoolean("estado"));
                    f.setTituloPelicula(rs.getString("titulo"));
                    f.setNumeroSala(rs.getInt("numeroSala"));
                    f.setCapacidadSala(rs.getInt("capacidad"));
                    lista.add(f);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }
}
