package es.juliogtrenard.proyectobiblioteca.controller;

import es.juliogtrenard.proyectobiblioteca.dao.DaoLibro;
import es.juliogtrenard.proyectobiblioteca.model.Libro;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Clase controladora de la ventana libro
 */
public class LibroController implements Initializable {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(LibroController.class);

    /**
     * Libro
     */
    private Libro libro;

    /**
     * Blob portada
     */
    private Blob portada;

    /**
     * Boton borrar foto
     */
    @FXML
    private Button btnFotoBorrar;

    /**
     * Baja
     */
    @FXML
    private CheckBox cbBaja;

    /**
     * ComboBox de estado
     */
    @FXML
    private ComboBox<String> cbEstado;

    /**
     * Foto
     */
    @FXML
    private ImageView foto;

    /**
     * Autor
     */
    @FXML
    private TextField txtAutor;

    /**
     * Codigo
     */
    @FXML
    private Label lblCodigo;

    /**
     * Editorial
     */
    @FXML
    private TextField txtEditorial;

    /**
     * Titulo
     */
    @FXML
    private TextField txtTitulo;

    /**
     * Recursos de la aplicación
     */
    private ResourceBundle resources;

    /**
     * Constructor con parámetros para la consulta o edición de un libro
     *
     * @param libro a consultar o editar
     */
    public LibroController(Libro libro) {
        this.libro = libro;
    }

    /**
     * Constructor vacío para la creación de un libro
     */
    public LibroController() {
        this.libro = null;
    }

    /**
     * Se ejecuta cuando se inicia la ventana
     *
     * @param url la url
     * @param resourceBundle los recursos
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resources = resourceBundle;
        cbEstado.getItems().addAll(resources.getString("libro.estado.disponible"),resources.getString("libro.estado.prestado"));
        cbEstado.getSelectionModel().select(0);
        if (libro != null) {
            lblCodigo.setText(libro.getCodigo() + "");
            txtTitulo.setText(libro.getTitulo());
            txtAutor.setText(libro.getAutor());
            txtEditorial.setText(libro.getEditorial());
            if (libro.getEstado().equals("disponible")) {
                cbEstado.getSelectionModel().select(resources.getString("libro.estado.disponible"));
            } else {
                cbEstado.getSelectionModel().select(resources.getString("libro.estado.prestado"));
            }
            if (libro.getBaja() == 1) {
                cbBaja.setSelected(true);
            }
            if (libro.getPortada() != null) {
                portada = libro.getPortada();
                try {
                    InputStream imagen = libro.getPortada().getBinaryStream();
                    foto.setImage(new Image(imagen));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                btnFotoBorrar.setDisable(false);
            }
        } else {
            lblCodigo.setText("-");
        }
    }

    /**
     * Elimina la portada del libro
     *
     * @param event evento del usuario
     */
    @FXML
    void borrarFoto(ActionEvent event) {
        portada = null;
        foto.setImage(new Image(getClass().getResourceAsStream("/es/juliogtrenard/proyectobiblioteca/img/libro.png")));
        btnFotoBorrar.setDisable(true);
    }

    /**
     * Cierra la ventana
     *
     * @param event evento del usuario
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage)btnFotoBorrar.getScene().getWindow();
        stage.close();
    }

    /**
     * Valida y guarda los datos del libro
     *
     * @param event evento del usuario
     */
    @FXML
    void guardar(ActionEvent event) {
        String error = validar();
        if (!error.isEmpty()) {
            mostrarAlerta(error);
        } else {
            if (libro == null) {
                Libro libro1 = new Libro();
                libro1.setTitulo(txtTitulo.getText());
                libro1.setAutor(txtAutor.getText());
                libro1.setEditorial(txtEditorial.getText());
                if (cbEstado.getSelectionModel().getSelectedItem().equals(resources.getString("libro.estado.disponible"))) {
                    libro1.setEstado("disponible");
                } else {
                    libro1.setEstado("prestado");
                }
                libro1.setBaja(cbBaja.isSelected() ? 1 : 0);
                libro1.setPortada(portada);
                if (DaoLibro.insertar(libro1) != -1) {
                    mostrarConfirmacion(resources.getString("guardar.libro"));
                    cancelar(null);
                } else {
                    mostrarAlerta(resources.getString("guardar.fallo"));
                }
            } else {
                Libro libro1 = new Libro();
                libro1.setCodigo(libro.getCodigo());
                libro1.setTitulo(txtTitulo.getText());
                libro1.setAutor(txtAutor.getText());
                libro1.setEditorial(txtEditorial.getText());
                if (cbEstado.getSelectionModel().getSelectedItem().equals(resources.getString("libro.estado.disponible"))) {
                    libro1.setEstado("disponible");
                } else {
                    libro1.setEstado("prestado");
                }
                libro1.setBaja(cbBaja.isSelected() ? 1 : 0);
                libro1.setPortada(portada);
                if (DaoLibro.modificar(libro1)) {
                    mostrarConfirmacion(resources.getString("actualizar.libro"));
                    cancelar(null);
                } else {
                    mostrarAlerta(resources.getString("guardar.fallo"));
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
        if (txtTitulo.getText().isEmpty()) {
            error += resources.getString("validar.libro.titulo") + "\n";
        }
        if (txtAutor.getText().isEmpty()) {
            error += resources.getString("validar.libro.autor") + "\n";
        }
        if (txtEditorial.getText().isEmpty()) {
            error += resources.getString("validar.libro.editorial") + "\n";
        }
        return error;
    }

    /**
     * Abre un menú para seleccionar la portada del libro
     *
     * @param event evento del usuario
     */
    @FXML
    void seleccionImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resources.getString("libro.portada.selector"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files","*.jpg", "*.jpeg","*.png"));
        fileChooser.setInitialDirectory(new File("."));
        File file = fileChooser.showOpenDialog(null);
        try {
            InputStream imagen = new FileInputStream(file);
            Blob blob = DaoLibro.convertFileToBlob(file);
            this.portada = blob;
            foto.setImage(new Image(imagen));
            btnFotoBorrar.setDisable(false);
        } catch (IOException | NullPointerException e) {
            mostrarAlerta(resources.getString("libro.portada.selector.fallo"));
        }
    }

    /**
     * Muestra un mensaje de confirmación
     *
     * @param mensaje de confirmación a mostrar
     */
    public void mostrarConfirmacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setTitle(resources.getString("ventana.confirmar"));
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra un mensaje de alerta
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