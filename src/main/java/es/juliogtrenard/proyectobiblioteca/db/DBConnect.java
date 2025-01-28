package es.juliogtrenard.proyectobiblioteca.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Conexion a la base de datos
 *
 * @author Julio González
 */
public class DBConnect {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(DBConnect.class);

    /**
     * La conexion
     */
    private final Connection connection;

    /**
     * Lanza la conexion
     *
     * @throws SQLException Errores de SQL
     */
    public DBConnect() throws SQLException {
        Properties configuracion = getConfiguration();

        if (configuracion != null) {
            Properties connConfig = new Properties();
            connConfig.setProperty("user", configuracion.getProperty("user"));
            connConfig.setProperty("password", configuracion.getProperty("password"));

            connection = DriverManager.getConnection("jdbc:mariadb://" + configuracion.getProperty("address") + ":" + configuracion.getProperty("port") + "/" + configuracion.getProperty("database") + "?serverTimezone=Europe/Madrid", connConfig);
            connection.setAutoCommit(true);
            DatabaseMetaData databaseMetaData = connection.getMetaData();

            logger.debug("----- Datos de la conexión -----");
            logger.debug("Base de datos: {}", databaseMetaData.getDatabaseProductName());
            logger.debug("Versión: {}", databaseMetaData.getDatabaseProductVersion());
            logger.debug("Driver: {}", databaseMetaData.getDriverName());
            logger.debug("Versión driver: {}", databaseMetaData.getDriverVersion());
            logger.debug("-----------------------------------");
            connection.setAutoCommit(true);
        } else {
            this.connection = null;
        }
    }

    /**
     * Prueba la conexión a una base de datos
     *
     * @param address direccion
     * @param port puerto
     * @param user usuario
     * @param password contraseña
     * @param database base de datos
     * @return 1/error/null
     */
    public static String testConnection(String address, int port, String user, String password, String database) {
        try {
            Properties authentication = new Properties();
            authentication.setProperty("user", user);
            authentication.setProperty("password", password);
            Connection connection1 = DriverManager.getConnection("jdbc:mariadb://" + address + ":" + port + "/" + database + "?serverTimezone=Europe/Madrid", authentication);
            if (connection1.isValid(10)) {
                connection1.close();
                return "1";
            }
            connection1.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return e.getMessage();
        }
        return null;
    }

    /**
     * Obtiene la configuración para la conexión a la base de datos
     *
     * @return objeto Properties con los datos de conexión a la base de datos
     */
    public static Properties getConfiguration() {
        HashMap<String,String> map = new HashMap<String,String>();
        File f = new File("configuration.properties");
        Properties properties;
        try {
            FileInputStream configFileReader=new FileInputStream(f);
            properties = new Properties();
            try {
                properties.load(configFileReader);
                configFileReader.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            return null;
        }
        return properties;
    }

    /**
     * Crea el fichero configuration.properties si este no existe con valores predeterminados
     *
     * @param db propiedades de la base de datos
     */
    public static void createConfiguration(Properties db) {
        File f = new File("configuration.properties");
        try {
            FileOutputStream fos = new FileOutputStream(f);
            db.store(fos, "");
            fos.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Devuelve la conexión creada
     *
     * @return una conexión a la BBDD
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Cierra la conexion con la base de datos
     *
     * @return La conexión cerrada.
     * @throws SQLException En caso de errores de SQL.
     */
    public Connection closeConnection() throws SQLException{
        connection.close();
        return connection;
    }
}

