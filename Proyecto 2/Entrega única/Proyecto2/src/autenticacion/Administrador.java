package autenticacion;

public class Administrador extends Usuario {

    public Administrador(String login, String password) {
        super(login, password);
    }

    @Override
    public String darNombre() {
        return "Administrador";
    }
}
