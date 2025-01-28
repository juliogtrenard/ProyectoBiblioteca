package es.juliogtrenard.proyectobiblioteca.controller;

import es.juliogtrenard.proyectobiblioteca.dao.DaoAlumno;
import es.juliogtrenard.proyectobiblioteca.dao.DaoHistorialPrestamo;
import es.juliogtrenard.proyectobiblioteca.dao.DaoPrestamo;
import es.juliogtrenard.proyectobiblioteca.model.Alumno;
import es.juliogtrenard.proyectobiblioteca.model.HistorialPrestamo;
import es.juliogtrenard.proyectobiblioteca.model.Prestamo;
import es.juliogtrenard.proyectobiblioteca.utils.FechaFormatter;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Clase que controla la ventana de un alumno
 */
public class AlumnoController implements Initializable {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AlumnoController.class);

    /**
     * Alumno
     */
    private Alumno alumno;

    /**
     * Boton editar historial
     */
    @FXML
    private Button btnEditarHistorial;

    /**
     * Boton editar prestamo
     */
    @FXML
    private Button btnEditarPrestamo;

    /**
     * Panel
     */
    @FXML
    private TabPane tabPane;

    /**
     * Boton eliminar historial
     */
    @FXML
    private Button btnEliminarHistorial;

    /**
     * Boton eliminar prestamo
     */
    @FXML
    private Button btnEliminarPrestamo;

    /**
     * Tabla historial de préstamos
     */
    @FXML
    private TableView<HistorialPrestamo> tablaHistorial;

    /**
     * Tabla préstamos
     */
    @FXML
    private TableView<Prestamo> tablaPrestamos;

    /**
     * Apellidos 1
     */
    @FXML
    private TextField txtApellido1;

    /**
     * Apellidos 2
     */
    @FXML
    private TextField txtApellido2;

    /**
     * DNI
     */
    @FXML
    private TextField txtDni;

    /**
     * Nombre
     */
    @FXML
    private TextField txtNombre;

    /**
     * Recursos de la aplicación
     */
    private ResourceBundle resources;

    /**
     * Constructor con parámetros para la consulta o edición de un alumno
     *
     * @param alumno a consultar o editar
     */
    public AlumnoController(Alumno alumno) {
        this.alumno = alumno;
    }

    /**
     * Constructor vacío para la creación de un alumno
     */
    public AlumnoController() {
        this.alumno = null;
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
        if (alumno == null) {
            tabPane.setDisable(true);
        } else {
            txtDni.setText(alumno.getDni());
            txtDni.setDisable(true);
            txtNombre.setText(alumno.getNombre());
            txtApellido1.setText(alumno.getApellido1());
            txtApellido2.setText(alumno.getApellido2());
        }

        tablaPrestamos.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> deshabilitarBotonesPrestamos(newValue == null));
        tablaHistorial.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> deshabilitarBotonesHistorial(newValue == null));

        cargarPrestamos();
        cargarHistorialPrestamos();
    }

    /**
     * Abre un menú para añadir un nuevo préstamo
     *
     * @param event evento del usuario
     */
    @FXML
    void aniadirPrestamo(ActionEvent event) {
        try {
            Window ventana = tablaHistorial.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/prestamo.fxml"), resources);
            PrestamoController controlador = new PrestamoController();
            fxmlLoader.setController(controlador);
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMinWidth(350);
            stage.setMinHeight(350);
            stage.setTitle(resources.getString("ventana.aniadir") + " " + resources.getString("ventana.prestamo") + " - " + resources.getString("principal.titulo"));
            stage.initOwner(ventana);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarPrestamos();
        } catch (IOException e) {
            logger.error(e.getMessage());
            mostrarAlerta(resources.getString("mensaje.ventana.error"));
        }
    }

    /**
     * Abre un menú para añadir un nuevo préstamo
     *
     * @param event evento del usuario
     */
    @FXML
    void aniadirHistorial(ActionEvent event) {
        try {
            Window ventana = tablaHistorial.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/nuevoHistorialPrestamo.fxml"), resources);
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMinWidth(500);
            stage.setMinHeight(350);
            stage.setTitle(resources.getString("ventana.aniadir") + " " + resources.getString("ventana.historial") + " - " + resources.getString("principal.titulo"));
            stage.initOwner(ventana);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarPrestamos();
        } catch (IOException e) {
            logger.error(e.getMessage());
            mostrarAlerta(resources.getString("mensaje.ventana.error"));
        }
    }

    /**
     * Cierra la ventana
     *
     * @param event evento del usuario
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage)txtNombre.getScene().getWindow();
        stage.close();
    }

    /**
     * Abre un menú para editar el préstamo seleccionado
     *
     * @param event evento del usuario
     */
    @FXML
    void editarHistorial(ActionEvent event) {
        HistorialPrestamo historialPrestamo = tablaHistorial.getSelectionModel().getSelectedItem();
        if (historialPrestamo != null) {
            try {
                Window ventana = tablaHistorial.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/editarNuevoHistorialPrestamo.fxml"), resources);
                EditarHistorialPrestamoController controlador = new EditarHistorialPrestamoController(historialPrestamo);
                fxmlLoader.setController(controlador);
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setMinWidth(400);
                stage.setMinHeight(400);
                stage.setTitle(resources.getString("ventana.aniadir") + " " + resources.getString("ventana.historial") + " - " + resources.getString("principal.titulo"));
                stage.initOwner(ventana);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                cargarHistorialPrestamos();
            } catch (IOException e) {
                logger.error(e.getMessage());
                mostrarAlerta(resources.getString("mensaje.ventana.error"));
            }
        }
    }

    /**
     * Abre un menú para editar el préstamo seleccionado
     *
     * @param event evento del usuario
     */
    @FXML
    void editarPrestamo(ActionEvent event) {
        Prestamo prestamo = tablaPrestamos.getSelectionModel().getSelectedItem();
        if (prestamo != null) {
            try {
                Window ventana = tablaHistorial.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/prestamo.fxml"), resources);
                PrestamoController controlador = new PrestamoController(prestamo);
                fxmlLoader.setController(controlador);
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setMinWidth(350);
                stage.setMinHeight(350);
                stage.setTitle(resources.getString("ventana.aniadir") + " " + resources.getString("ventana.prestamo") + " - " + resources.getString("principal.titulo"));
                stage.initOwner(ventana);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                cargarPrestamos();
            } catch (IOException e) {
                logger.error(e.getMessage());
                mostrarAlerta(resources.getString("mensaje.ventana.error"));
            }
        }
    }

    /**
     * Elimina el préstamo seleccionado
     *
     * @param event evento del usuario
     */
    @FXML
    void eliminarHistorial(ActionEvent event) {
        HistorialPrestamo historialPrestamo = tablaHistorial.getSelectionModel().getSelectedItem();
        if (historialPrestamo != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(tablaHistorial.getScene().getWindow());
            alert.setHeaderText(null);
            alert.setTitle(resources.getString("ventana.confirmar"));
            alert.setContentText(resources.getString("eliminar.historial.prompt"));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (DaoHistorialPrestamo.eliminar(historialPrestamo)) {
                    cargarHistorialPrestamos();
                    mostrarConfirmacion(resources.getString("eliminar.historial.bien"));
                } else {
                    mostrarAlerta(resources.getString("eliminar.historial.fallo"));
                }
            }
        }
    }

    /**
     * Elimina el préstamo seleccionado
     *
     * @param event evento del usuario
     */
    @FXML
    void eliminarPrestamo(ActionEvent event) {
        Prestamo prestamo = tablaPrestamos.getSelectionModel().getSelectedItem();
        if (prestamo != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(tablaPrestamos.getScene().getWindow());
            alert.setHeaderText(null);
            alert.setTitle(resources.getString("ventana.confirmar"));
            alert.setContentText(resources.getString("eliminar.prestamo.prompt"));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (DaoPrestamo.eliminar(prestamo)) {
                    cargarPrestamos();
                    mostrarConfirmacion(resources.getString("eliminar.prestamo.bien"));
                } else {
                    mostrarAlerta(resources.getString("eliminar.prestamo.fallo"));
                }
            }
        }
    }

    /**
     * Valída y guarda el alumno
     *
     * @param event evento del usuario
     */
    @FXML
    void guardar(ActionEvent event) {
        String error = validar();
        if (!error.isEmpty()) {
            mostrarAlerta(error);
        } else {
            if (alumno == null) {
                Alumno alumno1 = new Alumno();
                alumno1.setDni(txtDni.getText());
                alumno1.setNombre(txtNombre.getText());
                alumno1.setApellido1(txtApellido1.getText());
                alumno1.setApellido2(txtApellido2.getText());
                if (DaoAlumno.getAlumno(txtDni.getText()) == null) {
                    if (DaoAlumno.insertar(alumno1)) {
                        mostrarConfirmacion(resources.getString("guardar.estudiante"));
                        cancelar(null);
                    } else {
                        mostrarAlerta(resources.getString("guardar.fallo"));
                    }
                } else {
                    mostrarAlerta(resources.getString("guardar.estudiante.fallo"));
                }
            } else {
                // Actualizar
                Alumno alumno1 = new Alumno();
                alumno1.setDni(alumno.getDni());
                alumno1.setNombre(txtNombre.getText());
                alumno1.setApellido1(txtApellido1.getText());
                alumno1.setApellido2(txtApellido2.getText());
                if (DaoAlumno.modificar(alumno1)) {
                    mostrarConfirmacion(resources.getString("actualizar.estudiante"));
                    cancelar(null);
                } else {
                    mostrarAlerta(resources.getString("guardar.fallo"));
                }
            }
        }
    }

    /**
     * Función que valida los datos del formulario y devuelve los posibles errores
     *
     * @return string con posibles errores
     */
    private String validar() {
        String error = "";
        if (txtDni.getText().isEmpty()) {
            error += resources.getString("validar.estudiante.dni") + "\n";
        }
        if (txtNombre.getText().isEmpty()) {
            error += resources.getString("validar.estudiante.nombre") + "\n";
        }
        if (txtApellido1.getText().isEmpty()) {
            error += resources.getString("validar.estudiante.apellido1") + "\n";
        }
        if (txtApellido2.getText().isEmpty()) {
            error += resources.getString("validar.estudiante.apellido2") + "\n";
        }
        return error;
    }

    /**
     * Función que carga en la tabla las columnas de préstamos y los préstamos
     */
    public void cargarPrestamos() {
        tablaPrestamos.getSelectionModel().clearSelection();
        tablaPrestamos.getItems().clear();
        tablaPrestamos.getColumns().clear();

        TableColumn<Prestamo, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id_prestamo"));
        TableColumn<Prestamo, String> colLibro = new TableColumn<>(resources.getString("tabla.prestamo.libro"));
        colLibro.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getLibro().getTitulo()));
        TableColumn<Prestamo, String> colFechaPrestamo = new TableColumn<>(resources.getString("tabla.prestamo.fecha_prestamo"));
        colFechaPrestamo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> FechaFormatter.formatearFecha(cellData.getValue().getFecha_prestamo())));
        tablaPrestamos.getColumns().addAll(colId,colLibro,colFechaPrestamo);

        if (alumno != null) {
            ObservableList<Prestamo> prestamos = DaoPrestamo.prestamosDeAlumno(alumno);
            tablaPrestamos.setItems(prestamos);
        }
    }

    /**
     * Función que carga en la tabla las columnas de préstamos y los préstamos
     */
    public void cargarHistorialPrestamos() {
        tablaHistorial.getSelectionModel().clearSelection();
        tablaHistorial.getItems().clear();
        tablaHistorial.getColumns().clear();

        TableColumn<HistorialPrestamo, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id_prestamo"));
        TableColumn<HistorialPrestamo, String> colLibro = new TableColumn<>(resources.getString("tabla.prestamo.libro"));
        colLibro.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getLibro().getTitulo()));
        TableColumn<HistorialPrestamo, String> colFechaPrestamo = new TableColumn<>(resources.getString("tabla.prestamo.fecha_prestamo"));
        colFechaPrestamo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> FechaFormatter.formatearFecha(cellData.getValue().getFecha_prestamo())));
        TableColumn<HistorialPrestamo, String> colFechaDevolucion = new TableColumn<>(resources.getString("tabla.historial.fecha_devolucion"));
        colFechaDevolucion.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> FechaFormatter.formatearFecha(cellData.getValue().getFecha_devolucion())));
        tablaHistorial.getColumns().addAll(colId,colLibro,colFechaPrestamo,colFechaDevolucion);

        if (alumno != null) {
            ObservableList<HistorialPrestamo> historialPrestamos = DaoHistorialPrestamo.historialDeAlumno(alumno);
            tablaHistorial.setItems(historialPrestamos);
        }
    }

    /**
     * Deshabilita o habilita los botones de edición
     *
     * @param deshabilitado los menus
     */
    private void deshabilitarBotonesPrestamos(boolean deshabilitado) {
        btnEditarPrestamo.setDisable(deshabilitado);
        btnEliminarPrestamo.setDisable(deshabilitado);
    }

    /**
     * Deshabilita o habilita los botones de edición
     *
     * @param deshabilitado los menus
     */
    private void deshabilitarBotonesHistorial(boolean deshabilitado) {
        btnEditarHistorial.setDisable(deshabilitado);
        btnEliminarHistorial.setDisable(deshabilitado);
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