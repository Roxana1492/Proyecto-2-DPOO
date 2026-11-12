package autenticacion;

public class Empleado extends Usuario {
    private String nombre;

    public Empleado(String login, String password, String nombre) {
        super(login, password);
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}