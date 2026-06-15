package cine.md;

import cine.dp.VentaBoleto;
import cine.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Capa MD: acceso a datos de Venta de Boletos, con JOIN para visualización. */
public class VentaBoletoMD {

    private static final String BASE =
        "SELECT v.idVenta, v.idFuncion, v.idEmpleado, v.asientos, v.fechaVenta, v.estado, " +
        "p.titulo AS titulo, s.numeroSala AS numeroSala, f.fecha AS fecha, f.horario AS horario " +
        "FROM VentaBoletos v " +
        "JOIN Funciones f ON f.idFuncion = v.idFuncion " +
        "JOIN Peliculas p ON p.idPelicula = f.idPelicula " +
        "JOIN Salas s ON s.idSala = f.idSala ";

    public boolean insertar(VentaBoleto v) {
        String sql = "INSERT INTO VentaBoletos(idFuncion,idEmpleado,asientos,fechaVenta,estado) VALUES(?,?,?,?,?)";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, v.getIdFuncion()); ps.setInt(2, v.getIdEmpleado());
            ps.setString(3, v.getAsientos()); ps.setString(4, v.getFechaVenta()); ps.setBoolean(5, v.isEstado());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public boolean actualizarEstado(int idVenta, boolean estado) {
        String sql = "UPDATE VentaBoletos SET estado=? WHERE idVenta=?";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBoolean(1, estado); ps.setInt(2, idVenta);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public boolean verificarEstado(int idVenta) {
        String sql = "SELECT estado FROM VentaBoletos WHERE idVenta=?";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getBoolean("estado"); }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    /** true si NINGUNO de los asientos solicitados está ya vendido (en ventas vigentes). */
    public boolean verificarAsientos(int idFuncion, String asientosSolicitados) {
        Set<String> ocupados = new HashSet<>(asientosOcupados(idFuncion));
        for (String a : asientosSolicitados.split(",")) {
            if (ocupados.contains(a.trim())) return false;
        }
        return true;
    }
    public List<String> asientosOcupados(int idFuncion) {
        List<String> ocupados = new ArrayList<>();
        String sql = "SELECT asientos FROM VentaBoletos WHERE idFuncion=? AND estado=true";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idFuncion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String a = rs.getString("asientos");
                    if (a != null) for (String x : a.split(",")) ocupados.add(x.trim());
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ocupados;
    }
    public List<VentaBoleto> cargarVentas() { return query(BASE + "ORDER BY v.idVenta", null); }
    public List<VentaBoleto> buscarVentas(String criterio, String valor) {
        String cond;
        if ("Fecha".equalsIgnoreCase(criterio))    cond = "v.fechaVenta LIKE ?";
        else if ("Boleto".equalsIgnoreCase(criterio)) cond = "CAST(v.idVenta AS TEXT) LIKE ?";
        else                                        cond = "LOWER(p.titulo) LIKE LOWER(?)";
        return query(BASE + "WHERE " + cond + " ORDER BY v.idVenta", "%" + valor + "%");
    }
    private List<VentaBoleto> query(String sql, String param) {
        List<VentaBoleto> lista = new ArrayList<>();
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            if (param != null) ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VentaBoleto v = new VentaBoleto(rs.getInt("idVenta"), rs.getInt("idFuncion"),
                            rs.getInt("idEmpleado"), rs.getString("asientos"),
                            rs.getString("fechaVenta"), rs.getBoolean("estado"));
                    v.setDescripcionFuncion(rs.getString("titulo") + " — Sala " + rs.getInt("numeroSala")
                            + " (" + rs.getString("fecha") + " " + rs.getString("horario") + ")");
                    lista.add(v);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }
}
