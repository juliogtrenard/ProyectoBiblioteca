package es.juliogtrenard.proyectobiblioteca;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ejecuta la aplicación
 */
public class Lanzador {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(Lanzador.class);

    /**
     * Función main donde se lanza la aplicación
     *
     * @param args parámetros por consola
     */
    public static void main(String[] args) {
        logger.info("Iniciando aplicación");
        BibliotecaApplication.main(args);
    }
}
