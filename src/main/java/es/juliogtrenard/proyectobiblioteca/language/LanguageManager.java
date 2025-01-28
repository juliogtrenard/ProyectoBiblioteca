package es.juliogtrenard.proyectobiblioteca.language;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Maneja los idiomas
 */
public class LanguageManager {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(LanguageManager.class);

    private static LanguageManager instance;
    private Locale locale = new Locale.Builder().setLanguage(LanguageManager.getLanguage()).build();
    private ResourceBundle bundle;

    /**
     * Obtiene el idioma para la aplicación
     *
     * @return String con el idioma del fichero
     */
    public static String getLanguage() {
        File f = new File("lang.properties");
        Properties properties;
        try {
            FileInputStream configFileReader=new FileInputStream(f);
            properties = new Properties();
            try {
                properties.load(configFileReader);
                configFileReader.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            return null;
        }
        return properties.getProperty("language");
    }

    /**
     * Crea el fichero lang.properties si este no existe
     */
    public static void createFile(String language) {
        File f = new File("lang.properties");
        try {
            Properties properties = new Properties();

            properties.setProperty("language", language);

            FileOutputStream output = new FileOutputStream(f);
            properties.store(output, "Updated properties");
            output.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Cambia el idioma para la aplicación
     *
     * @param language string con el idioma del fichero
     */
    public static void setLanguage(String language) {
        File f = new File("lang.properties");
        try {
            Properties properties = new Properties();
            FileInputStream input = new FileInputStream(f);
            properties.load(input);
            input.close();

            properties.setProperty("language", language);

            FileOutputStream output = new FileOutputStream(f);
            properties.store(output, "Updated properties");
            output.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Carga el bundle
     */
    private LanguageManager() {
        loadResourceBundle();
    }

    /**
     * Crea una instancia de LanguageManager y la devuelve
     *
     * @return instancia de LanguageManager
     */
    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    /**
     * Carga el bundle
     */
    private void loadResourceBundle() {
        bundle = ResourceBundle.getBundle("/es/juliogtrenard/proyectobiblioteca/languages/lang", locale);
    }

    /**
     * Setter de locale
     *
     * @param locale nuevo
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
        loadResourceBundle();
    }

    /**
     * Getter de bundle
     *
     * @return bundle
     */
    public ResourceBundle getBundle() {
        return bundle;
    }

    /**
     * Getter de locale
     *
     * @return locale
     */
    public Locale getLocale() {
        return locale;
    }

}