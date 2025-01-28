package es.juliogtrenard.proyectobiblioteca.controller;

import es.juliogtrenard.proyectobiblioteca.db.DBConnect;
import es.juliogtrenard.proyectobiblioteca.language.LanguageManager;
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
            String valido = DBConnect.testConnection(txtUsuario.getText(), txtContrasenia.getText());
            if (valido != null && valido.equals("1")) {
                mostrarConfirmacion("Conexión valida");
                btnConfirmar.setDisable(false);
            } else {
                btnConfirmar.setDisable(true);
                if (valido == null) {
                    mostrarAlerta("Conexión invalida, intentalo de nuevo");
                } else {
                    mostrarAlerta("Error: " + valido);
                }
            }
        }
    }

    /**
     * Valida los datos ingresados y devuelve los posibles errores
     *
     * @return string con posibles errores
     */
    private String validar() {
        String error = "";

        if (txtUsuario.getText().isEmpty()) {
            error += "Ingresa el usuario\n";
        } else {
            db.setProperty("user", txtUsuario.getText());
        }
        if (txtContrasenia.getText().isEmpty()) {
            error += "Ingresa la contraseña\n";
        } else {
            db.setProperty("password", txtContrasenia.getText());
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