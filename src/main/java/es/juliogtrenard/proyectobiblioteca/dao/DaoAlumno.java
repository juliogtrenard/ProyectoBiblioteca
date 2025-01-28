package es.juliogtrenard.proyectobiblioteca.dao;

import es.juliogtrenard.proyectobiblioteca.db.DBConnect;
import es.juliogtrenard.proyectobiblioteca.model.Alumno;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que maneja al alumno en la BBDD
 */
public class DaoAlumno {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(DaoAlumno.class);

    /**
     * Busca un alumno por medio de su id
     *
     * @param dni dni del alumno a buscar
     * @return alumno o null
     */
    public static Alumno getAlumno(String dni) {
        DBConnect connection;
        Alumno alumno = null;
        try {
            connection = new DBConnect();
            String consulta = "SELECT dni,nombre,apellido1,apellido2 FROM Alumno WHERE dni = ?";
            PreparedStatement ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String dni_db = rs.getString("dni");
                String nombre = rs.getString("nombre");
                String apellido1 = rs.getString("apellido1");
                String apellido2 = rs.getString("apellido2");
                alumno = new Alumno(dni_db, nombre, apellido1, apellido2);
            }
            rs.close();
            connection.closeConnection();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return alumno;
    }

    /**
     * Carga los datos de la tabla Alumnos y los devuelve para usarlos en un listado de alumnos
     *
     * @return listado de alumnos para cargar en un tableview
     */
    public static ObservableList<Alumno> cargarListado() {
        DBConnect connection;
        ObservableList<Alumno> alumnos = FXCollections.observableArrayList();
        try{
            connection = new DBConnect();
            String consulta = "SELECT dni,nombre,apellido1,apellido2 FROM Alumno";
            PreparedStatement ps = connection.getConnection().prepareStatement(consulta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String dni = rs.getString("dni");
                String nombre = rs.getString("nombre");
                String apellido1 = rs.getString("apellido1");
                String apellido2 = rs.getString("apellido2");
                Alumno alumno = new Alumno(dni, nombre, apellido1, apellido2);
                alumnos.add(alumno);
            }
            rs.close();
            connection.closeConnection();
        }catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return alumnos;
    }

    /**
     * Busca un alumno y mira a ver si se puede eliminar
     *
     * @param alumno alumno a buscar
     * @return true/false
     */
    public static boolean esEliminable(Alumno alumno) {
        DBConnect connection;
        try {
            connection = new DBConnect();
            String consulta = "SELECT count(*) as cont FROM Prestamo WHERE dni_alumno = ?";
            PreparedStatement ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, alumno.getDni());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int cont = rs.getInt("cont");
                if (cont != 0) {
                    rs.close();
                    connection.closeConnection();
                    return false;
                }
            }
            rs.close();
            ps.close();

            consulta = "SELECT count(*) as cont FROM Historico_prestamo WHERE dni_alumno = ?";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, alumno.getDni());
            rs = ps.executeQuery();
            if (rs.next()) {
                int cont = rs.getInt("cont");
                rs.close();
                connection.closeConnection();
                return (cont == 0);
            }
            rs.close();
            connection.closeConnection();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    /**
     * Modifica los datos de un alumno en la BD
     *
     * @param alumno Instancia del alumno con datos a actualizar
     * @return true/false
     */
    public static boolean modificar(Alumno alumno) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "UPDATE Alumno SET nombre = ?,apellido1 = ?,apellido2 = ? WHERE dni = ?";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellido1());
            ps.setString(3, alumno.getApellido2());
            ps.setString(4, alumno.getDni());
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
     * Crea un nuevo alumno en la BD
     *
     * @param alumno Instancia del modelo alumno con datos nuevos
     * @return true/false
     */
    public  static boolean insertar(Alumno alumno) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "INSERT INTO Alumno (dni,nombre,apellido1,apellido2) VALUES (?,?,?,?) ";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, alumno.getDni());
            ps.setString(2, alumno.getNombre());
            ps.setString(3, alumno.getApellido1());
            ps.setString(4, alumno.getApellido2());
            int filasAfectadas = ps.executeUpdate();
            connection.closeConnection();
            return (filasAfectadas > 0);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un alumno en función del modelo Alumno que le hayamos pasado
     *
     * @param alumno Alumno a eliminar
     * @return a boolean
     */
    public static boolean eliminar(Alumno alumno) {
        DBConnect connection;
        PreparedStatement ps;
        try {
            connection = new DBConnect();
            String consulta = "DELETE FROM Alumno WHERE dni = ?";
            ps = connection.getConnection().prepareStatement(consulta);
            ps.setString(1, alumno.getDni());
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