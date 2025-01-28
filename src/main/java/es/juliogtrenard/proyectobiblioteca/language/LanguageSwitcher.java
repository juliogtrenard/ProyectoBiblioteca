package es.juliogtrenard.proyectobiblioteca.language;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Cambia el idioma
 */
public class LanguageSwitcher {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(LanguageSwitcher.class);

    private Stage stage;

    /**
     * Constructor de la clase
     *
     * @param stage de la aplicación
     */
    public LanguageSwitcher(Stage stage) {
        this.stage = stage;
    }

    /**
     * Cambia el idioma de la aplicación
     *
     * @param locale nuevo locale
     */
    public void switchLanguage(Locale locale) {
        LanguageManager.getInstance().setLocale(locale);
        LanguageManager.setLanguage(locale.getLanguage());

        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/juliogtrenard/proyectobiblioteca/fxml/principal.fxml"), bundle);
            Parent root = loader.load();
            stage.setTitle(bundle.getString("principal.titulo"));
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}