package es.juliogtrenard.proyectobiblioteca;

import es.juliogtrenard.proyectobiblioteca.language.LanguageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Clase que ejecuta la aplicación
 *
 * @author Julio Gonzalez
 */
public class BibliotecaApplication extends Application {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(BibliotecaApplication.class);

    /**
     * Carga y muestra la ventana de la aplicación
     *
     * @param stage escena de la aplicación
     */
    @Override
    public void start(Stage stage) throws IOException {
        File f1 = new File("lang.properties");
        File f2 = new File("configuration.properties");
        if (!f1.exists() || !f2.exists()) {
            logger.info("Cargando ventana login");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage1 = new Stage();
            stage1.setTitle("Login");
            stage1.setResizable(false);
            stage1.setScene(scene);
            stage1.showAndWait();
        }

        f1 = new File("lang.properties");
        f2 = new File("configuration.properties");
        if (!f1.exists() || !f2.exists()) {
            logger.info("Archivos no encontrados");
            return;
        }

        logger.info("Iniciando ventana principal");
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/principal.fxml"), bundle);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(bundle.getString("principal.titulo"));
        stage.setMinWidth(900);
        stage.setMinHeight(700);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Lanza la aplicación
     *
     * @param args parámetros por consola
     */
    public static void main(String[] args) {
        Application.launch();
    }
}