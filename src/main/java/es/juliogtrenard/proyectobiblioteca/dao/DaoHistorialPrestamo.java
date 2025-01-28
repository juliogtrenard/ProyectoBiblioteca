package es.juliogtrenard.proyectobiblioteca.dao;

import es.juliogtrenard.proyectobiblioteca.db.DBConnect;
import es.juliogtrenard.proyectobiblioteca.model.Alumno;
import es.juliogtrenard.proyectobiblioteca.model.HistorialPrestamo;
import es.juliogtrenard.proyectobiblioteca.model.Libro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Clase que maneja el historial de prestamo en la BBDD
 */
public class DaoHistorialPrestamo {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(DaoHistorialPrestamo.class);

    /**
     * Busca un prestamo por medio de su id
     *
     * @param id_prestamo id_prestamo del prestamo a buscar
     * @return prestamo o null
     */
    public static HistorialPrestamo getHistorialPrestamo(String id_prestamo) {
        DBConnect connection;
        HistorialPrestamo prestamo = null;
        try {
            connection = new DBConnect();
            String consulta = "SELECT id_prestamo,dni_alumno,codigo_libro,fecha_prestamo,fecha_devolucion FROM Historico_prestamo WHERE id_prestamo = ?";
            PreparedStatement ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, id_prestamo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id_prestamo_db = rs.getInt("id_prestamo");
                String dni_alumno = rs.getString("dni_alumno");
                Alumno alumno = DaoAlumno.getAlumno(dni_alumno);
                int codigo_libro = rs.getInt("codigo_libro");
                Libro libro = DaoLibro.getLibro(codigo_libro);
                LocalDateTime fecha_prestamo = rs.getTimestamp("fecha_prestamo").toLocalDateTime();
                LocalDateTime fecha_devolucion = rs.getTimestamp("fecha_devolucion").toLocalDateTime();
                prestamo = new HistorialPrestamo(id_prestamo_db, alumno, libro, fecha_prestamo, fecha_devolucion);
            }
            rs.close();
            connection.closeConnection();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return prestamo;
    }

    /**
     * Carga los datos de la tabla HistorialPrestamos y los devuelve para usarlos en un listado de préstamos
     *
     * @param alumno a obtener el listado
     * @return listado de préstamos para cargar en un tableview
     */
    public static ObservableList<HistorialPrestamo> historialDeAlumno(Alumno alumno) {
        DBConnect connection;
        ObservableList<HistorialPrestamo> prestamos = FXCollections.observableArrayList();
        try{
            connection = new DBConnect();
            String consulta = "SELECT id_prestamo,dni_alumno,codigo_libro,fecha_prestamo,fecha_devolucion FROM Historico_prestamo WHERE dni_alumno = ?";
            PreparedStatement ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, alumno.getDni());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id_prestamo_db = rs.getInt("id_prestamo");
                String dni_alumno = rs.getString("dni_alumno");
                Alumno alumno_db = DaoAlumno.getAlumno(dni_alumno);
                int codigo_libro = rs.getInt("codigo_libro");
                Libro libro = DaoLibro.getLibro(codigo_libro);
                LocalDateTime fecha_prestamo = rs.getTimestamp("fecha_prestamo").toLocalDateTime();
                LocalDateTime fecha_devolucion = rs.getTimestamp("fecha_devolucion").toLocalDateTime();
                HistorialPrestamo prestamo = new HistorialPrestamo(id_prestamo_db, alumno_db, libro, fecha_prestamo, fecha_devolucion);
                prestamos.add(prestamo);
            }
            rs.close();
            connection.closeConnection();
        }catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return prestamos;
    }

    /**
     * Carga los datos de la tabla HistorialPrestamos y los devuelve para usarlos en un listado de préstamos
     *
     * @return listado de préstamos para cargar en un tableview
     */
    public static ObservableList<HistorialPrestamo> cargarListado() {
        DBConnect connection;
        ObservableList<HistorialPrestamo> prestamos = FXCollections.observableArrayList();
        try{
            connection = new DBConnect();
            String consulta = "SELECT id_prestamo,dni_alumno,codigo_libro,fecha_prestamo,fecha_devolucion FROM Historico_prestamo";
            PreparedStatement ps = connection.getConnection().prepareStatement(consulta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id_prestamo_db = rs.getInt("id_prestamo");
                String dni_alumno = rs.getString("dni_alumno");
                Alumno alumno = DaoAlumno.getAlumno(dni_alumno);
                int codigo_libro = rs.getInt("codigo_libro");
                Libro libro = DaoLibro.getLibro(codigo_libro);
                LocalDateTime fecha_prestamo = rs.getTimestamp("fecha_prestamo").toLocalDateTime();
                LocalDateTime fecha_devolucion = rs.getTimestamp("fecha_devolucion").toLocalDateTime();
                HistorialPrestamo prestamo = new HistorialPrestamo(id_prestamo_db, alumno, libro, fecha_prestamo, fecha_devolucion);
                prestamos.add(prestamo);
            }
            rs.close();
            connection.closeConnection();
        }catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return prestamos;
    }

    /**
     * Modifica los datos de un prestamo en la BD
     *
     * @param prestamo Instancia del prestamo con datos a actualizar
     * @return true/false
     */
    public static boolean modificar(HistorialPrestamo prestamo) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "UPDATE Historico_prestamo SET dni_alumno = ?,codigo_libro = ?,fecha_prestamo = ?,fecha_devolucion = ? WHERE id_prestamo = ?";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, prestamo.getAlumno().getDni());
            ps.setInt(2, prestamo.getLibro().getCodigo());
            ps.setTimestamp(3, Timestamp.valueOf(prestamo.getFecha_prestamo()));
            ps.setTimestamp(4, Timestamp.valueOf(prestamo.getFecha_devolucion()));
            ps.setInt(5, prestamo.getId_prestamo());
            int filasAfectadas = ps.executeUpdate();
            ps.close();
            connection.closeConnection();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Crea un nuevo prestamo en la BD
     *
     * @param prestamo Instancia del modelo historial prestamo con datos nuevos
     * @return true/false
     */
    public  static boolean insertar(HistorialPrestamo prestamo) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "INSERT INTO Historico_prestamo (id_prestamo,dni_alumno,codigo_libro,fecha_prestamo,fecha_devolucion) VALUES (?,?,?,?,?) ";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setInt(1, prestamo.getId_prestamo());
            ps.setString(2, prestamo.getAlumno().getDni());
            ps.setInt(3, prestamo.getLibro().getCodigo());
            ps.setTimestamp(4, Timestamp.valueOf(prestamo.getFecha_prestamo()));
            ps.setTimestamp(5, Timestamp.valueOf(prestamo.getFecha_devolucion()));
            int filasAfectadas = ps.executeUpdate();
            ps.close();
            connection.closeConnection();
            return (filasAfectadas > 0);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un prestamo en función del modelo HistorialPrestamo que le hayamos pasado
     *
     * @param prestamo HistorialPrestamo a eliminar
     * @return a boolean
     */
    public static boolean eliminar(HistorialPrestamo prestamo) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "DELETE FROM Historico_prestamo WHERE id_prestamo = ?";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setInt(1, prestamo.getId_prestamo());
            int filasAfectadas = ps.executeUpdate();
            ps.close();
            connection.closeConnection();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
