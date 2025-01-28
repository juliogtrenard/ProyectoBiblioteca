package es.juliogtrenard.proyectobiblioteca.controller;

import es.juliogtrenard.proyectobiblioteca.dao.DaoHistorialPrestamo;
import es.juliogtrenard.proyectobiblioteca.dao.DaoPrestamo;
import es.juliogtrenard.proyectobiblioteca.model.HistorialPrestamo;
import es.juliogtrenard.proyectobiblioteca.model.Prestamo;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

/**
 * Clase que controla la ventana de los historiales de prestamo nuevos
 */
public class NuevoHistorialPrestamoController implements Initializable {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(NuevoHistorialPrestamoController.class);

    /**
     * DatePicker
     */
    @FXML
    private DatePicker datePicker;

    /**
     * Hora
     */
    @FXML
    private Spinner<Integer> hora;

    /**
     * Lista de prestamos
     */
    @FXML
    private ListView<Prestamo> lista;

    /**
     * Minuto
     */
    @FXML
    private Spinner<Integer> minuto;

    /**
     * Recursos de la aplicación
     */
    private ResourceBundle resources;

    /**
     * Se ejecuta cuando se inicia la ventana
     *
     * @param url la url
     * @param resourceBundle los recursos
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resources = resourceBundle;
        ObservableList<Prestamo> prestamos = DaoPrestamo.cargarListado();
        lista.setItems(prestamos);
        datePicker.setValue(LocalDate.now());
        hora.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalTime.now().getHour()));
        minuto.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalTime.now().getMinute()));
    }

    /**
     * Cierra la ventana
     *
     * @param event evento del usuario
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }

    /**
     * Valida y guarda los datos del formulario
     *
     * @param event evento del usuario
     */
    @FXML
    void guardar(ActionEvent event) {
        String error = "";
        if (lista.getSelectionModel().getSelectedItem() == null) {
            error += resources.getString("validar.historial.prestamo") + "\n";
        }
        if (datePicker.getValue() == null) {
            error += resources.getString("validar.historial.fecha_devolucion") + "\n";
        }
        if (!error.isEmpty()) {
            mostrarAlerta(error);
        } else {
            HistorialPrestamo historialPrestamo = new HistorialPrestamo();
            Prestamo prestamo = lista.getSelectionModel().getSelectedItem();
            historialPrestamo.setId_prestamo(prestamo.getId_prestamo());
            historialPrestamo.setAlumno(prestamo.getAlumno());
            historialPrestamo.setLibro(prestamo.getLibro());
            historialPrestamo.setFecha_prestamo(prestamo.getFecha_prestamo());
            LocalDateTime fecha_devolucion = LocalDateTime.of(datePicker.getValue().getYear(),datePicker.getValue().getMonth(),datePicker.getValue().getDayOfMonth(),hora.getValue(),minuto.getValue());
            historialPrestamo.setFecha_devolucion(fecha_devolucion);
            if (DaoHistorialPrestamo.insertar(historialPrestamo)) {
                if (DaoPrestamo.eliminar(prestamo)) {
                    mostrarConfirmacion(resources.getString("guardar.historial"));
                    cancelar(null);
                } else {
                    mostrarAlerta(resources.getString("guardar.historial.fallo"));
                    cancelar(null);
                }
            } else {
                mostrarAlerta(resources.getString("guardar.fallo"));
            }
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
