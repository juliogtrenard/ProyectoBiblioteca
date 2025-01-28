package es.juliogtrenard.proyectobiblioteca.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Formateador de fechas
 */
public class FechaFormatter {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(FechaFormatter.class);

    /**
     * Formateo
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    /**
     * Formatea la fecha pasada como parámetro a string
     *
     * @param fecha a formatear
     * @return string de la fecha
     */
    public static String formatearFecha(LocalDateTime fecha) {
        String string = FORMATTER.format(fecha);
        logger.info("Fecha formateada a string: {}", string);
        return string;
    }

    /**
     * Formatea el string pasado como parámetro a LocalDateTime
     *
     * @param string a formatear
     * @return fecha en formato LocalDateTime
     */
    public static LocalDateTime formatearString(String string) {
        LocalDateTime fecha = LocalDateTime.parse(string, FORMATTER);
        logger.info("String formateada a fecha: {}", fecha.toString());
        return fecha;
    }
}
