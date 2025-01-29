package es.juliogtrenard.proyectobiblioteca.controller;

import es.juliogtrenard.proyectobiblioteca.model.Ayuda;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Clase para controlar la ventana de ayuda HTML
 */
public class GuiaHTMLController implements Initializable {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(GuiaHTMLController.class);

    /**
     * WebEngine
     */
    private WebEngine webEngine;

    /**
     * Contenido
     */
    @FXML
    private WebView visor;

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
        TreeItem<Ayuda> root = new TreeItem<>(new Ayuda("Raiz", ""));

        TreeItem<Ayuda> base = new TreeItem<>(new Ayuda(resources.getString("ayuda.html"), "index.html"));

        webEngine = visor.getEngine();
        webEngine.load(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/documentacion/html/index.html").toExternalForm());
    }
}
