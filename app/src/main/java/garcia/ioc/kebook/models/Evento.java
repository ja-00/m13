package garcia.ioc.kebook.models;

import java.util.Date;

public class Evento {
    private int id;
    private User proponente;
    private Book libro;
    private String fecha;
    private boolean isAproved;
    private User aprobador;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getProponente() {
        return proponente;
    }

    public void setProponente(User proponente) {
        this.proponente = proponente;
    }

    public Book getLibro() {
        return libro;
    }

    public void setLibro(Book libro) {
        this.libro = libro;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isAproved() {
        return isAproved;
    }

    public void setAproved(boolean aproved) {
        isAproved = aproved;
    }

    public User getAprobador() {
        return aprobador;
    }

    public void setAprobador(User aprobador) {
        this.aprobador = aprobador;
    }
}
