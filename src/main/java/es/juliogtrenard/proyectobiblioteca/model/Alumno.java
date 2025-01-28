package es.juliogtrenard.proyectobiblioteca.model;

import java.util.Objects;

/**
 * Modelo de un alumno
 */
public class Alumno {
    private String dni;
    private String nombre;
    private String apellido1;
    private String apellido2;

    /**
     * Constructor con parámetros de alumno
     *
     * @param dni del alumno
     * @param nombre del alumno
     * @param apellido1 del alumno
     * @param apellido2 del alumno
     */
    public Alumno(String dni, String nombre, String apellido1, String apellido2) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
    }

    /**
     * Constructor vacío de alumno
     */
    public Alumno() {}

    /**
     * ToString de alumno
     *
     * @return string con información de alumno
     */
    @Override
    public String toString() {
        return nombre;
    }

    /**
     * Getter para el dni
     *
     * @return dni
     */
    public String getDni() {
        return dni;
    }

    /**
     * Setter para el dni
     *
     * @param dni nuevo valor de dni
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Getter para el nombre
     *
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Setter para el nombre
     *
     * @param nombre nuevo valor de nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Getter para el apellido1
     *
     * @return apellido1
     */
    public String getApellido1() {
        return apellido1;
    }

    /**
     * Setter para el apellido1
     *
     * @param apellido1 nuevo valor de apellido1
     */
    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    /**
     * Getter para el apellido2
     *
     * @return apellido2
     */
    public String getApellido2() {
        return apellido2;
    }

    /**
     * Setter para el apellido2
     *
     * @param apellido2 nuevo valor de apellido2
     */
    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Alumno alumno = (Alumno) o;
        return Objects.equals(dni, alumno.dni);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dni);
    }
}
