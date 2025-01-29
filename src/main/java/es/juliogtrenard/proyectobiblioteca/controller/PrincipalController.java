package es.juliogtrenard.proyectobiblioteca.controller;

import es.juliogtrenard.proyectobiblioteca.dao.DaoAlumno;
import es.juliogtrenard.proyectobiblioteca.dao.DaoHistorialPrestamo;
import es.juliogtrenard.proyectobiblioteca.dao.DaoLibro;
import es.juliogtrenard.proyectobiblioteca.dao.DaoPrestamo;
import es.juliogtrenard.proyectobiblioteca.db.DBConnect;
import es.juliogtrenard.proyectobiblioteca.language.LanguageSwitcher;
import es.juliogtrenard.proyectobiblioteca.model.Alumno;
import es.juliogtrenard.proyectobiblioteca.model.HistorialPrestamo;
import es.juliogtrenard.proyectobiblioteca.model.Libro;
import es.juliogtrenard.proyectobiblioteca.model.Prestamo;
import es.juliogtrenard.proyectobiblioteca.utils.FechaFormatter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Clase que controla la ventana principal de la aplicacion
 */
public class PrincipalController implements Initializable {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(PrincipalController.class);

    /**
     * Menu añadir
     */
    @FXML
    private MenuItem miAniadir;

    /**
     * Boton de editar
     */
    @FXML
    private MenuItem btnEditar;

    /**
     * Boton de eliminar
     */
    @FXML
    private MenuItem btnEliminar;

    /**
     * ComboBox de tabla
     */
    @FXML
    private ComboBox<String> cbTabla;

    /**
     * Filtro
     */
    @FXML
    private Tooltip filterTooltip;

    /**
     * ComboBox filtro
     */
    @FXML
    private ComboBox<String> cbFiltro;

    /**
     * Texto filtro
     */
    @FXML
    private TextField txtFiltro;

    /**
     * Idioma ingles
     */
    @FXML
    private RadioMenuItem langEN;

    /**
     * Idioma español
     */
    @FXML
    private RadioMenuItem langES;

    /**
     * Tabla
     */
    @FXML
    private TableView tabla;

    /**
     * Toggle idioma
     */
    @FXML
    private ToggleGroup tgIdioma;

    @FXML
    private ResourceBundle resources;

    private ObservableList masterData = FXCollections.observableArrayList();
    private ObservableList filteredData = FXCollections.observableArrayList();

