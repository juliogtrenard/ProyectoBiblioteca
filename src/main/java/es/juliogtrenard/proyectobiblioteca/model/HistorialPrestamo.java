package es.juliogtrenard.proyectobiblioteca.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modelo del historial de préstamo
 */
public class HistorialPrestamo {
    private int id_prestamo;
    private Alumno alumno;
    private Libro libro;
    private LocalDateTime fecha_prestamo;
    private LocalDateTime fecha_devolucion;

    /**
     * Constructor con parameters de historial de préstado
     *
     * @param id_prestamo del historial de prestado
     * @param alumno del historial de prestado
     * @param libro del historial de prestado
     * @param fecha_prestamo del historial de prestado
     * @param fecha_devolucion del historial de prestado
     */
    public HistorialPrestamo(int id_prestamo, Alumno alumno, Libro libro, LocalDateTime fecha_prestamo, LocalDateTime fecha_devolucion) {
        this.id_prestamo = id_prestamo;
        this.alumno = alumno;
        this.libro = libro;
        this.fecha_prestamo = fecha_prestamo;
        this.fecha_devolucion = fecha_devolucion;
    }

    /**
     * Constructor vacío de historial de préstamo
     */
    public HistorialPrestamo() {}

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

    /**
     * Getter para el fecha_devolucion
     *
     * @return fecha_devolucion
     */
    public LocalDateTime getFecha_devolucion() {
        return fecha_devolucion;
    }

    /**
     * Setter para el fecha_devolucion
     *
     * @param fecha_devolucion nuevo valor de fecha_devolucion
     */
    public void setFecha_devolucion(LocalDateTime fecha_devolucion) {
        this.fecha_devolucion = fecha_devolucion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HistorialPrestamo that = (HistorialPrestamo) o;
        return id_prestamo == that.id_prestamo;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_prestamo);
    }
}