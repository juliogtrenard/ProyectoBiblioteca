package es.juliogtrenard.proyectobiblioteca.model;

import java.sql.Blob;
import java.util.Objects;

/**
 * Modelo Libro
 */
public class Libro {
    private int codigo;
    private String titulo;
    private String autor;
    private String editorial;
    private String estado;
    private int baja;
    private Blob portada;

    /**
     * Constructor con parámetros de libro
     *
     * @param codigo del libro
     * @param titulo del libro
     * @param autor del libro
     * @param editorial del libro
     * @param estado del libro
     * @param baja del libro
     * @param portada del libro
     */
    public Libro(int codigo, String titulo, String autor, String editorial, String estado, int baja, Blob portada) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.estado = estado;
        this.baja = baja;
        this.portada = portada;
    }

    /**
     * Constructor vacío de libro
     */
    public Libro() {}

    /**
     * ToString de libro
     *
     * @return string con información de libro
     */
    @Override
    public String toString() {
        return titulo;
    }

    /**
     * Getter para el código
     *
     * @return código
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Setter para el código
     *
     * @param codigo nuevo valor de código
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * Getter para el titulo
     *
     * @return titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Setter para el titulo
     *
     * @param titulo nuevo valor de titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Getter para el autor
     *
     * @return autor
     */
    public String getAutor() {
        return autor;
    }

    /**
     * Setter para el autor
     *
     * @param autor nuevo valor de autor
     */
    public void setAutor(String autor) {
        this.autor = autor;
    }

    /**
     * Getter para el editorial
     *
     * @return editorial
     */
    public String getEditorial() {
        return editorial;
    }

    /**
     * Setter para el editorial
     *
     * @param editorial nuevo valor de editorial
     */
    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    /**
     * Getter para el estado
     *
     * @return estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Setter para el estado
     *
     * @param estado nuevo valor de estado
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Getter para la baja
     *
     * @return baja
     */
    public int getBaja() {
        return baja;
    }

    /**
     * Setter para la baja
     *
     * @param baja nuevo valor de baja
     */
    public void setBaja(int baja) {
        this.baja = baja;
    }

    /**
     * Getter para la portada
     *
     * @return portada
     */
    public Blob getPortada() {
        return portada;
    }

    /**
     * Setter para la portada
     *
     * @param portada nuevo valor de portada
     */
    public void setPortada(Blob portada) {
        this.portada = portada;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Libro libro = (Libro) o;
        return codigo == libro.codigo;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}

