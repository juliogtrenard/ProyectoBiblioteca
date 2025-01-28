package es.juliogtrenard.proyectobiblioteca.controller;

import es.juliogtrenard.proyectobiblioteca.db.DBConnect;
import es.juliogtrenard.proyectobiblioteca.language.LanguageManager;
import es.juliogtrenard.proyectobiblioteca.utils.ValidadorNumero;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Clase que controla el login a la base de datos
 */
public class LoginController implements Initializable {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * Boton de confirmar
     */
    @FXML
    private Button btnConfirmar;

    /**
     * Selector de idioma
     */
    @FXML
    private ComboBox<String> cbIdioma;

    /**
     * Contraseña
     */
    @FXML
    private PasswordField txtContrasenia;

    /**
     * Usuario
     */
    @FXML
    private TextField txtUsuario;

    /**
     * Direccion IP
     */
    @FXML
    private TextField txtDireccion;

    /**
     * Puerto
     */
    @FXML
    private TextField txtPuerto;

    /**
     * Nombre de la BBDD
     */
    @FXML
    private TextField txtBD;

    /**
     * BBDD
     */
    private Properties db;

    /**
     * Se ejecuta cuando se inicia la ventana
     *
     * @param url la url
     * @param resourceBundle los recursos
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db = new Properties();
        cbIdioma.getItems().addAll("Español", "English");
        cbIdioma.getSelectionModel().select(0);
    }

    /**
     * Prueba la conexión a la base de datos y activa/desactiva el botón de confirmar
     *
     * @param event evento del usuario
     */
    @FXML
    void probarConexion(ActionEvent event) {
        String error = validar();
        if (!error.isEmpty()) {
            btnConfirmar.setDisable(true);
            mostrarAlerta(error);
        } else {
            String valido = DBConnect.testConnection(txtDireccion.getText(), Integer.parseInt(txtPuerto.getText()), txtUsuario.getText(), txtContrasenia.getText(), txtBD.getText());
            if (valido != null && valido.equals("1")) {
                mostrarConfirmacion("Conexión valida");
                btnConfirmar.setDisable(false);
            } else {
                btnConfirmar.setDisable(true);
                if (valido == null) {
                    mostrarAlerta("Conexión invalida");
                } else {
                    mostrarAlerta("Error: " + valido);
                }
            }
        }
    }

    /**
     * Valida el formulario y devuelve los posibles errores
     *
     * @return string con posibles errores
     */
    private String validar() {
        String error = "";
        if (txtDireccion.getText().isEmpty()) {
            error += "El campo dirección no puede estar vacío\n";
        } else {
            db.setProperty("address", txtDireccion.getText());
        }
        if (txtPuerto.getText().isEmpty()) {
            error += "El campo puerto no puede estar vacío\n";
        } else if (!ValidadorNumero.validarInt(txtPuerto.getText())) {
            error += "El campo puerto tiene que ser numérico\n";
        } else {
            db.setProperty("port", txtPuerto.getText());
        }
        if (txtUsuario.getText().isEmpty()) {
            error += "El campo usuario no puede estar vacío\n";
        } else {
            db.setProperty("user", txtUsuario.getText());
        }
        if (txtContrasenia.getText().isEmpty()) {
            error += "El campo contraseña no puede estar vacío\n";
        } else {
            db.setProperty("password", txtContrasenia.getText());
        }
        if (txtBD.getText().isEmpty()) {
            error += "EL nombre de la base de datos no puede estar vacío\n";
        } else {
            db.setProperty("database", txtBD.getText());
        }
        return error;
    }

    /**
     * Guarda la selección y carga la ventana principal
     *
     * @param event evento del usuario
     */
    @FXML
    void confirmar(ActionEvent event) {
        String language = cbIdioma.getSelectionModel().getSelectedItem();
        Locale locale;
        if (language.equals("Español")) {
            locale = Locale.of("es");
        } else {
            locale = Locale.of("en");
        }
        LanguageManager.createFile(locale.getLanguage());
        DBConnect.createConfiguration(db);
        cerrar(null);
    }

    /**
     * Cierra la aplicación
     *
     * @param event evento del usuario
     */
    @FXML
    void cerrar(ActionEvent event) {
        Stage stage = (Stage) cbIdioma.getScene().getWindow();
        stage.close();
    }

    /**
     * Muestra un mensaje de confirmación al usuario
     *
     * @param mensaje de confirmación a mostrar
     */
    public void mostrarConfirmacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setTitle("Confirmar");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra un mensaje de alerta al usuario
     *
     * @param mensaje de error a mostrar
     */
    public void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setTitle("Error");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}