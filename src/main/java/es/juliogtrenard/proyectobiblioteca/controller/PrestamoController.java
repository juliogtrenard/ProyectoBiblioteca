package es.juliogtrenard.proyectobiblioteca.controller;

import es.juliogtrenard.proyectobiblioteca.dao.DaoAlumno;
import es.juliogtrenard.proyectobiblioteca.dao.DaoLibro;
import es.juliogtrenard.proyectobiblioteca.dao.DaoPrestamo;
import es.juliogtrenard.proyectobiblioteca.model.Alumno;
import es.juliogtrenard.proyectobiblioteca.model.Libro;
import es.juliogtrenard.proyectobiblioteca.model.Prestamo;
import es.juliogtrenard.proyectobiblioteca.utils.FechaFormatter;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Clase que controla la ventana de préstamos
 */
public class PrestamoController implements Initializable {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(PrestamoController.class);

    /**
     * Préstamo
     */
    private Prestamo prestamo;

    /**
     * ComboBox de alumno
     */
    @FXML
    private ComboBox<Alumno> cbAlumno;

    /**
     * ComboBox de libro
     */
    @FXML
    private ComboBox<Libro> cbLibro;

    /**
     * DatePicker
     */
    @FXML
    private DatePicker datePicker;

    /**
     * Label de ID
     */
    @FXML
    private Label lblId;

    /**
     * Boton de informe
     */
    @FXML
    private Button btnInforme;

    /**
     * Spinner de hora
     */
    @FXML
    private Spinner<Integer> hora;

    /**
     * Spinner de minuto
     */
    @FXML
    private Spinner<Integer> minuto;

    /**
     * Recursos de la aplicación
     */
    private ResourceBundle resources;

    /**
     * Constructor con parámetros para la consulta o edición de un préstamo
     *
     * @param prestamo a consultar o editar
     */
    public PrestamoController(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    /**
     * Constructor vacío
     */
    public PrestamoController() {
        this.prestamo = null;
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

        ObservableList<Alumno> alumnos = DaoAlumno.cargarListado();
        cbAlumno.getItems().add(new Alumno(null,resources.getString("prestamo.estudiante.seleccionar"),null,null));
        cbAlumno.getItems().addAll(alumnos);
        cbAlumno.getSelectionModel().select(0);
        ObservableList<Libro> libros = DaoLibro.cargarListado();
        cbLibro.getItems().add(new Libro(0,resources.getString("prestamo.libro.seleccionar"),null,null,null,0,null));
        cbLibro.getItems().addAll(libros);
        cbLibro.getSelectionModel().select(0);

        if (prestamo != null) {
            lblId.setText(prestamo.getId_prestamo() + "");
            cbAlumno.getSelectionModel().select(prestamo.getAlumno());
            cbLibro.getSelectionModel().select(prestamo.getLibro());
            datePicker.setValue(prestamo.getFecha_prestamo().toLocalDate());
            hora.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, prestamo.getFecha_prestamo().getHour()));
            minuto.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, prestamo.getFecha_prestamo().getMinute()));
            btnInforme.setDisable(false);
        } else {
            datePicker.setValue(LocalDate.now());
            hora.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalTime.now().getHour()));
            minuto.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalTime.now().getMinute()));
        }
    }

    /**
     * Cierra la ventana
     *
     * @param event evento del usuario
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage) cbAlumno.getScene().getWindow();
        stage.close();
    }

    /**
     * Valida y guarda los datos del formulario
     *
     * @param event evento del usuario
     */
    @FXML
    void generarInforme(ActionEvent event) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("id", prestamo.getId_prestamo());
        parameters.put("nombre", prestamo.getAlumno().getNombre());
        parameters.put("apellidos", prestamo.getAlumno().getApellido1() + " " + prestamo.getAlumno().getApellido2());
        parameters.put("dni", prestamo.getAlumno().getDni());
        parameters.put("titulo", prestamo.getLibro().getTitulo());
        parameters.put("codigo", prestamo.getLibro().getCodigo());
        parameters.put("autor", prestamo.getLibro().getAutor());
        parameters.put("editorial", prestamo.getLibro().getEditorial());
        parameters.put("estado", prestamo.getLibro().getEstado());
        parameters.put("fecha", FechaFormatter.formatearFecha(prestamo.getFecha_prestamo()));
        parameters.put("fecha_limite", FechaFormatter.formatearFecha(prestamo.getFecha_prestamo().plusDays(15)));
        try {
            JasperReport report = (JasperReport) JRLoader.loadObject(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/reports/InformeAltaPrestamo.jasper"));
            JasperPrint jprint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
            JasperViewer viewer = new JasperViewer(jprint, false);
            viewer.setVisible(true);
        } catch (JRException e) {
            logger.error(e.getMessage());
            mostrarAlerta(resources.getString("report.cargar.error"));
        }
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
            if (prestamo == null) {
                Prestamo prestamo1 = new Prestamo();
                prestamo1.setAlumno(cbAlumno.getSelectionModel().getSelectedItem());
                prestamo1.setLibro(cbLibro.getSelectionModel().getSelectedItem());
                LocalDateTime fecha_prestamo = LocalDateTime.of(datePicker.getValue().getYear(),datePicker.getValue().getMonth(),datePicker.getValue().getDayOfMonth(),hora.getValue(),minuto.getValue());
                prestamo1.setFecha_prestamo(fecha_prestamo);
                if (DaoPrestamo.insertar(prestamo1) != -1) {
                    mostrarConfirmacion(resources.getString("guardar.prestamo"));
                    cancelar(null);
                } else {
                    mostrarAlerta(resources.getString("guardar.error"));
                }
            } else {
                Prestamo prestamo1 = new Prestamo();
                prestamo1.setId_prestamo(prestamo.getId_prestamo());
                prestamo1.setAlumno(cbAlumno.getSelectionModel().getSelectedItem());
                prestamo1.setLibro(cbLibro.getSelectionModel().getSelectedItem());
                LocalDateTime fecha_prestamo = LocalDateTime.of(datePicker.getValue().getYear(),datePicker.getValue().getMonth(),datePicker.getValue().getDayOfMonth(),hora.getValue(),minuto.getValue());
                prestamo1.setFecha_prestamo(fecha_prestamo);
                if (DaoPrestamo.modificar(prestamo1)) {
                    mostrarConfirmacion(resources.getString("actualizar.prestamo"));
                    cancelar(null);
                } else {
                    mostrarAlerta(resources.getString("guardar.error"));
                }
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
        if (cbAlumno.getSelectionModel().getSelectedItem().getNombre().equals(resources.getString("prestamo.estudiante.seleccionar"))) {
            error += resources.getString("validar.prestamo.estudiante") + "\n";
        }
        if (cbLibro.getSelectionModel().getSelectedItem().getTitulo().equals(resources.getString("prestamo.libro.seleccionar"))) {
            error += resources.getString("validar.prestamo.libro") + "\n";
        }
        if (datePicker.getValue() == null) {
            error += resources.getString("validar.prestamo.fecha_prestamo") + "\n";
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