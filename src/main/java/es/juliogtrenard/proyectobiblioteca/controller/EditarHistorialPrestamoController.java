package es.juliogtrenard.proyectobiblioteca.controller;

import es.juliogtrenard.proyectobiblioteca.dao.DaoHistorialPrestamo;
import es.juliogtrenard.proyectobiblioteca.model.HistorialPrestamo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Clase que controla la ventana para editar el historial de prestamo
 */
public class EditarHistorialPrestamoController implements Initializable {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(EditarHistorialPrestamoController.class);

    /**
     * Historial de préstamo
     */
    private HistorialPrestamo historialPrestamo;

    /**
     * DatePicker historial
     */
    @FXML
    private DatePicker datePickerHistorial;

    /**
     * DatePicker prestamo
     */
    @FXML
    private DatePicker datePickerPrestamo;

    /**
     * Hora del historial
     */
    @FXML
    private Spinner<Integer> horaHistorial;

    /**
     * Hora del prestamo
     */
    @FXML
    private Spinner<Integer> horaPrestamo;

    /**
     * Alumno
     */
    @FXML
    private Label lblAlumno;

    /**
     * ID
     */
    @FXML
    private Label lblId;

    /**
     * Libro
     */
    @FXML
    private Label lblLibro;

    /**
     * Minuto historial
     */
    @FXML
    private Spinner<Integer> minutoHistorial;

    /**
     * Minuto prestamo
     */
    @FXML
    private Spinner<Integer> minutoPrestamo;

    /**
     * Recursos de la aplicación
     */
    private ResourceBundle resources;

    /**
     * Constructor con parámetros para la consulta o edición de un historial préstamo
     *
     * @param historialPrestamo a consultar o editar
     */
    public EditarHistorialPrestamoController(HistorialPrestamo historialPrestamo) {
        this.historialPrestamo = historialPrestamo;
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
        if (historialPrestamo == null) {
            mostrarAlerta(resources.getString("historial.error"));
            cancelar(null);
        } else {
            lblId.setText(historialPrestamo.getId_prestamo() + "");
            lblAlumno.setText(historialPrestamo.getAlumno().toString());
            lblLibro.setText(historialPrestamo.getLibro().toString());
            datePickerPrestamo.setValue(historialPrestamo.getFecha_prestamo().toLocalDate());
            horaPrestamo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, historialPrestamo.getFecha_prestamo().getHour()));
            minutoPrestamo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, historialPrestamo.getFecha_prestamo().getMinute()));
            datePickerHistorial.setValue(historialPrestamo.getFecha_devolucion().toLocalDate());
            horaHistorial.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, historialPrestamo.getFecha_devolucion().getHour()));
            minutoHistorial.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, historialPrestamo.getFecha_devolucion().getMinute()));
        }
    }

    /**
     * Cierra la ventana
     *
     * @param event evento del usuario
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage) lblId.getScene().getWindow();
        stage.close();
    }

    /**
     * Valida y guarda los datos del formulario
     *
     * @param event evento del usuario
     */
    @FXML
    void guardar(ActionEvent event) {
        String error = validar();
        if (!error.isEmpty()) {
            mostrarAlerta(error);
        } else {
            HistorialPrestamo historialPrestamo1 = new HistorialPrestamo();
            historialPrestamo1.setId_prestamo(historialPrestamo.getId_prestamo());
            historialPrestamo1.setAlumno(historialPrestamo.getAlumno());
            historialPrestamo1.setLibro(historialPrestamo.getLibro());
            LocalDateTime fecha_prestamo = LocalDateTime.of(datePickerPrestamo.getValue().getYear(),datePickerPrestamo.getValue().getMonth(),datePickerPrestamo.getValue().getDayOfMonth(),horaPrestamo.getValue(),minutoPrestamo.getValue());
            historialPrestamo1.setFecha_devolucion(fecha_prestamo);
            LocalDateTime fecha_devolucion = LocalDateTime.of(datePickerHistorial.getValue().getYear(),datePickerHistorial.getValue().getMonth(),datePickerHistorial.getValue().getDayOfMonth(),horaHistorial.getValue(),minutoHistorial.getValue());
            historialPrestamo1.setFecha_devolucion(fecha_devolucion);
            if (DaoHistorialPrestamo.modificar(historialPrestamo1)) {
                mostrarConfirmacion(resources.getString("actualizar.historial"));
                cancelar(null);
            } else {
                mostrarAlerta(resources.getString("guardar.fallo"));
            }
        }
    }

    /**
     * Valida el formulario y devuelve un string con posibles errores
     *
     * @return string con posibles errores
     */
    private String validar() {
        String error = "";
        if (datePickerPrestamo.getValue() == null) {
            error += resources.getString("validar.prestamo.fecha_prestamo") + "\n";
        }
        if (datePickerHistorial.getValue() == null) {
            error += resources.getString("validar.prestamo.fecha_devolucion") + "\n";
        }
        return error;
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