    /**
     * Se ejecuta cuando se inicia la ventana
     *
     * @param location  location
     * @param resources recursos
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        try {
            DBConnect db = new DBConnect();
            if (db.getConnection() == null) {
                mostrarAlerta(resources.getString("db.archivo.error"));
                Platform.exit();
                return;
            }
        } catch (SQLException e) {
            mostrarAlerta(resources.getString("db.error"));
            Platform.exit();
            return;
        }

        if (resources.getLocale().equals(Locale.of("es"))) {
            langES.setSelected(true);
        } else {
            langEN.setSelected(true);
        }

        tgIdioma.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
            Locale locale;
            if (langES.isSelected()) {
                locale = Locale.of("es");
            } else {
                locale = Locale.of("en");
            }
            new LanguageSwitcher((Stage) tabla.getScene().getWindow()).switchLanguage(locale);
        });

        cbTabla.getItems().addAll(resources.getString("cb.estudiantes"), resources.getString("cb.libros"), resources.getString("cb.prestamos"), resources.getString("cb.historial"));
        cbTabla.setValue(resources.getString("cb.estudiantes"));
        cbTabla.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.equals(resources.getString("cb.estudiantes"))) {
                cargarAlumnos();
            } else if (newValue.equals(resources.getString("cb.libros"))) {
                cargarLibros();
            } else if (newValue.equals(resources.getString("cb.prestamos"))) {
                cargarPrestamos();
            } else {
                cargarHistorialPrestamos();
            }
        });

        cbFiltro.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            cambiarFiltroTooltip();
        });

        tabla.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            deshabilitarMenus(newValue == null);
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem editarItem = new MenuItem(resources.getString("contextmenu.editar"));
        MenuItem borrarItem = new MenuItem(resources.getString("contextmenu.eliminar"));
        contextMenu.getItems().addAll(editarItem, borrarItem);
        editarItem.setOnAction(this::editar);
        borrarItem.setOnAction(this::eliminar);
        tabla.setRowFactory(tv -> {
            TableRow<Object> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    tabla.getSelectionModel().select(row.getItem());
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });

        txtFiltro.setOnKeyTyped(keyEvent -> filtrar());

        tabla.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                editar(null);
            }
        });

        cargarAlumnos();
    }

    /**
     * Filtra la tabla
     */
    public void filtrar() {
        String valor = txtFiltro.getText();

        if (valor != null) {
            valor = valor.toLowerCase();
            String item = cbTabla.getSelectionModel().getSelectedItem();

            if (item.equals(resources.getString("cb.estudiantes"))) {
                if (valor.isEmpty()) {
                    tabla.setItems(masterData);
                } else {
                    filteredData.clear();
                    for (Object obj : masterData) {
                        Alumno alumno = (Alumno) obj;
                        String nombre = alumno.getNombre().toLowerCase();
                        if (nombre.contains(valor)) {
                            filteredData.add(alumno);
                        }
                    }
                    tabla.setItems(filteredData);
                }
            } else if (item.equals(resources.getString("cb.libros"))) {
                if (valor.isEmpty()) {
                    tabla.setItems(masterData);
                } else {
                    filteredData.clear();
                    for (Object obj : masterData) {
                        Libro libro = (Libro) obj;
                        if (cbFiltro.getSelectionModel().getSelectedItem().equals(resources.getString("principal.label.filtro.titulo"))) {
                            String titulo = libro.getTitulo().toLowerCase();
                            if (titulo.contains(valor)) {
                                filteredData.add(libro);
                            }
                        } else {
                            String autor = libro.getAutor().toLowerCase();
                            if (autor.contains(valor)) {
                                filteredData.add(libro);
                            }
                        }
                    }
                    tabla.setItems(filteredData);
                }
            } else if (item.equals(resources.getString("cb.prestamos"))) {
                if (valor.isEmpty()) {
                    tabla.setItems(masterData);
                } else {
                    filteredData.clear();
                    for (Object obj : masterData) {
                        Prestamo prestamo = (Prestamo) obj;
                        if (cbFiltro.getSelectionModel().getSelectedItem().equals(resources.getString("principal.label.filtro.estudiante"))) {
                            String nombre = prestamo.getAlumno().getNombre().toLowerCase();
                            if (nombre.contains(valor)) {
                                filteredData.add(prestamo);
                            }
                        } else if (cbFiltro.getSelectionModel().getSelectedItem().equals(resources.getString("principal.label.filtro.libro_titulo"))) {
                            String titulo = prestamo.getLibro().getTitulo().toLowerCase();
                            if (titulo.contains(valor)) {
                                filteredData.add(prestamo);
                            }
                        } else {
                            String autor = prestamo.getLibro().getAutor().toLowerCase();
                            if (autor.contains(valor)) {
                                filteredData.add(prestamo);
                            }
                        }
                    }
                    tabla.setItems(filteredData);
                }
            } else {
                if (valor.isEmpty()) {
                    tabla.setItems(masterData);
                } else {
                    filteredData.clear();
                    for (Object obj : masterData) {
                        HistorialPrestamo historialPrestamo = (HistorialPrestamo) obj;
                        if (cbFiltro.getSelectionModel().getSelectedItem().equals(resources.getString("principal.label.filtro.estudiante"))) {
                            String nombre = historialPrestamo.getAlumno().getNombre().toLowerCase();
                            if (nombre.contains(valor)) {
                                filteredData.add(historialPrestamo);
                            }
                        } else if (cbFiltro.getSelectionModel().getSelectedItem().equals(resources.getString("principal.label.filtro.libro_titulo"))) {
                            String titulo = historialPrestamo.getLibro().getTitulo().toLowerCase();
                            if (titulo.contains(valor)) {
                                filteredData.add(historialPrestamo);
                            }
                        } else {
                            String autor = historialPrestamo.getLibro().getAutor().toLowerCase();
                            if (autor.contains(valor)) {
                                filteredData.add(historialPrestamo);
                            }
                        }
                    }
                    tabla.setItems(filteredData);
                }
            }
        }
    }

