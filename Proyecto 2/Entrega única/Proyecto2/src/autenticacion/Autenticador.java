package autenticacion;

import java.util.HashMap;
import java.util.Map;

public class Autenticador {

    private Map<String, Usuario> usuarios;

    public Autenticador() {
        usuarios = new HashMap<>();
    }

    public void registrarUsuario(Usuario usuario) {
        if (usuario != null && usuario.getLogin() != null) {
            usuarios.put(usuario.getLogin(), usuario);
        }
    }

    public Usuario autenticar(String login, String password) {
        if (login == null || password == null) {
            return null;
        }

        Usuario usuario = usuarios.get(login);

        if (usuario != null && password.equals(usuario.getPassword())) {
            return usuario;
        }

        return null;
    }
}