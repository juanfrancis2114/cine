package cine.md;

import cine.dp.Sala;
import cine.util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Capa MD: acceso a datos de Salas. */
public class SalaMD {

    public boolean insertar(Sala s) {
        String sql = "INSERT INTO Salas(numeroSala,capacidad,tipoSala,estado) VALUES(?,?,?,?)";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, s.getNumeroSala()); ps.setInt(2, s.getCapacidad());
            ps.setString(3, s.getTipoSala()); ps.setBoolean(4, s.isEstado());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public boolean modificar(Sala s) {
        String sql = "UPDATE Salas SET numeroSala=?,capacidad=?,tipoSala=?,estado=? WHERE idSala=?";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, s.getNumeroSala()); ps.setInt(2, s.getCapacidad());
            ps.setString(3, s.getTipoSala()); ps.setBoolean(4, s.isEstado()); ps.setInt(5, s.getIdSala());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public boolean borrar(Sala s) {
        String sql = "DELETE FROM Salas WHERE idSala=?";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, s.getIdSala());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public boolean verificarExistencia(int numeroSala) {
        String sql = "SELECT 1 FROM Salas WHERE numeroSala=?";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, numeroSala);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public boolean tieneFunciones(int idSala) {
        String sql = "SELECT 1 FROM Funciones WHERE idSala=? AND estado=true";
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idSala);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    public List<Sala> cargarSalas() { return query("SELECT * FROM Salas ORDER BY idSala", null, false); }
    public List<Sala> buscarSalas(String criterio, String valor) {
        if ("Tipo".equalsIgnoreCase(criterio))
            return query("SELECT * FROM Salas WHERE LOWER(tipoSala) LIKE LOWER(?) ORDER BY idSala", "%" + valor + "%", true);
        return query("SELECT * FROM Salas WHERE CAST(numeroSala AS TEXT) LIKE ? ORDER BY idSala", "%" + valor + "%", true);
    }
    private List<Sala> query(String sql, String param, boolean hasParam) {
        List<Sala> lista = new ArrayList<>();
        try (Connection cn = new Conexion().getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            if (hasParam) ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Sala(rs.getInt("idSala"), rs.getInt("numeroSala"),
                            rs.getInt("capacidad"), rs.getString("tipoSala"), rs.getBoolean("estado")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }
}
