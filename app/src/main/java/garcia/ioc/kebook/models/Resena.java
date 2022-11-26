package garcia.ioc.kebook.models;

public class Resena {
    private int id;
    private Book libro;
    private User usuario;
    private String resena;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book getLibro() {
        return libro;
    }

    public void setLibro(Book libro) {
        this.libro = libro;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public String getResena() {
        return resena;
    }

    public void setResena(String resena) {
        this.resena = resena;
    }
}
