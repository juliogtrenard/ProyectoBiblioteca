package es.juliogtrenard.proyectobiblioteca.dao;

import es.juliogtrenard.proyectobiblioteca.db.DBConnect;
import es.juliogtrenard.proyectobiblioteca.model.Alumno;
import es.juliogtrenard.proyectobiblioteca.model.Libro;
import es.juliogtrenard.proyectobiblioteca.model.Prestamo;
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
 * Clase que maneja los datos del prestamo con la BBDD
 */
public class DaoPrestamo {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(DaoPrestamo.class);

    /**
     * Busca un prestamo por medio de su id
     *
     * @param id_prestamo id_prestamo del prestamo a buscar
     * @return prestamo o null
     */
    public static Prestamo getPrestamo(String id_prestamo) {
        DBConnect connection;
        Prestamo prestamo = null;
        try {
            connection = new DBConnect();
            String consulta = "SELECT id_prestamo,dni_alumno,codigo_libro,fecha_prestamo FROM Prestamo WHERE id_prestamo = ?";
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
                prestamo = new Prestamo(id_prestamo_db, alumno, libro, fecha_prestamo);
            }
            rs.close();
            connection.closeConnection();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return prestamo;
    }

    /**
     * Carga los datos de la tabla Préstamos y los devuelve para usarlos en un listado de préstamos
     *
     * @param alumno a obtener el listado
     * @return listado de préstamos para cargar en un tableview
     */
    public static ObservableList<Prestamo> prestamosDeAlumno(Alumno alumno) {
        DBConnect connection;
        ObservableList<Prestamo> prestamos = FXCollections.observableArrayList();
        try{
            connection = new DBConnect();
            String consulta = "SELECT id_prestamo,dni_alumno,codigo_libro,fecha_prestamo FROM Prestamo WHERE dni_alumno = ?";
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
                Prestamo prestamo = new Prestamo(id_prestamo_db, alumno_db, libro, fecha_prestamo);
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
     * Carga los datos de la tabla Préstamos y los devuelve para usarlos en un listado de préstamos
     *
     * @return listado de préstamos para cargar en un tableview
     */
    public static ObservableList<Prestamo> cargarListado() {
        DBConnect connection;
        ObservableList<Prestamo> prestamos = FXCollections.observableArrayList();
        try{
            connection = new DBConnect();
            String consulta = "SELECT id_prestamo,dni_alumno,codigo_libro,fecha_prestamo FROM Prestamo";
            PreparedStatement ps = connection.getConnection().prepareStatement(consulta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id_prestamo_db = rs.getInt("id_prestamo");
                String dni_alumno = rs.getString("dni_alumno");
                Alumno alumno = DaoAlumno.getAlumno(dni_alumno);
                int codigo_libro = rs.getInt("codigo_libro");
                Libro libro = DaoLibro.getLibro(codigo_libro);
                LocalDateTime fecha_prestamo = rs.getTimestamp("fecha_prestamo").toLocalDateTime();
                Prestamo prestamo = new Prestamo(id_prestamo_db, alumno, libro, fecha_prestamo);
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
    public static boolean modificar(Prestamo prestamo) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "UPDATE Prestamo SET dni_alumno = ?,codigo_libro = ?,fecha_prestamo = ? WHERE id_prestamo = ?";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, prestamo.getAlumno().getDni());
            ps.setInt(2, prestamo.getLibro().getCodigo());
            ps.setTimestamp(3, Timestamp.valueOf(prestamo.getFecha_prestamo()));
            ps.setInt(4, prestamo.getId_prestamo());
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
     * @param prestamo Instancia del modelo prestamo con datos nuevos
     * @return id/-1
     */
    public  static int insertar(Prestamo prestamo) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "INSERT INTO Prestamo (dni_alumno,codigo_libro,fecha_prestamo) VALUES (?,?,?) ";
            ps = connection.getConnection().prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, prestamo.getAlumno().getDni());
            ps.setInt(2, prestamo.getLibro().getCodigo());
            ps.setTimestamp(3, Timestamp.valueOf(prestamo.getFecha_prestamo()));
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    ps.close();
                    connection.closeConnection();
                    return id;
                }
            }
            ps.close();
            connection.closeConnection();
            return -1;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return -1;
        }
    }

    /**
     * Elimina un prestamo en función del modelo Prestamo que le hayamos pasado
     *
     * @param prestamo Prestamo a eliminar
     * @return a boolean
     */
    public static boolean eliminar(Prestamo prestamo) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "DELETE FROM Prestamo WHERE id_prestamo = ?";
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
