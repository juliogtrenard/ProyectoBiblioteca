package es.juliogtrenard.proyectobiblioteca.model;

import es.juliogtrenard.proyectobiblioteca.utils.FechaFormatter;

import java.time.LocalDateTime;

/**
 * Modelo Préstamo
 */
public class Prestamo {
    private int id_prestamo;
    private Alumno alumno;
    private Libro libro;
    private LocalDateTime fecha_prestamo;

    /**
     * Constructor con parámetros de préstamo
     *
     * @param id_prestamo del préstamo
     * @param alumno del préstamo
     * @param libro del préstamo
     * @param fecha_prestamo del préstamo
     */
    public Prestamo(int id_prestamo, Alumno alumno, Libro libro, LocalDateTime fecha_prestamo) {
        this.id_prestamo = id_prestamo;
        this.alumno = alumno;
        this.libro = libro;
        this.fecha_prestamo = fecha_prestamo;
    }

    /**
     * Constructor vacío de préstamo
     */
    public Prestamo() {}

    /**
     * ToString de préstamo
     *
     * @return string con información de préstamo
     */
    @Override
    public String toString() {
        return id_prestamo + " - " + alumno + " - " + libro + " - " + FechaFormatter.formatearFecha(fecha_prestamo);
    }

    /**
     * Getter para el id_prestamo
     *
     * @return id_prestamo
     */
    public int getId_prestamo() {
        return id_prestamo;
    }

    /**
     * Setter para el id_prestamo
     *
     * @param id_prestamo nuevo valor de id_prestamo
     */
    public void setId_prestamo(int id_prestamo) {
        this.id_prestamo = id_prestamo;
    }

    /**
     * Getter para el alumno
     *
     * @return alumno
     */
    public Alumno getAlumno() {
        return alumno;
    }

    /**
     * Setter para el alumno
     *
     * @param alumno nuevo valor de alumno
     */
    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    /**
     * Getter para el libro
     *
     * @return libro
     */
    public Libro getLibro() {
        return libro;
    }

    /**
     * Setter para el libro
     *
     * @param libro nuevo valor de libro
     */
    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    /**
     * Getter para el fecha_prestamo
     *
     * @return fecha_prestamo
     */
    public LocalDateTime getFecha_prestamo() {
        return fecha_prestamo;
    }

    /**
     * Setter para el fecha_prestamo
     *
     * @param fecha_prestamo nuevo valor de fecha_prestamo
     */
    public void setFecha_prestamo(LocalDateTime fecha_prestamo) {
        this.fecha_prestamo = fecha_prestamo;
    }
}