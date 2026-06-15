package cine.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.FileInputStream;

/**
 * Capa de utilidad: obtiene la conexión a PostgreSQL leyendo db.properties.
 * Es el ÚNICO punto desde el cual la capa MD obtiene una Connection.
 */
public class Conexion {
    private String db;
    private Connection _connection;

    public Properties cargarPropiedades() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            props.load(in);
        } catch (Exception e) {
            System.err.println("No se pudo leer db.properties: " + e.getMessage());
        }
        return props;
    }

    public Connection getConexion() {
        try {
            Properties p = cargarPropiedades();
            this.db = p.getProperty("db.url");
            try { Class.forName("org.postgresql.Driver"); } catch (ClassNotFoundException ignored) {}
            _connection = DriverManager.getConnection(
                    db, p.getProperty("db.user"), p.getProperty("db.password"));
        } catch (Exception e) {
            System.err.println("Error de conexión a la base de datos: " + e.getMessage());
        }
        return _connection;
    }
}