    /**
     * Abre una ventana para añadir un nuevo item
     *
     * @param event evento del usuario
     */
    @FXML
    void aniadir(ActionEvent event) {
        String item = cbTabla.getSelectionModel().getSelectedItem();
        if (item.equals(resources.getString("cb.estudiantes"))) {
            try {
                Window ventana = tabla.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/alumno.fxml"),resources);
                AlumnoController controlador = new AlumnoController();
                fxmlLoader.setController(controlador);
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setMinWidth(600);
                stage.setMinHeight(450);
                stage.setTitle(resources.getString("ventana.aniadir") + " " + resources.getString("ventana.estudiante") + " - " + resources.getString("principal.titulo"));
                stage.initOwner(ventana);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                cargarAlumnos();
            } catch (IOException e) {
                logger.error(e.getMessage());
                mostrarAlerta(resources.getString("mensaje.ventana.error"));
            }
        } else if (item.equals(resources.getString("cb.libros"))) {
            try {
                Window ventana = tabla.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/libro.fxml"),resources);
                LibroController controlador = new LibroController();
                fxmlLoader.setController(controlador);
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setMinWidth(400);
                stage.setMinHeight(425);
                stage.setTitle(resources.getString("ventana.aniadir") + " " + resources.getString("ventana.libro") + " - " + resources.getString("principal.titulo"));
                stage.initOwner(ventana);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                cargarLibros();
            } catch (IOException e) {
                logger.error(e.getMessage());
                mostrarAlerta(resources.getString("mensaje.ventana.error"));
            }
        } else if (item.equals(resources.getString("cb.prestamos"))) {
            try {
                Window ventana = tabla.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/prestamo.fxml"),resources);
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
        } else {
            try {
                Window ventana = tabla.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/nuevoHistorialPrestamo.fxml"),resources);
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setMinWidth(500);
                stage.setMinHeight(350);
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
     * Abre una ventana para editar un item
     *
     * @param event evento del usuario
     */
    @FXML
    void editar(ActionEvent event) {
        Object seleccion = tabla.getSelectionModel().getSelectedItem();
        if (seleccion != null) {
            String item = cbTabla.getSelectionModel().getSelectedItem();
            if (item.equals(resources.getString("cb.estudiantes"))) {
                Alumno alumno = (Alumno) seleccion;
                try {
                    Window ventana = tabla.getScene().getWindow();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/alumno.fxml"),resources);
                    AlumnoController controlador = new AlumnoController(alumno);
                    fxmlLoader.setController(controlador);
                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setMinWidth(600);
                    stage.setMinHeight(450);
                    stage.setTitle(resources.getString("ventana.editar") + " " + resources.getString("ventana.estudiante") + " - " + resources.getString("principal.titulo"));
                    stage.initOwner(ventana);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                    cargarAlumnos();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    mostrarAlerta(resources.getString("mensaje.ventana.error"));
                }
            } else if (item.equals(resources.getString("cb.libros"))) {
                Libro libro = (Libro) seleccion;
                try {
                    Window ventana = tabla.getScene().getWindow();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/libro.fxml"),resources);
                    LibroController controlador = new LibroController(libro);
                    fxmlLoader.setController(controlador);
                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setMinWidth(400);
                    stage.setMinHeight(425);
                    stage.setTitle(resources.getString("ventana.editar") + " " + resources.getString("ventana.libro") + " - " + resources.getString("principal.titulo"));
                    stage.initOwner(ventana);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                    cargarLibros();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    mostrarAlerta(resources.getString("mensaje.ventana.error"));
                }
            } else if (item.equals(resources.getString("cb.prestamos"))) {
                // Préstamo
                Prestamo prestamo = (Prestamo) seleccion;
                try {
                    Window ventana = tabla.getScene().getWindow();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/prestamo.fxml"),resources);
                    PrestamoController controlador = new PrestamoController(prestamo);
                    fxmlLoader.setController(controlador);
                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setMinWidth(350);
                    stage.setMinHeight(350);
                    stage.setTitle(resources.getString("ventana.editar") + " " + resources.getString("ventana.prestamo") + " - " + resources.getString("principal.titulo"));
                    stage.initOwner(ventana);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                    cargarPrestamos();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    mostrarAlerta(resources.getString("mensaje.ventana.error"));
                }
            } else {
                // Historial
                HistorialPrestamo historialPrestamo = (HistorialPrestamo) seleccion;
                try {
                    Window ventana = tabla.getScene().getWindow();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/editarHistorialPrestamo.fxml"),resources);
                    EditarHistorialPrestamoController controlador = new EditarHistorialPrestamoController(historialPrestamo);
                    fxmlLoader.setController(controlador);
                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setMinWidth(400);
                    stage.setMinHeight(400);
                    stage.setTitle(resources.getString("ventana.editar") + " " + resources.getString("ventana.historial") + " - " + resources.getString("principal.titulo"));
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
    }

    /**
     * Elimina el item seleccionado
     *
     * @param event evento del usuario
     */
    @FXML
    void eliminar(ActionEvent event) {
        Object seleccion = tabla.getSelectionModel().getSelectedItem();

        if (seleccion != null) {
            String item = cbTabla.getSelectionModel().getSelectedItem();
            if (item.equals(resources.getString("cb.estudiantes"))) {
                Alumno alumno = (Alumno) seleccion;

                if (DaoAlumno.esEliminable(alumno)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.initOwner(tabla.getScene().getWindow());
                    alert.setHeaderText(null);
                    alert.setTitle(resources.getString("ventana.confirmar"));
                    alert.setContentText(resources.getString("eliminar.estudiante.prompt"));
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        if (DaoAlumno.eliminar(alumno)) {
                            cargarAlumnos();
                            mostrarConfirmacion(resources.getString("eliminar.estudiante.bien"));
                        } else {
                            mostrarAlerta(resources.getString("eliminar.estudiante.fallo"));
                        }
                    }
                } else {
                    mostrarAlerta(resources.getString("eliminar.estudiante.error"));
                }
            } else if (item.equals(resources.getString("cb.libros"))) {
                Libro libro = (Libro) seleccion;
                if (DaoLibro.esEliminable(libro)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.initOwner(tabla.getScene().getWindow());
                    alert.setHeaderText(null);
                    alert.setTitle(resources.getString("ventana.confirmar"));
                    alert.setContentText(resources.getString("eliminar.libro.prompt"));
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        if (DaoLibro.darDeBaja(libro)) {
                            cargarLibros();
                            mostrarConfirmacion(resources.getString("eliminar.libro.bien"));
                        } else {
                            mostrarAlerta(resources.getString("eliminar.libro.fallo"));
                        }
                    }
                } else {
                    mostrarAlerta(resources.getString("eliminar.libro.error"));
                }
            } else if (item.equals(resources.getString("cb.prestamos"))) {
                Prestamo prestamo = (Prestamo) seleccion;
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(tabla.getScene().getWindow());
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
            } else {
                HistorialPrestamo historialPrestamo = (HistorialPrestamo) seleccion;
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(tabla.getScene().getWindow());
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
    }

    /**
     * Abre la guía rápida
     *
     * @param event evento del usuario
     */
    @FXML
    void ayuda(ActionEvent event) {
        try {
            Window ventana = tabla.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/guiahtml.fxml"),resources);
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setTitle(resources.getString("ayuda.html") + " - " + resources.getString("principal.titulo"));
            stage.initOwner(ventana);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            logger.error(e.getMessage());
            mostrarAlerta(resources.getString("mensaje.ventana.error"));
        }
    }

    /**
     * Muestra el informe de Jasper Reports pasado
     *
     * @param informe a mostrar
     */
    private void mostrarInforme(String informe) {
        DBConnect connection;
        try {
            connection = new DBConnect();
            JasperReport report = (JasperReport) JRLoader.loadObject(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/reports/" + informe + ".jasper"));
            JasperPrint jprint = JasperFillManager.fillReport(report, null, connection.getConnection());
            JasperViewer viewer = new JasperViewer(jprint, false);
            viewer.setVisible(true);
        } catch (JRException e) {
            logger.error(e.getMessage());
            mostrarAlerta(resources.getString("report.cargar.error"));
        } catch (SQLException e) {
            logger.error(e.getMessage());
            mostrarAlerta(resources.getString("report.cargar.error.db"));
        }
    }

    /**
     * Abre el informe de alumnos
     *
     * @param event evento del usuario
     */
    @FXML
    void informeAlumnos(ActionEvent event) {
        mostrarInforme("InformeAlumnos");
    }

    /**
     * Abre el informe de libros
     *
     * @param event evento del usuario
     */
    @FXML
    void informeLibros(ActionEvent event) {
        DBConnect connection;
        try {
            connection = new DBConnect();
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("informePrestamos", JasperCompileManager.compileReport(getClass().getResourceAsStream("/es/juliogtrenard/proyectobiblioteca/reports/SubinformePrestamos.jrxml")));
            JasperReport report = (JasperReport) JRLoader.loadObject(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/reports/InformeLibros.jasper"));
            JasperPrint jprint = JasperFillManager.fillReport(report, parameters, connection.getConnection());
            JasperViewer viewer = new JasperViewer(jprint, false);
            viewer.setVisible(true);
        } catch (JRException | SQLException e) {
            logger.error(e.getMessage());
            mostrarAlerta(resources.getString("report.cargar.error"));
        }
    }

    /**
     * Abre el informe de gráficos
     *
     * @param event evento del usuario
     */
    @FXML
    void informeGraficos(ActionEvent event) {
        DBConnect connection;
        try {
            connection = new DBConnect();
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("SubinformeGraficos1", JasperCompileManager.compileReport(getClass().getResourceAsStream("/es/juliogtrenard/proyectobiblioteca/reports/SubinformeGraficos1.jrxml")));
            parameters.put("SubinformeGraficos2", JasperCompileManager.compileReport(getClass().getResourceAsStream("/es/juliogtrenard/proyectobiblioteca/reports/SubinformeGraficos2.jrxml")));
            parameters.put("SubinformeGraficos3", JasperCompileManager.compileReport(getClass().getResourceAsStream("/es/juliogtrenard/proyectobiblioteca/reports/SubinformeGraficos3.jrxml")));
            JasperReport report = (JasperReport) JRLoader.loadObject(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/reports/InformeGraficos.jasper"));
            JasperPrint jprint = JasperFillManager.fillReport(report, parameters, connection.getConnection());
            JasperViewer viewer = new JasperViewer(jprint, false);
            viewer.setVisible(true);
        } catch (JRException | SQLException e) {
            logger.error(e.getMessage());
            mostrarAlerta(resources.getString("report.cargar.error"));
        }
    }

    /**
     * Cierra la aplicación
     *
     * @param event evento del usuario
     */
    @FXML
    void cerrar(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Resetea la aplicación
     *
     * @param event evento del usuario
     */
    @FXML
    void resetear(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(tabla.getScene().getWindow());
        alert.setHeaderText(null);
        alert.setTitle(resources.getString("ventana.confirmar"));
        alert.setContentText(resources.getString("app.resetear.prompt"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            File f1 = new File("lang.properties");
            File f2 = new File("configuration.properties");
            if (f1.exists() && f2.exists()) {
                if (f1.delete() && f2.delete()) {
                    mostrarConfirmacion(resources.getString("app.resetear.bien"));
                    cerrar(null);
                } else {
                    mostrarAlerta(resources.getString("app.resetear.error"));
                }
            } else {
                mostrarAlerta(resources.getString("app.resetear.error"));
            }
        }
    }

    /**
     * Deshabilita o habilita los menus de edición
     *
     * @param deshabilitado los menus
     */
    private void deshabilitarMenus(boolean deshabilitado) {
        btnEditar.setDisable(deshabilitado);
        btnEliminar.setDisable(deshabilitado);
    }

    /**
     * Cambia el texto del tooltip del filtro
     */
    private void cambiarFiltroTooltip() {
        String object = cbTabla.getSelectionModel().getSelectedItem();
        String filter = cbFiltro.getSelectionModel().getSelectedItem();
        if (object != null && filter != null) {
            filterTooltip.setText(resources.getString("principal.tooltip.filtro1") + " " + object.toLowerCase() + " " + resources.getString("principal.tooltip.filtro2") + " " + filter.toLowerCase());
        }
    }

    /**
     * Cambia los textos de los items de edición del menú
     *
     * @param text texto a cambiar
     */
    private void editarMenuItemText(String text) {
        miAniadir.setText(resources.getString("principal.menu.archivo.aniadir") + " " + text.toLowerCase());
        btnEditar.setText(resources.getString("principal.menu.edicion.editar") + " " + text.toLowerCase());
        if (text.equals(resources.getString("ventana.libro"))) {
            btnEliminar.setText(resources.getString("principal.menu.edicion.baja"));
        } else {
            btnEliminar.setText(resources.getString("principal.menu.edicion.borrar") + " " + text.toLowerCase());
        }
    }

    /**
     * Carga en la tabla las columnas de alumnos y los alumnos
     */
    public void cargarAlumnos() {
        tabla.getSelectionModel().clearSelection();
        txtFiltro.setText(null);
        cbFiltro.getItems().clear();
        cbFiltro.getItems().addAll(resources.getString("principal.label.filtro.nombre"));
        cbFiltro.getSelectionModel().select(0);
        masterData.clear();
        filteredData.clear();
        tabla.getItems().clear();
        tabla.getColumns().clear();
        editarMenuItemText(resources.getString("ventana.estudiante"));

        TableColumn<Alumno, String> colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        TableColumn<Alumno, String> colNombre = new TableColumn<>(resources.getString("tabla.estudiante.nombre"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        TableColumn<Alumno, String> colApellido1 = new TableColumn<>(resources.getString("tabla.estudiante.apellido1"));
        colApellido1.setCellValueFactory(new PropertyValueFactory<>("apellido1"));
        TableColumn<Alumno, String> colApellido2 = new TableColumn<>(resources.getString("tabla.estudiante.apellido2"));
        colApellido2.setCellValueFactory(new PropertyValueFactory<>("apellido2"));
        tabla.getColumns().addAll(colDni,colNombre,colApellido1,colApellido2);

        ObservableList<Alumno> alumnos = DaoAlumno.cargarListado();
        masterData.setAll(alumnos);
        tabla.setItems(alumnos);
    }

    /**
     * Carga en la tabla las columnas de libros y los libros
     */
    public void cargarLibros() {
        tabla.getSelectionModel().clearSelection();
        txtFiltro.setText(null);
        cbFiltro.getItems().clear();
        cbFiltro.getItems().addAll(resources.getString("principal.label.filtro.titulo"),resources.getString("principal.label.filtro.autor"));
        cbFiltro.getSelectionModel().select(0);
        masterData.clear();
        filteredData.clear();
        tabla.getItems().clear();
        tabla.getColumns().clear();
        editarMenuItemText(resources.getString("ventana.libro"));

        TableColumn<Libro, Integer> colCodigo = new TableColumn<>(resources.getString("tabla.libro.codigo"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        TableColumn<Libro, String> colTitulo = new TableColumn<>(resources.getString("tabla.libro.titulo"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        TableColumn<Libro, String> colAutor = new TableColumn<>(resources.getString("tabla.libro.autor"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        TableColumn<Libro, String> colEditorial = new TableColumn<>(resources.getString("tabla.libro.editorial"));
        colEditorial.setCellValueFactory(new PropertyValueFactory<>("editorial"));
        TableColumn<Libro, String> colEstado = new TableColumn<>(resources.getString("tabla.libro.estado"));
        colEstado.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getEstado().equals("disponible") ? resources.getString("libro.estado.disponible") : resources.getString("libro.estado.prestado")));
        TableColumn<Libro, String> colBaja = new TableColumn<>(resources.getString("tabla.libro.baja"));
        colBaja.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getBaja() == 1 ? resources.getString("libro.baja.si") : resources.getString("libro.baja.no")));
        tabla.getColumns().addAll(colCodigo,colTitulo,colAutor,colEditorial,colEstado,colBaja);

        ObservableList<Libro> libros = DaoLibro.cargarListado();
        masterData.setAll(libros);
        tabla.setItems(libros);
    }

    /**
     * Carga en la tabla las columnas de préstamos y los préstamos
     */
    public void cargarPrestamos() {
        tabla.getSelectionModel().clearSelection();
        txtFiltro.setText(null);
        cbFiltro.getItems().clear();
        cbFiltro.getItems().addAll(resources.getString("principal.label.filtro.estudiante"),resources.getString("principal.label.filtro.libro_titulo"),resources.getString("principal.label.filtro.libro_autor"));
        cbFiltro.getSelectionModel().select(0);
        masterData.clear();
        filteredData.clear();
        tabla.getItems().clear();
        tabla.getColumns().clear();
        editarMenuItemText(resources.getString("ventana.prestamo"));

        TableColumn<Prestamo, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id_prestamo"));
        TableColumn<Prestamo, String> colAlumno = new TableColumn<>(resources.getString("tabla.prestamo.estudiante"));
        colAlumno.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getAlumno().getNombre()));
        TableColumn<Prestamo, String> colLibroTitulo = new TableColumn<>(resources.getString("tabla.prestamo.libro_titulo"));
        colLibroTitulo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getLibro().getTitulo()));
        TableColumn<Prestamo, String> colLibroAutor = new TableColumn<>(resources.getString("tabla.prestamo.libro_autor"));
        colLibroAutor.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getLibro().getAutor()));
        TableColumn<Prestamo, String> colFechaPrestamo = new TableColumn<>(resources.getString("tabla.prestamo.fecha_prestamo"));
        colFechaPrestamo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> FechaFormatter.formatearFecha(cellData.getValue().getFecha_prestamo())));
        tabla.getColumns().addAll(colId,colAlumno,colLibroTitulo,colLibroAutor,colFechaPrestamo);

        ObservableList<Prestamo> prestamos = DaoPrestamo.cargarListado();
        masterData.setAll(prestamos);
        tabla.setItems(prestamos);
    }

    /**
     * Carga en la tabla las columnas de historial de préstamos
     */
    public void cargarHistorialPrestamos() {
        tabla.getSelectionModel().clearSelection();
        cbFiltro.getItems().clear();
        cbFiltro.getItems().addAll(resources.getString("principal.label.filtro.estudiante"),resources.getString("principal.label.filtro.libro_titulo"),resources.getString("principal.label.filtro.libro_autor"));
        cbFiltro.getSelectionModel().select(0);
        masterData.clear();
        filteredData.clear();
        tabla.getItems().clear();
        tabla.getColumns().clear();
        editarMenuItemText(resources.getString("ventana.historial"));

        TableColumn<HistorialPrestamo, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id_prestamo"));
        TableColumn<HistorialPrestamo, String> colAlumno = new TableColumn<>(resources.getString("tabla.prestamo.estudiante"));
        colAlumno.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getAlumno().getNombre()));
        TableColumn<HistorialPrestamo, String> colLibroTitulo = new TableColumn<>(resources.getString("tabla.prestamo.libro_titulo"));
        colLibroTitulo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getLibro().getTitulo()));
        TableColumn<HistorialPrestamo, String> colLibroAutor = new TableColumn<>(resources.getString("tabla.prestamo.libro_autor"));
        colLibroAutor.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getLibro().getAutor()));
        TableColumn<HistorialPrestamo, String> colFechaPrestamo = new TableColumn<>(resources.getString("tabla.prestamo.fecha_prestamo"));
        colFechaPrestamo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> FechaFormatter.formatearFecha(cellData.getValue().getFecha_prestamo())));
        TableColumn<HistorialPrestamo, String> colFechaDevolucion = new TableColumn<>(resources.getString("tabla.historial.fecha_devolucion"));
        colFechaDevolucion.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> FechaFormatter.formatearFecha(cellData.getValue().getFecha_devolucion())));
        tabla.getColumns().addAll(colId,colAlumno,colLibroTitulo,colLibroAutor,colFechaPrestamo,colFechaDevolucion);

        ObservableList<HistorialPrestamo> historialPrestamos = DaoHistorialPrestamo.cargarListado();
        masterData.setAll(historialPrestamos);
        tabla.setItems(historialPrestamos);
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